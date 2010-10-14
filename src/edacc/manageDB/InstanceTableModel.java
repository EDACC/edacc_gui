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
    private String[] columns = {"Name", "MD5"};
    protected Vector<Instance> instances;

    public InstanceTableModel(){
        this.instances = new Vector<Instance>();
    }

    public boolean isEmpty(){
        if(instances.isEmpty()) return true;
        return false;
    }

    public Vector<Instance> getInstances() {
        return instances;
    }

    public void addInstances(Vector<Instance> instances){
        for(int i = 0; i < instances.size(); i++){
            addInstance(instances.get(i));
        }
    }

    private void addInstance(Instance in){
        if(!instances.contains(in))
            instances.add(in);
    }

    public void clearTable(){
        instances.removeAllElements();
    }

    public void remove(Instance instance){
        instances.remove(instance);
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

    @Override
    public Class getColumnClass(int col) {
        if (this.getRowCount() == 0)
            return this.getClass();
        else
            return getValueAt(0, col).getClass();
    }

    /**
     *
     * @param rowIndex
     * @param columnIndex 
     * @return the Object from the choosen cell; return == "" when columnIndex is out of columnRange
     */
    public Object getValueAt(int rowIndex, int columnIndex) {
         switch (columnIndex) {
            case 0:
                return instances.get(rowIndex).getName();
            case 1:
                return instances.get(rowIndex).getMd5();
            default:
                return "";
        }
    }

    public Instance getInstance(int rowIndex){
        return instances.elementAt(rowIndex);
    }
}
