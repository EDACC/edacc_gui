/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edacc.properties;

import java.util.ArrayList;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author rretz
 */
public class CSVPropertyTableModel extends AbstractTableModel {

    private ArrayList<String> csvProps;
    private String[] columns = {"CSV Property"};

    CSVPropertyTableModel(ArrayList<String> csvProps) {
        this.csvProps = csvProps;
    }

    @Override
    public int getRowCount() {
        return csvProps.size();
    }

    @Override
    public int getColumnCount() {
        return columns.length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        switch (columnIndex) {
            case 0:               
                return csvProps.get(rowIndex);
            default:
                return "";
        }
    }

    @Override
    public String getColumnName(int column) {
        return columns[column];
    }

    @Override
    public Class getColumnClass(int column) {
        return String.class;
    }

    public void removeCSVProperty(int convertRowIndexToModel) {
        csvProps.remove(convertRowIndexToModel);
    }
}
