package edacc.model;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author simon
 */
public class SolverConfigurationDAO {

    private static final String table = "SolverConfig";
    private static final String deleteQuery = "DELETE FROM " + table + " WHERE idSolverConfig=?";
    private static final String insertQuery = "INSERT INTO " + table + " (SolverBinaries_IdSolverBinary, Experiment_IdExperiment, seed_group, name, cost, cost_function, parameter_hash, hint) VALUES (?,?,?,?,?,?,?,?)";
    private static final String updateQuery = "UPDATE " + table + " SET SolverBinaries_IdSolverBinary = ?, seed_group=?, name=?, cost=?, cost_function=?, parameter_hash=?, hint=? WHERE idSolverConfig=?";
    public static ObjectCache<SolverConfiguration> cache = new ObjectCache<SolverConfiguration>();

    private static SolverConfiguration getSolverConfigurationFromResultset(ResultSet rs) throws SQLException {
        SolverConfiguration i = new SolverConfiguration();
        i.setExperiment_id(rs.getInt("Experiment_idExperiment"));
        i.setSolverBinary(SolverBinariesDAO.getById(rs.getInt("SolverBinaries_IdSolverBinary")));
        i.setId(rs.getInt("IdSolverConfig"));
        i.setSeed_group(rs.getInt("seed_group"));
        i.setName(rs.getString("name"));
        i.setCost(rs.getFloat("cost"));
        if (rs.wasNull()) {
            i.setCost(null);
        }
        i.setCost_function(rs.getString("cost_function"));
        if (rs.wasNull()) {
            i.setCost_function(null);
        }
        i.setParameter_hash(rs.getString("parameter_hash"));
        i.setHint(rs.getString("hint"));
        return i;
    }

    protected static void save(SolverConfiguration i) throws SQLException {
        if (i.isDeleted()) {
            cache.remove(i);
            PreparedStatement st = DatabaseConnector.getInstance().getConn().prepareStatement(deleteQuery);
            st.setInt(1, i.getId());
            st.executeUpdate();
            st.close();
        } else if (i.isNew()) {
            PreparedStatement st = DatabaseConnector.getInstance().getConn().prepareStatement(insertQuery, PreparedStatement.RETURN_GENERATED_KEYS);
            st.setInt(1, i.getSolverBinary().getId());
            st.setInt(2, i.getExperiment_id());
            st.setInt(3, i.getSeed_group());
            st.setString(4, i.getName());
            if (i.getCost() == null) {
                st.setNull(5, java.sql.Types.FLOAT);
            } else {
                st.setFloat(5, i.getCost());
            }
            st.setString(6, i.getCost_function());
            st.setString(7, i.getParameter_hash());
            st.setString(8, i.getHint());
            st.executeUpdate();
            ResultSet generatedKeys = st.getGeneratedKeys();
            if (generatedKeys.next()) {
                i.setId(generatedKeys.getInt(1));
            }
            generatedKeys.close();
            i.setSaved();
            cache.cache(i);
            st.close();
        } else if (i.isModified()) {
            PreparedStatement st = DatabaseConnector.getInstance().getConn().prepareStatement(updateQuery);
            st.setInt(1, i.getSolverBinary().getIdSolverBinary());
            st.setInt(2, i.getSeed_group());
            st.setString(3, i.getName());
            if (i.getCost() == null) {
                st.setNull(4, java.sql.Types.FLOAT);
            } else {
                st.setFloat(4, i.getCost());
            }
            st.setString(5, i.getCost_function());
            st.setString(6, i.getParameter_hash());
            st.setString(7, i.getHint());
            st.setInt(8, i.getId());
            st.executeUpdate();
            i.setSaved();
            st.close();
        }
    }

    private static String getDeleteQuery(int count) {
        String query = "DELETE FROM " + table + " WHERE idSolverConfig IN (?";
        count--;
        for (int i = 0; i < count; i++) {
            query += ",?";
        }
        query += ")";
        return query;
    }

    private static String getInsertQuery(int count) {
        String query = "INSERT INTO " + table + " (SolverBinaries_IdSolverBinary, Experiment_IdExperiment, seed_group, name, cost, cost_function, parameter_hash, hint) VALUES (?,?,?,?,?,?,?,?)";
        count--;
        for (int i = 0; i < count; i++) {
            query += ",(?,?,?,?,?,?,?,?)";
        }
        return query;
    }

    public static void saveAll(List<SolverConfiguration> scs) throws SQLException {
        int deletedCount = 0, newCount = 0, modifiedCount = 0;
        for (SolverConfiguration sc : scs) {
            if (sc.isDeleted()) {
                deletedCount++;
            } else if (sc.isModified()) {
                modifiedCount++;
            } else if (sc.isNew()) {
                newCount++;
            }
        }
        PreparedStatement stDelete = null, stInsert = null, stUpdate = null;
        if (deletedCount > 0) {
            String query = getDeleteQuery(deletedCount);
            stDelete = DatabaseConnector.getInstance().getConn().prepareStatement(query);
        }
        if (newCount > 0) {
            String query = getInsertQuery(newCount);
            stInsert = DatabaseConnector.getInstance().getConn().prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS);
        }
        int posDelete = 1, posInsert = 1, posUpdate = 1;
        for (SolverConfiguration sc : scs) {
            if (sc.isDeleted()) {
                stDelete.setInt(posDelete++, sc.getId());
            } else if (sc.isModified()) {
                if (stUpdate == null) {
                    stUpdate = DatabaseConnector.getInstance().getConn().prepareStatement(updateQuery);
                } 
                stUpdate.setInt(1, sc.getSolverBinary().getIdSolverBinary());
                stUpdate.setInt(2, sc.getSeed_group());
                stUpdate.setString(3, sc.getName());
                if (sc.getCost() == null) {
                    stUpdate.setNull(4, java.sql.Types.FLOAT);
                } else {
                    stUpdate.setFloat(4, sc.getCost());
                }
                stUpdate.setString(5, sc.getCost_function());
                stUpdate.setString(6, sc.getParameter_hash());
                stUpdate.setString(7, sc.getHint());
                stUpdate.setInt(8, sc.getId());
                stUpdate.addBatch();
            } else if (sc.isNew()) {
                stInsert.setInt(posInsert++, sc.getSolverBinary().getId());
                stInsert.setInt(posInsert++, sc.getExperiment_id());
                stInsert.setInt(posInsert++, sc.getSeed_group());
                stInsert.setString(posInsert++, sc.getName());
                if (sc.getCost() == null) {
                    stInsert.setNull(posInsert++, java.sql.Types.FLOAT);
                } else {
                    stInsert.setFloat(posInsert++, sc.getCost());
                }
                stInsert.setString(posInsert++, sc.getCost_function());
                stInsert.setString(posInsert++, sc.getParameter_hash());
                stInsert.setString(posInsert++, sc.getHint());
            }
        }

        if (stDelete != null) {
            stDelete.executeUpdate();
            stDelete.close();
        }
        if (stUpdate != null) {
            stUpdate.executeBatch();
            stUpdate.close();
        }
        if (stInsert != null) {
            stInsert.executeUpdate();
            ResultSet generatedKeys = stInsert.getGeneratedKeys();

            for (SolverConfiguration sc : scs) {
                if (sc.isNew()) {
                    if (generatedKeys.next()) {
                        sc.setId(generatedKeys.getInt(1));
                    }
                }
            }
            generatedKeys.close();
            stInsert.close();
        }
        for (SolverConfiguration sc : scs) {
            if (sc.isModified() || sc.isNew()) {
                sc.setSaved();
            }
            cache.cache(sc);
        }
    }

    public static ArrayList<ParameterInstance> getSolverConfigurationParameters(SolverConfiguration i) throws SQLException {
        return ParameterInstanceDAO.getBySolverConfig(i);
    }

    /**
     * Sets the solverConfig as deleted.
     * @param solverConfig
     */
    public static void removeSolverConfiguration(SolverConfiguration solverConfig) {
        solverConfig.setDeleted();
    }

    public static SolverConfiguration createSolverConfiguration(SolverBinaries solverBinary, int experimentId, int seed_group, String name, String hint) throws SQLException, Exception {
        return createSolverConfiguration(solverBinary, experimentId, seed_group, name, hint, null, null, null);
    }

    public static SolverConfiguration createSolverConfiguration(SolverBinaries solverBinary, int experimentId, int seed_group, String name, String hint, Float cost, String cost_function, String parameter_hash) throws SQLException, Exception {
        if (solverBinary == null) {
            throw new Exception("Solver binary missing.");
        }
        SolverConfiguration i = new SolverConfiguration();
        i.setSolverBinary(solverBinary);
        i.setExperiment_id(experimentId);
        i.setSeed_group(seed_group);
        i.setName(name);
        i.setHint(hint);
        i.setCost(cost);
        i.setCost_function(cost_function);
        i.setParameter_hash(parameter_hash);
        save(i);
        cache.cache(i);
        return i;
    }

    public static ArrayList<SolverConfiguration> getSolverConfigurationByExperimentId(int experimentId) throws SQLException {
        ArrayList<SolverConfiguration> res = new ArrayList<SolverConfiguration>();
        // TODO: was ordered by solver id, has it to be ordered by solver id?
        PreparedStatement st = DatabaseConnector.getInstance().getConn().prepareStatement("SELECT * FROM " + table + " WHERE Experiment_IdExperiment=?");
        st.setInt(1, experimentId);
        ResultSet rs = st.executeQuery();
        while (rs.next()) {
            SolverConfiguration c = cache.getCached(rs.getInt("IdSolverConfig"));
            if (c != null) {
                if (c.isSaved()) {
                    SolverConfiguration tmp = getSolverConfigurationFromResultset(rs);
                    c.setName(tmp.getName());
                    c.setSaved();
                }
                res.add(c);
            } else {
                SolverConfiguration i = getSolverConfigurationFromResultset(rs);
                cache.cache(i);
                i.setSaved();
                res.add(i);
            }
        }
        rs.close();
        st.close();
        return res;
    }

    public static SolverConfiguration getSolverConfigurationById(int id) throws SQLException {
        SolverConfiguration sc = cache.getCached(id);
        if (sc != null) {
            return sc;
        }

        PreparedStatement st = DatabaseConnector.getInstance().getConn().prepareStatement("SELECT * FROM " + table + " WHERE idSolverConfig=?");
        st.setInt(1, id);
        ResultSet rs = st.executeQuery();
        sc = new SolverConfiguration();
        if (rs.next()) {
            sc = getSolverConfigurationFromResultset(rs);
            sc.setSaved();
            cache.cache(sc);
            sc.setSaved();
            st.close();
            rs.close();
            return sc;
        }
        st.close();
        rs.close();
        return null;
    }

    public static void clearCache() {
        cache.clear();
    }

    /**
     * Checks if the solver configuration <code>sc</code> is deleted.
     * @param sc the solver configuration
     * @return
     */
    public static boolean isDeleted(SolverConfiguration sc) {
        return sc.isDeleted();
    }

    public static ArrayList<SolverConfiguration> getAll() throws SQLException {
        ArrayList<SolverConfiguration> res = new ArrayList<SolverConfiguration>();
        PreparedStatement st = DatabaseConnector.getInstance().getConn().prepareStatement("SELECT * FROM " + table);
        ResultSet rs = st.executeQuery();
        while (rs.next()) {
            SolverConfiguration sc = cache.getCached(rs.getInt("idSolverConfig"));
            if (sc != null) {
                res.add(sc);
            } else {
                sc = getSolverConfigurationFromResultset(rs);
                sc.setSaved();
                cache.cache(sc);
                sc.setSaved();
                res.add(sc);
            }
        }
        rs.close();
        st.close();
        return res;
    }

    public static SolverConfiguration getByParameterHash(int experimentId, String parameter_hash) throws SQLException {
        PreparedStatement st = DatabaseConnector.getInstance().getConn().prepareStatement("SELECT * FROM " + table + " WHERE parameter_hash=? AND Experiment_idExperiment=?");
        st.setString(1, parameter_hash);
        st.setInt(2, experimentId);
        ResultSet rs = st.executeQuery();
        if (rs.next()) {
            SolverConfiguration sc = getSolverConfigurationFromResultset(rs);
            sc.setSaved();
            cache.cache(sc);
            sc.setSaved();
            rs.close();
            st.close();
            return sc;
        }
        rs.close();
        st.close();
        return null;
    }
}
