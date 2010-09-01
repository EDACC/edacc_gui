/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edacc.properties;

import edacc.model.ResultProperty;
import java.util.Vector;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author rretz
 */
public class ResultPropertyTableModel extends AbstractTableModel {
    private String[] columns = {"Name", "Prefix", "Valuetype"};
    private Vector<ResultProperty> rows;

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

    /**
     * Returns the value of the requested cell. ColumnIndex of 0 returns a String representing the name of the ResultProperty.
     * ColumnIndex of 1 returns a String representing the prefix of the ResultProperty. ColumnIndex 2 returns
     * the PropertyValueType object of the ResultProperty and a ColumnIndex of 3 returns the ResultProperty object.
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
                return rows.get(rowIndex);
            default:
                return "";
        }
    }

    /**
     * Adds the given ResultProperty object.
     * @param resProperty ResultProperty object to add
     */
    private void addResultProperty(ResultProperty resProperty){
        rows.add(resProperty);

    }

    /**
     * Adds all given ResultProperty objects.
     * @param resProperties the ResultProperty objects to add
     */
    public void addResultProperties(Vector<ResultProperty> resProperties){
        for(int i = 0; i <= resProperties.size(); i++){
            addResultProperty(resProperties.get(i));
        }
        this.fireTableDataChanged();
    }

    /**
     * Removes the ResultProperty at the given position of the table.
     * @param rowIndex of the ResultProperty to remove
     */
    private void removeResultProperty(int rowIndex){
        rows.remove(rowIndex);
    }

    /**
     * Removes all the ResultProperty objects at the given position of the table.
     * @param rowIndexes of all ResultProperty objects to remove
     */
    public void removeResultProperties(Vector<Integer> rowIndexes){
        for(int i = 0; i <= rowIndexes.size(); i++){
            removeResultProperty(rowIndexes.get(i));
        }
        this.fireTableDataChanged();
    }

}
