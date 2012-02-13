package edacc.importexport;

import edacc.experiment.ExperimentTableModel;
import edacc.model.Experiment;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author simon
 */
public class ExperimentSelectionTableModel extends ExperimentTableModel {

    private final String selectedColumnName = "Selected";
    private boolean[] selected;

    public ExperimentSelectionTableModel() {
        super(true);
    }

    @Override
    public Class getColumnClass(int col) {
        if (col < super.getColumnCount()) {
            return super.getColumnClass(col);
        } else {
            return Boolean.class;
        }
    }

    @Override
    public int getColumnCount() {
        return super.getColumnCount() + 1;
    }

    @Override
    public String getColumnName(int col) {
        if (col < super.getColumnCount()) {
            return super.getColumnName(col);
        } else {
            return selectedColumnName;
        }
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        if (columnIndex < super.getColumnCount()) {
            return super.getValueAt(rowIndex, columnIndex);
        } else {
            return selected[rowIndex];
        }
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        if (columnIndex < super.getColumnCount()) {
            return super.isCellEditable(rowIndex, columnIndex);
        } else {
            return true;
        }
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        if (columnIndex < super.getColumnCount())
            super.setValueAt(aValue, rowIndex, columnIndex);
        else {
            selected[rowIndex] = (Boolean) aValue;
            this.fireTableRowsUpdated(rowIndex, rowIndex);
        }            
    }
    
    

    @Override
    public void setExperiments(List<Experiment> experiments) {
        selected = new boolean[experiments.size()];
        super.setExperiments(experiments);
    }
    
    public List<Experiment> getSelectedExperiments() {
        List<Experiment> res = new ArrayList<Experiment>();
        for (int i = 0; i < selected.length; i++) {
            if (selected[i])
                res.add(this.getExperimentAt(i));
        }
        return res;
    }
}
