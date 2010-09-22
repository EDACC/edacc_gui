package edacc.experiment;

import java.sql.SQLException;
import java.util.LinkedList;
import javax.swing.table.AbstractTableModel;
import java.util.Vector;
import edacc.model.ExperimentResult;
import edacc.model.ExperimentResultStatus;
import edacc.model.Instance;
import edacc.model.InstanceDAO;
import edacc.model.Parameter;
import edacc.model.ParameterDAO;
import edacc.model.ParameterInstance;
import edacc.model.ParameterInstanceDAO;
import edacc.model.Solver;
import edacc.model.SolverConfiguration;
import edacc.model.SolverConfigurationDAO;
import edacc.model.SolverDAO;
import java.util.HashMap;
import java.util.HashSet;
import javax.swing.SwingUtilities;

/**
 * In this class rowIndexes are always the visible rowIndexes and columnIndexes
 * are always the visible column indexes.
 * @author daniel
 */
public class ExperimentResultsBrowserTableModel extends AbstractTableModel {
    public static int COL_ID = 0;
    public static int COL_SOLVER = 1;
    public static int COL_PARAMETERS = 2;
    public static int COL_INSTANCE = 3;
    public static int COL_RUN = 4;
    public static int COL_TIME = 5;
    public static int COL_SEED = 6;
    public static int COL_STATUS = 7;
    public static int COL_RESULTCODE = 8;
    private Vector<ExperimentResult> jobs;
    private String[] columns = {"ID", "Solver", "Parameters", "Instance", "Run", "Time", "Seed", "Status", "Result Code"};
    private boolean[] visible = {true, true, true, true, true, true, true, true, true};
    private HashMap<Integer, Vector<ParameterInstance>> parameterInstances;

    /**
     * Returns the job id
     * @param row
     * @return
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
            return SolverDAO.getById(sc.getSolver_id());
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
     * @return
     */
    public Integer getRun(int row) {
        return jobs.get(row).getRun();
    }

    /**
     * Returns the seed
     * @param row
     * @return
     */
    public Integer getSeed(int row) {
        return jobs.get(row).getSeed();
    }

    /**
     * Returns the status
     * @param row
     * @return
     */
    public ExperimentResultStatus getStatus(int row) {
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
    public Vector<ParameterInstance> getParameters(int row) {
        try {
            SolverConfiguration sc = SolverConfigurationDAO.getSolverConfigurationById(jobs.get(row).getSolverConfigId());
            Vector<ParameterInstance> params = parameterInstances.get(sc.getId());
            if (params == null) {
                params = ParameterInstanceDAO.getBySolverConfigId(sc.getId());
                parameterInstances.put(sc.getId(), params);
            }
            return params;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Transforms the parameters obtained by getParameters to a string
     * @param row
     * @return
     */
    public String getParameterString(int row) {
        try {
            Vector<ParameterInstance> params = getParameters(row);
            if (params == null) {
                return "";
            }
            String paramString = "";

            for (ParameterInstance param : params) {
                Parameter solverParameter = ParameterDAO.getById(param.getParameter_id());
                if (solverParameter.getHasValue()) {
                    paramString += solverParameter.getPrefix() + " " + param.getValue();
                } else {
                    paramString += solverParameter.getPrefix() + " ";
                }

                if (params.lastElement() != param) {
                    paramString += " ";
                }
            }
            return paramString;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Sets the jobs for that model
     * @param jobs
     * @throws SQLException
     */
    public void setJobs(final Vector<ExperimentResult> jobs) throws SQLException {

        /*   boolean fullUpdate = false;
        if (this.jobs == null || jobs == null || this.jobs.size() != jobs.size()) {
        fullUpdate = true;
        }
        LinkedList<Integer> changedRows = new LinkedList<Integer>();
        if (!fullUpdate) {
        // here: this.jobs.size() == jobs.size() => we only want to update the rows that have changed (GUI)
        HashMap<ExperimentResult, ExperimentResult> jobMap = new HashMap<ExperimentResult, ExperimentResult>();
        for (ExperimentResult job: jobs) {
        jobMap.put(job, job);
        }
        for (ExperimentResult job: this.jobs) {
        if (!jobMap.containsKey(job)) {
        // there is a job we don't have in our local table, so we replace
        // this.jobs with jobs => fullUpdate
        fullUpdate = true;
        }
        }
        if (!fullUpdate) {
        // update the changed jobs
        for (int i = 0; i < this.jobs.size(); i++) {
        ExperimentResult j1 = this.jobs.get(i);
        ExperimentResult j2 = jobMap.get(j1);
        if (j1.getStatus() != j2.getStatus() || j1.getTime() != j2.getTime() || j1.getMaxTimeLeft() != j2.getMaxTimeLeft()) {
        j1.setStatus(j2.getStatus());
        j1.setTime(j2.getTime());
        j1.setMaxTimeLeft(j2.getMaxTimeLeft());
        changedRows.add(i);
        }
        }
        }
        }*/
        //  if (fullUpdate) {

        Runnable updateTable = new Runnable() {

            @Override
            public void run() {
                ExperimentResultsBrowserTableModel.this.jobs = jobs;
                parameterInstances = new HashMap<Integer, Vector<ParameterInstance>>();
                ExperimentResultsBrowserTableModel.this.fireTableDataChanged();
            }
        };
        if (SwingUtilities.isEventDispatchThread()) {
            // already in EDT
            updateTable.run();
        } else {
            // we have to run this in the EDT, otherwise sync exceptions
            try {
                SwingUtilities.invokeAndWait(updateTable);
            } catch (Exception _) {
            }
        }

        //  } else {
        //      fireTableDataChanged();
        // for (Integer changedRow: changedRows) {
        //     fireTableRowsUpdated(changedRow, changedRow);
        // }
        //  }


        //  } else {
        //      fireTableDataChanged();

        // for (Integer changedRow: changedRows) {
        //     fireTableRowsUpdated(changedRow, changedRow);
        // }
        //  }
    }

    public int getIndexForColumn(int col) {
        for (int i = 0; i < visible.length; i++) {
            if (visible[i]) {
                col--;
            }
            if (col == -1) {
                return i;
            }
        }
        return 0;
    }

    /**
     * Returns an array of all column names, not only the visible ones.
     * @return
     */
    public String[] getAllColumnNames() {
        return columns;
    }

    @Override
    public int getRowCount() {
        return jobs == null ? 0 : jobs.size();
    }

    @Override
    public int getColumnCount() {
        int res = 0;
        for (int i = 0; i < visible.length; i++) {
            if (visible[i]) {
                res++;
            }
        }
        return res;
    }

    @Override
    public String getColumnName(int col) {
        return columns[getIndexForColumn(col)];
    }

    @Override
    public Class getColumnClass(int col) {
        if (getRowCount() == 0) {
            return String.class;
        } else {
            return getValueAt(0, col).getClass();
        }
    }

    /**
     * Sets the column visibility.
     * @param visible a boolean array - length must equal getAllCoulumnNames().length or this method does nothing.
     */
    public void setColumnVisibility(boolean[] visible) {
        if (columns.length != visible.length) {
            return;
        }
        this.visible = visible;
        this.fireTableStructureChanged();
    }

    /**
     * Returns the visibility array
     * @return
     */
    public boolean[] getColumnVisibility() {
        return visible;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        if (rowIndex < 0 || rowIndex >= getRowCount()) {
            return null;
        }
        ExperimentResult j = jobs.get(rowIndex);

        if (columnIndex != -1) {
            columnIndex = getIndexForColumn(columnIndex);
        }
        switch (columnIndex) {
            case 0:
                return j.getId();
            case 1:
                Solver solver = getSolver(rowIndex);
                return solver == null ? "" : solver.getName();
            case 2:
                return getParameterString(rowIndex);
            case 3:
                Instance instance = getInstance(rowIndex);
                return instance == null ? "" : instance.getName();
            case 4:
                return j.getRun();
            case 5:
                return j.getResultTime();
            case 6:
                return j.getSeed();
            case 7:
                String status = j.getStatus().toString();
                if (j.getStatus() == ExperimentResultStatus.RUNNING && j.getRunningTime() != null) {
                    status += " (" + j.getRunningTime() + ")";
                }
                return status;
            case 8:
                return j.getResultCode().toString();
            default:
                return "";
        }
    }

    /**
     * Returns all disjunct instance names which are currently in that model
     * @return
     */
    public Vector<String> getInstances() {
        Vector<String> res = new Vector<String>();
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
     */
    public Vector<ExperimentResultStatus> getStatusEnums() {
        Vector<ExperimentResultStatus> res = new Vector<ExperimentResultStatus>();
        HashSet<ExperimentResultStatus> tmp = new HashSet<ExperimentResultStatus>();
        for (int i = 0; i < getRowCount(); i++) {
            if (!tmp.contains(getStatus(i))) {
                tmp.add(getStatus(i));
            }
        }
        res.addAll(tmp);
        return res;
    }

    /**
     * Returns all disjunct solver names which are currently in that model
     */
    public Vector<String> getSolvers() {
        Vector<String> res = new Vector<String>();
        HashSet<String> tmp = new HashSet<String>();
        for (int i = 0; i < getRowCount(); i++) {
            if (!tmp.contains(getSolver(i).getName())) {
                tmp.add(getSolver(i).getName());
            }
        }
        res.addAll(tmp);
        return res;
    }

    public Vector<ExperimentResult> getJobs() {
        return jobs;
    }

    public int getJobsCount() {
        return jobs.size();
    }
    
    public int getJobsCount(ExperimentResultStatus status) {
        int res = 0;
        for (ExperimentResult j : jobs) {
            if (j.getStatus().equals(status)) {
                res++;
            }
        }
        return res;
    }
}
