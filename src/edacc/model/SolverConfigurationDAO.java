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
    private static final Hashtable<SolverConfiguration, SolverConfiguration> cache = new Hashtable<SolverConfiguration, SolverConfiguration>();

    private static SolverConfiguration getSolverConfigurationFromResultset(ResultSet rs) throws SQLException {
        SolverConfiguration i = new SolverConfiguration();
        i.setExperiment_id(rs.getInt("Experiment_idExperiment"));
        i.setSolver_id(rs.getInt("Solver_IdSolver"));
        i.setId(rs.getInt("IdSolverConfig"));
        i.setSeed_group(rs.getInt("seed_group"));
        return i;
    }

    private static SolverConfiguration getCached(SolverConfiguration i) {
        if (cache.containsKey(i)) {
            return cache.get(i);
        } else {
            return null;
        }
    }

    private static void cacheSolverConfiguration(SolverConfiguration i) {
        if (cache.containsKey(i)) {
            return;
        } else {
            cache.put(i, i);
        }
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
        }
        else if (i.isModified()) {
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

    public static void removeSolverConfiguration(SolverConfiguration solverConfig) {
        solverConfig.setDeleted();
    }

    public static SolverConfiguration createSolverConfiguration(int solverId, int experimentId, int seed_group) throws SQLException {
        SolverConfiguration i = new SolverConfiguration();
        i.setSolver_id(solverId);
        i.setExperiment_id(experimentId);
        i.setSeed_group(seed_group);
        save(i);
        cacheSolverConfiguration(i);
        return i;
    }

    public static Vector<SolverConfiguration> getSolverConfigurationByExperimentId(int experimentId) throws SQLException {
        Vector<SolverConfiguration> res = new Vector<SolverConfiguration>();
        PreparedStatement st = DatabaseConnector.getInstance().getConn().prepareStatement("SELECT * FROM " + table + " WHERE Experiment_IdExperiment=? ORDER BY Solver_IdSolver");
        st.setInt(1, experimentId);
        ResultSet rs = st.executeQuery();
        while (rs.next()) {
            SolverConfiguration i = getSolverConfigurationFromResultset(rs);
            SolverConfiguration c = getCached(i);
            if (c != null) {
                res.add(c);
            } else {
                cacheSolverConfiguration(i);
                i.setSaved();
                res.add(i);
            }
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
}
