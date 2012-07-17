/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edacc.model;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

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

    public static void saveBatch(List<ParameterInstance> parameters) throws SQLException {
        PreparedStatement st = DatabaseConnector.getInstance().getConn().prepareStatement(insertQuery);
        for (ParameterInstance pi : parameters) {
            st.setInt(1, pi.getSolverConfiguration().getId());
            st.setInt(2, pi.getParameter_id());
            st.setString(3, pi.getValue());
            pi.setSaved();
            st.addBatch();
        }
        st.executeBatch();
        st.close();
    }

    public static void saveBulk(List<ParameterInstance> parameters) throws SQLException {
        if (parameters.isEmpty()) {
            return;
        }
        List<ParameterInstance> modified = new LinkedList<ParameterInstance>();
        List<ParameterInstance> deleted = new LinkedList<ParameterInstance>();
        List<ParameterInstance> newOnes = new LinkedList<ParameterInstance>();

        for (ParameterInstance pi : parameters) {
            if (pi.isNew()) {
                newOnes.add(pi);
            } else if (pi.isModified()) {
                modified.add(pi);
            } else if (pi.isDeleted()) {
                deleted.add(pi);
            }
        }

        if (!newOnes.isEmpty()) {
            StringBuilder insertQuery = new StringBuilder();
            insertQuery.append("INSERT INTO " + table + " (SolverConfig_IdSolverConfig, Parameters_IdParameter, value) VALUES ");
            ParameterInstance last = newOnes.get(newOnes.size() - 1);
            for (ParameterInstance pi : newOnes) {
                insertQuery.append("(");
                insertQuery.append(pi.getSolverConfiguration().getId());
                insertQuery.append(",");
                insertQuery.append(pi.getParameter_id());
                insertQuery.append(",?)");
                if (pi != last) {
                    insertQuery.append(",");
                }
            }
            PreparedStatement st = DatabaseConnector.getInstance().getConn().prepareStatement(insertQuery.toString());
            int count = 1;
            for (ParameterInstance pi : newOnes) {
                // update cache
                ArrayList<ParameterInstance> cachedPis = getCached(pi.getSolverConfiguration());
                if (cachedPis != null) {
                    cachedPis.add(pi);
                }
                
                st.setString(count++, pi.getValue());
                pi.setSaved();
            }
            st.executeUpdate();
            st.close();
        }
        if (!modified.isEmpty()) {
            PreparedStatement st = DatabaseConnector.getInstance().getConn().prepareStatement(updateQuery);
            for (ParameterInstance pi : modified) {
                st.setString(1, pi.getValue());
                st.setInt(2, pi.getSolverConfiguration().getId());
                st.setInt(3, pi.getParameter_id());
                st.addBatch();
            }
            st.executeBatch();
            st.close();
        }
        if (!deleted.isEmpty()) {
            StringBuilder tmp = new StringBuilder();
            
            int count = deleted.size();
            tmp.append("(?");
            count--;
            for (int i = 0; i < count; i++) {
                tmp.append(",?");
            }
            tmp.append(")");
            StringBuilder deleteQuery = new StringBuilder("DELETE FROM " + table + " WHERE ");
            ParameterInstance last = deleted.get(deleted.size()-1);
            for (int i = 0; i < deleted.size(); i++) {
                deleteQuery.append("SolverConfig_IdSolverConfig=").append(deleted.get(i).getSolverConfiguration().getId()).append(" AND Parameters_IdParameter=").append(deleted.get(i).getParameter_id());
                if (deleted.get(i) != last) {
                    deleteQuery.append(" OR ");
                }
            }
            Statement st = DatabaseConnector.getInstance().getConn().createStatement();
            st.executeUpdate(deleteQuery.toString());
            st.close();
        }
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
            st.close();
        } else if (i.isModified()) {
            PreparedStatement st = DatabaseConnector.getInstance().getConn().prepareStatement(updateQuery);
            st.setString(1, i.getValue());
            st.setInt(2, i.getSolverConfiguration().getId());
            st.setInt(3, i.getParameter_id());
            st.executeUpdate();
            i.setSaved();
            st.close();
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
            st.close();
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
        rs.close();
        st.close();
        return res;
    }

    public static void cacheParameterInstances(List<SolverConfiguration> p_scs) throws SQLException {
        if (p_scs == null) {
            return;
        }
        ArrayList<SolverConfiguration> scs = new ArrayList<SolverConfiguration>();
        for (SolverConfiguration sc : p_scs) {
            if (getCached(sc) == null) {
                scs.add(sc);
            }
        }
        if (scs.isEmpty()) {
            return;
        }
        Collections.sort(scs, new Comparator<SolverConfiguration>() {

            @Override
            public int compare(SolverConfiguration o1, SolverConfiguration o2) {
                return o1.getId() - o2.getId();
            }
        });
        StringBuilder sb = new StringBuilder("(");
        for (int i = 0; i < scs.size() - 1; i++) {
            sb.append(scs.get(i).getId()).append(',');
        }
        sb.append(scs.get(scs.size() - 1).getId()).append(')');
        String idString = sb.toString();
        PreparedStatement st = DatabaseConnector.getInstance().getConn().prepareStatement("SELECT * FROM " + table + " WHERE SolverConfig_idSolverConfig IN " + idString + " ORDER BY SolverConfig_idSolverConfig ASC");
        int cur_idx = 0;
        ArrayList<ParameterInstance> cur = new ArrayList<ParameterInstance>();
        SolverConfiguration cur_sc = scs.get(cur_idx);
        ResultSet rs = st.executeQuery();
        while (rs.next()) {
            if (rs.getInt("SolverConfig_idSolverConfig") != cur_sc.getId()) {
                cacheParameterInstances(cur_sc, cur);
                while (cur_sc.getId() != rs.getInt("SolverConfig_idSolverConfig")) {
                    cur_sc = scs.get(++cur_idx);
                }
                cur = new ArrayList<ParameterInstance>();
            }
            ParameterInstance i = getParameterInstanceFromResultset(rs, cur_sc);
            i.setSaved();
            cur.add(i);
        }
        rs.close();
        st.close();
        cacheParameterInstances(cur_sc, cur);
    }

    public static void clearCache() {
        cache.clear();
    }
}
