/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edacc.properties;

import edacc.EDACCManageComputationMethodDialog;
import edacc.model.ComputationMethod;
import java.util.Vector;
import javax.swing.JPanel;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author rretz
 */
public class ComputationMethodTableModel extends AbstractTableModel{

    private String[] columns = {"Name"};
    private Vector<ComputationMethod> rows = new Vector<ComputationMethod>();

    /**
     *
     * @return the number of rows
     */
    @Override
    public int getRowCount() {
        return rows.size();
    }

    /**
     *
     * @return the number of columns
     */
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
        switch(col){
            case 0:
                return String.class;
            default:
                return null;
        }
    }

    /**
     * Returns the value of the requested cell. ColumnIndex of 0 returns a String representing the name of ComputationMethod.
     * @param rowIndex
     * @param columnIndex
     * @return value of the requested cell or "" if the requested cell is invalid.
     */
    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        switch(columnIndex){
            case 0:
                return rows.get(rowIndex).getName();
            default:
                return "";
        }
    }

    /**
     *
     * @param rowIndex the row of the requested ComputationMethod in the TableModel
     * @return the requested ComputationMethod at the given rowIndex of the TableModel
     */
    public ComputationMethod getComputationMethod(int rowIndex){
        return rows.get(rowIndex);
    }
    /**
     * Adds the given ComputationMethod object.
     * @param computationMethod ComputationMethod object to add
     */
    private void addComputationMethod(ComputationMethod computationMethod){
        rows.add(computationMethod);
    }

    /**
     * Adds all given ComputationMethod objects.
     * @param computationMethods the ComputationMethod objects to add
     */
    public void addComputationMethods(Vector<ComputationMethod> computationMethods){
        for(int i = 0; i < computationMethods.size(); i++){
            addComputationMethod(computationMethods.get(i));
        }
        this.fireTableDataChanged();
    }

    /**
     * Removes the ComputationMethod at the given position of the table.
     * @param rowIndex of the ComputationMethod to remove
     */
    public void removeComputationMethod(int rowIndex){
        rows.remove(rowIndex);
        this.fireTableDataChanged();
    }

    /**
     * Removes all ComputationMethod objects from the table.
     */
    public void clear(){
        this.rows.clear();
        this.fireTableDataChanged();
    }

    /**
     * Removes the given ComputationMethod object from the table
     * @param toRemove the ComputationMethod object to remove
     */
    public void removeComputationMethod(ComputationMethod toRemove){
        rows.remove(toRemove);
        this.fireTableDataChanged();
    }
}
