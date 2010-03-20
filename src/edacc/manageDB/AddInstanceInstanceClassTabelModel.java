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
public class AddInstanceInstanceClassTabelModel extends AbstractTableModel {
    private String[] columns = {"Name", "Description"};
    protected Vector <InstanceClass> classes;

    public AddInstanceInstanceClassTabelModel(){
        this.classes = new Vector <InstanceClass>();
    }

    public int getRowCount() {
        return classes.size();
    }

    public int getColumnCount() {
        return columns.length;
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
         switch (columnIndex) {
            case 0:
                return classes.get(rowIndex).getName();
            case 1:
                return classes.get(rowIndex).getDescription();
            case 2:
                return classes.get(rowIndex);
            default:
                return "";
        }
    }

    public void addClasses (Vector <InstanceClass> classes){
        this.classes.addAll(classes);
    }

}
