/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edacc.experiment;

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
    protected Vector<Boolean> selected;

    public void setInstances(Vector<Instance> instances) {
        this.instances = instances;
        selected.setSize(instances.size());
        for (int i = 0; i < selected.size(); i++) {
            selected.set(i, new Boolean(false));
        }
        System.out.println(selected.size());

        this.fireTableDataChanged();
    }

    public InstanceTableModel() {
        this.instances = new Vector<Instance>();
        this.selected = new Vector<Boolean>();
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
        return getValueAt(0, col).getClass();
    }

    @Override
    public boolean isCellEditable(int row, int col) {
        if (col == 5) return true;
        else return false;
    }

    public void setValueAt(Object value, int row, int col) {
        if (col == 5) selected.set(row, (Boolean)value);
        fireTableCellUpdated(row, col);
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
            
            default:
                return "";
        }
    }

}
