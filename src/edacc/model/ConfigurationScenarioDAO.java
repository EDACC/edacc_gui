package edacc.model;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class ConfigurationScenarioDAO {
    private final static String table = "ConfigurationScenario";
    private final static String table_params = "ConfigurationScenario_has_Parameters";
    
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
            idSolver = rs.getInt("idSolver");
            rs.close();
            st.close();
        }
        else {
            rs.close();
            st.close();
            return null;
        }
        
        Map<Integer, Parameter> solver_parameters = new HashMap<Integer, Parameter>();
        for (Parameter p: ParameterDAO.getParameterFromSolverId(idSolver)) {
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
            cs.getParameters().add(csp);
        }
        rs2.close();
        st2.close();
        

        return null;
    }
}
