/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edacc.manageDB;

import edacc.model.Instance;
import java.util.Vector;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author rretz
 */
public class InstanceTableModel extends AbstractTableModel{
    private String[] columns = {"Name", "numAtoms", "numClauses", "ratio", "maxClauseLength"};
    protected Vector<Instance> instances;

    public InstanceTableModel(){
        this.instances = new Vector<Instance>();
    }

    public Vector<Instance> getInstances() {
        return instances;
    }

    public void addInstances(Vector<Instance> instances){
        this.instances.addAll(instances);
    }

    public void clearTable(){
        instances.removeAllElements();
    }

    public void removeInstances(Vector<Instance> instances){
        this.instances.removeAll(instances);
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

    /**
     *
     * @param rowIndex
     * @param columnIndex For columnIndex = 5 the instance at rowIndex is returned
     * @return the Object from the choosen cell; return == "" when columnIndex is out of columnRange
     */
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
                return instances.get(rowIndex);
            default:
                return "";
        }
    }

}
