/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edacc.model;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Vector;

/**
 *
 * @author simon
 */
public class ExperimentHasInstanceDAO {

    protected static final String table = "Experiment_has_Instances";
    protected static final String insertQuery = "INSERT INTO " + table + " (Experiment_idExperiment, Instances_idInstance) VALUES (?, ?)";
    protected static final String deleteQuery = "DELETE FROM " + table + " WHERE idEI=?";
    private static final ObjectCache<ExperimentHasInstance> cache = new ObjectCache<ExperimentHasInstance>();

    /**
     * ExperimentHasInstance factory method, ensures that the created experiment is persisted and assigned an ID
     * @return new Experiment object
     */
    public static ExperimentHasInstance createExperimentHasInstance(int experiment_id, int instances_id) throws SQLException {
        ExperimentHasInstance i = new ExperimentHasInstance();
        i.setExperiment_id(experiment_id);
        i.setInstances_id(instances_id);
        i.setNew();
        save(i);
        return i;
    }

    private static ExperimentHasInstance getExperimentHasInstanceFromResultset(ResultSet rs) throws SQLException {
        ExperimentHasInstance i = new ExperimentHasInstance();
        i.setId(rs.getInt(1));
        i.setExperiment_id(rs.getInt(2));
        i.setInstances_id(rs.getInt(3));
        return i;
    }

    private static void save(ExperimentHasInstance i) throws SQLException {
        if (i.isDeleted()) {
            PreparedStatement st = DatabaseConnector.getInstance().getConn().prepareStatement(deleteQuery);
            st.setInt(1, i.getId());
            st.executeUpdate();
            st.close();
            cache.remove(i);
        } else if (i.isNew()) {
            PreparedStatement st = DatabaseConnector.getInstance().getConn().prepareStatement(insertQuery, PreparedStatement.RETURN_GENERATED_KEYS);
            st.setInt(1, i.getExperiment_id());
            st.setInt(2, i.getInstances_id());
            st.executeUpdate();
            ResultSet generatedKeys = st.getGeneratedKeys();
            if (generatedKeys.next()) {
                i.setId(generatedKeys.getInt(1));
            }
            generatedKeys.close();
            st.close();
            i.setSaved();
            cache.cache(i);
        }
        
    }

    public static void removeExperimentHasInstance(ExperimentHasInstance e) throws SQLException {
        e.setDeleted();
        save(e);
    }

    public static Vector<ExperimentHasInstance> getExperimentHasInstanceByExperimentId(int id) throws SQLException {
        Vector<ExperimentHasInstance> res = new Vector<ExperimentHasInstance>();
        PreparedStatement st = DatabaseConnector.getInstance().getConn().prepareStatement("SELECT * FROM " + table + " WHERE Experiment_idExperiment=?");
        st.setInt(1, id);
        ResultSet rs = st.executeQuery();
        while (rs.next()) {
            ExperimentHasInstance i = getExperimentHasInstanceFromResultset(rs);

            ExperimentHasInstance c = cache.getCached(i.getId());
            if (c != null) {
                res.add(c);
            } else {
                i.setSaved();
                cache.cache(i);
                res.add(i);
            }            
        }
        rs.close();
        st.close();
        return res;
    }

    /**
     * Returns all instance ids associated with the experiment specified by id.
     * @param id the experiment id
     * @return vector of instance ids
     * @throws SQLException
     */
    public static ArrayList<Integer> getAllInstanceIdsByExperimentId(int id) throws SQLException {
        ArrayList<Integer> res = new ArrayList<Integer>();
        PreparedStatement st = DatabaseConnector.getInstance().getConn().prepareStatement(
                "SELECT Instances_idInstance " +
                "FROM " + table + " " +
                "WHERE Experiment_idExperiment=? GROUP BY Instances_idInstance ORDER BY Instances_idInstance;");
        st.setInt(1, id);
        ResultSet rs = st.executeQuery();
        while (rs.next()) {
            res.add(rs.getInt(1));
        }
        return res;
    }

    /**
     * Returns vector of Experiments Objects, which are related to the given instances.
     * @param instances The instance objects
     * @return Vector of experiments
     * @throws SQLException
     */
    public static ArrayList<Experiment> getAllExperimentsByInstances(Vector<Instance> instances) throws SQLException {
        String query = "SELECT Experiment_idExperiment " +
                "FROM " + table + " " +
                "WHERE Instances_idInstance=" + instances.firstElement().getId() + " ";
        for(int i = 1; i < instances.size(); i++){
            query += " OR Instances_idInstance=" + instances.firstElement().getId() + " ";
        }
        query += " GROUP BY Experiment_idExperiment ORDER BY Experiment_idExperiment;";
        PreparedStatement st = DatabaseConnector.getInstance().getConn().prepareStatement(query);
        ResultSet rs = st.executeQuery();
        ArrayList<Experiment> exp = new ArrayList<Experiment>();
        while (rs.next()) {
            exp.add(ExperimentDAO.getById(rs.getInt("Experiment_idExperiment")));
        }
        return exp;
    }

}
