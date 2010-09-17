/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edacc.properties;

import java.util.Vector;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author rretz
 */
public class PropertyValueTypeSelectionModel extends AbstractTableModel{
        private String[] columns;
        private Vector<String> rows = new Vector<String>();

    public PropertyValueTypeSelectionModel(String[] columns){
        this.columns = columns;
    }

    @Override
    public int getRowCount() {
        return rows.size();
    }

    @Override
    public int getColumnCount() {
        return columns.length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        switch(columnIndex){
            case 0:
                return rows.get(rowIndex);
            default:
                return "";
        }
    }

    @Override
    public String getColumnName(int col) {
        return columns[col];
    }


    public void addRows(Vector<String> rows){
        this.rows = rows;
        this.fireTableDataChanged();
    }
}
