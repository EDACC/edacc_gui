package edacc.model;

import edacc.util.Pair;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.LinkedList;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

/**
 *
 * @author daniel
 */
public class ExperimentDAO {
    
    protected static final String table = "Experiment";
    protected static final String insertQuery = "INSERT INTO " + table + " (Name, Date, description, configurationExp, "
            + "priority, defaultCost, active, solverOutputPreserveFirst, solverOutputPreserveLast, watcherOutputPreserveFirst, "
            + "watcherOutputPreserveLast, verifierOutputPreserveFirst, verifierOutputPreserveLast, Cost_idCost) "
            + "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
    protected static final String updateQuery = "UPDATE " + table + " SET Name =?, Date =?, description =?, "
            + "configurationExp =?, priority =?, defaultCost=?, active=?,solverOutputPreserveFirst=?,solverOutputPreserveLast=?,"
            + "watcherOutputPreserveFirst=?,watcherOutputPreserveLast=?,verifierOutputPreserveFirst=?,"
            + "verifierOutputPreserveLast=?,Cost_idCost=? WHERE idExperiment=?";
    protected static final String deleteQuery = "DELETE FROM " + table + " WHERE idExperiment=?";
    private static final ObjectCache<Experiment> cache = new ObjectCache<Experiment>();

    /**
     * Experiment factory method, ensures that the created experiment is persisted and assigned an ID
     * so it can be referenced by related objects
     * @return new Experiment object
     */
    public static Experiment createExperiment(String name, Date date, String description, boolean configurationExp, Experiment.Cost defaultCost, Integer solverOutputPreserveFirst, Integer solverOutputPreserveLast, Integer watcherOutputPreserveFirst, Integer watcherOutputPreserveLast, Integer verifierOutputPreserveFirst, Integer verifierOutputPreserveLast, Integer idCost) throws SQLException {
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
        i.setDefaultCost(defaultCost);
        i.setSolverOutputPreserveFirst(solverOutputPreserveFirst);
        i.setSolverOutputPreserveLast(solverOutputPreserveLast);
        i.setWatcherOutputPreserveFirst(watcherOutputPreserveFirst);
        i.setWatcherOutputPreserveLast(watcherOutputPreserveLast);
        i.setVerifierOutputPreserveFirst(verifierOutputPreserveFirst);
        i.setVerifierOutputPreserveLast(verifierOutputPreserveLast);
        i.setIdCost(idCost);
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
            st.setInt(15, experiment.getId());
        } else {
            return;
        }
        st.setString(1, experiment.getName());
        st.setDate(2, experiment.getDate());
        st.setString(3, experiment.getDescription());
        st.setBoolean(4, experiment.isConfigurationExp());
        st.setInt(5, experiment.getPriority());
        st.setString(6, experiment.getDefaultCost().toString());
        st.setBoolean(7, experiment.isActive());
        if (experiment.getSolverOutputPreserveFirst() == null) {
            st.setNull(8, java.sql.Types.INTEGER);
            st.setNull(9, java.sql.Types.INTEGER);
        } else {
            st.setInt(8, experiment.getSolverOutputPreserveFirst());
            st.setInt(9, experiment.getSolverOutputPreserveLast());
        }
        if (experiment.getWatcherOutputPreserveFirst() == null) {
            st.setNull(10, java.sql.Types.INTEGER);
            st.setNull(11, java.sql.Types.INTEGER);            
        } else {
            st.setInt(10, experiment.getWatcherOutputPreserveFirst());
            st.setInt(11, experiment.getWatcherOutputPreserveLast());
        }
        if (experiment.getVerifierOutputPreserveFirst() == null) {
            st.setNull(12, java.sql.Types.INTEGER);
            st.setNull(13, java.sql.Types.INTEGER);            
        } else {
            st.setInt(12, experiment.getVerifierOutputPreserveFirst());
            st.setInt(13, experiment.getVerifierOutputPreserveLast());
        }
        if (experiment.getIdCost() == null) {
            st.setNull(14, java.sql.Types.INTEGER);
        } else {
            st.setInt(14, experiment.getIdCost());
        }
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
        try {
            i.setDefaultCost(Experiment.Cost.valueOf(rs.getString("defaultCost")));
        } catch (Exception ex) {
            i.setDefaultCost(Experiment.Cost.resultTime);
        }
        i.setActive(rs.getBoolean("active"));
        i.setSolverOutputPreserveFirst(rs.getInt("solverOutputPreserveFirst"));
        if (rs.wasNull()) {
            i.setSolverOutputPreserveFirst(null);
        }
        i.setSolverOutputPreserveLast(rs.getInt("solverOutputPreserveLast"));
        if (rs.wasNull()) {
            i.setSolverOutputPreserveLast(null);
        }
        i.setWatcherOutputPreserveFirst(rs.getInt("watcherOutputPreserveFirst"));
        if (rs.wasNull()) {
            i.setWatcherOutputPreserveFirst(null);
        }
        i.setWatcherOutputPreserveLast(rs.getInt("watcherOutputPreserveLast"));
        if (rs.wasNull()) {
            i.setWatcherOutputPreserveLast(null);
        }
        i.setVerifierOutputPreserveFirst(rs.getInt("verifierOutputPreserveFirst"));
        if (rs.wasNull()) {
            i.setVerifierOutputPreserveFirst(null);
        }
        i.setVerifierOutputPreserveLast(rs.getInt("verifierOutputPreserveLast"));
        if (rs.wasNull()) {
            i.setVerifierOutputPreserveLast(null);
        }
        i.setIdCost(rs.getInt("Cost_idCost"));
        if (rs.wasNull()) {
            i.setIdCost(null);
        }
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
        ResultSet rs = st.executeQuery("SELECT * FROM " + table + " ORDER BY idExperiment DESC");
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
        Pair<Integer, Boolean> p = new Pair<Integer, Boolean>(0, false);
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
    
    public static void exportExperiments(Tasks task, final ZipOutputStream stream, List<Experiment> experiments) throws IOException, SQLException, NoConnectionToDBException, InstanceNotInDBException, InterruptedException {
        
        int current = 1;
        for (Experiment exp : experiments) {
            task.setOperationName("Exporting experiment " + current + " / " + experiments.size());
            task.setStatus("Writing jobs..");
            task.setTaskProgress(current / (float) experiments.size());
            ExperimentResultDAO.exportExperimentResults(stream, exp);
            task.setStatus("Writing solver configurations..");
            SolverConfigurationDAO.exportSolverConfigurations(stream, exp);
            task.setStatus("Retrieving instance and configuration scenario informations..");
            exp.instances = ExperimentHasInstanceDAO.getExperimentHasInstanceByExperimentId(exp.getId());
            exp.scenario = ConfigurationScenarioDAO.getConfigurationScenarioByExperimentId(exp.getId());
            exp.verifierConfig = VerifierConfigurationDAO.getByExperimentId(exp.getId());
            current++;
        }
        task.setOperationName("Exporting experiments..");
        task.setTaskProgress(0.f);
        task.setStatus("Writing experiment informations..");
        stream.putNextEntry(new ZipEntry("experiments.edacc"));
        writeExperimentsToStream(new ObjectOutputStream(stream), experiments);
        for (Experiment exp : experiments) {
            exp.instances = null;
            exp.scenario = null;
            exp.verifierConfig = null;
        }
        task.setStatus("Done.");
    }
    
    public static void importExperiments(Tasks task, ZipFile file, List<Experiment> experiments, HashMap<Integer, SolverBinaries> solverBinaryMap, HashMap<Integer, Parameter> parameterMap, HashMap<Integer, Instance> instanceMap) throws SQLException, IOException, ClassNotFoundException {
        
        int current = 1;
        for (Experiment experiment : experiments) {
            task.setTaskProgress(0.f);
            task.setOperationName("Importing experiment " + current + " / " + experiments.size());
            task.setStatus("Saving experiment..");
            Experiment dbExperiment = new Experiment(experiment);
            ExperimentDAO.save(dbExperiment);
            task.setStatus("Saving instances..");
            int currentInstance = 1;
            for (ExperimentHasInstance ehi : experiment.instances) {
                task.setTaskProgress(currentInstance / (float) experiment.instances.size());
                ExperimentHasInstanceDAO.createExperimentHasInstance(dbExperiment.getId(), instanceMap.get(ehi.getInstances_id()).getId());
                currentInstance++;
            }
            task.setTaskProgress(0.f);
            
            if (experiment.scenario != null) {
                task.setStatus("Saving configuration scenario..");
                ConfigurationScenario dbScenario = new ConfigurationScenario(experiment.scenario);
                for (InstanceSeed is : dbScenario.getCourse().getInstanceSeedList()) {
                    is.instance = InstanceDAO.getById(instanceMap.get(is.instance.getId()).getId());
                }
                for (ConfigurationScenarioParameter param : dbScenario.getParameters()) {
                    param.setParameter(parameterMap.get(param.getParameter().getId()));
                    param.setIdParameter(param.getParameter().getId());
                }
                dbScenario.setIdExperiment(dbExperiment.getId());
                dbScenario.setIdSolverBinary(solverBinaryMap.get(dbScenario.getIdSolverBinary()).getId());
                
                ConfigurationScenarioDAO.save(dbScenario);
            }
            HashMap<Integer, SolverConfiguration> solverConfigMap = SolverConfigurationDAO.importSolverConfigurations(task, file, experiment, dbExperiment, solverBinaryMap, parameterMap);
            
            ExperimentResultDAO.importExperimentResults(task, file, experiment, dbExperiment, solverConfigMap, instanceMap);
            current++;
        }
    }
    
    public static void writeExperimentsToStream(ObjectOutputStream stream, List<Experiment> experiments) throws IOException, SQLException {
        for (Experiment exp : experiments) {
            stream.writeUnshared(exp);
        }
    }
    
    public static List<Experiment> readExperimentsFromFile(ZipFile file) throws IOException, ClassNotFoundException {
        ZipEntry entry = file.getEntry("experiments.edacc");
        if (entry == null) {
            throw new IOException("Invalid file.");
        }
        ObjectInputStream ois = new ObjectInputStream(file.getInputStream(entry));
        List<Experiment> res = new LinkedList<Experiment>();
        Experiment exp;
        while ((exp = readExperimentFromStream(ois)) != null) {
            res.add(exp);
        }
        return res;
    }
    
    public static Experiment readExperimentFromStream(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        try {
            return (Experiment) stream.readUnshared();
        } catch (EOFException ex) {
            return null;
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
