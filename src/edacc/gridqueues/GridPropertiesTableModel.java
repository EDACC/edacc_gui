/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edacc.gridqueues;

import edacc.model.GridQueue;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author dgall
 */
public class GridPropertiesTableModel extends AbstractTableModel {

    private GridQueue q;
    private final String[] rowLabels = { "CPU name", "cache size", "cpu flags", 
        "hyperthreading", "turboboost", "memory", "number of cores",
        "number of threads", "cpuinfo", "meminfo"};
    private final String[] columnNames = { "Property", "Value" };

    public GridPropertiesTableModel(GridQueue q) {
        this.q = q;
    }

    @Override
    public int getRowCount() {
        if (q == null)
            return 0;
        return rowLabels.length;
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        if (q == null)
            return null;
        if (columnIndex == 0)
            if (rowIndex < rowLabels.length)
                return rowLabels[rowIndex];
        switch (rowIndex) {
            case 0: return q.getCPUName();
            case 1: return q.getCacheSize();
            case 2: return q.getCpuflags();
            case 3: return q.isHyperthreading();
            case 4: return q.isTurboboost();
            case 5: return q.getMemory();
            case 6: return q.getNumCores();
            case 7: return q.getNumThreads();
            case 8: return q.getCpuinfo();
            case 9: return q.getMeminfo();
        }
        return null;
    }
    
    public String getColumnName(int columnIndex) {
        if (columnIndex < 0 || columnIndex >= getColumnCount())
            return "";
        return columnNames[columnIndex];
    }

    public void setGridQueue(GridQueue q) {
        this.q = q;
        fireTableDataChanged();
    }

}
