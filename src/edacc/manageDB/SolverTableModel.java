/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edacc.manageDB;

import edacc.model.Solver;
import java.util.Vector;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author dgall
 */
public class SolverTableModel extends AbstractTableModel {
    private final int NAME = 0;
    private final int BINNAME = 1;
    private final int MD5 = 2;
    private final int DESCRIPTION = 3;

    private String[] columns = {"Name", "Binary Name", "MD5 hash", "Description" };
    private Vector<Solver> solvers;

    public SolverTableModel() {
        solvers = new Vector<Solver>();
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

    public Object getValueAt(int rowIndex, int columnIndex) {
        Solver s = solvers.get(rowIndex);
        switch (columnIndex) {
            case NAME:
                return s.getName();
            case BINNAME:
                return s.getBinaryName();
            case MD5:
                return s.getMd5();
            case DESCRIPTION:
                return s.getDescription();
        }
        return null;
    }

    public void addSolver(Solver solver) {
        solvers.add(solver);
    }

    public Vector<Solver> getSolvers() {
        return (Vector<Solver>) solvers.clone();
    }

    Solver getSolver(int row) {
        if (row >= 0 && row < getRowCount())
            return solvers.get(row);
        else
            return null;
    }
}
