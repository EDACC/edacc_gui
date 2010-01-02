package edacc.experiment;

import javax.swing.table.AbstractTableModel;
import java.util.Vector;
import edacc.model.Solver;

/**
 *
 * @author daniel
 */
public class SolverTableModel extends AbstractTableModel {
    private String[] columns = {"Name", "binary name", "md5", "description","Selected"};
    private Vector<Solver> solvers;
    private Boolean[] selected;
    
    public SolverTableModel() {
        this.solvers = new Vector<Solver>();
    }

    public void setSolvers(Vector<Solver> solvers) {
        this.solvers = solvers;
        this.selected = new Boolean[solvers.size()];
        for (int i = 0; i < solvers.size(); i++) {
            selected[i] = false;
        }
        this.fireTableDataChanged();
    }

    public int getRowCount() {
        return solvers.size();
    }

    public int getColumnCount() {
        return columns.length;
    }

    @Override
    public String getColumnName(int col) {
        return columns[col];
    }

    @Override
    public Class getColumnClass(int col) {
        if (col == 4) return Boolean.class;
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
}
