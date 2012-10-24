/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edacc.properties;

import edacc.satinstances.PropertyValueType;
import java.util.Vector;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author rretz
 */
public class PropertyValueTypeTableModel extends AbstractTableModel{
    String[] columns = {"Name", "Default"};
    Vector<PropertyValueType<?>> rows = new Vector<PropertyValueType<?>>();

    @Override
    public int getRowCount() {
        return rows.size();
    }

    @Override
    public int getColumnCount() {
        return columns.length;
    }

     @Override
    public Class getColumnClass(int column){
        if(column == 2 || column == 3) return Boolean.class;
        return String.class;
    }

    /**
     *  Returns the value of the requested cell. If the columnIndex is 2, the PropertyValueObject from the table
     * is returned. On default the String "" is returned.
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
                if(rows.get(rowIndex).isDefault())
                    return "\u2713";
                else
                    return "";
            case 2:
                return rows.get(rowIndex);
            default:
                return "";
        }
    }

    @Override
    public String getColumnName(int col) {
        return columns[col];
    }

    /**
     * Adds the given PropertyValueType objects to the TableModel and updates the view of the table.
     * @param toAdd the PropertyValueType objects to add
     */
    public void addPropertyValueTypes(Vector<PropertyValueType<?>> toAdd){
        for(int i = 0; i < toAdd.size(); i++){
            rows.add(toAdd.get(i));
        }
        this.fireTableDataChanged();
    }

    /**
     * Removes the given PropertyValueType objects to the TableModel and updates the view of the table.
     * @param toRemove the PropertyValueType objects to remove
     */
    public void removePropertyValueTypes(Vector<PropertyValueType<?>> toRemove){
        for(int i = 0; i < toRemove.size(); i++){
            rows.remove(toRemove.get(i));
        }
        this.fireTableDataChanged();
    }

    /**
     * Removes all PropertyValueType objects from the TableModel.
     */
    public void clearTable(){
        rows.clear();
    }

    /**
     * Removes the object at the given rowIndex.
     * @param rowIndex of the object to remove.
     */
    public void remove(PropertyValueType<?> toRemove){
        rows.remove(toRemove);
        this.fireTableDataChanged();
    }
}
