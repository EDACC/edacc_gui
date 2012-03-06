/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edacc.manageDB;

import java.util.ArrayList;
import java.util.Vector;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author rretz
 */
public class FileNameTableModel extends AbstractTableModel{
     private String[] columns = {"Name"};
     private ArrayList<String> names;

     public FileNameTableModel(){
         names = new ArrayList<String>();
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
    
    public String getColumnName(int column){
        return columns[column];
    }

    public void setAll(ArrayList<String> toAdd){
        names.addAll(toAdd);
    }

}
