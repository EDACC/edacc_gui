package edacc.manageDB;

import edacc.model.CostBinary;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author simon
 */
public class CostBinaryTableModel extends DefaultTableModel {

    public static final int COL_NAME = 0;
    public static final int COL_MD5 = 1;
    public static final int COL_RUNCOMMAND = 2;
    public static final int COL_RUNPATH = 3;
    public static final int COL_PARAMETERS = 4;
    public static final int COL_VERSION = 5;
    public static final int COL_COST = 6;
    private static final String[] columns = {"Name", "MD5", "Run Command", "Run Path", "Parameters", "Version", "Cost"};
    private ArrayList<CostBinary> costBinaries;

    @Override
    public int getColumnCount() {
        return columns.length;
    }

    @Override
    public String getColumnName(int column) {
        return columns[column];
    }

    @Override
    public Object getValueAt(int row, int column) {
        CostBinary costBinary = costBinaries.get(row);
        switch (column) {
            case COL_NAME:
                return costBinary.getBinaryName();
            case COL_MD5:
                return costBinary.getMd5();
            case COL_RUNCOMMAND:
                return costBinary.getRunCommand();
            case COL_RUNPATH:
                return costBinary.getRunPath();
            case COL_PARAMETERS:
                return costBinary.getParameters();
            case COL_VERSION:
                return costBinary.getVersion();
            case COL_COST:
                return costBinary.getCost().toString();
            default:
                return "";
        }
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return column == COL_NAME || column == COL_RUNCOMMAND || column == COL_RUNPATH || column == COL_VERSION;
    }

    @Override
    public void setValueAt(Object aValue, int row, int column) {
        switch (column) {
            case COL_NAME:
                costBinaries.get(row).setBinaryName((String) aValue);
                break;
            case COL_RUNCOMMAND:
                costBinaries.get(row).setRunCommand((String) aValue);
                break;
            case COL_RUNPATH:
                costBinaries.get(row).setRunPath((String) aValue);
                break;
            case COL_VERSION:
                costBinaries.get(row).setVersion((String) aValue);
                break;
        }
    }

    @Override
    public int getRowCount() {
        return costBinaries == null ? 0 : costBinaries.size();
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return getRowCount() == 0 ? String.class : getValueAt(0, columnIndex) == null ? String.class : getValueAt(0, columnIndex).getClass();
    }

    public void setCostBinaries(List<CostBinary> costBinaries) {
        if (this.costBinaries == null) {
            this.costBinaries = new ArrayList<CostBinary>();
        } else {
            this.costBinaries.clear();
        }
        if (costBinaries != null) {
            this.costBinaries.addAll(costBinaries);
        }
        this.fireTableDataChanged();
    }

    public CostBinary getCostBinary(int index) {
        if (this.costBinaries == null)
            return null;
        return costBinaries.get(index);
    }
}