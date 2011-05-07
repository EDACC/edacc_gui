package edacc.experiment;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import edacc.model.Experiment;
import edacc.model.ExperimentDAO;
import java.sql.SQLException;

public class ExperimentTableModel extends AbstractTableModel {

    public static final int COL_ID = 0;
    public static final int COL_NAME = 1;
    public static final int COL_DATE = 2;
    public static final int COL_DESCRIPTION = 3;
    public static final int COL_NUMRUNS = 4;
    public static final int COL_NOTSTARTED = 5;
    public static final int COL_RUNNING = 6;
    public static final int COL_FINISHED = 7;
    public static final int COL_FAILED = 8;
    public static final int COL_PRIORITY = 9;
    public static final int COL_ACTIVE = 10;
    private String[] columns = {"ID", "Name", "Date", "Description", "Number of jobs", "Not started", "Running", "Finished", "Failed", "Priority", "Active"};
    private ArrayList<Experiment> experiments;
    private Integer[] running;
    private Integer[] finished;
    private Integer[] failed;
    private Integer[] not_started;

    public ExperimentTableModel() {
        this.experiments = new ArrayList<Experiment>();
    }

    public void setExperiments(ArrayList<Experiment> experiments) {
        this.experiments = experiments;
        if (experiments != null) {
            running = new Integer[experiments.size()];
            finished = new Integer[experiments.size()];
            failed = new Integer[experiments.size()];
            not_started = new Integer[experiments.size()];
        } else {
            running = null;
            finished = null;
            failed = null;
            not_started = null;
        }
        this.fireTableDataChanged();
    }

    @Override
    public int getRowCount() {
        return experiments == null ? 0 : experiments.size();
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
        if (col == COL_RUNNING || col == COL_FINISHED || col == COL_FAILED || col == COL_NOTSTARTED) {
            return Integer.class;
        }
        if (experiments == null || experiments.isEmpty()) {
            return String.class;
        }
        return getValueAt(0, col).getClass();
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        if (rowIndex > experiments.size() - 1) {
            return null;
        }
        switch (columnIndex) {
            case COL_ID:
                return experiments.get(rowIndex).getId();
            case COL_NAME:
                return experiments.get(rowIndex).getName();
            case COL_DATE:
                return experiments.get(rowIndex).getDate();
            case COL_DESCRIPTION:
                return experiments.get(rowIndex).getDescription();
            case COL_NUMRUNS:
                return experiments.get(rowIndex).getNumJobs();
            case COL_NOTSTARTED:
                return not_started[rowIndex];
            case COL_RUNNING:
                return running[rowIndex];
            case COL_FINISHED:
                return finished[rowIndex];
            case COL_FAILED:
                return failed[rowIndex];
            case COL_PRIORITY:
                return experiments.get(rowIndex).getPriority();
            case COL_ACTIVE:
                return experiments.get(rowIndex).isActive();
            default:
                return "";
        }
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return columnIndex == COL_ACTIVE || columnIndex == COL_PRIORITY;
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        if (columnIndex == COL_ACTIVE) {
            experiments.get(rowIndex).setActive((Boolean) aValue);
            this.fireTableCellUpdated(rowIndex, columnIndex);
        } else if (columnIndex == COL_PRIORITY) {
            experiments.get(rowIndex).setPriority((Integer) aValue);
            this.fireTableCellUpdated(rowIndex, columnIndex);
        }
        try {
            ExperimentDAO.save(experiments.get(rowIndex));
        } catch (SQLException ex) {
            // TODO: error
        }
    }

    public void setRunningAt(int rowIndex, Integer value) {
        running[rowIndex] = value;
        this.fireTableCellUpdated(rowIndex, COL_RUNNING);
    }

    public void setFinishedAt(int rowIndex, Integer value) {
        finished[rowIndex] = value;
        this.fireTableCellUpdated(rowIndex, COL_FINISHED);
    }

    public void setFailedAt(int rowIndex, Integer value) {
        failed[rowIndex] = value;
        this.fireTableCellUpdated(rowIndex, COL_FAILED);
    }
    
    public void setNotStartedAt(int rowIndex, Integer value) {
        not_started[rowIndex] = value;
        this.fireTableCellUpdated(rowIndex, COL_NOTSTARTED);
    }

    public Experiment getExperimentAt(int rowIndex) {
        return experiments.get(rowIndex);
    }
}
