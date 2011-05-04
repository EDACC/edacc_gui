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
    public static final int COL_NUMRUNS = 3;
    public static final int COL_DESCRIPTION = 4;
    public static final int COL_STATUS = 5;
    public static final int COL_PRIORITY = 6;
    public static final int COL_ACTIVE = 7;
    private String[] columns = {"ID", "Name", "Date", "Number of jobs", "Description", "Status", "Priority", "Active"};
    private ArrayList<Experiment> experiments;
    private String[] status;

    public ExperimentTableModel() {
        this.experiments = new ArrayList<Experiment>();
    }

    public void setExperiments(ArrayList<Experiment> experiments) {
        this.experiments = experiments;
        status = new String[experiments.size()];
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
            case COL_NUMRUNS:
                return experiments.get(rowIndex).getNumJobs();
            case COL_DESCRIPTION:
                return experiments.get(rowIndex).getDescription();
            case COL_STATUS:
                return status[rowIndex] == null ? "none" : status[rowIndex];
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

    public void setStatusAt(int rowIndex, String s) {
        status[rowIndex] = s;
        this.fireTableCellUpdated(rowIndex, COL_STATUS);
    }

    public Experiment getExperimentAt(int rowIndex) {
        return experiments.get(rowIndex);
    }
}
