package edacc.model;

import edacc.properties.PropertyTypeNotExistException;
import edacc.util.Pair;
import java.io.BufferedReader;
import java.io.EOFException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.Blob;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

public class ExperimentResultDAO {

    protected static PreparedStatement curSt = null;
    protected static final String table = "ExperimentResults";
    protected static final String outputTable = "ExperimentResultsOutput";
    protected static final String insertQuery = "INSERT INTO " + table + " (SolverConfig_idSolverConfig, Experiment_idExperiment,"
            + "Instances_idInstance, run, status, seed, "
            + "startTime, priority, resultTime, wallTime, cost, computeQueue, resultCode, CPUTimeLimit, memoryLimit, wallClockTimeLimit, stackSizeLimit) "
            + "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
    protected static final String insertOutputsQuery = "INSERT INTO " + outputTable + " (ExperimentResults_idJob, solverOutput, launcherOutput, watcherOutput, verifierOutput, solverExitCode, watcherExitCode, verifierExitCode) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
    protected static final String deleteQuery = "DELETE FROM " + table + " WHERE idJob=?";
    protected static final String selectQuery = "SELECT SolverConfig_idSolverConfig, Experiment_idExperiment, Instances_idInstance, "
            + "idJob, run, seed, status, resultTime, wallTime, cost, resultCode, "
            //   + "solverExitCode, watcherExitCode, verifierExitCode, "
            + "computeQueue, TIMESTAMPDIFF(SECOND, startTime, NOW()) AS runningTime, "
            + "IF(status = " + StatusCode.RUNNING.getStatusCode() + ", TIMESTAMPADD(SECOND, -1, CURRENT_TIMESTAMP), date_modified) AS date_modified,"
            + "priority, startTime, CPUTimeLimit, memoryLimit, wallClockTimeLimit, stackSizeLimit, computeNode, computeNodeIP, Client_idClient "
            + "FROM " + table + " ";
    // + "LEFT JOIN ExperimentResultsOutput ON (idJob = ExperimentResults_idJob) ";
    protected static final String copyOutputQuery = "UPDATE ExperimentResultsOutput as dest, ExperimentResultsOutput as src "
            + "SET "
            + "dest.solverOutput = src.solverOutput, "
            + "dest.watcherOutput = src.watcherOutput, "
            + "dest.launcherOutput = src.launcherOutput, "
            + "dest.verifierOutput = src.verifierOutput, "
            + "dest.solverExitCode = src.solverExitCode, "
            + "dest.watcherExitCode = src.watcherExitCode, "
            + "dest.verifierExitCode = src.verifierExitCode "
            + "WHERE src.ExperimentResults_idJob = ? AND dest.ExperimentResults_idJob = ?";

    public static ExperimentResult createExperimentResult(int run, int priority, int computeQueue, StatusCode status, int seed, ResultCode resultCode, float resultTime, float wallTime, double cost, int SolverConfigId, int ExperimentId, int InstanceId, Timestamp startTime, int cpuTimeLimit, int memoryLimit, int wallClockTimeLimit, int stackSizeLimit) throws SQLException {
        ExperimentResult r = new ExperimentResult(run, priority, computeQueue, status, seed, resultCode, resultTime, wallTime, cost, SolverConfigId, ExperimentId, InstanceId, startTime, cpuTimeLimit, memoryLimit, wallClockTimeLimit, stackSizeLimit);
        r.setNew();
        return r;
    }

    public static ExperimentResultEx createExperimentResult(int run, int priority, int computeQueue, StatusCode status, ResultCode resultCode, int seed, float resultTime, float wallTime, double cost, int SolverConfigId, int ExperimentId, int InstanceId, Timestamp startTime, int cpuTimeLimit, int memoryLimit, int wallClockTimeLimit, int stackSizeLimit, byte[] solverOutput, byte[] launcherOutput, byte[] watcherOutput, byte[] verifierOutput) {
        ExperimentResultEx r = new ExperimentResultEx(run, priority, computeQueue, status, resultCode, seed, resultTime, wallTime, cost, SolverConfigId, ExperimentId, InstanceId, startTime, cpuTimeLimit, memoryLimit, wallClockTimeLimit, stackSizeLimit, solverOutput, launcherOutput, watcherOutput, verifierOutput);
        r.setNew();
        return r;
    }

    private static String getInsertQuery(int count) {
        StringBuilder query = new StringBuilder("INSERT INTO " + table + " "
                + "(SolverConfig_idSolverConfig, Experiment_idExperiment,"
                + "Instances_idInstance, run, status, seed, "
                + "startTime, priority, resultTime, wallTime, cost, computeQueue, resultCode, "
                + "CPUTimeLimit, memoryLimit, wallClockTimeLimit, stackSizeLimit) "
                + "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");

        count--;
        for (int i = 0; i < count; i++) {
            query.append(",(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
        }
        return query.toString();
    }

    private static String getInsertOutputsQuery(int count) {
        StringBuilder query = new StringBuilder("INSERT INTO " + outputTable + " "
                + "(ExperimentResults_idJob, solverOutput, launcherOutput, "
                + "watcherOutput, verifierOutput, solverExitCode, watcherExitCode, "
                + "verifierExitCode) "
                + "VALUES (?,?,?,?,?,?,?,?)");

        count--;
        for (int i = 0; i < count; i++) {
            query.append(",(?,?,?,?,?,?,?,?)");
        }
        return query.toString();
    }

    private static String getDeleteQuery(int count) {
        StringBuilder query = new StringBuilder("DELETE FROM ExperimentResults WHERE idJob IN (?");
        count--;
        for (int i = 0; i < count; i++) {
            query.append(",?");
        }
        query.append(')');
        return query.toString();
    }

    public static void batchSave(List<ExperimentResult> v) throws SQLException {
        batchSave(v, null);
    }

    /**
     * Saves the experiment results at once (batch).
     * @param v
     * @throws SQLException
     */
    public static void batchSave(List<ExperimentResult> v, Tasks task) throws SQLException {
        if (v.isEmpty()) {
            return;
        }
        if (v.size() > 10000) {
            boolean autoCommit = DatabaseConnector.getInstance().getConn().getAutoCommit();
            try {
                DatabaseConnector.getInstance().getConn().setAutoCommit(false);
                ArrayList<ExperimentResult> res = new ArrayList<ExperimentResult>();
                int jobsCreated = 0;
                for (ExperimentResult ex : v) {
                    res.add(ex);
                    if (res.size() == 10000) {
                        batchSave(res);
                        jobsCreated += 10000;
                        if (task != null) {
                            task.setTaskProgress(jobsCreated / (float) v.size());
                        }
                        res.clear();
                    }

                }
                if (!res.isEmpty()) {
                    batchSave(res);
                }
                if (task != null) {
                    task.setTaskProgress(0.f);
                }
            } catch (SQLException ex) {
                if (autoCommit) {
                    DatabaseConnector.getInstance().getConn().rollback();
                }
                throw ex;
            } catch (Error ex) {
                if (autoCommit) {
                    DatabaseConnector.getInstance().getConn().rollback();
                }
                throw ex;
            } catch (Throwable ex) {
                if (autoCommit) {
                    DatabaseConnector.getInstance().getConn().rollback();
                }
            } finally {
                DatabaseConnector.getInstance().getConn().setAutoCommit(autoCommit);
            }
            return;
        }

        boolean autoCommit = DatabaseConnector.getInstance().getConn().getAutoCommit();
        try {

            DatabaseConnector.getInstance().getConn().setAutoCommit(false);
            String query = getInsertQuery(v.size());
            PreparedStatement st = DatabaseConnector.getInstance().getConn().prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS);
            int curCount = 1;
            curSt = st;
            for (ExperimentResult r : v) {
                st.setInt(curCount++, r.getSolverConfigId());
                st.setInt(curCount++, r.getExperimentId());
                st.setInt(curCount++, r.getInstanceId());
                st.setInt(curCount++, r.getRun());
                st.setInt(curCount++, r.getStatus().getStatusCode());
                st.setInt(curCount++, r.getSeed());
                st.setTimestamp(curCount++, r.getStartTime());
                st.setInt(curCount++, r.getPriority());
                st.setFloat(curCount++, r.getResultTime());
                st.setFloat(curCount++, r.getWallTime());
                st.setDouble(curCount++, r.getCost());
                st.setInt(curCount++, r.getComputeQueue());
                st.setInt(curCount++, r.getResultCode().getResultCode());

                st.setInt(curCount++, r.getCPUTimeLimit());
                st.setInt(curCount++, r.getMemoryLimit());
                st.setInt(curCount++, r.getWallClockTimeLimit());
                st.setInt(curCount++, r.getStackSizeLimit());
            }
            st.executeUpdate();
            ResultSet rs = st.getGeneratedKeys();
            int i = 0;
            while (rs.next()) {
                v.get(i).setSaved();
                v.get(i).setId(rs.getInt(1));
                i++;
            }
            rs.close();
            st.close();

            query = getInsertOutputsQuery(v.size());
            st = DatabaseConnector.getInstance().getConn().prepareStatement(query);
            curSt = st;
            curCount = 1;
            for (ExperimentResult r : v) {
                st.setInt(curCount++, r.getId());
                if (r instanceof ExperimentResultEx) {
                    ExperimentResultEx rx = (ExperimentResultEx) r;
                    st.setBytes(curCount++, rx.getSolverOutput());
                    st.setBytes(curCount++, rx.getLauncherOutput());
                    st.setBytes(curCount++, rx.getWatcherOutput());
                    st.setBytes(curCount++, rx.getVerifierOutput());
                } else {
                    st.setNull(curCount++, java.sql.Types.BLOB);
                    st.setNull(curCount++, java.sql.Types.BLOB);
                    st.setNull(curCount++, java.sql.Types.BLOB);
                    st.setNull(curCount++, java.sql.Types.BLOB);
                }
                st.setNull(curCount++, java.sql.Types.INTEGER);
                st.setNull(curCount++, java.sql.Types.INTEGER);
                st.setNull(curCount++, java.sql.Types.INTEGER);
            }
            st.executeUpdate();
            st.close();
        } catch (SQLException ex) {
            if (autoCommit) {
                DatabaseConnector.getInstance().getConn().rollback();
            }
            throw ex;
        } catch (Error ex) {
            if (autoCommit) {
                DatabaseConnector.getInstance().getConn().rollback();
            }
            throw ex;
        } catch (Throwable ex) {
            if (autoCommit) {
                DatabaseConnector.getInstance().getConn().rollback();
            }
        } finally {
            curSt = null;
            DatabaseConnector.getInstance().getConn().setAutoCommit(autoCommit);
        }

    }

    /*
     * Updates the CPUTimeLimit property of given list of jobs
     * @param v list of the jobs to update
     * @throws SQLException
     */
    public static void batchUpdateCPUTimeLimit(List<ExperimentResult> v) throws SQLException {
        boolean autoCommit = DatabaseConnector.getInstance().getConn().getAutoCommit();
        try {
            DatabaseConnector.getInstance().getConn().setAutoCommit(false);
            final String query = "UPDATE " + table + " SET CPUTimeLimit=? WHERE idJob=?";
            PreparedStatement st = DatabaseConnector.getInstance().getConn().prepareStatement(query);
            curSt = st;
            for (ExperimentResult r : v) {
                st.setInt(1, r.getCPUTimeLimit());
                st.setInt(2, r.getId());
                st.addBatch();
            }
            st.executeBatch();
            st.close();
        } catch (SQLException e) {
            DatabaseConnector.getInstance().getConn().rollback();
            throw e;
        } finally {
            curSt = null;
            DatabaseConnector.getInstance().getConn().setAutoCommit(autoCommit);
        }
    }

    /**
     * Updates the run property of the ExperimentResults at once (batch).
     * @param v vector of ExperimentResults to be updated
     * @throws SQLException
     */
    public static void batchUpdateRun(ArrayList<IdValue<Integer>> v) throws SQLException {
        boolean autoCommit = DatabaseConnector.getInstance().getConn().getAutoCommit();
        try {
            DatabaseConnector.getInstance().getConn().setAutoCommit(false);
            final String query = "UPDATE " + table + " SET run=? WHERE idJob=?";
            PreparedStatement st = DatabaseConnector.getInstance().getConn().prepareStatement(query);
            curSt = st;
            for (IdValue<Integer> r : v) {
                st.setInt(1, r.value);
                st.setInt(2, r.id);
                st.addBatch();
            }
            st.executeBatch();
            st.close();
        } catch (SQLException e) {
            DatabaseConnector.getInstance().getConn().rollback();
            throw e;
        } finally {
            curSt = null;
            DatabaseConnector.getInstance().getConn().setAutoCommit(autoCommit);
        }
    }

    /**
     * This will copy the output from the experiment results specified in the <code>from</code>-ArrayList to the experiment results
     * specified in the <code>to</code>-ArrayList.
     * @param from
     * @param to
     * @throws NoConnectionToDBException
     * @throws SQLException
     * @throws IllegalArgumentException if <code>from.size() != to.size()</code> or <code>from == null</code> or <code>to == null</code>
     */
    public static void batchCopyOutputs(ArrayList<ExperimentResult> from, ArrayList<ExperimentResult> to) throws NoConnectionToDBException, SQLException {
        if (from.size() != to.size() || from == null || to == null) {
            throw new IllegalArgumentException("from.size() != to.size()");
        }
        boolean autoCommit = DatabaseConnector.getInstance().getConn().getAutoCommit();
        try {
            DatabaseConnector.getInstance().getConn().setAutoCommit(false);
            final String query = copyOutputQuery;
            PreparedStatement st = DatabaseConnector.getInstance().getConn().prepareStatement(query);
            curSt = st;
            for (int i = 0; i < from.size(); i++) {
                st.setInt(1, from.get(i).getId());
                st.setInt(2, to.get(i).getId());
                st.addBatch();
            }

            st.executeBatch();
            st.close();
        } catch (Throwable e) {
            if (autoCommit) {
                DatabaseConnector.getInstance().getConn().rollback();
                DatabaseConnector.getInstance().getConn().setAutoCommit(autoCommit);
            }
            if (e instanceof SQLException) {
                throw (SQLException) e;
            }
        } finally {
            curSt = null;
            DatabaseConnector.getInstance().getConn().setAutoCommit(autoCommit);
        }
    }

    /**
     * Updates the priority of the ExperimentResults at once (batch).
     * @param v vector of ExperimentResults to be updated
     * @throws SQLException
     */
    public static void batchUpdatePriority(ArrayList<IdValue<Integer>> v) throws SQLException {
        boolean autoCommit = DatabaseConnector.getInstance().getConn().getAutoCommit();
        try {
            DatabaseConnector.getInstance().getConn().setAutoCommit(false);
            final String query = "UPDATE " + table + " SET priority=? WHERE idJob=?";
            PreparedStatement st = DatabaseConnector.getInstance().getConn().prepareStatement(query);
            curSt = st;
            for (IdValue<Integer> r : v) {
                st.setInt(1, r.value);
                st.setInt(2, r.id);
                st.addBatch();
            }
            st.executeBatch();
            st.close();
        } catch (Throwable e) {
            DatabaseConnector.getInstance().getConn().rollback();
            if (e instanceof SQLException) {
                throw (SQLException) e;
            }
        } finally {
            curSt = null;
            DatabaseConnector.getInstance().getConn().setAutoCommit(autoCommit);
        }
    }

    /**
     * Updates the status of the experiment results to <code>status</code>.
     * If <code>status</code> equals <code>ExperimentResultStatus.NOTSTARTED</code> then all fields which are set when saving results (client)
     * are set to <code>NULL</code>.<br/><br/>
     * <b>Note</b>: After that operation the local cached experiment results should be reloaded to prevent inconsistency.
     * @param v
     * @param status
     * @throws SQLException
     */
    public static void batchUpdateStatus(ArrayList<ExperimentResult> v, StatusCode status) throws SQLException {
        boolean autoCommit = DatabaseConnector.getInstance().getConn().getAutoCommit();
        try {
            DatabaseConnector.getInstance().getConn().setAutoCommit(false);

            String updateString = "status = " + status.getStatusCode();
            if (status == StatusCode.NOT_STARTED) {
                updateString += ",startTime=NULL,resultTime=NULL,wallTime=NULL,cost=NULL,resultCode=" + ResultCode.UNKNOWN.getResultCode() + ",solverOutput=NULL,launcherOutput=NULL,"
                        + "verifierOutput=NULL,watcherOutput=NULL,solverExitCode=NULL,watcherExitCode=NULL,"
                        + "verifierExitCode=NULL,computeQueue=NULL";
            }
            final String query = "UPDATE " + table + " LEFT JOIN " + outputTable + " ON (idJob=ExperimentResults_idJob) SET " + updateString + " WHERE idJob=?";
            PreparedStatement st = DatabaseConnector.getInstance().getConn().prepareStatement(query);
            curSt = st;
            for (ExperimentResult r : v) {
                st.setInt(1, r.getId());
                st.addBatch();
                r.setSaved();
            }
            st.executeBatch();
            st.close();
        } catch (Throwable e) {
            DatabaseConnector.getInstance().getConn().rollback();
            if (e instanceof SQLException) {
                throw (SQLException) e;
            }
        } finally {
            curSt = null;
            DatabaseConnector.getInstance().getConn().setAutoCommit(autoCommit);
        }
    }

    public static void deleteExperimentResults(ArrayList<ExperimentResult> experimentResults) throws SQLException {
        deleteExperimentResults(experimentResults, null);
    }

    /**
     * Deletes all experiment results at once (batch).
     * @param experimentResults the experiment results to be deleted
     * @throws SQLException
     */
    public static void deleteExperimentResults(ArrayList<ExperimentResult> experimentResults, Tasks task) throws SQLException {
        if (experimentResults.isEmpty()) {
            return;
        }
        if (experimentResults.size() > 10000) {
            boolean autoCommit = DatabaseConnector.getInstance().getConn().getAutoCommit();
            try {
                ArrayList<ExperimentResult> res = new ArrayList<ExperimentResult>();
                int deletedCount = 0;
                for (ExperimentResult ex : experimentResults) {
                    res.add(ex);
                    if (res.size() == 10000) {
                        deleteExperimentResults(res, task);
                        deletedCount += 10000;
                        if (task != null) {
                            task.setTaskProgress(deletedCount / (float) experimentResults.size());
                        }
                        res.clear();
                    }
                }
                if (!res.isEmpty()) {
                    deleteExperimentResults(res);
                }
                if (task != null) {
                    task.setTaskProgress(0.f);
                }
            } catch (SQLException e) {
                if (autoCommit) {
                    DatabaseConnector.getInstance().getConn().rollback();
                }
                throw e;
            } catch (Error e) {
                if (autoCommit) {
                    DatabaseConnector.getInstance().getConn().rollback();
                }
                throw e;
            } catch (Throwable e) {
                if (autoCommit) {
                    DatabaseConnector.getInstance().getConn().rollback();
                }
            } finally {
                curSt = null;
                DatabaseConnector.getInstance().getConn().setAutoCommit(autoCommit);
            }
            return;
        }

        boolean autoCommit = DatabaseConnector.getInstance().getConn().getAutoCommit();
        try {
            DatabaseConnector.getInstance().getConn().setAutoCommit(false);
            String query = getDeleteQuery(experimentResults.size());
            PreparedStatement st = DatabaseConnector.getInstance().getConn().prepareStatement(query);
            curSt = st;
            int curCount = 1;
            for (ExperimentResult r : experimentResults) {
                st.setInt(curCount++, r.getId());
                r.setDeleted();
                /* this should only be done if the batch delete actually
                 * gets commited, right now this might not be the case
                 * if there's an DB exception or the executeBatch() is
                 * cancelled (see cancelStatement()).
                 * Without caching this might not be a problem.
                 */
            }
            st.executeUpdate();
            st.close();

            // send message to clients to stop calculation of deleted jobs
            HashMap<Integer, ArrayList<Integer>> clientJobs = new HashMap<Integer, ArrayList<Integer>>();
            for (ExperimentResult r : experimentResults) {
                if (r.getIdClient() == null || !r.getStatus().equals(StatusCode.RUNNING)) {
                    continue;
                }
                ArrayList<Integer> tmp = clientJobs.get(r.getIdClient());
                if (tmp == null) {
                    tmp = new ArrayList<Integer>();
                    clientJobs.put(r.getIdClient(), tmp);
                }
                tmp.add(r.getId());
            }
            for (Integer clientId : clientJobs.keySet()) {
                String message = "";
                for (Integer jobId : clientJobs.get(clientId)) {
                    message += "kill " + jobId + '\n';
                }
                ClientDAO.sendMessage(clientId, message);
            }

        } catch (SQLException e) {
            if (autoCommit) {
                DatabaseConnector.getInstance().getConn().rollback();
            }
            throw e;
        } catch (Error e) {
            if (autoCommit) {
                DatabaseConnector.getInstance().getConn().rollback();
            }
            throw e;
        } catch (Throwable e) {
            if (autoCommit) {
                DatabaseConnector.getInstance().getConn().rollback();
            }
        } finally {
            curSt = null;
            DatabaseConnector.getInstance().getConn().setAutoCommit(autoCommit);
        }
    }

    private static ExperimentResult getExperimentResultFromResultSet(ResultSet rs) throws SQLException, StatusCodeNotInDBException, ResultCodeNotInDBException {
        ExperimentResult r = new ExperimentResult();
        r.setSolverConfigId(rs.getInt("SolverConfig_idSolverConfig"));
        r.setInstanceId(rs.getInt("Instances_idInstance"));
        r.setExperimentId(rs.getInt("Experiment_idExperiment"));
        r.setId(rs.getInt("idJob"));
        r.setRun(rs.getInt("run"));
        r.setSeed(rs.getInt("seed"));
        r.setStatus(StatusCodeDAO.getByStatusCode(rs.getInt("status")));
        r.setResultTime(rs.getFloat("resultTime"));
        r.setWallTime(rs.getFloat("wallTime"));
        r.setCost(rs.getDouble("cost"));
        r.setResultCode(ResultCodeDAO.getByResultCode(rs.getInt("resultCode")));
        // r.setSolverExitCode(rs.getInt("solverExitCode"));
        // r.setWatcherExitCode(rs.getInt("watcherExitCode"));
        // r.setVerifierExitCode(rs.getInt("verifierExitCode"));
        r.setComputeQueue(rs.getInt("computeQueue"));
        r.setPriority(rs.getInt("priority"));
        r.setStartTime(rs.getTimestamp("startTime"));

        r.setCPUTimeLimit(rs.getInt("CPUTimeLimit"));
        r.setMemoryLimit(rs.getInt("memoryLimit"));
        r.setWallClockTimeLimit(rs.getInt("wallClockTimeLimit"));
        r.setStackSizeLimit(rs.getInt("stackSizeLimit"));

        r.setComputeNode(rs.getString("computeNode"));
        r.setComputeNodeIP(rs.getString("computeNodeIP"));

        r.setIdClient(rs.getInt("Client_idClient"));
        if (rs.wasNull()) {
            r.setIdClient(null);
        }

        r.setDatemodified(rs.getTimestamp("date_modified"));
        if (r.getStatus() == StatusCode.RUNNING) {
            r.setRunningTime(rs.getInt("runningTime"));
        } else {
            r.setRunningTime(0);
        }
        return r;
    }

    public static Timestamp getLastModifiedByExperimentId(int id) throws NoConnectionToDBException, SQLException {
        PreparedStatement st = DatabaseConnector.getInstance().getConn().prepareStatement("select MAX(date_modified) AS ermodified FROM " + table + " WHERE Experiment_idExperiment = ?");
        st.setInt(1, id);
        ResultSet rs = st.executeQuery();

        rs.next();
        Timestamp res = rs.getTimestamp(1);
        if (res == null) {
            res = new Timestamp(0);
        } else {
            res.setTime(res.getTime() + 1);
        }
        rs.close();
        st.close();
        return res;
    }
    private static final int CONST_MAXSELECTROWCOUNT = 100000;

    public static ArrayList<ExperimentResult> getAllModifiedByExperimentId(int id, Timestamp modified) throws SQLException, IOException, PropertyNotInDBException, PropertyTypeNotExistException, ComputationMethodDoesNotExistException, ExpResultHasSolvPropertyNotInDBException, ExperimentResultNotInDBException, StatusCodeNotInDBException, ResultCodeNotInDBException {
        return getAllModifiedByExperimentId(id, modified, null);
    }

    /**
     * Returns all experiment results which were modified after the modified timestamp for a given experiment id
     * @param id the experiment id for the experiment results
     * @param modified the modified timestamp
     * @return
     * @throws NoConnectionToDBException
     * @throws SQLException
     * @throws IOException
     * @throws PropertyNotInDBException
     * @throws PropertyTypeNotExistException
     */
    public static ArrayList<ExperimentResult> getAllModifiedByExperimentId(int id, Timestamp modified, Tasks task) throws SQLException, IOException, PropertyNotInDBException, PropertyTypeNotExistException, ComputationMethodDoesNotExistException, ExpResultHasSolvPropertyNotInDBException, ExperimentResultNotInDBException, StatusCodeNotInDBException, ResultCodeNotInDBException {
        ArrayList<ExperimentResult> results = new ArrayList<ExperimentResult>();
        boolean autoCommit = DatabaseConnector.getInstance().getConn().getAutoCommit();
        try {
            DatabaseConnector.getInstance().getConn().setAutoCommit(false);
            int lastCount = 0;
            do {
                ArrayList<ExperimentResult> tmp = getAllModifiedByExperimentId(id, modified, results.size());
                lastCount = tmp.size();
                results.addAll(tmp);
                if (task != null) {
                    task.setStatus("Loading experiment results (" + results.size() + ")..");
                }
            } while (lastCount == CONST_MAXSELECTROWCOUNT);
        } catch (SQLException ex) {
            if (autoCommit) {
                DatabaseConnector.getInstance().getConn().rollback();
            }
            throw ex;
        } catch (IOException ex) {
            if (autoCommit) {
                DatabaseConnector.getInstance().getConn().rollback();
            }
            throw ex;
        } catch (Error err) {
            if (autoCommit) {
                DatabaseConnector.getInstance().getConn().rollback();
            }
            throw err;
        } catch (Throwable t) {
            if (autoCommit) {
                DatabaseConnector.getInstance().getConn().rollback();
            }
            t.printStackTrace();
        } finally {
            DatabaseConnector.getInstance().getConn().setAutoCommit(autoCommit);
        }
        if (task != null) {
            task.setStatus("Experiment results loaded.");
        }
        return results;
    }

    private static ArrayList<ExperimentResult> getAllModifiedByExperimentId(int id, Timestamp modified, int offset) throws SQLException, IOException, PropertyNotInDBException, PropertyTypeNotExistException, ComputationMethodDoesNotExistException, ExpResultHasSolvPropertyNotInDBException, ExperimentResultNotInDBException, StatusCodeNotInDBException, ResultCodeNotInDBException {
        ArrayList<ExperimentResult> v = new ArrayList<ExperimentResult>();
        PreparedStatement st = DatabaseConnector.getInstance().getConn().prepareStatement(
                selectQuery
                + "WHERE Experiment_idExperiment=? AND IF(status = " + StatusCode.RUNNING.getStatusCode() + ", TIMESTAMPADD(SECOND, -1, CURRENT_TIMESTAMP), date_modified) >= ? LIMIT " + offset + "," + CONST_MAXSELECTROWCOUNT);
        st.setInt(1, id);
        st.setTimestamp(2, modified);
        curSt = st;
        ResultSet rs;
        try {
            rs = st.executeQuery();
        } finally {
            curSt = null;
        }
        while (rs.next()) {
            ExperimentResult er = getExperimentResultFromResultSet(rs);
            v.add(er);
            er.setSaved();
        }
        rs.close();
        st.close();
        ExperimentResultHasPropertyDAO.assign(v, id);
        return v;
    }

    /**
     * returns all jobs of the given Experiment
     * @param id
     * @return ExperimentResults vector
     * @throws SQLException
     */
    public static ArrayList<ExperimentResult> getAllByExperimentId(int id) throws SQLException, IOException, PropertyTypeNotExistException, PropertyNotInDBException, NoConnectionToDBException, ComputationMethodDoesNotExistException, ExpResultHasSolvPropertyNotInDBException, ExperimentResultNotInDBException, StatusCodeNotInDBException, ResultCodeNotInDBException {
        ArrayList<ExperimentResult> v = new ArrayList<ExperimentResult>();
        PreparedStatement st = DatabaseConnector.getInstance().getConn().prepareStatement(
                selectQuery
                + "WHERE Experiment_idExperiment=?;");
        st.setInt(1, id);
        ResultSet rs = st.executeQuery();
        while (rs.next()) {
            ExperimentResult er = getExperimentResultFromResultSet(rs);
            v.add(er);
            er.setSaved();
        }
        ExperimentResultHasPropertyDAO.assign(v, id);
        rs.close();
        st.close();
        return v;
    }

    public static ArrayList<ExperimentResultEx> getExtendedExperimentResultsFromExperimentResults(List<ExperimentResult> results) throws NoConnectionToDBException, SQLException {
        ArrayList<ExperimentResultEx> resx = new ArrayList<ExperimentResultEx>();
        if (results.isEmpty()) {
            return resx;
        }
        HashMap<Integer, ExperimentResult> resultMap = new HashMap<Integer, ExperimentResult>();
        String constTable = "(";
        for (ExperimentResult er : results) {
            resultMap.put(er.getId(), er);
            constTable += "" + er.getId() + ",";
        }
        constTable = constTable.substring(0, constTable.length() - 1) + ")";
        Statement st = DatabaseConnector.getInstance().getConn().createStatement();
        ResultSet rs = st.executeQuery("SELECT idJob, startTime, solverOutput, launcherOutput, watcherOutput, verifierOutput FROM " + table + " JOIN " + outputTable + " ON (idJob = ExperimentResults_idJob) WHERE idJob IN " + constTable);
        while (rs.next()) {
            Timestamp startTime = null;
            try {
                startTime = rs.getTimestamp(2);
            } catch (Exception e) {
                // fails if the db DateTime objekt could not be converted to a Timestamp (illegal date.)
            }
            ExperimentResult er = resultMap.get(rs.getInt(1));
            ExperimentResultEx erx = createExperimentResult(er.getRun(), er.getPriority(), er.getComputeQueue(), er.getStatus(),
                    er.getResultCode(), er.getSeed(), er.getResultTime(), er.getWallTime(), er.getCost(), er.getSolverConfigId(), er.getExperimentId(),
                    er.getInstanceId(),
                    startTime,
                    er.getCPUTimeLimit(),
                    er.getMemoryLimit(),
                    er.getWallClockTimeLimit(),
                    er.getStackSizeLimit(),
                    rs.getBytes(3),
                    rs.getBytes(4),
                    rs.getBytes(5),
                    rs.getBytes(6));
            // set other fields
            erx.setResultTime(er.getResultTime());
            erx.setSolverExitCode(er.getSolverExitCode());
            erx.setVerifierExitCode(er.getVerifierExitCode());
            erx.setWatcherExitCode(er.getWatcherExitCode());
            resx.add(erx);
        }
        rs.close();
        st.close();
        return resx;
    }

    /**
     * Returns all experiment results for the given solver configuration
     * @param sc
     * @return
     * @throws SQLException
     * @throws PropertyNotInDBException
     * @throws PropertyTypeNotExistException
     * @throws IOException
     */
    public static ArrayList<ExperimentResult> getAllBySolverConfiguration(SolverConfiguration sc) throws SQLException, PropertyNotInDBException, PropertyTypeNotExistException, IOException, NoConnectionToDBException, ComputationMethodDoesNotExistException, ExpResultHasSolvPropertyNotInDBException, ExperimentResultNotInDBException, StatusCodeNotInDBException, ResultCodeNotInDBException {
        ArrayList<ExperimentResult> res = new ArrayList<ExperimentResult>();
        PreparedStatement st = DatabaseConnector.getInstance().getConn().prepareStatement(
                selectQuery + "WHERE Experiment_idExperiment=? AND SolverConfig_idSolverConfig=?;");
        st.setInt(1, sc.getExperiment_id());
        st.setInt(2, sc.getId());
        ResultSet rs = st.executeQuery();
        while (rs.next()) {
            ExperimentResult er = getExperimentResultFromResultSet(rs);
            res.add(er);
            er.setSaved();
        }
        rs.close();
        st.close();
        ExperimentResultHasPropertyDAO.assign(res, sc.getExperiment_id());
        return res;
    }

    /**
     * Returns all experiment results for the given ExperimentHasInstance object.
     * @param ehi
     * @return
     * @throws SQLException
     * @throws PropertyNotInDBException
     * @throws PropertyTypeNotExistException
     * @throws IOException
     */
    public static ArrayList<ExperimentResult> getAllByExperimentHasInstance(ExperimentHasInstance ehi) throws SQLException, PropertyNotInDBException, PropertyTypeNotExistException, IOException, NoConnectionToDBException, ComputationMethodDoesNotExistException, ExpResultHasSolvPropertyNotInDBException, ExperimentResultNotInDBException, StatusCodeNotInDBException, ResultCodeNotInDBException {
        ArrayList<ExperimentResult> res = new ArrayList<ExperimentResult>();
        PreparedStatement st = DatabaseConnector.getInstance().getConn().prepareStatement(
                selectQuery + "WHERE Experiment_idExperiment=? AND Instances_idInstance=?;");
        st.setInt(1, ehi.getExperiment_id());
        st.setInt(2, ehi.getInstances_id());
        ResultSet rs = st.executeQuery();
        while (rs.next()) {
            ExperimentResult er = getExperimentResultFromResultSet(rs);
            res.add(er);
            er.setSaved();
        }
        rs.close();
        st.close();
        ExperimentResultHasPropertyDAO.assign(res, ehi.getExperiment_id());
        return res;
    }

    /**
     * Sets the auto commit of the underlying connection.
     * @param commit
     * @throws SQLException
     */
    public static void setAutoCommit(boolean commit) throws SQLException {
        DatabaseConnector.getInstance().getConn().setAutoCommit(commit);
    }

    public static void cancelStatement() throws SQLException {
        if (curSt != null) {
            try {
                curSt.cancel();
            } catch (Exception _) {
            }
        }
    }

    /**
     * returns the experiment results with job IDs that are in the
     * passed list.
     * @param ids
     * @return
     * @throws Exception 
     */
    public static List<ExperimentResult> getByIds(List<Integer> ids) throws Exception {
        final int SINGLE_BATCH = 1;
        final int SMALL_BATCH = 50;
        final int MEDIUM_BATCH = 1000;
        final int LARGE_BATCH = 10000;
        int totalNumberOfValuesLeftToBatch = ids.size();

        List<ExperimentResult> results = new ArrayList<ExperimentResult>();
        while (totalNumberOfValuesLeftToBatch > 0) {
            int batchSize = SINGLE_BATCH;
            if (totalNumberOfValuesLeftToBatch >= LARGE_BATCH) {
                batchSize = LARGE_BATCH;
            } else if (totalNumberOfValuesLeftToBatch >= MEDIUM_BATCH) {
                batchSize = MEDIUM_BATCH;
            } else if (totalNumberOfValuesLeftToBatch >= SMALL_BATCH) {
                batchSize = SMALL_BATCH;
            }

            StringBuilder inClause = new StringBuilder();

            for (int i = 0; i < batchSize; i++) {
                inClause.append('?');
                if (i < batchSize - 1) {
                    inClause.append(",");
                }
            }
            PreparedStatement ps = DatabaseConnector.getInstance().getConn().prepareStatement(
                    selectQuery + " WHERE idJob IN (" + inClause.toString() + ");");
            for (int i = 1; i <= batchSize; i++) {
                ps.setInt(i, ids.get(totalNumberOfValuesLeftToBatch - i));
            }
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                results.add(getExperimentResultFromResultSet(rs));
            }
            rs.close();
            ps.close();
            totalNumberOfValuesLeftToBatch -= batchSize;
        }
        return results;
    }

    /**
     *
     * @param id of the requested ExperimentResult
     * @return the ExperimentResult object with the given id
     * @throws NoConnectionToDBException
     * @throws SQLException
     * @throws ExperimentResultNotInDBException
     * @author rretz
     */
    public static ExperimentResult getById(int id) throws NoConnectionToDBException, SQLException, ExperimentResultNotInDBException, PropertyTypeNotExistException, IOException, PropertyNotInDBException, ComputationMethodDoesNotExistException, ExpResultHasSolvPropertyNotInDBException, StatusCodeNotInDBException, ResultCodeNotInDBException {
        PreparedStatement ps = DatabaseConnector.getInstance().getConn().prepareStatement(
                selectQuery
                + "WHERE idJob=?;");
        ps.setInt(1, id);
        ResultSet rs = ps.executeQuery();
        if (!rs.next()) {
            rs.close();
            ps.close();
            throw new ExperimentResultNotInDBException();
        }
        ExperimentResult er = getExperimentResultFromResultSet(rs);
        rs.close();
        ps.close();
        ExperimentResultHasPropertyDAO.assign(er);
        return er;
    }

    /**
     *
     * @param id of the requested ExperimentResult
     * @return the ExperimentResult object with the given id
     * @throws NoConnectionToDBException
     * @throws SQLException
     * @throws ExperimentResultNotInDBException
     * @author rretz
     */
    public static ExperimentResult getByIdWithoutAssign(int id) throws NoConnectionToDBException, SQLException, ExperimentResultNotInDBException, PropertyTypeNotExistException, IOException, PropertyNotInDBException, ComputationMethodDoesNotExistException, ExpResultHasSolvPropertyNotInDBException, StatusCodeNotInDBException, ResultCodeNotInDBException {
        PreparedStatement ps = DatabaseConnector.getInstance().getConn().prepareStatement(
                selectQuery
                + "WHERE idJob=?;");
        ps.setInt(1, id);
        ResultSet rs = ps.executeQuery();
        if (!rs.next()) {
            rs.close();
            ps.close();
            throw new ExperimentResultNotInDBException();
        }
        ExperimentResult er = getExperimentResultFromResultSet(rs);
        //ArrayList<ExperimentResult> tmp = new ArrayList<ExperimentResult>();
        //tmp.add(er);
        rs.close();
        ps.close();
        //ExperimentResultHasPropertyDAO.assign(tmp, er.getExperimentId());
        return er;
    }

    public static Blob getLauncherOutput(ExperimentResult expRes) throws NoConnectionToDBException, SQLException, FileNotFoundException, IOException, ExperimentResultNotInDBException {
        PreparedStatement ps = DatabaseConnector.getInstance().getConn().prepareStatement(
                "SELECT launcherOutput "
                + "FROM " + outputTable + " "
                + "WHERE ExperimentResults_idJob=?;");
        ps.setInt(1, expRes.getId());
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            try {
                return rs.getBlob(1);
            } finally {
                ps.close();
            }
        } else {
            return null;
        }
    }

    public static Blob getSolverOutput(ExperimentResult expRes) throws NoConnectionToDBException, SQLException, FileNotFoundException, IOException, ExperimentResultNotInDBException {
        PreparedStatement ps = DatabaseConnector.getInstance().getConn().prepareStatement(
                "SELECT solverOutput "
                + "FROM " + outputTable + " "
                + "WHERE ExperimentResults_idJob=?;");
        ps.setInt(1, expRes.getId());
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            try {
                return rs.getBlob(1);
            } finally {
                ps.close();
            }
        } else {
            return null;
        }
    }

    public static Blob getVerifierOutput(ExperimentResult expRes) throws NoConnectionToDBException, SQLException, FileNotFoundException, IOException, ExperimentResultNotInDBException {
        PreparedStatement ps = DatabaseConnector.getInstance().getConn().prepareStatement(
                "SELECT verifierOutput "
                + "FROM " + outputTable + " "
                + "WHERE ExperimentResults_idJob=?;");
        ps.setInt(1, expRes.getId());
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            try {
                return rs.getBlob(1);
            } finally {
                ps.close();
            }
        } else {
            return null;
        }
    }

    public static Blob getWatcherOutput(ExperimentResult expRes) throws NoConnectionToDBException, SQLException, FileNotFoundException, IOException, ExperimentResultNotInDBException {
        PreparedStatement ps = DatabaseConnector.getInstance().getConn().prepareStatement(
                "SELECT watcherOutput "
                + "FROM " + outputTable + " "
                + "WHERE ExperimentResults_idJob=?;");
        ps.setInt(1, expRes.getId());
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            Blob blob = rs.getBlob(1);
            ps.close();
            return blob;
        } else {
            return null;
        }
    }

    /**
     * Copies the binary file of a result file of an ExperimentResult to a specified location on the filesystem.
     * @param id the id of the ExperimentResult
     * @param f the file in which the binary file is copied
     * @throws NoConnectionToDBException
     * @throws SQLException
     * @throws FileNotFoundException
     * @throws IOException
     */
    public static void getSolverOutput(int id, File f) throws NoConnectionToDBException, SQLException, FileNotFoundException, IOException {
        PreparedStatement ps = DatabaseConnector.getInstance().getConn().prepareStatement(
                "SELECT solverOutput "
                + "FROM " + table + " "
                + "WHERE idJob=?;");
        ps.setInt(1, id);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            FileOutputStream out = new FileOutputStream(f);
            InputStream in = rs.getBinaryStream("solverOutput");
            int len;
            byte[] buf = new byte[256 * 1024];
            while ((len = in.read(buf)) > -1) {
                out.write(buf, 0, len);
            }
            out.close();
            in.close();
        }
    }

    /**
     * Copies the binary file of the launcher output of an ExperimentResult to a specified location on the filesystem.
     * @param id th id of the ExperimentResult
     * @param f the file in which the binary file is copied
     * @throws NoConnectionToDBException
     * @throws SQLException
     * @throws FileNotFoundException
     * @throws IOException
     */
    private static void getLauncherOutput(int id, File f) throws NoConnectionToDBException, SQLException, FileNotFoundException, IOException {
        PreparedStatement ps = DatabaseConnector.getInstance().getConn().prepareStatement(
                "SELECT lancherOutput "
                + "FROM " + table + " "
                + "WHERE idJob=?;");
        ps.setInt(1, id);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            FileOutputStream out = new FileOutputStream(f);
            InputStream in = rs.getBinaryStream("lancherOutput");
            int len;
            byte[] buf = new byte[256 * 1024];
            while ((len = in.read(buf)) > -1) {
                out.write(buf, 0, len);
            }
            out.close();
            in.close();
        }
    }

    /**
     * Copies the binary file of the watcher output of an ExperimentResult to a specified location on the filesystem.
     * @param id th id of the ExperimentResult
     * @param f the file in which the binary file is copied
     * @throws NoConnectionToDBException
     * @throws SQLException
     * @throws FileNotFoundException
     * @throws IOException
     */
    private static void getWatcherOutput(int id, File f) throws NoConnectionToDBException, SQLException, FileNotFoundException, IOException {
        PreparedStatement ps = DatabaseConnector.getInstance().getConn().prepareStatement(
                "SELECT watcherOutput "
                + "FROM " + table + " "
                + "WHERE idJob=?;");
        ps.setInt(1, id);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            FileOutputStream out = new FileOutputStream(f);
            InputStream in = rs.getBinaryStream("watcherOutput");
            int len;
            byte[] buf = new byte[256 * 1024];
            while ((len = in.read(buf)) > -1) {
                out.write(buf, 0, len);
            }
            out.close();
            in.close();
        }
    }

    /**
     * Copies the binary file of the verifier output of an ExperimentResult to a specified location on the filesystem.
     * @param id th id of the ExperimentResult
     * @param f the file in which the binary file is copied
     * @throws NoConnectionToDBException
     * @throws SQLException
     * @throws FileNotFoundException
     * @throws IOException
     */
    private static void getVerifierOutput(int id, File f) throws NoConnectionToDBException, SQLException, FileNotFoundException, IOException {
        PreparedStatement ps = DatabaseConnector.getInstance().getConn().prepareStatement(
                "SELECT verifierOutput "
                + "FROM " + table + " "
                + "WHERE idJob=?;");
        ps.setInt(1, id);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            FileOutputStream out = new FileOutputStream(f);
            InputStream in = rs.getBinaryStream("verifierOutput");
            int len;
            byte[] buf = new byte[256 * 1024];
            while ((len = in.read(buf)) > -1) {
                out.write(buf, 0, len);
            }
            out.close();
            in.close();
        }
    }

    /**
     * Returns a string representing the output of the given type.
     * @param type one of ExperimentResult.SOLVER_OUTPUT/LAUNCHER_OUTPUT/VERIFIER_OUTPUT/WATCHER_OUTPUT
     * @param er the experiment result for which the output string should be generated
     * @return null if there is no output, the string representing the output otherwise
     * @throws NoConnectionToDBException
     * @throws SQLException
     * @throws IOException
     */
    public static String getOutputText(int type, ExperimentResult er) throws NoConnectionToDBException, SQLException, IOException {
        String col = null;
        switch (type) {
            case ExperimentResult.SOLVER_OUTPUT:
                col = "solverOutput";
                break;
            case ExperimentResult.LAUNCHER_OUTPUT:
                col = "launcherOutput";
                break;
            case ExperimentResult.VERIFIER_OUTPUT:
                col = "verifierOutput";
                break;
            case ExperimentResult.WATCHER_OUTPUT:
                col = "watcherOutput";
        }
        if (col == null) {
            return null;
        }

        PreparedStatement ps = DatabaseConnector.getInstance().getConn().prepareStatement(
                "SELECT " + col + " "
                + "FROM " + outputTable + " "
                + "WHERE ExperimentResults_idJob=?;");
        ps.setInt(1, er.getId());
        ResultSet rs = ps.executeQuery();
        String res = null;
        if (rs.next()) {
            StringBuilder sb = new StringBuilder();
            InputStream in = rs.getBinaryStream(1);
            if (in == null) {
                return null;
            }
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }
            in.close();
            res = sb.toString();
        }
        rs.close();
        ps.close();
        return res;
    }

    /**
     * Returns CURRENT_TIMESTAMP - 1 second
     * @return
     * @throws NoConnectionToDBException
     * @throws SQLException
     */
    public static Timestamp getCurrentTimestamp() throws NoConnectionToDBException, SQLException {
        PreparedStatement st = DatabaseConnector.getInstance().getConn().prepareStatement(
                "SELECT TIMESTAMPADD(SECOND, -1, CURRENT_TIMESTAMP);");
        ResultSet rs = st.executeQuery();
        Timestamp res = null;
        if (rs.next()) {
            res = rs.getTimestamp(1);
        }
        rs.close();
        st.close();
        return res;
    }

    public static int getMaximumRun(Experiment exp) throws SQLException {
        PreparedStatement st = DatabaseConnector.getInstance().getConn().prepareStatement(
                "SELECT MAX(run) FROM " + table + " WHERE Experiment_idExperiment = ?");
        st.setInt(1, exp.getId());
        ResultSet rs = st.executeQuery();
        int res;
        if (rs.next()) {
            res = rs.getInt(1);
        } else {
            res = 0;
        }
        rs.close();
        st.close();
        return res;
    }

    public static ArrayList<ExperimentResult> getAllByInstanceId(int id) throws NoConnectionToDBException, SQLException, PropertyNotInDBException, PropertyTypeNotExistException, IOException, ComputationMethodDoesNotExistException, ExpResultHasSolvPropertyNotInDBException, ExperimentResultNotInDBException, StatusCodeNotInDBException, ResultCodeNotInDBException {
        ArrayList<ExperimentResult> v = new ArrayList<ExperimentResult>();
        HashMap<Integer, ExperimentResult> expResultsMap = new HashMap<Integer, ExperimentResult>();
        PreparedStatement st = DatabaseConnector.getInstance().getConn().prepareStatement(
                selectQuery
                + "WHERE Instances_idInstance=?;");
        st.setInt(1, id);
        ResultSet rs = st.executeQuery();
        /*while (rs.next()) {
        ExperimentResult er = getExperimentResultFromResultSet(rs);
        ExperimentResultHasPropertyDAO.assign(er);
        v.add(er);
        er.setSaved();
        }*/
        while (rs.next()) {
            ExperimentResult er = getExperimentResultFromResultSet(rs);
            er.setSaved();
            expResultsMap.put(er.getId(), er);
            v.add(er);
        }
        ExperimentResultHasPropertyDAO.assign(expResultsMap, v);

        rs.close();
        st.close();
        return v;
    }

        public static ArrayList<ExperimentResult> getAllByExperimentAndInstanceId(int expid, int id) throws NoConnectionToDBException, SQLException, PropertyNotInDBException, PropertyTypeNotExistException, IOException, ComputationMethodDoesNotExistException, ExpResultHasSolvPropertyNotInDBException, ExperimentResultNotInDBException, StatusCodeNotInDBException, ResultCodeNotInDBException {
        ArrayList<ExperimentResult> v = new ArrayList<ExperimentResult>();
        HashMap<Integer, ExperimentResult> expResultsMap = new HashMap<Integer, ExperimentResult>();
        PreparedStatement st = DatabaseConnector.getInstance().getConn().prepareStatement(
                selectQuery
                + "WHERE Experiment_idExperiment = ? AND Instances_idInstance=?;");
        st.setInt(1, expid);
        st.setInt(2, id);
        ResultSet rs = st.executeQuery();
        while (rs.next()) {
            ExperimentResult er = getExperimentResultFromResultSet(rs);
            er.setSaved();
            expResultsMap.put(er.getId(), er);
            v.add(er);
        }
     //   ExperimentResultHasPropertyDAO.assign(expResultsMap, v);
        rs.close();
        st.close();
        return v;
    }
    
    
    public static ArrayList<ExperimentResult> getAllModifiedByClientId(int id, Timestamp modified) throws NoConnectionToDBException, SQLException, IOException, PropertyNotInDBException, PropertyTypeNotExistException, ComputationMethodDoesNotExistException, ExpResultHasSolvPropertyNotInDBException, ExperimentResultNotInDBException, StatusCodeNotInDBException, ResultCodeNotInDBException {
        ArrayList<ExperimentResult> v = new ArrayList<ExperimentResult>();
        PreparedStatement st = DatabaseConnector.getInstance().getConn().prepareStatement(
                selectQuery
                + "WHERE Client_idClient=? AND IF(status = " + StatusCode.RUNNING.getStatusCode() + ", TIMESTAMPADD(SECOND, -1, CURRENT_TIMESTAMP), date_modified) >= ?;");
        st.setInt(1, id);
        st.setTimestamp(2, modified);
        ResultSet rs = st.executeQuery();
        while (rs.next()) {
            ExperimentResult er = getExperimentResultFromResultSet(rs);
            v.add(er);
            er.setSaved();
        }
        ExperimentResultHasPropertyDAO.assign(v, id);
        rs.close();
        st.close();
        return v;
    }

    public static ArrayList<ExperimentResult> getAllByClientId(int id) throws SQLException, StatusCodeNotInDBException, ResultCodeNotInDBException, PropertyNotInDBException, PropertyTypeNotExistException, IOException, NoConnectionToDBException, ComputationMethodDoesNotExistException, ExpResultHasSolvPropertyNotInDBException, ExperimentResultNotInDBException {
        ArrayList<ExperimentResult> v = new ArrayList<ExperimentResult>();
        PreparedStatement st = DatabaseConnector.getInstance().getConn().prepareStatement(
                selectQuery
                + "WHERE Client_idClient=?;");
        st.setInt(1, id);
        ResultSet rs = st.executeQuery();
        while (rs.next()) {
            ExperimentResult er = getExperimentResultFromResultSet(rs);
            v.add(er);
            er.setSaved();
        }
        ExperimentResultHasPropertyDAO.assign(v, id);
        rs.close();
        st.close();
        return v;
    }

    public static Timestamp getLastModifiedByClientId(int id) throws NoConnectionToDBException, SQLException {
        PreparedStatement st = DatabaseConnector.getInstance().getConn().prepareStatement("select MAX(date_modified) AS ermodified FROM " + table + " WHERE Client_idClient = ?");
        st.setInt(1, id);
        ResultSet rs = st.executeQuery();

        rs.next();
        Timestamp res = rs.getTimestamp(1);
        if (res == null) {
            res = new Timestamp(0);
        } else {
            res.setTime(res.getTime() + 1);
        }
        rs.close();
        st.close();
        return res;
    }

    public static HashMap<Pair<SolverConfiguration, Instance>, List<ExperimentResult>> getBySolverConfigurationsAndInstances(List<SolverConfiguration> scs, List<Instance> instances) throws SQLException {
        //TODO: also assign experiment result properties?

        HashMap<Pair<SolverConfiguration, Instance>, List<ExperimentResult>> res = new HashMap<Pair<SolverConfiguration, Instance>, List<ExperimentResult>>();
        if (scs.isEmpty() || instances.isEmpty()) {
            return res;
        }

        List<Integer> scIds = new ArrayList<Integer>();
        for (SolverConfiguration sc : scs) {
            scIds.add(sc.getId());
        }
        List<Integer> iIds = new ArrayList<Integer>();
        for (Instance i : instances) {
            iIds.add(i.getId());
        }

        String scIdsStr = edacc.experiment.Util.getIdArray(scIds);
        String iIdStr = edacc.experiment.Util.getIdArray(iIds);
        PreparedStatement st = DatabaseConnector.getInstance().getConn().prepareStatement(
                selectQuery
                + "WHERE Instances_idInstance in (" + iIdStr + ") AND SolverConfig_idSolverConfig IN (" + scIdsStr + ");");
        ResultSet rs = st.executeQuery();
        while (rs.next()) {
            ExperimentResult er = getExperimentResultFromResultSet(rs);
            Pair<SolverConfiguration, Instance> identifier = new Pair<SolverConfiguration, Instance>(SolverConfigurationDAO.getSolverConfigurationById(er.getSolverConfigId()), InstanceDAO.getById(er.getInstanceId()));
            List<ExperimentResult> results = res.get(identifier);
            if (results == null) {
                results = new ArrayList<ExperimentResult>();
                res.put(identifier, results);
            }
            results.add(er);
            er.setSaved();
        }
        rs.close();
        st.close();

        return res;
    }

    public static ArrayList<ExperimentResult> getBySolverConfigurationAndInstance(SolverConfiguration sc, Instance i) throws SQLException, StatusCodeNotInDBException, ResultCodeNotInDBException, Exception {
        ArrayList<ExperimentResult> v = new ArrayList<ExperimentResult>();
        PreparedStatement st = DatabaseConnector.getInstance().getConn().prepareStatement(
                selectQuery
                + "WHERE Instances_idInstance=? AND SolverConfig_idSolverConfig=?;");
        st.setInt(1, i.getId());
        st.setInt(2, sc.getId());
        ResultSet rs = st.executeQuery();
        while (rs.next()) {
            ExperimentResult er = getExperimentResultFromResultSet(rs);
            v.add(er);
            er.setSaved();
        }
        ExperimentResultHasPropertyDAO.assign(v, sc.getExperiment_id());
        rs.close();
        st.close();
        return v;
    }

    public static HashMap<SeedGroupExperimentIdInstanceId, Integer> getMaxRunsForSeedGroupsByExperimentIdsAndInstanceId(List<Integer> seed_groups, List<Integer> expIds, List<Integer> instanceIds) throws SQLException {
        HashMap<SeedGroupExperimentIdInstanceId, Integer> res = new HashMap<SeedGroupExperimentIdInstanceId, Integer>();
        if (expIds.isEmpty() || seed_groups.isEmpty() || instanceIds.isEmpty()) {
            return res;
        }
        Statement st = DatabaseConnector.getInstance().getConn().createStatement();

        ResultSet rs = st.executeQuery(
                "SELECT MAX(RUN), ExperimentResults.Experiment_idExperiment, seed_group, Instances_idInstance "
                + "FROM ExperimentResults JOIN SolverConfig ON (ExperimentResults.SolverConfig_idSolverConfig = SolverConfig.idSolverConfig) "
                + "WHERE ExperimentResults.Experiment_idExperiment IN (" + edacc.experiment.Util.getIdArray(expIds) + ") AND seed_group IN (" + edacc.experiment.Util.getIdArray(seed_groups) + ") AND Instances_idInstance IN (" + edacc.experiment.Util.getIdArray(instanceIds) + ") GROUP BY ExperimentResults.Experiment_idExperiment, seed_group, Instances_idInstance;");
        while (rs.next()) {
            int maxRuns = rs.getInt(1);
            int expId = rs.getInt(2);
            int seedGroup = rs.getInt(3);
            int instanceId = rs.getInt(4);
            res.put(new SeedGroupExperimentIdInstanceId(seedGroup, expId, instanceId), maxRuns);
        }
        rs.close();
        st.close();
        return res;
    }

    public static int getMaxRunForSeedGroupByExperimentIdAndInstanceId(int seed_group, int expId, int instanceId) throws SQLException {
        PreparedStatement st = DatabaseConnector.getInstance().getConn().prepareStatement(
                "SELECT MAX(run) FROM " + table + " "
                + "JOIN SolverConfig ON (ExperimentResults.SolverConfig_idSolverConfig = SolverConfig.idSolverConfig) "
                + "WHERE ExperimentResults.Experiment_idExperiment = ? AND seed_group = ? AND Instances_idInstance = ?;");
        st.setInt(2, seed_group);
        st.setInt(1, expId);
        st.setInt(3, instanceId);
        ResultSet rs = st.executeQuery();
        int res = -1;
        if (rs.next()) {
            res = rs.getInt(1);
        }
        if (rs.wasNull()) {
            res = -1;
        }
        rs.close();
        st.close();
        return res;
    }

    public static int getNumJobsBySolverConfigurationId(int idSolverConfig) throws SQLException {
        PreparedStatement st = DatabaseConnector.getInstance().getConn().prepareStatement(
                "SELECT COUNT(idJob) FROM " + table + " WHERE SolverConfig_idSolverConfig=?");
        st.setInt(1, idSolverConfig);
        ResultSet rs = st.executeQuery();
        int count = 0;
        if (rs.next()) {
            count = rs.getInt(1);
        }
        rs.close();
        st.close();
        return count;
    }

    public static void exportExperimentResults(final ZipOutputStream stream, Experiment experiment) throws IOException, SQLException, NoConnectionToDBException, InstanceNotInDBException, InterruptedException {
        stream.putNextEntry(new ZipEntry("experiment_" + experiment.getId() + ".jobs"));
        writeExperimentResultsToStream(new ObjectOutputStream(stream), ExperimentResultDAO.getAllByExperimentId(experiment.getId()));
    }

    public static void importExperimentResults(Tasks task, ZipFile file, Experiment fileExp, Experiment dbExp, HashMap<Integer, SolverConfiguration> solverConfigMap, HashMap<Integer, Instance> instanceMap) throws IOException, ClassNotFoundException, SQLException {
        task.setStatus("Reading jobs..");
        task.setTaskProgress(0.f);
        List<ExperimentResult> results = new LinkedList<ExperimentResult>();
        for (ExperimentResult er : readExperimentResultsFromFile(file, fileExp)) {
            ExperimentResult dbEr = new ExperimentResult(er);
            dbEr.setExperimentId(dbExp.getId());
            dbEr.setInstanceId(instanceMap.get(dbEr.getInstanceId()).getId());
            dbEr.setSolverConfigId(solverConfigMap.get(dbEr.getSolverConfigId()).getId());
            results.add(dbEr);
        }
        task.setStatus("Saving jobs..");
        ExperimentResultDAO.batchSave(results, task);
    }

    public static void writeExperimentResultsToStream(ObjectOutputStream stream, List<ExperimentResult> results) throws IOException, SQLException {
        for (ExperimentResult er : results) {
            stream.writeUnshared(er);
        }
    }

    public static List<ExperimentResult> readExperimentResultsFromFile(ZipFile file, Experiment experiment) throws IOException, ClassNotFoundException {
        ZipEntry entry = file.getEntry("experiment_" + experiment.getId() + ".jobs");
        ObjectInputStream ois = new ObjectInputStream(file.getInputStream(entry));
        List<ExperimentResult> res = new LinkedList<ExperimentResult>();
        ExperimentResult er;
        while ((er = readExperimentResultFromStream(ois)) != null) {
            res.add(er);
        }
        return res;
    }

    public static ExperimentResult readExperimentResultFromStream(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        try {
            return (ExperimentResult) stream.readUnshared();
        } catch (EOFException ex) {
            return null;
        }
    }

    public static class IdValue<T> {

        private int id;
        private T value;

        public IdValue(int id, T value) {
            this.id = id;
            this.value = value;
        }
    }

    public static class SeedGroupExperimentIdInstanceId {

        int seedGroup;
        int expId;
        int instanceId;

        public SeedGroupExperimentIdInstanceId(int seedGroup, int expId, int instanceId) {
            this.seedGroup = seedGroup;
            this.expId = expId;
            this.instanceId = instanceId;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final SeedGroupExperimentIdInstanceId other = (SeedGroupExperimentIdInstanceId) obj;
            if (this.seedGroup != other.seedGroup) {
                return false;
            }
            if (this.expId != other.expId) {
                return false;
            }
            if (this.instanceId != other.instanceId) {
                return false;
            }
            return true;
        }

        @Override
        public int hashCode() {
            int hash = 3;
            hash = 79 * hash + this.seedGroup;
            hash = 79 * hash + this.expId;
            hash = 79 * hash + this.instanceId;
            return hash;
        }
    }
}
