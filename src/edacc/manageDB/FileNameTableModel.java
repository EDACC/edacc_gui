/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edacc.manageDB;

import java.util.Vector;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author rretz
 */
public class FileNameTableModel extends AbstractTableModel{
     private String[] columns = {"Path"};
     private Vector<String> names;

     public FileNameTableModel(){
         names = new Vector<String>();
     }


    public int getRowCount() {
        return names.size();
    }

    public int getColumnCount() {
        return columns.length;
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        switch (columnIndex) {
            case 0:
                return names.get(rowIndex);
            default:
                return "";
        }
    }

    public void setAll(Vector<String> toAdd){
        names.addAll(toAdd);
    }

}
