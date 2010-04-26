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

    /**
     * Factory method for building a ExperimentHasGridQueue object.
     * This method persists the created object in the DB automatically.
     * @return
     * @throws SQLException
     */
    public static ExperimentHasGridQueue createExperimentHasGridQueue(Experiment e, GridQueue q) throws SQLException {
        ExperimentHasGridQueue eq = new ExperimentHasGridQueue(e.getId(), q.getId());
        save(eq);
        return eq;
    }

    private static ExperimentHasGridQueue getExperimentHasGridQueueFromResultset(ResultSet rs) throws SQLException {
        return new ExperimentHasGridQueue(rs.getInt("Experiment_idExperiment"), rs.getInt("gridQueue_idgridQueue"));
    }

    private static void save(ExperimentHasGridQueue q) throws SQLException {
        if (q.isDeleted()) {
            PreparedStatement st = DatabaseConnector.getInstance().getConn().prepareStatement(deleteQuery);
            st.setInt(1, q.getIdExperiment());
            st.setInt(2, q.getIdGridQueue());
            st.executeUpdate();
            st.close();
        } else if (q.isNew()) {
            PreparedStatement st = DatabaseConnector.getInstance().getConn().prepareStatement(insertQuery, PreparedStatement.RETURN_GENERATED_KEYS);
            st.setInt(1, q.getIdExperiment());
            st.setInt(2, q.getIdGridQueue());

            st.executeUpdate();
            st.close();
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

            q.setSaved();
            res.add(q);
        }
        rs.close();
        st.close();
        return res;
    }

    public static ExperimentHasGridQueue getByExpAndQueue(Experiment e, GridQueue q) throws NoConnectionToDBException, SQLException {
        PreparedStatement st = DatabaseConnector.getInstance().getConn().prepareStatement("SELECT * FROM " + table + " WHERE Experiment_idExperiment=? AND gridQueue_idgridQueue=?");
        st.setInt(1, e.getId());
        st.setInt(2, q.getId());
        ResultSet rs = st.executeQuery();
        if (rs.next()) {
            ExperimentHasGridQueue eq = getExperimentHasGridQueueFromResultset(rs);

            q.setSaved();
            return eq;
        }
        rs.close();
        st.close();
        return null;
    }
}
