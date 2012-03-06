/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edacc.properties;

import edacc.model.Property;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Vector;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author rretz
 */
public class SystemPropertyTableModel extends AbstractTableModel {

    private ImportPropertyCSVController controller;
    private String[] columns = {"Name", "Type", "Selected"};
    private ArrayList<Property> properties;
    // User choice of the mapping of the proerties found in the csv and the 
    // system properties < Sys Property, CSVProperty>
    private HashMap<Property, String> selected;

    SystemPropertyTableModel(Vector<Property> properties, ImportPropertyCSVController controller) {
        this.properties = new ArrayList<Property>(properties);
        this.controller = controller;
        this.selected = new HashMap<Property, String>();
    }

    @Override
    public int getRowCount() {
        return properties.size();
    }

    @Override
    public int getColumnCount() {
        return columns.length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        switch (columnIndex) {
            case 0:
                return properties.get(rowIndex).getName();
            case 1:
                return properties.get(rowIndex).getType().name();
            case 2:
                if (selected.containsKey(properties.get(rowIndex))) {
                    if ((selected.get(properties.get(rowIndex))).equals(controller.getSelectedCSVProperty())) {
                        return true;
                    } else {
                        return false;
                    }
                } else {
                    return false;
                }
            default:
                return null;
        }
    }

    @Override
    public boolean isCellEditable(int row, int col) {
        return col == 2;
    }

    @Override
    public void setValueAt(Object value, int row, int col) {
        if (col == 2) {
            selected.put(properties.get(row), controller.getSelectedCSVProperty());
        }
        this.fireTableCellUpdated(row, col);
    }

    @Override
    public Class getColumnClass(int column) {
        if (column == 2) {
            return Boolean.class;
        } else {
            return String.class;
        }
    }

    public void removeRelated(String toDrop) {
        for (Entry entry : selected.entrySet()) {
            if (entry.getValue().equals(toDrop)) {
                selected.remove(entry.getKey());
                return;
            }
        }
    }

    public Set<Entry<Property, String>> getSelected() {
        return selected.entrySet();
    }
}
