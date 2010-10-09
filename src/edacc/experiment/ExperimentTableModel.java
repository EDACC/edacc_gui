package edacc.experiment;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import edacc.model.Experiment;


public class ExperimentTableModel extends AbstractTableModel {
    private String[] columns = {"Name", "Date", "Number of runs", "Description"};
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
            case 0:
                return experiments.get(rowIndex).getName();
            case 1:
                return experiments.get(rowIndex).getDate();
            case 2:
                return experiments.get(rowIndex).getNumRuns();
            case 3:
                return experiments.get(rowIndex).getDescription();
            case 4:
                return experiments.get(rowIndex).getId();
            default:
                return "";
        }
    }

    public Experiment getExperimentAt(int rowIndex) {
        return experiments.get(rowIndex);
    }
}
