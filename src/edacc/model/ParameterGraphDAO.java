package edacc.model;

import edacc.parameterspace.ParameterConfiguration;
import edacc.parameterspace.domain.FlagDomain;
import edacc.parameterspace.domain.OptionalDomain;
import edacc.parameterspace.graph.ParameterGraph;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

/**
 *
 * @author simon
 */
public class ParameterGraphDAO {

    public static final String INSERT = "INSERT INTO ParameterGraph (Solver_idSolver, serializedGraph) VALUES (?, ?)";
    public static final String UPDATE = "UPDATE ParameterGraph SET serializedGraph = ? WHERE Solver_idSolver = ?";

    /**
     * Loads the parameter graph object of the solver binary selected in the configuration experiment
     * specified by the idExperiment argument. Parameter graphs objects provide methods
     * to build, modify and validate solver parameters.
     * @param idExperiment ID of the configuration experiment
     * @return parameter graph object providing parameter space methods.
     * @throws SQLException 
     */
    public static ParameterGraph loadParameterGraph(Solver solver) throws SQLException, JAXBException {
        Statement st = DatabaseConnector.getInstance().getConn().createStatement();
        ResultSet rs = st.executeQuery("SELECT serializedGraph FROM ParameterGraph WHERE Solver_idSolver = " + solver.getId());
        try {
            if (rs.next()) {
                ParameterGraph p = unmarshal(ParameterGraph.class, rs.getBlob("serializedGraph").getBinaryStream());
                p.buildAdjacencyList();
                return p;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            rs.close();
            st.close();
        }
        return null;
    }

    public static ParameterGraph loadParameterGraph(File file) throws FileNotFoundException, JAXBException {
        FileInputStream input = new FileInputStream(file);
        return unmarshal(ParameterGraph.class, input);
    }

    public static void saveParameterGraph(File file, ParameterGraph graph) throws JAXBException, FileNotFoundException, IOException {
        InputStream stream = marshall(ParameterGraph.class, graph);
        FileOutputStream fo = new FileOutputStream(file);
        byte[] buf = new byte[2048];
        int len;
        while ((len = stream.read(buf)) > 0) {
            fo.write(buf, 0, len);
        }
        fo.close();
        stream.close();
    }

    public static void saveParameterGraph(ParameterGraph graph, Solver solver) throws SQLException, JAXBException {
        PreparedStatement st;
        if (loadParameterGraph(solver) != null) {
            st = DatabaseConnector.getInstance().getConn().prepareStatement(UPDATE);
            st.setInt(2, solver.getId());
            st.setBinaryStream(1, marshall(ParameterGraph.class, graph));
        } else {
            st = DatabaseConnector.getInstance().getConn().prepareStatement(INSERT);
            st.setInt(1, solver.getId());
            st.setBinaryStream(2, marshall(ParameterGraph.class, graph));
        }
        st.executeUpdate();
    }

    /**
     * Generic XML unmarshalling method.
     * @param docClass
     * @param inputStream
     * @return
     * @throws JAXBException
     */
    private static <T> T unmarshal(Class<T> docClass, InputStream inputStream) throws JAXBException {
        JAXBContext jc = JAXBContext.newInstance(docClass);
        Unmarshaller u = jc.createUnmarshaller();
        return (T) u.unmarshal(inputStream);
    }

    /**
     * Generic XML marshalling method.
     * @param <T>
     * @param docClass
     * @param object
     * @return
     * @throws JAXBException 
     */
    private static <T> InputStream marshall(Class<T> docClass, T object) throws JAXBException {
        JAXBContext jc = JAXBContext.newInstance(docClass);
        Marshaller m = jc.createMarshaller();
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        m.marshal(object, os);
        return new ByteArrayInputStream(os.toByteArray());
    }

    public static List<Integer> createSolverConfigs(int idExperiment, List<ParameterConfiguration> configs, List<String> names) throws SQLException {
        if (configs.size() != names.size()) {
            throw new IllegalArgumentException();
        }
        List<Integer> solverConfigIds = new LinkedList<Integer>();
        if (configs.isEmpty()) {
            return solverConfigIds;
        }
        
        boolean autocommit = DatabaseConnector.getInstance().getConn().getAutoCommit();
        try {
            DatabaseConnector.getInstance().getConn().setAutoCommit(false);
            ConfigurationScenario cs = ConfigurationScenarioDAO.getConfigurationScenarioByExperimentId(idExperiment);
            SolverBinaries solver_binary = SolverBinariesDAO.getById(cs.getIdSolverBinary());

            List<SolverConfiguration> solverConfigurations = new LinkedList<SolverConfiguration>();
            for (int i = 0; i < configs.size(); i++) {
                ParameterConfiguration config = configs.get(i);
                config.updateChecksum();
                SolverConfiguration solver_config = new SolverConfiguration();
                solver_config.setHint("");
                solver_config.setSolverBinary(solver_binary);
                solver_config.setExperiment_id(idExperiment);
                solver_config.setName(names.get(i));
                solver_config.setParameter_hash(toHex(config.getChecksum()));
                solverConfigurations.add(solver_config);
                SolverConfigurationDAO.save(solver_config);
                solverConfigIds.add(solver_config.getId());
            }

            List<ParameterInstance> paramInstances = new LinkedList<ParameterInstance>();
            for (int i = 0; i < configs.size(); i++) {
                SolverConfiguration solver_config = solverConfigurations.get(i);
                ParameterConfiguration config = configs.get(i);
                for (ConfigurationScenarioParameter param : cs.getParameters()) {
                    ParameterInstance pi = new ParameterInstance();
                    if ("instance".equals(param.getParameter().getName()) || "seed".equals(param.getParameter().getName())) {
                        pi.setParameter_id(param.getParameter().getId());
                        pi.setSolverConfiguration(solver_config);
                        pi.setValue("");
                        paramInstances.add(pi);
                    } else if (!param.isConfigurable()) {
                        if (param.getParameter().getHasValue()) {
                            pi.setParameter_id(param.getParameter().getId());
                            pi.setSolverConfiguration(solver_config);
                            pi.setValue(param.getFixedValue());
                            paramInstances.add(pi);
                        } else { // flag
                            pi.setParameter_id(param.getParameter().getId());
                            pi.setSolverConfiguration(solver_config);
                            pi.setValue("");
                            paramInstances.add(pi);
                        }
                    } else if (param.isConfigurable()) {
                        edacc.parameterspace.Parameter config_param = null;
                        for (edacc.parameterspace.Parameter p : config.getParameter_instances().keySet()) {
                            if (p.getName().equals(param.getParameter().getName())) {
                                config_param = p;
                                break;
                            }
                        }
                        if (config_param == null) {
                            System.out.println("no parameterspace param corresponding to " + param.getParameter().getName());
                            continue;
                        }

                        if (OptionalDomain.OPTIONS.NOT_SPECIFIED.equals(config.getParameterValue(config_param))) {
                            continue;
                        } else if (FlagDomain.FLAGS.OFF.equals(config.getParameterValue(config_param))) {
                            continue;
                        } else {
                            pi.setParameter_id(param.getParameter().getId());
                            pi.setSolverConfiguration(solver_config);
                            pi.setValue(config.getParameterValue(config_param).toString());
                            paramInstances.add(pi);
                        }
                    }
                }
            }
            ParameterInstanceDAO.saveBulk(paramInstances);
        } catch (Exception e) {
            DatabaseConnector.getInstance().getConn().rollback();
            e.printStackTrace();
        } finally {
            DatabaseConnector.getInstance().getConn().setAutoCommit(autocommit);
        }

        return solverConfigIds;
    }

    /**
     * Creates a new solver configuration in the database for the experiment specified by the idExperiment argument.
     * The solver binary of the configuration is determined by the configuration scenario that the user created in the GUI.
     * The parameters values are assigned by looping over the parameters that were chosen in the GUI for the configuration scenario.
     * Non-configurable parameters take on the value that the user specified as "fixed value" while configurable parameters
     * take on the values that are specified in the ParameterConfiguration config that is passed to this function.
     * @param idExperiment ID of the experiment for which to create a solver configuration.
     * @param config parameter configuration object that specifies the values of parameters.
     * @return unique database ID > 0 of the created solver configuration, 0 on errors.
     */
    public static int createSolverConfig(int idExperiment, ParameterConfiguration config, String name) throws SQLException {
        List<ParameterConfiguration> configs = new LinkedList<ParameterConfiguration>();
        configs.add(config);
        List<String> names = new LinkedList<String>();
        names.add(name);
        List<Integer> res = createSolverConfigs(idExperiment, configs, names);
        if (res.size()>0) {
            return res.get(0);
        }
        return 0;
    }

    public static String toHex(byte[] bytes) {
        if (bytes == null) {
            return "";
        }
        BigInteger bi = new BigInteger(1, bytes);
        return String.format("%0" + (bytes.length << 1) + "X", bi);
    }
}
