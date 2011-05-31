/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edacc.model;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author simon
 */
public class ParameterInstanceDAO {

    private static final String table = "SolverConfig_has_Parameters";
    private static final String deleteQuery = "DELETE FROM " + table + " WHERE SolverConfig_IdSolverConfig=? AND Parameters_IdParameter=?";
    private static final String insertQuery = "INSERT INTO " + table + " (SolverConfig_IdSolverConfig, Parameters_IdParameter, value) VALUES (?,?,?)";
    private static final String updateQuery = "UPDATE " + table + " SET value=? WHERE SolverConfig_IdSolverConfig=? AND Parameters_IdParameter=?";
    private static final HashMap<SolverConfiguration, ArrayList<ParameterInstance>> cache = new HashMap<SolverConfiguration, ArrayList<ParameterInstance>>();

    /**
     * ParameterInstance factory method. Saves the new object in the database.
     * @param parameterId
     * @param solverConfigId
     * @return
     * @throws SQLException
     */
    public static ParameterInstance createParameterInstance(int parameterId, SolverConfiguration sc, String value) throws SQLException {
        ParameterInstance i = new ParameterInstance();
        i.setParameter_id(parameterId);
        i.setSolverConfiguration(sc);
        i.setValue(value);
        save(i);
        return i;
    }

    public static void save(ParameterInstance i) throws SQLException {
        ArrayList<ParameterInstance> pi = getCached(i.getSolverConfiguration());
        if (i.isDeleted()) {
            PreparedStatement st = DatabaseConnector.getInstance().getConn().prepareStatement(deleteQuery);
            st.setInt(1, i.getSolverConfiguration().getId());
            st.setInt(2, i.getParameter_id());
            st.executeUpdate();
            
            // update cache
            if (pi != null) {
                for (int k = 0; k < pi.size(); k++) {
                    if (pi.get(k).equals(i)) {
                        pi.remove(k);
                        break;
                    }
                }
            }
        } else if (i.isModified()) {
            PreparedStatement st = DatabaseConnector.getInstance().getConn().prepareStatement(updateQuery);
            st.setString(1, i.getValue());
            st.setInt(2, i.getSolverConfiguration().getId());
            st.setInt(3, i.getParameter_id());
            st.executeUpdate();
            i.setSaved();
        } else if (i.isNew()) {
            PreparedStatement st = DatabaseConnector.getInstance().getConn().prepareStatement(insertQuery);
            st.setInt(1, i.getSolverConfiguration().getId());
            st.setInt(2, i.getParameter_id());
            st.setString(3, i.getValue());
            st.executeUpdate();
            i.setSaved();
            
            // update cache
            if (pi != null) {
                pi.add(i);
            }
        }

    }

    public static void setModified(ParameterInstance i) {
        if (i.isSaved()) {
            i.setModified();
        }
    }

    public static void setDeleted(ParameterInstance i) {
        i.setDeleted();
    }

    private static ParameterInstance getParameterInstanceFromResultset(ResultSet rs, SolverConfiguration sc) throws SQLException {
        ParameterInstance i = new ParameterInstance();
        i.setSolverConfiguration(sc);
        i.setParameter_id(rs.getInt("Parameters_idParameter"));
        i.setValue(rs.getString("value"));
        return i;
    }

    private static ArrayList<ParameterInstance> getCached(SolverConfiguration i) {
        if (cache.containsKey(i)) {
            return cache.get(i);
        } else {
            return null;
        }
    }

    private static void cacheParameterInstances(SolverConfiguration sc, ArrayList<ParameterInstance> i) {
        if (cache.containsKey(sc)) {
            return;
        } else {
            cache.put(sc, i);
        }
    }

    /**
     * Gets all parameter instances from a solver configuration.
     * @param sc
     * @return
     * @throws SQLException
     */
    public static ArrayList<ParameterInstance> getBySolverConfig(SolverConfiguration sc) throws SQLException {
        ArrayList<ParameterInstance> res = getCached(sc);
        if (res != null) {
            return res;
        }
        res = new ArrayList<ParameterInstance>();
        PreparedStatement st = DatabaseConnector.getInstance().getConn().prepareStatement("SELECT * FROM " + table + " WHERE SolverConfig_idSolverConfig=?");
        st.setInt(1, sc.getId());
        ResultSet rs = st.executeQuery();
        while (rs.next()) {
            ParameterInstance i = getParameterInstanceFromResultset(rs, sc);
            i.setSaved();
            res.add(i);
        }
        cacheParameterInstances(sc, res);
        return res;
    }

    public static void clearCache() {
        cache.clear();
    }
}
