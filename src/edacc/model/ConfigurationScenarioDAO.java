package edacc.model;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

public class ConfigurationScenarioDAO {

    private final static String table = "ConfigurationScenario";
    private final static String table_params = "ConfigurationScenario_has_Parameters";

    public static void save(ConfigurationScenario cs) throws SQLException {
        boolean autocommit = DatabaseConnector.getInstance().getConn().getAutoCommit();
        try {
            DatabaseConnector.getInstance().getConn().setAutoCommit(false);
            if (cs.isNew()) {
                PreparedStatement st = DatabaseConnector.getInstance().getConn().prepareStatement("INSERT INTO ConfigurationScenario (SolverBinaries_idSolverBinary, Experiment_idExperiment) VALUES (?, ?)", Statement.RETURN_GENERATED_KEYS);
                st.setInt(1, cs.getIdSolverBinary());
                st.setInt(2, cs.getIdExperiment());
                st.executeUpdate();
                ResultSet generatedKeys = st.getGeneratedKeys();
                if (generatedKeys.next()) {
                    cs.setId(generatedKeys.getInt(1));
                }
                generatedKeys.close();
                st.close();
            } else if (cs.isModified()) {
                PreparedStatement st = DatabaseConnector.getInstance().getConn().prepareStatement("UPDATE ConfigurationScenario SET SolverBinaries_idSolverBinary = ? WHERE Experiment_idExperiment = ?");
                st.setInt(1, cs.getIdSolverBinary());
                st.setInt(2, cs.getIdExperiment());
                st.executeUpdate();
                st.close();
            }
            cs.setSaved();
            Statement st = DatabaseConnector.getInstance().getConn().createStatement();
            st.executeUpdate("DELETE FROM ConfigurationScenario_has_Parameters WHERE ConfigurationScenario_idConfigurationScenario = " + cs.getId());
            st.close();
            for (ConfigurationScenarioParameter param : cs.getParameters()) {
                    PreparedStatement ps = DatabaseConnector.getInstance().getConn().prepareStatement("INSERT INTO ConfigurationScenario_has_Parameters (ConfigurationScenario_idConfigurationScenario, Parameters_idParameter, configurable, fixedValue) VALUES (?, ?, ?, ?)");
                    ps.setInt(1, cs.getId());
                    param.setIdConfigurationScenario(cs.getId());
                    ps.setInt(2, param.getIdParameter());
                    ps.setBoolean(3, param.isConfigurable());
                    ps.setString(4, param.getFixedValue());
                    ps.executeUpdate();
                    ps.close();
                    param.setSaved();
            }
        } catch (Exception ex) {
            DatabaseConnector.getInstance().getConn().rollback();
            if (ex instanceof SQLException) {
                throw (SQLException) ex;
            }
        } finally {
            DatabaseConnector.getInstance().getConn().setAutoCommit(autocommit);
        }
    }

    private static ConfigurationScenario getConfigurationScenarioFromResultSet(ResultSet rs) throws SQLException {
        ConfigurationScenario cs = new ConfigurationScenario();
        cs.setId(rs.getInt("idConfigurationScenario"));
        cs.setIdExperiment(rs.getInt("Experiment_idExperiment"));
        cs.setIdSolverBinary(rs.getInt("SolverBinaries_idSolverBinary"));
        return cs;
    }

    public static ConfigurationScenario getConfigurationScenarioByExperimentId(int idExperiment) throws SQLException {
        PreparedStatement st = DatabaseConnector.getInstance().getConn().prepareStatement("SELECT idConfigurationScenario, SolverBinaries_idSolverBinary, Experiment_idExperiment, idSolver FROM " + table + " JOIN SolverBinaries ON SolverBinaries.idSolverBinary=SolverBinaries_idSolverBinary WHERE Experiment_idExperiment=?");
        st.setInt(1, idExperiment);
        ResultSet rs = st.executeQuery();
        ConfigurationScenario cs = null;
        int idSolver;
        if (rs.next()) {
            cs = getConfigurationScenarioFromResultSet(rs);
            cs.setSaved();
            idSolver = rs.getInt("idSolver");
            rs.close();
            st.close();
        } else {
            rs.close();
            st.close();
            return null;
        }

        Map<Integer, Parameter> solver_parameters = new HashMap<Integer, Parameter>();
        for (Parameter p : ParameterDAO.getParameterFromSolverId(idSolver)) {
            solver_parameters.put(p.getId(), p);
        }

        PreparedStatement st2 = DatabaseConnector.getInstance().getConn().prepareStatement("SELECT * FROM " + table_params + " WHERE ConfigurationScenario_idConfigurationScenario=?");
        st2.setInt(1, cs.getId());
        ResultSet rs2 = st2.executeQuery();
        while (rs2.next()) {
            ConfigurationScenarioParameter csp = new ConfigurationScenarioParameter();
            csp.setIdConfigurationScenario(cs.getId());
            csp.setFixedValue(rs2.getString("fixedValue"));
            csp.setIdParameter(rs2.getInt("Parameters_idParameter"));
            csp.setConfigurable(rs2.getBoolean("configurable"));
            csp.setParameter(solver_parameters.get(rs2.getInt("Parameters_idParameter")));
            csp.setSaved();
            cs.getParameters().add(csp);
        }
        rs2.close();
        st2.close();
        return cs;
    }
    
    public static boolean configurationScenarioParameterIsSaved(ConfigurationScenarioParameter param) {
        return param.isSaved();
    }
}
