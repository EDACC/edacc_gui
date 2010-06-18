/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edacc.model;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

/**
 *
 * @author simon
 */
public class SolverConfigurationDAO {

    private static final String table = "SolverConfig";
    private static final String deleteQuery = "DELETE FROM " + table + " WHERE idSolverConfig=?";
    private static final String insertQuery = "INSERT INTO " + table + " (Solver_IdSolver, Experiment_IdExperiment, seed_group) VALUES (?,?,?)";
    private static final String updateQuery = "UPDATE " + table + " SET seed_group=? WHERE idSolverConfig=?";
    private static final ObjectCache<SolverConfiguration> cache = new ObjectCache<SolverConfiguration>();

    private static SolverConfiguration getSolverConfigurationFromResultset(ResultSet rs) throws SQLException {
        SolverConfiguration i = new SolverConfiguration();
        i.setExperiment_id(rs.getInt("Experiment_idExperiment"));
        i.setSolver_id(rs.getInt("Solver_IdSolver"));
        i.setId(rs.getInt("IdSolverConfig"));
        i.setSeed_group(rs.getInt("seed_group"));
        return i;
    }

    private static void save(SolverConfiguration i) throws SQLException {
        if (i.isDeleted()) {
            cache.remove(i);
            PreparedStatement st = DatabaseConnector.getInstance().getConn().prepareStatement(deleteQuery);
            st.setInt(1, i.getId());
            st.executeUpdate();
        } else if (i.isNew()) {
            PreparedStatement st = DatabaseConnector.getInstance().getConn().prepareStatement(insertQuery, PreparedStatement.RETURN_GENERATED_KEYS);
            st.setInt(1, i.getSolver_id());
            st.setInt(2, i.getExperiment_id());
            st.setInt(3, i.getSeed_group());
            st.executeUpdate();
            ResultSet generatedKeys = st.getGeneratedKeys();
            if (generatedKeys.next()) {
                i.setId(generatedKeys.getInt(1));
            }
            i.setSaved();
            cache.cache(i);
        } else if (i.isModified()) {
            PreparedStatement st = DatabaseConnector.getInstance().getConn().prepareStatement(updateQuery);
            st.setInt(1, i.getSeed_group());
            st.setInt(2, i.getId());
            st.executeUpdate();
            i.setSaved();
        }
    }

    public static Vector<ParameterInstance> getSolverConfigurationParameters(SolverConfiguration i) throws SQLException {
        return ParameterInstanceDAO.getBySolverConfigId(i.getId());
    }

    /**
     * Sets the solverConfig as deleted.
     * @param solverConfig
     */
    public static void removeSolverConfiguration(SolverConfiguration solverConfig) {
        solverConfig.setDeleted();
    }

    public static SolverConfiguration createSolverConfiguration(int solverId, int experimentId, int seed_group) throws SQLException {
        SolverConfiguration i = new SolverConfiguration();
        i.setSolver_id(solverId);
        i.setExperiment_id(experimentId);
        i.setSeed_group(seed_group);
        save(i);
        cache.cache(i);
        return i;
    }

    public static Vector<SolverConfiguration> getSolverConfigurationByExperimentId(int experimentId) throws SQLException {
        Vector<SolverConfiguration> res = new Vector<SolverConfiguration>();
        PreparedStatement st = DatabaseConnector.getInstance().getConn().prepareStatement("SELECT * FROM " + table + " WHERE Experiment_IdExperiment=? ORDER BY Solver_IdSolver");
        st.setInt(1, experimentId);
        ResultSet rs = st.executeQuery();
        while (rs.next()) {
            SolverConfiguration c = cache.getCached(rs.getInt("IdSolverConfig"));
            if (c != null) {
                res.add(c);
            } else {
                SolverConfiguration i = getSolverConfigurationFromResultset(rs);
                cache.cache(i);
                i.setSaved();
                res.add(i);
            }
        }
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
            sc.setId(rs.getInt("idSolverConfig"));
            sc.setSolver_id(rs.getInt("Solver_idSolver"));
            sc.setExperiment_id(rs.getInt("Experiment_idExperiment"));
            sc.setSeed_group(rs.getInt("seed_group"));
            sc.setSaved();
            cache.cache(sc);
            sc.setSaved();
            return sc;
        }
        rs.close();
        return null;
    }

    /**
     * Returns all solver config ids associated with the experiment specified by id.
     * @param id the experiment id
     * @return vector of solver config ids
     * @throws SQLException
     */
    public static Vector<Integer> getAllSolverConfigIdsByExperimentId(int id) throws SQLException {
        Vector<Integer> res = new Vector<Integer>();
        PreparedStatement st = DatabaseConnector.getInstance().getConn().prepareStatement(
                "SELECT idSolverConfig " +
                "FROM " + table + " " +
                "WHERE Experiment_idExperiment=? GROUP BY idSolverConfig ORDER BY idSolverConfig;");
        st.setInt(1, id);
        ResultSet rs = st.executeQuery();
        while (rs.next()) {
            res.add(rs.getInt(1));
        }

        return res;
    }

    /**
     * Equalises local data with database data so that the state of all
     * solver configurations is saved.
     * @throws SQLException
     */
    public static void saveAll() throws SQLException {
        Enumeration<SolverConfiguration> e = cache.elements();
        while (e.hasMoreElements()) {
            save(e.nextElement());
        }
    }

    /**
     * Returns a vector of all solver configurations which are marked as deleted.
     * @return a vector of solver configurations
     */
    public static Vector<SolverConfiguration> getAllDeleted() {
        Vector<SolverConfiguration> res = new Vector<SolverConfiguration>();
        Enumeration<SolverConfiguration> e = cache.elements();
        while (e.hasMoreElements()) {
            SolverConfiguration sc = e.nextElement();
            if (sc.isDeleted()) {
                res.add(sc);
            }
        }
        return res;
    }

    public static void clearCache() {
        cache.clear();
    }

    /**
     * Checks if there are unsaved solver configurations in the cache
     * @return true, if and only if there are unsaved solver configurations
     * in the cache, false otherwise
     */
    public static boolean isModified() {
        for (SolverConfiguration sc : cache.values()) {
            if (!sc.isSaved()) {
                return true;
            }
        }
        return false;
    }

    public static Vector<SolverConfiguration> getAllCached() {
        Vector<SolverConfiguration> res = new Vector<SolverConfiguration>();
        for (SolverConfiguration sc : cache.values()) {
            res.add(sc);
        }
        return res;
    }
}
