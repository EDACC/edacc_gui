/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edacc.properties;

import edacc.model.SolverProperty;
import java.util.Vector;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author rretz
 */
public class SolverPropertyTableModel extends AbstractTableModel {
    private String[] columns = {"Name", "Prefix", "Value type", "Property type", "Multiple"};
    private Vector<SolverProperty> rows = new Vector<SolverProperty>();

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
        if (this.getRowCount() == 0)
            return this.getClass();
        else
            return getValueAt(0, col).getClass();
    }

    /**
     * Returns the value of the requested cell. ColumnIndex of 0 returns a String representing the name of the SolverProperty.
     * ColumnIndex of 1 returns a String representing the prefix of the SolverProperty. ColumnIndex 2 returns
     * the PropertyValueType object of the SolverProperty. ColumnIndex of 3 returns the SolverPropertyType.
     * ColumnIndex of 4 returns the multiple status and a ColumnIndex of 5 returns the SolverProperty object.
     *
     * @param rowIndex
     * @param columnIndex
     * @return value of the requested cell or "" if the requested cell is invalid.
     */
    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        switch(columnIndex){
            case 0:
                return rows.get(rowIndex).getName();
            case 1:
                return rows.get(rowIndex).getPrefix();
            case 2:
                return rows.get(rowIndex).getPropertyValueType();
            case 3:
                return rows.get(rowIndex).getSolverPropertyType();
            case 4:
                return rows.get(rowIndex).isMultiple();
            case 5:
                return rows.get(rowIndex);
            default:
                return "";
        }
    }

    /**
     * Adds the given SolverProperty object.
     * @param resProperty SolverProperty object to add
     */
    private void addResultProperty(SolverProperty resProperty){
        rows.add(resProperty);
    }

    /**
     * Adds all given SolverProperty objects.
     * @param resProperties the SolverProperty objects to add
     */
    public void addResultProperties(Vector<SolverProperty> resProperties){
        for(int i = 0; i < resProperties.size(); i++){
            addResultProperty(resProperties.get(i));
        }
        this.fireTableDataChanged();
    }

    /**
     * Removes the SolverProperty at the given position of the table.
     * @param rowIndex of the SolverProperty to remove
     */
    private void removeResultProperty(int rowIndex){
        rows.remove(rowIndex);
    }

    /**
     * Removes all the SolverProperty objects at the given position of the table.
     * @param rowIndexes of all SolverProperty objects to remove
     */
    public void removeResultProperties(Vector<Integer> rowIndexes){
        for(int i = 0; i <= rowIndexes.size(); i++){
            removeResultProperty(rowIndexes.get(i));
        }
        this.fireTableDataChanged();
    }

    public void clear(){
        this.rows.clear();
        this.fireTableDataChanged();
    }

}
