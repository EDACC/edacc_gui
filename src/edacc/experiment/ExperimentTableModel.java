package edacc.experiment;

import javax.swing.table.AbstractTableModel;
import java.util.Vector;
import edacc.model.Experiment;


public class ExperimentTableModel extends AbstractTableModel {
    private String[] columns = {"Name", "Date", "Number of runs", "Timeout", "Description"};
    private Vector<Experiment> experiments;

    public ExperimentTableModel() {
        this.experiments = new Vector<Experiment>();
    }

    public void setExperiments(Vector<Experiment> experiments) {
        this.experiments = experiments;
        this.fireTableDataChanged();
    }

    @Override
    public int getRowCount() {
        return experiments.size();
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
        if (experiments.size() == 0) {
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
                return experiments.get(rowIndex).getTimeOut();
            case 4:
                return experiments.get(rowIndex).getDescription();
            case 5:
                return experiments.get(rowIndex).getId();
            default:
                return "";
        }
    }

    public Experiment getExperimentAt(int rowIndex) {
        return experiments.get(rowIndex);
    }
}
