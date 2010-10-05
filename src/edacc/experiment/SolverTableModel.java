package edacc.experiment;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import edacc.model.Solver;

/**
 *
 * @author daniel
 */
public class SolverTableModel extends AbstractTableModel {
    private String[] columns = {"Name", "binary name", "md5", "description","Selected"};
    private ArrayList<Solver> solvers;
    private Boolean[] selected;
    
    public SolverTableModel() {
        this.solvers = new ArrayList<Solver>();
    }

    public void setSolvers(ArrayList<Solver> solvers) {
        this.solvers = solvers;
        this.selected = new Boolean[solvers.size()];
        for (int i = 0; i < solvers.size(); i++) {
            selected[i] = false;
        }
        this.fireTableDataChanged();
    }

    @Override
    public int getRowCount() {
        return solvers.size();
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
        return getValueAt(0, col).getClass();
    }

    @Override
    public boolean isCellEditable(int row, int col) {
        if (col == 4) return true;
        return false;
    }

    @Override
    public void setValueAt(Object value, int row, int col) {
        if (col == 4) selected[row] = (Boolean)value;
        fireTableCellUpdated(row, col);
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        switch (columnIndex) {
            case 0:
                return solvers.get(rowIndex).getName();
            case 1:
                return solvers.get(rowIndex).getBinaryName();
            case 2:
                return solvers.get(rowIndex).getMd5();
            case 3:
                return solvers.get(rowIndex).getDescription();
            case 4:
                return selected[rowIndex];
            case 5:
                return solvers.get(rowIndex);
            default:
                return "";
        }
    }

    public void setSolverSelected(int solverId, boolean value) {
        for (int i = 0; i < solvers.size(); i++) {
            if (solvers.get(i).getId() == solverId) {
                selected[i] = value;
                this.fireTableCellUpdated(i, 4);
                break;
            }
        }
    }

    public Solver getSolver(int row) {
        return solvers.get(row);
    }
}
