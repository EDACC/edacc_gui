package edacc.experiment;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import edacc.model.Experiment;


public class ExperimentTableModel extends AbstractTableModel {
    public static final int COL_ID = 0;
    public static final int COL_NAME = 1;
    public static final int COL_DATE = 2;
    public static final int COL_NUMRUNS = 3;
    public static final int COL_DESCRIPTION = 4;
    private String[] columns = {"ID", "Name", "Date", "Number of runs", "Description"};
    private ArrayList<Experiment> experiments;

    public ExperimentTableModel() {
        this.experiments = new ArrayList<Experiment>();
    }

    public void setExperiments(ArrayList<Experiment> experiments) {
        this.experiments = experiments;
        this.fireTableDataChanged();
    }

    @Override
    public int getRowCount() {
        return experiments==null?0:experiments.size();
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
        if (experiments==null || experiments.isEmpty()) {
            return String.class;
        }
        return getValueAt(0, col).getClass();
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        if (rowIndex > experiments.size()-1) {
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
                return experiments.get(rowIndex).getNumRuns();
            case COL_DESCRIPTION:
                return experiments.get(rowIndex).getDescription();
            default:
                return "";
        }
    }

    public Experiment getExperimentAt(int rowIndex) {
        return experiments.get(rowIndex);
    }
}
