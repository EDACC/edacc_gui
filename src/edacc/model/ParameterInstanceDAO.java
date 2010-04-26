/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edacc.model;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Hashtable;
import java.util.Vector;

/**
 *
 * @author simon
 */
public class ParameterInstanceDAO {

    private static final String table = "SolverConfig_has_Parameters";
    private static final String deleteQuery = "DELETE FROM " + table + " WHERE SolverConfig_IdSolverConfig=? AND Parameters_IdParameter=?";
    private static final String insertQuery = "INSERT INTO " + table + " (SolverConfig_IdSolverConfig, Parameters_IdParameter, value) VALUES (?,?,?)";
    private static final String updateQuery = "UPDATE "+ table+" SET value=? WHERE SolverConfig_IdSolverConfig=? AND Parameters_IdParameter=?";
    private static final Hashtable<ParameterInstance, ParameterInstance> cache = new Hashtable<ParameterInstance, ParameterInstance>();

    /**
     * ParameterInstance factory method. Saves the new object in the database.
     * @param parameterId
     * @param solverConfigId
     * @return
     * @throws SQLException
     */
    public static ParameterInstance createParameterInstance(int parameterId, int solverConfigId, String value) throws SQLException {
        ParameterInstance i = new ParameterInstance();
        i.setParameter_id(parameterId);
        i.setSolver_config_id(solverConfigId);
        i.setValue(value);
        save(i);
        cacheParameterInstance(i);
        return i;
    }

    public static void save(ParameterInstance i) throws SQLException {
        if (i.isDeleted()) {
            PreparedStatement st = DatabaseConnector.getInstance().getConn().prepareStatement(deleteQuery);
            st.setInt(1, i.getSolver_config_id());
            st.setInt(2, i.getParameter_id());
            st.executeUpdate();
        } else if (i.isModified()) {
            PreparedStatement st = DatabaseConnector.getInstance().getConn().prepareStatement(updateQuery);
            st.setString(1, i.getValue());
            st.setInt(2, i.getSolver_config_id());
            st.setInt(3, i.getParameter_id());
            st.executeUpdate();
            i.setSaved();
        } else if (i.isNew()) {
            PreparedStatement st = DatabaseConnector.getInstance().getConn().prepareStatement(insertQuery);
            st.setInt(1, i.getSolver_config_id());
            st.setInt(2, i.getParameter_id());
            st.setString(3, i.getValue());
            st.executeUpdate();
            i.setSaved();
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
    public static void removeParameterInstance(ParameterInstance param) throws SQLException {
        param.setDeleted();
        save(param);
        cache.remove(param);
    }

    private static ParameterInstance getParameterInstanceFromResultset(ResultSet rs) throws SQLException {
        ParameterInstance i = new ParameterInstance();
        i.setSolver_config_id(rs.getInt("SolverConfig_idSolverConfig"));
        i.setParameter_id(rs.getInt("Parameters_idParameter"));
        i.setValue(rs.getString("value"));
        return i;
    }

    private static ParameterInstance getCached(ParameterInstance i) {
        if (cache.containsKey(i)) {
            return cache.get(i);
        } else {
            return null;
        }
    }

    private static void cacheParameterInstance(ParameterInstance i) {
        if (cache.containsKey(i)) {
            return;
        } else {
            cache.put(i, i);
        }
    }

    /**
     * Gets all paramter instances from a solver configuration.
     * @param id
     * @return
     * @throws SQLException
     */
    public static Vector<ParameterInstance> getBySolverConfigId(int id) throws SQLException {
        Vector<ParameterInstance> res = new Vector<ParameterInstance>();
        PreparedStatement st = DatabaseConnector.getInstance().getConn().prepareStatement("SELECT * FROM " + table + " WHERE SolverConfig_idSolverConfig=?");
        st.setInt(1, id);
        ResultSet rs = st.executeQuery();
        while (rs.next()) {
            ParameterInstance i = getParameterInstanceFromResultset(rs);
            ParameterInstance c = getCached(i);
            if (c != null) {
                res.add(c);
            } else {
                i.setSaved();
                res.add(i);
                cacheParameterInstance(i);
            }
        }
        return res;
    }

    public static void clearCache() {
        cache.clear();
    }
}
