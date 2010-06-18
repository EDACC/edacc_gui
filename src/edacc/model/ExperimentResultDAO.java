package edacc.model;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.util.Hashtable;
import java.util.Vector;

public class ExperimentResultDAO {
    protected static final String table = "ExperimentResults";
    protected static final String insertQuery = "INSERT INTO " + table + " (run, status, seed, resultFileName, time, statusCode, " +
                                                "SolverConfig_idSolverConfig, Experiment_idExperiment, Instances_idInstance) VALUES (?,?,?,?,?,?,?,?,?)";
    protected static final String deleteQuery = "DELETE FROM " + table + " WHERE idJob=?";

    public static ExperimentResult createExperimentResult(int run, int status, int seed, String resultFileName, float time, int statusCode, int SolverConfigId, int ExperimentId, int InstanceId) throws SQLException {
        ExperimentResult r = new ExperimentResult(run, status, seed, resultFileName, time, statusCode, SolverConfigId, ExperimentId, InstanceId);
        r.setNew();
        return r;
    }

    public static void batchSave(Vector<ExperimentResult> v) throws SQLException {
        boolean autoCommit = DatabaseConnector.getInstance().getConn().getAutoCommit();
        DatabaseConnector.getInstance().getConn().setAutoCommit(false);
        PreparedStatement st = DatabaseConnector.getInstance().getConn().prepareStatement(insertQuery);
        for (ExperimentResult r : v) {
            st.setInt(1, r.getRun());
            st.setInt(2, r.getStatus());
            st.setInt(3, r.getSeed());
            st.setString(4, r.getResultFileName());
            st.setFloat(5, r.getTime());
            st.setInt(6, r.getStatusCode());
            st.setInt(7, r.getSolverConfigId());
            st.setInt(8, r.getExperimentId());
            st.setInt(9, r.getInstanceId());
            st.addBatch();
            r.setSaved(); // this should only be done if the batch save actually 
                          // gets commited, right now this might not be the case if there's an DB exception
        }
        st.executeBatch();
        DatabaseConnector.getInstance().getConn().commit();
        DatabaseConnector.getInstance().getConn().setAutoCommit(autoCommit);
        st.close();
    }

    /**
     * Updates the run property of the ExperimentResults at once (batch).
     * @param v vector of ExperimentResults to be updated
     * @throws SQLException
     */
    public static void batchUpdateRun(Vector<ExperimentResult> v) throws SQLException {
        boolean autoCommit = DatabaseConnector.getInstance().getConn().getAutoCommit();
        DatabaseConnector.getInstance().getConn().setAutoCommit(false);
        final String query = "UPDATE " + table + " SET run=? WHERE idJob=?";
        PreparedStatement st = DatabaseConnector.getInstance().getConn().prepareStatement(query);
        for (ExperimentResult r: v) {
            st.setInt(1, r.getRun());
            st.setInt(2, r.getId());
            st.addBatch();
            r.setSaved(); // same as above.
        }
        st.executeBatch();
        DatabaseConnector.getInstance().getConn().commit();
        DatabaseConnector.getInstance().getConn().setAutoCommit(autoCommit);
        st.close();
    }

    private static ExperimentResult getExperimentResultFromResultSet(ResultSet rs) throws SQLException {
        ExperimentResult r = new ExperimentResult();
        r.setId(rs.getInt("idJob"));
        r.setRun(rs.getInt("run"));
        r.setStatus(rs.getInt("status"));
        r.setSeed(rs.getInt("seed"));
        r.setResultFileName(rs.getString("resultFileName"));
        r.setTime(rs.getFloat("time"));
        r.setStatusCode(rs.getInt("statusCode"));
        r.setSolverConfigId(rs.getInt("SolverConfig_idSolverConfig"));
        r.setInstanceId(rs.getInt("Instances_idInstance"));
        r.setExperimentId(rs.getInt("Experiment_idExperiment"));
        return r;
    }

    /**
     * returns the number of jobs in the database for the given experiment
     * @param id experiment id
     * @return
     * @throws SQLException
     */
    public static int getCountByExperimentId(int id) throws SQLException {
        PreparedStatement st = DatabaseConnector.getInstance().getConn().prepareStatement("SELECT COUNT(*) as count FROM " + table + " WHERE Experiment_idExperiment=?");
        st.setInt(1, id);
        ResultSet rs = st.executeQuery();
        rs.next(); // there will always be a count
        int count = rs.getInt("count");
        rs.close();
        return count;
    }


    /**
     * checks the database if a job with the given parameters already exists
     * @param run
     * @param solverConfigId
     * @param InstanceId
     * @param ExperimentId
     * @return bool
     * @throws SQLException
     */
    public static boolean jobExists(int run, int solverConfigId, int InstanceId, int ExperimentId) throws SQLException {
        PreparedStatement st = DatabaseConnector.getInstance().getConn().prepareStatement("SELECT COUNT(*) as count FROM " + table  + " " +
                "WHERE run=? AND SolverConfig_idSolverConfig=? AND Instances_idInstance=? AND Experiment_idExperiment=? ;");
        st.setInt(1, run);
        st.setInt(2, solverConfigId);
        st.setInt(3, InstanceId);
        st.setInt(4, ExperimentId);
        ResultSet rs = st.executeQuery();
        rs.next();
        int count = rs.getInt("count");
        rs.close();
        return count > 0;
    }

    /**
     * returns the seed value of the job specified by the given parameters
     * @param run
     * @param solverConfigId
     * @param InstanceId
     * @param ExperimentId
     * @return bool
     * @throws SQLException
     */
    public static int getSeedValue(int run, int solverConfigId, int InstanceId, int ExperimentId) throws SQLException {
        PreparedStatement st = DatabaseConnector.getInstance().getConn().prepareStatement("SELECT seed FROM " + table  + " " +
                "WHERE run=? AND SolverConfig_idSolverConfig=? AND Instances_idInstance=? AND Experiment_idExperiment=? ;");
        st.setInt(1, run);
        st.setInt(2, solverConfigId);
        st.setInt(3, InstanceId);
        st.setInt(4, ExperimentId);
        ResultSet rs = st.executeQuery();
        rs.next();
        int seed = rs.getInt("seed");
        rs.close();
        return seed;
    }

    /**
     * returns all jobs of the given Experiment
     * @param id
     * @return ExperimentResults vector
     * @throws SQLException
     */
    public static Vector<ExperimentResult> getAllByExperimentId(int id) throws SQLException {
        Vector<ExperimentResult> v = new Vector<ExperimentResult>();
        PreparedStatement st = DatabaseConnector.getInstance().getConn().prepareStatement(
                "SELECT idJob, run, status, seed, resultFileName, time, statusCode, SolverConfig_idSolverConfig, " +
                "Experiment_idExperiment, Instances_idInstance FROM " + table + " " +
                "WHERE Experiment_idExperiment=?;");
        st.setInt(1, id);
        ResultSet rs = st.executeQuery();
        while (rs.next()) {
            ExperimentResult er = getExperimentResultFromResultSet(rs);
            v.add(er);
            er.setSaved();
        }
        rs.close();
        st.close();
        return v;
    }

    /**
     * Returns all runs for an experiment specified by id
     * @param id the experiment id
     * @return vector of run numbers
     * @throws SQLException
     */
    public static Vector<Integer> getAllRunsByExperimentId(int id) throws SQLException {
        Vector<Integer> res = new Vector<Integer>();
        PreparedStatement st = DatabaseConnector.getInstance().getConn().prepareStatement(
                "SELECT run " +
                "FROM " + table + " " +
                "WHERE Experiment_idExperiment=? GROUP BY run ORDER BY run;");
        st.setInt(1, id);
        ResultSet rs = st.executeQuery();
        while (rs.next()) {
            res.add(rs.getInt(1));
        }

        return res;
    }

    /**
     * Returns all instance ids which have jobs in the database and are associated
     * with the experiment specified by id
     * @param id the experiment id
     * @return vector of solver config ids
     * @throws SQLException
     */
    public static Vector<Integer> getAllInstanceIdsByExperimentId(int id) throws SQLException {
        Vector<Integer> res = new Vector<Integer>();
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
     * Returns all solver config ids which have jobs in the database and are associated
     * with the experiment specified by id.
     * @param id the experiment id
     * @return vector of solver config ids
     * @throws SQLException
     */
    public static Vector<Integer> getAllSolverConfigIdsByExperimentId(int id) throws SQLException {
        Vector<Integer> res = new Vector<Integer>();
        PreparedStatement st = DatabaseConnector.getInstance().getConn().prepareStatement(
                "SELECT SolverConfig_idSolverConfig " +
                "FROM " + table + " " +
                "WHERE Experiment_idExperiment=? GROUP BY SolverConfig_idSolverConfig ORDER BY SolverConfig_idSolverConfig;");
        st.setInt(1, id);
        ResultSet rs = st.executeQuery();
        while (rs.next()) {
            res.add(rs.getInt(1));
        }

        return res;
    }


    public static Vector<ExperimentResult> getAllByExperimentIdAndRun(int eid, int run) throws SQLException {
        Vector<ExperimentResult> res = new Vector<ExperimentResult>();
        PreparedStatement st = DatabaseConnector.getInstance().getConn().prepareStatement(
                "SELECT * FROM " + table + " " +
                "WHERE Experiment_idExperiment=? AND run=?;"
                );
        st.setInt(1, eid);
        st.setInt(2, run);
        ResultSet rs = st.executeQuery();
        while (rs.next()) {
            ExperimentResult er = getExperimentResultFromResultSet(rs);
            res.add(er);
            er.setSaved();
        }
        return res;
    }

    public static int getInstanceCountByExperimentId(int id) throws SQLException {
        Vector<ExperimentResult> res = new Vector<ExperimentResult>();
        PreparedStatement st = DatabaseConnector.getInstance().getConn().prepareStatement(
                "SELECT COUNT(*) FROM " + table + " " +
                "WHERE Experiment_idExperiment=? " +
                "GROUP BY Instance_idInstance"
                );
        st.setInt(1, id);
        ResultSet rs = st.executeQuery();
        while (rs.next()) {
            return rs.getInt(1);
        }
        return 0;
    }

    public static int getSolverConfigCountByExperimentId(int id) throws SQLException {
        Vector<ExperimentResult> res = new Vector<ExperimentResult>();
        PreparedStatement st = DatabaseConnector.getInstance().getConn().prepareStatement(
                "SELECT COUNT(*) FROM " + table + " " +
                "WHERE Experiment_idExperiment=? " +
                "GROUP BY SolverConfig_idSolverConfig"
                );
        st.setInt(1, id);
        ResultSet rs = st.executeQuery();
        while (rs.next()) {
            return rs.getInt(1);
        }
        return 0;
    }

    public static Vector<ExperimentResult> getAllBySolverConfiguration(SolverConfiguration sc) throws SQLException {
        Vector<ExperimentResult> res = new Vector<ExperimentResult>();
        PreparedStatement st = DatabaseConnector.getInstance().getConn().prepareStatement(
                "SELECT * FROM " + table + " " +
                "WHERE Experiment_idExperiment=? AND SolverConfig_idSolverConfig=?;"
                );
        st.setInt(1, sc.getExperiment_id());
        st.setInt(2, sc.getId());
        ResultSet rs = st.executeQuery();
        while (rs.next()) {
            ExperimentResult er = getExperimentResultFromResultSet(rs);
            res.add(er);
            er.setSaved();
        }
        return res;
    }

    public static Vector<ExperimentResult> getAllByExperimentHasInstance(ExperimentHasInstance ehi) throws SQLException {
         Vector<ExperimentResult> res = new Vector<ExperimentResult>();
        PreparedStatement st = DatabaseConnector.getInstance().getConn().prepareStatement(
                "SELECT * FROM " + table + " " +
                "WHERE Experiment_idExperiment=? AND Instances_idInstance=?;"
                );
        st.setInt(1, ehi.getExperiment_id());
        st.setInt(2, ehi.getInstances_id());
        ResultSet rs = st.executeQuery();
        while (rs.next()) {
            ExperimentResult er = getExperimentResultFromResultSet(rs);
            res.add(er);
            er.setSaved();
        }
        return res;
    }

    public static void deleteExperimentResults(Vector<ExperimentResult> experimentResults) throws SQLException {
        boolean autoCommit = DatabaseConnector.getInstance().getConn().getAutoCommit();
        DatabaseConnector.getInstance().getConn().setAutoCommit(false);
        PreparedStatement st = DatabaseConnector.getInstance().getConn().prepareStatement(deleteQuery);
        for (ExperimentResult r : experimentResults) {
            st.setInt(1, r.getId());
            st.addBatch();
            r.setDeleted(); // this should only be done if the batch save actually
                          // gets commited, right now this might not be the case if there's an DB exception
        }
        st.executeBatch();
        DatabaseConnector.getInstance().getConn().commit();
        DatabaseConnector.getInstance().getConn().setAutoCommit(autoCommit);
        st.close();
    }
}
