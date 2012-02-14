package edacc.importexport;

import edacc.experiment.SolverTableModel;
import edacc.model.Solver;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author simon
 */
public class SolverFixedSelectionTableModel extends SolverTableModel {
    private boolean[] fixed;

    @Override
    public boolean isCellEditable(int row, int col) {
        if (col == SolverTableModel.COL_SELECTED && fixed[row])
            return false;
        return super.isCellEditable(row, col);
    }

    @Override
    public boolean isSelected(int row) {
        if (fixed[row])
            return true;
        return super.isSelected(row);
    }

    @Override
    public void setSolvers(List<Solver> solvers) {
        fixed = new boolean[solvers.size()];
        for (int i = 0; i < fixed.length; i++)
            fixed[i] = false;
        super.setSolvers(solvers);
    }
    
    public void setSolverFixed(int sid, boolean value) {
        for (int i = 0; i < fixed.length; i++)
            if (super.getSolver(i).getId() == sid) {
                fixed[i] = value;
                this.fireTableRowsUpdated(i, i);
                break;
            }
    }

    public void clearFixedSolvers() {
        for (int i = 0; i < fixed.length; i++)
            fixed[i] = false;
        this.fireTableRowsUpdated(0, fixed.length-1);
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        if (columnIndex == SolverTableModel.COL_SELECTED && fixed[rowIndex]) {
            return true;
        }
        return super.getValueAt(rowIndex, columnIndex);
    }
    
    public List<Solver> getSelectedSolvers() {
        List<Solver> res = new ArrayList<Solver>();
        for (int row = 0; row < super.getRowCount(); row++) {
            if (fixed[row] || super.isSelected(row))
                res.add(super.getSolver(row));
        }
        return res;
    }
    
    public boolean isFixed(int row) {
        return fixed[row];
    }
}
