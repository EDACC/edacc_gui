package edacc.experiment;

import edacc.satinstances.ConvertException;
import java.sql.SQLException;
import java.util.LinkedList;
import edacc.model.ExperimentResult;
import edacc.model.ExperimentResultHasProperty;
import edacc.model.StatusCode;
import edacc.model.GridQueue;
import edacc.model.GridQueueDAO;
import edacc.model.Instance;
import edacc.model.InstanceDAO;
import edacc.model.InstanceHasProperty;
import edacc.model.ParameterInstance;
import edacc.model.ParameterInstanceDAO;
import edacc.model.Solver;
import edacc.model.SolverConfiguration;
import edacc.model.SolverDAO;
import edacc.model.Property;
import edacc.model.PropertyDAO;
import edacc.model.PropertyType;
import edacc.model.SolverConfigurationDAO;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.HashMap;
import java.util.HashSet;
import javax.swing.SwingUtilities;

/**
 * In this class rowIndexes are always the visible rowIndexes and columnIndexes
 * are always the visible column indexes.
 * @author daniel, simon
 */
public class ExperimentResultsBrowserTableModel extends ThreadSafeDefaultTableModel {

    // constants for the columns
    /** The index of the ID column */
    public static final int COL_ID = 0;
    /** The index of the priority column */
    public static final int COL_PRIORITY = 1;
    /** The index of the compute queue column */
    public static final int COL_COMPUTEQUEUE = 2;
    /** The index of the compute node column */
    public static final int COL_COMPUTENODE = 3;
    /** The index of the compute node ip column */
    public static final int COL_COMPUTENODEIP = 4;
    /** The index of the solver column */
    public static final int COL_SOLVER = 5;
    /** The index of the solver configuration column */
    public static final int COL_SOLVERCONFIGURATION = 6;
    /** The index of the parameters column */
    public static final int COL_PARAMETERS = 7;
    /** The index of the instance column */
    public static final int COL_INSTANCE = 8;
    /** The index of the run column */
    public static final int COL_RUN = 9;
    /** The index of the time column */
    public static final int COL_TIME = 10;
    /** The index of the seed column */
    public static final int COL_SEED = 11;
    /** The index of the status column */
    public static final int COL_STATUS = 12;
    /** The index of the runtime column */
    public static final int COL_RUNTIME = 13;
    /** The index of the result code column */
    public static final int COL_RESULTCODE = 14;
    /** The index of the cpu time limit column */
    public static final int COL_CPUTIMELIMIT = 15;
    /** The index of the wall clock limit column */
    public static final int COL_WALLCLOCKLIMIT = 16;
    /** The index of the memory limit column */
    public static final int COL_MEMORYLIMIT = 17;
    /** The index of the stack size limit column */
    public static final int COL_STACKSIZELIMIT = 18;
    /** The index of the output size limit column */
    public static final int COL_OUTPUTSIZELIMIT = 19;
    /** The index of the solver output column */
    public static final int COL_SOLVER_OUTPUT = 20;
    /** The index of the launcher output column */
    public static final int COL_LAUNCHER_OUTPUT = 21;
    /** The index of the watcher output column */
    public static final int COL_WATCHER_OUTPUT = 22;
    /** The index of the verifier output column */
    public static final int COL_VERIFIER_OUTPUT = 23;
    /** The index of the first property column */
    public static final int COL_PROPERTY = 24;
    private ArrayList<ExperimentResult> jobs;
    // the constant columns
    private String[] CONST_COLUMNS = {"ID", "Priority", "Compute Queue", "Compute Node", "Compute Node IP", "Solver", "Solver Configuration", "Parameters", "Instance", "Run", "Time", "Seed", "Status", "Run time", "Result Code", "CPU Time Limit", "Wall Clock Time Limit", "Memory Limit", "Stack Size Limit", "Output Size Limit", "Solver Output", "Launcher Output", "Watcher Output", "Verifier Output"};
    /** the default visibility of each column */
    public static boolean[] DEFAULT_VISIBILITY = {false, false, true, false, false, true, true, true, true, true, true, true, true, true, true, false, false, false, false, false, false, false, false, false};
    private String[] columns;
    private ArrayList<Property> properties;
    private HashMap<Integer, GridQueue> gridQueues;
    private HashMap<Integer, String> parameters;
    private int firstInstancePropertyColumn;

    /** Creates a new experiment result browser table model */
    public ExperimentResultsBrowserTableModel() {
        columns = new String[CONST_COLUMNS.length];
        System.arraycopy(CONST_COLUMNS, 0, columns, 0, columns.length);
    }

    /** 
     * Updates the properties. 
     * @return true, iff something has changed (new properties or properties were removed)
     */
    public boolean updateProperties() {
        ArrayList<Property> tmp = new ArrayList<Property>();
        try {
            tmp.addAll(PropertyDAO.getAllResultProperties());
            firstInstancePropertyColumn = COL_PROPERTY + tmp.size();
            tmp.addAll(PropertyDAO.getAllInstanceProperties());
            for (int i = tmp.size() - 1; i >= 0; i--) {
                if (tmp.get(i).isMultiple()) {
                    tmp.remove(i);
                }
            }
        } catch (Exception e) {
            if (edacc.ErrorLogger.DEBUG) {
                e.printStackTrace();
            }
        }
        if (!tmp.equals(properties)) {
            properties = tmp;
            columns = java.util.Arrays.copyOf(columns, CONST_COLUMNS.length + properties.size());
            int j = 0;
            for (int i = CONST_COLUMNS.length; i < columns.length; i++) {
                columns[i] = properties.get(j).getName();
                j++;
            }
            this.fireTableStructureChanged();
            return true;
        }
        return false;
    }

    /**
     * Returns the job id
     * @param row
     * @return the id of the job
     */
    public Integer getId(int row) {
        return jobs.get(row).getId();
    }

    /**
     * Returns the solver
     * @param row
     * @return null if there was an error
     */
    public Solver getSolver(int row) {
        try {
            SolverConfiguration sc = SolverConfigurationDAO.getSolverConfigurationById(jobs.get(row).getSolverConfigId());
            return SolverDAO.getById(sc.getSolverBinary().getIdSolver());
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Returns the instance
     * @param row
     * @return null if there was an error
     */
    public Instance getInstance(int row) {
        try {
            return InstanceDAO.getById(jobs.get(row).getInstanceId());
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Returns the run
     * @param row
     * @return the run
     */
    public Integer getRun(int row) {
        return jobs.get(row).getRun();
    }

    /**
     * Returns the seed
     * @param row
     * @return the seed
     */
    public Integer getSeed(int row) {
        return jobs.get(row).getSeed();
    }

    /**
     * Returns the status
     * @param row
     * @return the status
     */
    public StatusCode getStatus(int row) {
        if (row < 0 || row >= getRowCount()) {
            return null;
        }
        return jobs.get(row).getStatus();
    }

    /**
     * Returns all parameter instances for that job
     * @param row
     * @return null, if there was an error
     */
    public ArrayList<ParameterInstance> getParameters(int row) {
        try {
            SolverConfiguration sc = SolverConfigurationDAO.getSolverConfigurationById(jobs.get(row).getSolverConfigId());
            return ParameterInstanceDAO.getBySolverConfig(sc);
        } catch (Exception e) {
            return null;
        }
    }

    /** 
     * Returns the experiment result represented by the row at the specified row index
     * @param row the row index
     * @return the experiment result
     */
    public ExperimentResult getExperimentResult(int row) {
        return jobs.get(row);
    }

    /**
     * Sets the jobs for that model
     * @param jobs
     * @throws SQLException
     */
    public void setJobs(final ArrayList<ExperimentResult> jobs) throws SQLException {

        Runnable updateTable = new Runnable() {

            @Override
            public void run() {
                ExperimentResultsBrowserTableModel.this.jobs = jobs;
                if (jobs != null) {
                    gridQueues = new HashMap<Integer, GridQueue>();
                    parameters = new HashMap<Integer, String>();
                    try {
                        ArrayList<GridQueue> queues = GridQueueDAO.getAll();
                        for (GridQueue q : queues) {
                            gridQueues.put(q.getId(), q);
                        }
                    } catch (Exception e) {
                    }
                }
                ExperimentResultsBrowserTableModel.this.fireTableDataChanged();
            }
        };
        if (SwingUtilities.isEventDispatchThread()) {
            // already in EDT
            updateTable.run();
        } else {
            // we have to run this in the EDT, otherwise sync exceptions
            SwingUtilities.invokeLater(updateTable);
        }
    }

    @Override
    public int getRowCount() {
        return jobs == null ? 0 : jobs.size();
    }

    @Override
    public int getColumnCount() {
        return columns.length;
    }

    @Override
    public String getColumnName(int col) {
        return columns[col];
    }

    @Override
    public Class getColumnClass(int col) {
        if (getRowCount() == 0) {
            return String.class;
        } else {
            if (col >= COL_PROPERTY) {
                int propertyIdx = col - COL_PROPERTY;
                if (propertyIdx >= properties.size()) {
                    return String.class;
                }
                if (properties.get(propertyIdx).getPropertyValueType() == null) {
                    return String.class;
                }
                return properties.get(propertyIdx).getPropertyValueType().getJavaType();
            } else {
                if (getValueAt(0, col) == null) {
                    return String.class;
                } else {
                    return getValueAt(0, col).getClass();
                }
            }
        }
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        if (rowIndex < 0 || rowIndex >= getRowCount()) {
            return null;
        }
        ExperimentResult j = jobs.get(rowIndex);
        switch (columnIndex) {
            case COL_ID:
                return j.getId();
            case COL_PRIORITY:
                return j.getPriority();
            case COL_COMPUTEQUEUE:
                GridQueue q = gridQueues.get(j.getComputeQueue());
                return q == null ? "none" : q.getName();
            case COL_COMPUTENODE:
                return j.getComputeNode() == null ? "none" : j.getComputeNode();
            case COL_COMPUTENODEIP:
                return j.getComputeNodeIP() == null ? "none" : j.getComputeNodeIP();
            case COL_SOLVER:
                Solver solver = getSolver(rowIndex);
                return solver == null ? "" : solver.getName();
            case COL_SOLVERCONFIGURATION:
                try {
                    return SolverConfigurationDAO.getSolverConfigurationById(j.getSolverConfigId()).getName();
                } catch (SQLException ex) {
                    return "-";
                }
            case COL_PARAMETERS:
                solver = getSolver(rowIndex);
                if (solver == null) return "";
                String params = parameters.get(j.getSolverConfigId());
                if (params == null) {
                    params = Util.getParameterString(getParameters(rowIndex), solver);
                    parameters.put(j.getSolverConfigId(), params);
                }
                return params;
            case COL_INSTANCE:
                Instance instance = getInstance(rowIndex);
                return instance == null ? "" : instance.getName();
            case COL_RUN:
                return j.getRun();
            case COL_TIME:
                return j.getResultTime();
            case COL_SEED:
                return j.getSeed();
            case COL_STATUS:
                return j.getStatus().toString();
            case COL_RUNTIME:
                if (j.getStatus() == StatusCode.RUNNING) {
                    int hours = j.getRunningTime() / 3600;
                    int minutes = (j.getRunningTime() / 60) % 60;
                    int seconds = j.getRunningTime() % 60;
                    return new Formatter().format("%02d:%02d:%02d", hours, minutes, seconds);
                }
                return null;
            case COL_RESULTCODE:
                return j.getResultCode().toString();
            case COL_CPUTIMELIMIT:
                return j.getCPUTimeLimit();
            case COL_WALLCLOCKLIMIT:
                return j.getWallClockTimeLimit();
            case COL_MEMORYLIMIT:
                return j.getMemoryLimit();
            case COL_STACKSIZELIMIT:
                return j.getStackSizeLimit();
            case COL_OUTPUTSIZELIMIT:
                if (j.getOutputSizeLimitFirst() == -1 || j.getOutputSizeLimitLast() == -1) {
                    return "none";
                } else {
                    return "Preserve first " + j.getOutputSizeLimitFirst() + " MB and last " + j.getOutputSizeLimitLast() + " MB";
                }
            case COL_SOLVER_OUTPUT:
                return "view";
            case COL_LAUNCHER_OUTPUT:
                return "view";
            case COL_WATCHER_OUTPUT:
                return "view";
            case COL_VERIFIER_OUTPUT:
                return "view";
            default:
                int propertyIdx = columnIndex - COL_PROPERTY;
                if (properties.size() <= propertyIdx) {
                    return null;
                }
                Property prop = properties.get(propertyIdx);
                if (prop.getType().equals(PropertyType.ResultProperty)) {
                    ExperimentResultHasProperty erp = j.getPropertyValues().get(prop.getId());
                    if (erp != null && !erp.getValue().isEmpty()) {
                        try {
                            if (prop.getPropertyValueType() == null) {
                                return erp.getValue().get(0);
                            }
                            return prop.getPropertyValueType().getJavaTypeRepresentation(erp.getValue().get(0));
                        } catch (ConvertException ex) {
                            return null;
                        }
                    } else {
                        return null;
                    }
                } else if (prop.getType().equals(PropertyType.InstanceProperty)) {
                    InstanceHasProperty ihp = null;
                    try {
                        ihp = InstanceDAO.getById(j.getInstanceId()).getPropertyValues().get(prop.getId());
                    } catch (Exception e) {
                    }
                    if (ihp == null) {
                        return null;
                    } else {
                        try {
                            return prop.getPropertyValueType().getJavaTypeRepresentation(ihp.getValue());
                        } catch (ConvertException ex) {
                            return null;
                        }
                    }
                } else {
                    return null;
                }
        }
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }

    /**
     * Returns the first column index which is a instance property column
     * @return the first instance property column
     */
    public int getFirstInstancePropertyColumn() {
        return firstInstancePropertyColumn;
    }

    /**
     * Returns all disjunct instance names which are currently in that model
     * @return arraylist with the instance names
     */
    public ArrayList<String> getInstances() {
        ArrayList<String> res = new ArrayList<String>();
        if (getRowCount() == 0) {
            return res;
        }
        int experimentId = jobs.get(0).getExperimentId();
        try {
            LinkedList<Instance> instances = InstanceDAO.getAllByExperimentId(experimentId);
            HashSet<String> tmp = new HashSet<String>();
            for (Instance i : instances) {
                if (!tmp.contains(i.getName())) {
                    tmp.add(i.getName());
                }
            }
            res.addAll(tmp);
            return res;
        } catch (Exception ex) {
            return res;
        }
    }

    /**
     * Returns all disjunct status codes which are currently in that model
     * @return 
     */
    public ArrayList<StatusCode> getStatusEnums() {
        ArrayList<StatusCode> res = new ArrayList<StatusCode>();
        HashSet<StatusCode> tmp = new HashSet<StatusCode>();
        for (int i = 0; i
                < getRowCount(); i++) {
            if (!tmp.contains(getStatus(i))) {
                tmp.add(getStatus(i));
            }
        }
        res.addAll(tmp);
        return res;
    }

    /**
     * Returns all disjunct solver names which are currently in that model
     * @return 
     */
    public ArrayList<String> getSolvers() {
        ArrayList<String> res = new ArrayList<String>();
        HashSet<String> tmp = new HashSet<String>();
        for (int i = 0; i
                < getRowCount(); i++) {
            if (!tmp.contains(getSolver(i).getName())) {
                tmp.add(getSolver(i).getName());
            }
        }
        res.addAll(tmp);
        return res;
    }

    /**
     * Returns the jobs in this model.
     * @return <code>ArrayList</code> of the jobs
     */
    public ArrayList<ExperimentResult> getJobs() {
        return jobs;
    }

    /**
     * Returns the jobs count.
     * @return 
     */
    public int getJobsCount() {
        return jobs == null ? 0 : jobs.size();
    }

    /**
     * Returns the jobs count with the specified status code.
     * @param status the status code
     * @return 
     */
    public int getJobsCount(StatusCode status) {
        if (jobs == null) {
            return 0;
        }
        int res = 0;
        for (ExperimentResult j : jobs) {
            if (j.getStatus().equals(status)) {
                res++;
            }
        }
        return res;
    }

    /**
     * Returns the default visibility.
     * @return 
     */
    public boolean[] getDefaultVisibility() {
        boolean[] res = new boolean[columns.length];
        System.arraycopy(DEFAULT_VISIBILITY, 0, res, 0, DEFAULT_VISIBILITY.length);
        for (int i = DEFAULT_VISIBILITY.length; i < res.length; i++) {
            res[i] = false;
        }
        return res;
    }
}
