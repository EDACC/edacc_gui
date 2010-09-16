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
    private final int BINNAME = 2;
    private final int MD5 = 3;
    private final int DESCRIPTION = 1;
    private final int AUTHORS = 4;
    private final int VERSION = 5;

    private String[] columns = {"Name", "Description", "Binary Name", "MD5 hash", "Authors", "Version"};
    private Vector<Solver> solvers;

    public SolverTableModel() {
        solvers = new Vector<Solver>();
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
            case AUTHORS:
                return s.getAuthors();
            case VERSION:
                return s.getVersion();
        }
        return null;
    }

    public void addSolver(Solver solver) {
        solvers.add(solver);
    }

    public Vector<Solver> getSolvers() {
        return solvers;
    }

    public void clear() {
        solvers.clear();
        this.fireTableDataChanged();
    }

    public Solver getSolver(int row) {
        if (row >= 0 && row < getRowCount())
            return solvers.get(row);
        else
            return null;
    }

    public void removeSolver(int index) {
        solvers.remove(index);
    }

    public void removeSolver(Solver solver) {
        solvers.remove(solver);
    }
}
