/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edacc.experiment;

import edacc.model.ExperimentHasInstance;
import edacc.model.Instance;
import javax.swing.table.AbstractTableModel;
import java.util.Vector;

/**
 *
 * @author daniel
 */
public class InstanceTableModel extends AbstractTableModel {
    private String[] columns = {"Name", "numAtoms", "numClauses", "ratio", "maxClauseLength", "selected"};
    protected Vector<Instance> instances;
    protected Vector<ExperimentHasInstance> experimentHasInstances;
    protected Vector <Boolean> selected;

    public void setInstances(Vector<Instance> instances) {
        this.instances = instances;
        experimentHasInstances = new Vector<ExperimentHasInstance>();
        selected = new Vector<Boolean>();
        this.fireTableDataChanged();
    }

    public void setExperimentHasInstances(Vector<ExperimentHasInstance> experimentHasInstances) {
        for (int i = 0; i < instances.size(); i++) {
            this.setValueAt(false, i, 5);
        }
        for (int i = 0; i < experimentHasInstances.size(); i++) {
            for (int j = 0; j < instances.size(); j++) {
                if (instances.get(j).getId() == experimentHasInstances.get(i).getInstances_id()) {
                    this.experimentHasInstances.add(j, experimentHasInstances.get(i));
                    this.selected.add(j, Boolean.TRUE);
                    break;
                }
            }
        }
        this.fireTableDataChanged();
    }

    public void setExperimentHasInstance(ExperimentHasInstance e, int row) {
        this.experimentHasInstances.add(row, e);
    }

    public InstanceTableModel() {
        this.instances = new Vector<Instance>();
        this.selected = new Vector<Boolean>();
        this.experimentHasInstances = new Vector<ExperimentHasInstance>();
    }

    public int getRowCount() {
        return instances.size();
    }

    public int getColumnCount() {
        return columns.length;
    }

    @Override
    public String getColumnName(int col) {
        return columns[col];
    }

    @Override
    public Class getColumnClass(int col) {
        if (this.getRowCount() == 0)
            return this.getClass();
        else
            return getValueAt(0, col).getClass();
    }

    @Override
    public boolean isCellEditable(int row, int col) {
        if (col == 5) return true;
        else return false;
    }

    @Override
    public void setValueAt(Object value, int row, int col) {
        if (col == 5)selected.set(row, (Boolean) value);
        //this.fireTableCellUpdated(row, col);
    }



    public Object getValueAt(int rowIndex, int columnIndex) {
        switch (columnIndex) {
            case 0:
                return instances.get(rowIndex).getName();
            case 1:
                return instances.get(rowIndex).getNumAtoms();
            case 2:
                return instances.get(rowIndex).getNumClauses();
            case 3:
                return instances.get(rowIndex).getRatio();
            case 4:
                return instances.get(rowIndex).getMaxClauseLength();
            case 5:
                return selected.get(rowIndex);
            case 6:
                return experimentHasInstances.get(rowIndex);
            case 7:
                return instances.get(rowIndex);
            default:
                return "";
        }
    }

    public void clearTable(){
        instances.clear();
        experimentHasInstances.clear();
        selected.clear();
        this.fireTableDataChanged();
    }

    public void setInstancesAndExperimentHasInstance(Instance instance, ExperimentHasInstance expHasInst){
        instances.add(instance);
        experimentHasInstances.add(expHasInst);
        selected.add(Boolean.TRUE);
        this.fireTableDataChanged();
    }

    public void setInstancesWithoutExp(Instance instance){
        instances.add(instance);
        experimentHasInstances.add(null);
        selected.add(Boolean.FALSE);
        this.fireTableDataChanged();
    }
}
