/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edacc.properties;

import edacc.model.Property;
import edacc.model.PropertyType;
import java.util.Vector;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author rretz
 */
public class PropertyTableModel extends AbstractTableModel {
    private String[] columns = {"Name", "Property type"};
    private Vector<Property> rows = new Vector<Property>();

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
            case 1:
                return PropertyType.class;
            default:
                return null;
        }
    }

    /**
     * Returns the value of the requested cell. ColumnIndex of 0 returns a String representing the name of the Property.
     * ColumnIndex of 1 returns a String representing the type of the Property.
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
                return rows.get(rowIndex).getType();
            default:
                return "";
        }
    }

    /**
     *
     * @param rowIndex the row of the requested Property in the TableModel
     * @return the requested Property at the given rowIndex of the TableModel
     */
    public Property getProperty(int rowIndex){
        return rows.get(rowIndex);
    }
    /**
     * Adds the given Property object.
     * @param property Property object to add
     */
    private void addProperty(Property property){
        rows.add(property);
    }

    /**
     * Adds all given Property objects.
     * @param properties the Property objects to add
     */
    public void addProperties(Vector<Property> properties){
        for(int i = 0; i < properties.size(); i++){
            addProperty(properties.get(i));
        }
        this.fireTableDataChanged();
    }

    /**
     * Removes the Property at the given position of the table.
     * @param rowIndex of the Property to remove
     */
    public void removeProperty(int rowIndex){
        rows.remove(rowIndex);
        this.fireTableDataChanged();
    }

    /**
     * Removes all Property objects from the table.
     */
    public void clear(){
        this.rows.clear();
        this.fireTableDataChanged();
    }

    /**
     * Removes the given Property object from the table
     * @param toRemove the Property object to remove
     */
    public void removeProperty(Property toRemove){
        rows.remove(toRemove);
        this.fireTableDataChanged();
    }
}
