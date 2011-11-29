/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edacc.manageDB;

import edacc.experiment.ThreadSafeDefaultTableModel;
import edacc.model.Instance;
import java.util.ArrayList;

/**
 *
 * @author rretz
 */
public class InstanceErrorTableModel extends ThreadSafeDefaultTableModel {

    private String[] columns = {"Name", "MD5"};
    ArrayList<Instance> instances;

    public InstanceErrorTableModel() {
    }

    @Override
    public int getRowCount() {
        return instances == null ? 0 : instances.size();
    }

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
        if (instances.isEmpty()) {
            return String.class;
        } else {
            return getValueAt(0, col).getClass();
        }
    }

    @Override
    public Object getValueAt(int row, int col) {
        switch (col) {
            case 0:
                instances.get(row).getName();
            case 1:
                instances.get(row).getMd5();
            default:
                return null;
        }
    }
}
