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
 * @author dgall
 */
public class ExperimentHasGridQueueDAO {

    protected static final String table = "Experiment_has_gridQueue";
    protected static final String insertQuery = "INSERT INTO " + table + " (Experiment_idExperiment, gridQueue_idgridQueue) VALUES (?, ?)";
    protected static final String deleteQuery = "DELETE FROM " + table + " WHERE Experiment_idExperiment=? AND gridQueue_idgridQueue=?";
    private static final Hashtable<ExperimentHasGridQueue, ExperimentHasGridQueue> cache = new Hashtable<ExperimentHasGridQueue, ExperimentHasGridQueue>();

    private static ExperimentHasGridQueue getExperimentHasGridQueueFromResultset(ResultSet rs) throws SQLException {
        ExperimentHasGridQueue q = new ExperimentHasGridQueue();
        q.setIdExperiment(rs.getInt("Experiment_idExperiment"));
        q.setIdGridQueue(rs.getInt("gridQueue_idgridQueue"));
        return q;
    }

    private static void save(ExperimentHasGridQueue q) throws SQLException {
        if (q.isDeleted()) {
            PreparedStatement st = DatabaseConnector.getInstance().getConn().prepareStatement(deleteQuery);
            st.setInt(1, q.getIdExperiment());
            st.setInt(2, q.getIdGridQueue());
            st.executeUpdate();
            cache.remove(q);
        } else if (q.isNew()) {
            PreparedStatement st = DatabaseConnector.getInstance().getConn().prepareStatement(insertQuery, PreparedStatement.RETURN_GENERATED_KEYS);
            st.setInt(1, q.getIdExperiment());
            st.setInt(2, q.getIdGridQueue());

            st.executeUpdate();
            q.setSaved();
            cacheExperimentHasGridQueue(q);
        }

    }

    private static ExperimentHasGridQueue getCached(ExperimentHasGridQueue q) {
        if (cache.containsKey(q)) {
            return cache.get(q);
        } else {
            return null;
        }
    }

    private static void cacheExperimentHasGridQueue(ExperimentHasGridQueue q) {
        if (cache.containsKey(q)) {
            return;
        } else {
            cache.put(q, q);
        }
    }

    public static void removeExperimentHasGridQueue(ExperimentHasGridQueue q) throws SQLException {
        q.setDeleted();
        save(q);
    }

    public static Vector<ExperimentHasGridQueue> getExperimentHasGridQueueByExperiment(Experiment e) throws SQLException {
        Vector<ExperimentHasGridQueue> res = new Vector<ExperimentHasGridQueue>();
        PreparedStatement st = DatabaseConnector.getInstance().getConn().prepareStatement("SELECT * FROM " + table + " WHERE Experiment_idExperiment=?");
        st.setInt(1, e.getId());
        ResultSet rs = st.executeQuery();
        while (rs.next()) {
            ExperimentHasGridQueue q = getExperimentHasGridQueueFromResultset(rs);

            ExperimentHasGridQueue c = getCached(q);
            if (c != null) {
                res.add(c);
            } else {
                q.setSaved();
                cacheExperimentHasGridQueue(q);
                res.add(q);
            }
        }
        return res;
    }
}
