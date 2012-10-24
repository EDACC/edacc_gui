package edacc.model;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author simon
 */
public class VerifierConfigurationDAO {

    private static final String table = "VerifierConfig";
    private static final String selectQuery = "SELECT * FROM " + table;
    private static final String insertQuery = "INSERT INTO " + table + " (Verifier_idVerifier, Experiment_idExperiment) VALUES (?,?)";
    private static final String updateQuery = "UPDATE " + table + " SET Verifier_idVerifier=?, Experiment_idExperiment=? WHERE idVerifierConfig=?";
    private static final String deleteQuery = "DELETE FROM " + table + " WHERE idVerifierConfig=?";
    private static ObjectCache<VerifierConfiguration> cache = new ObjectCache<VerifierConfiguration>();

    private static VerifierConfiguration getVerifierConfigurationFromResultSet(ResultSet rs) throws SQLException {
        VerifierConfiguration vc = new VerifierConfiguration();
        vc.setId(rs.getInt("idVerifierConfig"));
        vc.setVerifier(VerifierDAO.getById(rs.getInt("Verifier_idVerifier")));
        vc.setIdExperiment(rs.getInt("Experiment_idExperiment"));
        return vc;
    }

    public static void save(final VerifierConfiguration vc) throws SQLException {
        boolean autoCommit = DatabaseConnector.getInstance().getConn().getAutoCommit();
        DatabaseConnector.getInstance().getConn().setAutoCommit(false);
        try {
            if (vc.isNew()) {
                PreparedStatement st = DatabaseConnector.getInstance().getConn().prepareStatement(insertQuery, PreparedStatement.RETURN_GENERATED_KEYS);
                st.setInt(1, vc.getVerifier().getId());
                st.setInt(2, vc.getIdExperiment());
                st.executeUpdate();
                ResultSet rs = st.getGeneratedKeys();
                int i = 0;
                while (rs.next()) {
                    vc.setId(rs.getInt(1));
                    vc.setSaved();
                    DatabaseConnector.getInstance().addRollbackOperation(new Runnable() {

                        @Override
                        public void run() {
                            vc.setNew();
                        }
                    });
                    i++;
                }
                rs.close();
                st.close();
            }
            if (vc.isModified()) {
                PreparedStatement st = DatabaseConnector.getInstance().getConn().prepareStatement(updateQuery);
                st.setInt(1, vc.getVerifier().getId());
                st.setInt(2, vc.getIdExperiment());
                st.setInt(3, vc.getId());

                vc.setSaved();
                DatabaseConnector.getInstance().addRollbackOperation(new Runnable() {

                    @Override
                    public void run() {
                        vc.setModified();
                    }
                });
                st.executeUpdate();
                st.close();
            }
            if (vc.isDeleted()) {
                PreparedStatement st = DatabaseConnector.getInstance().getConn().prepareStatement(deleteQuery);
                st.setInt(1, vc.getId());
                
                st.executeUpdate();
                st.close();
            }
            for (VerifierParameterInstance pi : vc.getParameterInstances()) {
                pi.setVerifierConfigId(vc.getId());
            }
            List<VerifierConfiguration> vcs = new ArrayList<VerifierConfiguration>();
            vcs.add(vc);
            VerifierParameterInstanceDAO.saveAll(vcs);
            for (int i = vc.getParameterInstances().size()-1; i>=0; i--) {
                if (vc.getParameterInstances().get(i).isDeleted()) {
                    final VerifierParameterInstance pi = vc.getParameterInstances().get(i);
                    vc.getParameterInstances().remove(i);
                    DatabaseConnector.getInstance().addRollbackOperation(new Runnable() {

                        @Override
                        public void run() {
                            vc.getParameterInstances().add(pi);
                        }
                        
                    });
                }
            }
            
        } catch (Throwable t) {
            if (autoCommit) {
                DatabaseConnector.getInstance().rollback();
            }
            if (t instanceof Error) {
                throw (Error) t;
            } else if (t instanceof SQLException) {
                throw (SQLException) t;
            }
        } finally {
            if (autoCommit) {
                DatabaseConnector.getInstance().commit();
                DatabaseConnector.getInstance().getConn().setAutoCommit(autoCommit);
            }
        }
    }

    public static List<VerifierConfiguration> getAll() throws SQLException {
        List<VerifierConfiguration> res = new ArrayList<VerifierConfiguration>();
        Map<Integer, List<VerifierParameterInstance>> params = VerifierParameterInstanceDAO.getAll();
        Statement st = DatabaseConnector.getInstance().getConn().createStatement();
        ResultSet rs = st.executeQuery(selectQuery);
        while (rs.next()) {
            VerifierConfiguration vc = cache.getCached(rs.getInt("idVerifierConfig"));
            if (vc == null) {
                vc = getVerifierConfigurationFromResultSet(rs);
                vc.setParameterInstances(params.get(vc.getId()));
                vc.setSaved();
                cache.cache(vc);
            }
            res.add(vc);
        }
        rs.close();
        st.close();
        return res;
    }

    public static VerifierConfiguration getById(int id) throws SQLException {
        VerifierConfiguration vc = cache.getCached(id);
        if (vc == null) {
            getAll();
            vc = cache.getCached(id);
        }
        return vc;
    }

    public static void clearCache() {
        cache.clear();
    }

    public static VerifierConfiguration getByExperimentId(int id) throws SQLException {
        for (VerifierConfiguration vc : getAll()) {
            if (vc.getIdExperiment() == id) {
                return vc;
            }
        }
        return null;
    }
}
