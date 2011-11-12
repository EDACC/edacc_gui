package edacc.model;

import edacc.util.Pair;
import java.util.LinkedList;
import java.sql.*;
import java.util.ArrayList;

/**
 *
 * @author daniel
 */
public class ExperimentDAO {

    protected static final String table = "Experiment";
    protected static final String insertQuery = "INSERT INTO " + table + " (Name, Date, description, configurationExp, priority, active) VALUES (?, ?, ?, ?, ?, ?)";
    protected static final String updateQuery = "UPDATE " + table + " SET Name =?, Date =?, description =?, configurationExp =?, priority =?, active=? WHERE idExperiment=?";
    protected static final String deleteQuery = "DELETE FROM " + table + " WHERE idExperiment=?";
    private static final ObjectCache<Experiment> cache = new ObjectCache<Experiment>();

    /**
     * Experiment factory method, ensures that the created experiment is persisted and assigned an ID
     * so it can be referenced by related objects
     * @return new Experiment object
     */
    public static Experiment createExperiment(String name, Date date, String description, boolean configurationExp) throws SQLException {
        if (getExperimentByName(name) != null) {
            throw new SQLException("There exists already an experiment with the same name.");
        }
        Experiment i = new Experiment();
        i.setName(name);
        i.setDescription(description);
        i.setDate(date);
        i.setActive(true);
        i.setConfigurationExp(configurationExp);
        i.setPriority(0);
        save(i);
        cache.cache(i);
        return i;
    }

    /**
     * Returns an experiment for the unique name.
     * @param name the name of the experiment
     * @return experiment named `name`
     * @throws SQLException
     */
    public static Experiment getExperimentByName(String name) throws SQLException {
        PreparedStatement st = DatabaseConnector.getInstance().getConn().prepareStatement("SELECT * FROM " + table + " WHERE name=?");
        st.setString(1, name);
        ResultSet rs = st.executeQuery();
        if (rs.next()) {
            Experiment i = getExperimentFromResultset(rs);
            if (cache.getCached(i.getId()) != null) {
                i = cache.getCached(i.getId());
            } else {
                i.setSaved();
                cache.cache(i);
            }
            return i;
        }
        rs.close();
        st.close();
        return null;
    }

    /**
     * persists an Experiment object in the database
     * @param experiment The Experiment object to persist
     */
    public static void save(Experiment experiment) throws SQLException {
        PreparedStatement st = null;
        if (experiment.isNew()) {
            st = DatabaseConnector.getInstance().getConn().prepareStatement(insertQuery, PreparedStatement.RETURN_GENERATED_KEYS);
        } else if (experiment.isModified()) {
            st = DatabaseConnector.getInstance().getConn().prepareStatement(updateQuery);
            st.setInt(7, experiment.getId());
        } else {
            return;
        }
        st.setString(1, experiment.getName());
        st.setDate(2, experiment.getDate());
        st.setString(3, experiment.getDescription());
        st.setBoolean(4, experiment.isConfigurationExp());
        st.setInt(5, experiment.getPriority());
        st.setBoolean(6, experiment.isActive());
        st.executeUpdate();

        if (experiment.isNew()) {
            ResultSet generatedKeys = st.getGeneratedKeys();
            if (generatedKeys.next()) {
                experiment.setId(generatedKeys.getInt(1));
            }

            cache.cache(experiment);
        }
        experiment.setSaved();
        st.close();
    }

    /**
     * removes an experiment from the database
     * @param experiment
     * @throws SQLException
     */
    public static void removeExperiment(Experiment experiment) throws SQLException {
        PreparedStatement st = DatabaseConnector.getInstance().getConn().prepareStatement(deleteQuery);
        st.setInt(1, experiment.getId());
        st.executeUpdate();
        st.close();
        cache.remove(experiment);
        experiment.setDeleted();
    }

    private static Experiment getExperimentFromResultset(ResultSet rs) throws SQLException {
        Experiment i = new Experiment();
        i.setId(rs.getInt("idExperiment"));
        i.setName(rs.getString("Name"));
        i.setDate(rs.getDate("Date"));
        i.setDescription(rs.getString("description"));
        i.setConfigurationExp(rs.getBoolean("configurationExp"));
        i.setPriority(rs.getInt("priority"));
        i.setActive(rs.getBoolean("active"));
        return i;
    }

    /**
     * retrieves an experiment from the database
     * @param id the id of the experiment to be retrieved
     * @return the experiment specified by its id
     * @throws SQLException
     */
    public static Experiment getById(int id) throws SQLException {
        Experiment c = cache.getCached(id);
        if (c != null) {
            return c;
        }

        PreparedStatement st = DatabaseConnector.getInstance().getConn().prepareStatement("SELECT * FROM " + table + " WHERE idExperiment=?");
        st.setInt(1, id);
        ResultSet rs = st.executeQuery();
        if (rs.next()) {
            Experiment i = getExperimentFromResultset(rs);
            i.setSaved();
            cache.cache(i);
            return i;
        }
        rs.close();
        st.close();
        return null;
    }

    /**
     * retrieves all experiments from the database
     * @return all experiments in a List
     * @throws SQLException
     */
    public static LinkedList<Experiment> getAll() throws SQLException {
        Statement st = DatabaseConnector.getInstance().getConn().createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM " + table);
        LinkedList<Experiment> res = new LinkedList<Experiment>();
        while (rs.next()) {
            int id = rs.getInt("idExperiment");
            Experiment c = cache.getCached(id);
            if (c != null) {
                res.add(c);
            } else {
                Experiment i = getExperimentFromResultset(rs);
                i.setSaved();
                cache.cache(i);
                res.add(i);
            }
        }
        rs.close();
        st.close();
        return res;
    }

    public static void setModified(Experiment e) {
        e.setModified();
    }

    /**
     * returns all solvers used in an experiment.
     * @param e
     * @return
     * @throws NoConnectionToDBException
     * @throws SQLException
     */
    public static ArrayList<Solver> getSolversInExperiment(Experiment e) throws NoConnectionToDBException, SQLException {
        final String query = "SELECT DISTINCT Solver_idSolver FROM SolverConfig WHERE Experiment_idExperiment=?";
        PreparedStatement ps = DatabaseConnector.getInstance().getConn().prepareStatement(query);
        ps.setInt(1, e.getId());
        ResultSet rs = ps.executeQuery();
        ArrayList<Solver> solvers = new ArrayList<Solver>();
        while (rs.next()) {
            int id = rs.getInt("Solver_idSolver");
            Solver s = SolverDAO.getById(id);
            solvers.add(s);
        }
        rs.close();
        ps.close();
        return solvers;
    }

    public static int getRunCountInExperimentForSolverConfigurationAndInstance(Experiment exp, Integer idSolverConfig, Integer idInstance) throws SQLException {
        final String query = "select count(idJob) from ExperimentResults WHERE Experiment_idExperiment = ? AND solverConfig_idSolverConfig = ? AND Instances_idInstance = ?";
        PreparedStatement ps = DatabaseConnector.getInstance().getConn().prepareStatement(query);
        ps.setInt(1, exp.getId());
        ps.setInt(2, idSolverConfig);
        ps.setInt(3, idInstance);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            return rs.getInt(1);
        } else {
            return 0;
        }
    }

    public static int getJobCount(Experiment experiment) throws SQLException {
        final String query = "SELECT COUNT(idJob) FROM ExperimentResults WHERE Experiment_idExperiment = ?";
        PreparedStatement ps = DatabaseConnector.getInstance().getConn().prepareStatement(query);
        ps.setInt(1, experiment.getId());
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            return rs.getInt(1);
        } else {
            return 0;
        }
    }

    public static void clearCache() {
        cache.clear();
    }
    
    public static ArrayList<StatusCount> getJobCountForExperiment(Experiment exp) throws SQLException, StatusCodeNotInDBException {
        final String query = "SELECT status, COUNT(idJob) FROM ExperimentResults WHERE Experiment_idExperiment = ? GROUP BY status";
                PreparedStatement ps = DatabaseConnector.getInstance().getConn().prepareStatement(query);
        ps.setInt(1, exp.getId());
        ResultSet rs = ps.executeQuery();
        ArrayList<StatusCount> res = new ArrayList<StatusCount>();
        while (rs.next()) {
            res.add(new StatusCount(StatusCodeDAO.getByStatusCode(rs.getInt(1)), rs.getInt(2)));
        }
        rs.close();
        ps.close();
        return res;
    }
    
    public static Pair<Integer, Boolean> getPriorityActiveByExperiment(Experiment exp) throws SQLException {
        Pair<Integer, Boolean> p = new Pair<Integer, Boolean>(0,false);
        PreparedStatement ps = DatabaseConnector.getInstance().getConn().prepareStatement(
                "SELECT priority, active FROM " + table + " WHERE idExperiment = ?");
        ps.setInt(1, exp.getId());
        Integer priority = null;
        Boolean active = null;
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            priority = rs.getInt(1);
            active = rs.getBoolean(2);
        }
        rs.close();
        ps.close();
        if (priority == null | active == null) {
            return null;
        } else {
            return new Pair<Integer, Boolean>(priority, active);
        }
    }
    
    public static class StatusCount {
        StatusCode statusCode;
        Integer count;
        
        public StatusCount(StatusCode statusCode, Integer count) {
            this.statusCode = statusCode;
            this.count = count;
        }

        public Integer getCount() {
            return count;
        }

        public StatusCode getStatusCode() {
            return statusCode;
        }
    }
}
