/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edacc.experiment;

import edacc.model.ExperimentHasInstance;
import edacc.model.Instance;
import java.util.HashMap;
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

    protected HashMap<Integer, ExperimentHasInstance> selectedInstances;
    protected Vector<ExperimentHasInstance> savedExperimentInstances;

    public void setInstances(Vector<Instance> instances) {
        this.instances = instances;
        experimentHasInstances = new Vector<ExperimentHasInstance>();
        experimentHasInstances.setSize(instances.size());
        this.fireTableDataChanged();
    }

    /**
     * Deselects all selected instances and selects all instances which are specified
     * by the given parameter experimentHasInstances.
     * @param experimentHasInstances
     */
    public void setExperimentHasInstances(Vector<ExperimentHasInstance> experimentHasInstances) {
        this.savedExperimentInstances = experimentHasInstances;
        selectedInstances.clear();
        for (ExperimentHasInstance ehi: experimentHasInstances) {
            selectedInstances.put(ehi.getInstances_id(), ehi);
        }
        for (int i = 0; i < this.experimentHasInstances.size(); i++) {
            this.experimentHasInstances.set(i, null);
        }
        for (int i = 0; i < experimentHasInstances.size(); i++) {
            for (int j = 0; j < this.experimentHasInstances.size(); j++) {
                if (instances.get(j).getId() == experimentHasInstances.get(i).getInstances_id()) {
                    this.experimentHasInstances.set(j, experimentHasInstances.get(i));
                    break;
                }
            }
        }
    }

    public void undo() {
        setExperimentHasInstances(this.savedExperimentInstances);
        this.fireTableDataChanged();
    }

    /**
     * Returns a vector with all instance ids for which there is no
     * corresponding ExperimentHasInstance.
     * @return
     */
    public Vector<Integer> getNewInstanceIds() {
        Vector<Integer> res = new Vector<Integer>();
        for (Integer instanceId : selectedInstances.keySet()) {
            ExperimentHasInstance ehi = selectedInstances.get(instanceId);
            if (ehi == null) {
                res.add(instanceId);
            }
        }
        return res;
    }

    /**
     * Returns a vector with all deselected ExperimentHasInstance objects which
     * were specified by setExperimentHasInstances()
     * @return
     */
    public Vector<ExperimentHasInstance> getDeletedExperimentHasInstances() {
        Vector<ExperimentHasInstance> res = new Vector<ExperimentHasInstance>();
        if (savedExperimentInstances == null) {
            return res;
        }
        for (ExperimentHasInstance ehi : savedExperimentInstances) {
            if (!selectedInstances.containsKey(ehi.getInstances_id())) {
                res.add(ehi);
            }
        }
        return res;
    }

    public boolean isModified() {
        return getDeletedExperimentHasInstances().size() > 0 || getNewInstanceIds().size() > 0;
    }

    public void setExperimentHasInstance(ExperimentHasInstance e, int row) {
        this.experimentHasInstances.set(row, e);
    }

    public InstanceTableModel() {
        this.instances = new Vector<Instance>();
        this.experimentHasInstances = new Vector<ExperimentHasInstance>();
        selectedInstances = new HashMap<Integer, ExperimentHasInstance>();
    }

    @Override
    public int getRowCount() {
        return instances.size();
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
        if (col == 5) {
            if ((Boolean) value) {
                selectedInstances.put(instances.get(row).getId(), experimentHasInstances.get(row));
            } else {
                selectedInstances.remove(instances.get(row).getId());
            }
        } 
        this.fireTableCellUpdated(row, col);
    }



    @Override
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
                return selectedInstances.containsKey(instances.get(rowIndex).getId()); //selected.get(rowIndex);
            case 6:
                return experimentHasInstances.get(rowIndex);
            case 7:
                return instances.get(rowIndex);
            default:
                return "";
        }
    }
}
