package edacc.manageDB;

import edacc.model.SolverBinaries;
import edacc.model.SolverDAO;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author simon
 */
public class SolverBinariesTableModel extends DefaultTableModel {

    public static final int COL_NAME = 0;
    public static final int COL_MD5 = 1;
    public static final int COL_RUNCOMMAND = 2;
    public static final int COL_RUNPATH = 3;
    public static final int COL_VERSION = 4;
    private static final String[] columns = {"Name", "MD5", "Run Command", "Run Path", "Version"};
    private ArrayList<SolverBinaries> solverBinaries;

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
        SolverBinaries solverBinary = solverBinaries.get(row);
        switch (column) {
            case COL_NAME:
                return solverBinary.getBinaryName();
            case COL_MD5:
                return solverBinary.getMd5();
            case COL_RUNCOMMAND:
                return solverBinary.getRunCommand();
            case COL_RUNPATH:
                return solverBinary.getRunPath();
            case COL_VERSION:
                return solverBinary.getVersion();
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
                solverBinaries.get(row).setBinaryName((String) aValue);
                break;
            case COL_RUNCOMMAND:
                solverBinaries.get(row).setRunCommand((String) aValue);
                break;
            case COL_RUNPATH:
                solverBinaries.get(row).setRunPath((String) aValue);
                break;
            case COL_VERSION:
                solverBinaries.get(row).setVersion((String) aValue);
                break;
        }
    }

    @Override
    public int getRowCount() {
        return solverBinaries == null ? 0 : solverBinaries.size();
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return getRowCount() == 0 ? String.class : getValueAt(0, columnIndex) == null ? String.class : getValueAt(0, columnIndex).getClass();
    }

    public void setSolverBinaries(Vector<SolverBinaries> solverBinaries) {
        if (this.solverBinaries == null) {
            this.solverBinaries = new ArrayList<SolverBinaries>();
        } else {
            this.solverBinaries.clear();
        }
        if (solverBinaries != null) {
            this.solverBinaries.addAll(solverBinaries);
        }
        this.fireTableDataChanged();
    }
}
