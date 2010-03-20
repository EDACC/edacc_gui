/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edacc.manageDB;

import edacc.model.InstanceClass;
import java.util.Vector;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author rretz
 */
public class InstanceClassTableModel extends AbstractTableModel{
     private String[] columns = {"Name", "Description", "Source", "Select"};
     protected Vector <InstanceClass> classes;
     protected Vector <Boolean> classSelect;

    public InstanceClassTableModel(){
        this.classes = new Vector <InstanceClass>();
        this.classSelect = new Vector <Boolean>();
    }

    public int getRowCount() {
        return classes.size();
    }

    public int getColumnCount() {
        return columns.length;
    }

    @Override
    public String getColumnName(int column){
        return columns[column];
    }

    @Override
    public Class getColumnClass(int column){
        if(column == 2 || column == 3) return Boolean.class;
        return String.class;
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
         switch (columnIndex) {
            case 0:
                return classes.get(rowIndex).getName();
            case 1:
                return classes.get(rowIndex).getDescription();
            case 2:
                return classes.get(rowIndex).isSource();
            case 3:
                return classSelect.get(rowIndex);
            case 4:
                return classes.get(rowIndex);
            default:
                return "";
        }
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex){
        if( columnIndex == 3) return true;
        return false;
    }

    public void addClass(InstanceClass instanceClass){
        this.classes.add(instanceClass);
        this.classSelect.add(false);
    }

    public void addClasses (Vector <InstanceClass> classes){
        for(int i = 0; i < classes.size(); i++){
            this.classSelect.add(false);
        }
        this.classes.addAll(classes);
    }

    public void removeClass(int row){
        this.classes.remove(row);
        this.classSelect.remove(row);
    }

    public void setInstanceClassSelected(int row){
        this.classSelect.add(row, true);
    }

     public void setValueAt(Object value, int row, int col) {
        if(col == 3){
            classSelect.set(row, (Boolean) value);
        }
        fireTableCellUpdated(row, col);

    }


}
