package edacc.model;

import edacc.parameterspace.ParameterConfiguration;
import edacc.parameterspace.domain.FlagDomain;
import edacc.parameterspace.domain.OptionalDomain;
import edacc.parameterspace.graph.ParameterGraph;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Blob;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

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
        } finally {
            rs.close();
            st.close();
        }
        return null;
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
    public static int createSolverConfig(int idExperiment, ParameterConfiguration config, String name) {
        try {
            ConfigurationScenario cs = ConfigurationScenarioDAO.getConfigurationScenarioByExperimentId(idExperiment);
            SolverBinaries solver_binary = SolverBinariesDAO.getById(cs.getIdSolverBinary());
            SolverConfiguration solver_config = SolverConfigurationDAO.createSolverConfiguration(solver_binary, idExperiment, 0, name);

            for (ConfigurationScenarioParameter param : cs.getParameters()) {
                if ("instance".equals(param.getParameter().getName()) || "seed".equals(param.getParameter().getName())) {
                    ParameterInstance pi = ParameterInstanceDAO.createParameterInstance(param.getParameter().getId(), solver_config, "");
                    ParameterInstanceDAO.save(pi);
                } else if (!param.isConfigurable()) {
                    if (param.getParameter().getHasValue()) {
                        ParameterInstance pi = ParameterInstanceDAO.createParameterInstance(param.getParameter().getId(), solver_config, param.getFixedValue());
                        ParameterInstanceDAO.save(pi);
                    } else { // flag
                        System.out.println("flag" + param.getParameter().getName());
                        ParameterInstance pi = ParameterInstanceDAO.createParameterInstance(param.getParameter().getId(), solver_config, "");
                        ParameterInstanceDAO.save(pi);
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
                        ParameterInstance pi = ParameterInstanceDAO.createParameterInstance(param.getParameter().getId(), solver_config, config.getParameterValue(config_param).toString());
                        ParameterInstanceDAO.save(pi);
                    }
                }
            }

            return solver_config.getId();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
}
