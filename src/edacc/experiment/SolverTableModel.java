package edacc.experiment;

import edacc.model.DatabaseConnector;
import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import edacc.model.Solver;
import edacc.model.SolverDAO;
import java.util.HashMap;

/**
 *
 * @author daniel, simon
 */
public class SolverTableModel extends AbstractTableModel {

    /** The index of the name column */
    public static final int COL_NAME = 0;
    /** The index of the version column */
    public static final int COL_VERSION = 1;
    /** The index of the description column */
    public static final int COL_DESCRIPTION = 2;
    /** The index of the categories column */
    public static final int COL_CATEGORIES = 3;
    /** The index of the selected column */
    public static final int COL_SELECTED = 4;
    private static final String[] columns = {"Name", "Version", "description", "categories", "Selected"};
    private String[] categories;
    private boolean isCompetition;
    private ArrayList<Solver> solvers;
    private Boolean[] selected;

    /** Creates a new solver table model */
    public SolverTableModel() {
        this.solvers = new ArrayList<Solver>();
    }

    /**
     * Sets the solvers used for this model.
     * @param solvers the solvers to be used as an <code>ArrayList</code>
     */
    public void setSolvers(ArrayList<Solver> solvers) {
        this.solvers = solvers;
        this.selected = new Boolean[solvers.size()];
        for (int i = 0; i < solvers.size(); i++) {
            selected[i] = false;
        }
        try {
            isCompetition = DatabaseConnector.getInstance().isCompetitionDB();
        } catch (Exception e) {
            isCompetition = false;
        }
        categories = null;
        if (isCompetition) {
            if (solvers != null) {
                categories = new String[solvers.size()];
                try {
                    HashMap<Integer, ArrayList<String>> cats = SolverDAO.getCompetitionCategories();
                    for (int i = 0; i < solvers.size(); i++) {
                        String tmp = "";
                        ArrayList<String> catlist = cats.get(solvers.get(i).getId());
                        if (catlist != null) {
                            for (int k = 0; k < catlist.size(); k++) {
                                tmp += catlist.get(k);
                                if (k != catlist.size() - 1) {
                                    tmp += ", ";
                                }
                            }
                        }
                        categories[i] = tmp;
                    }
                } catch (Exception e) {
                }
            }
        } 
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
        if (col == COL_SELECTED) {
            return true;
        }
        return false;
    }

    @Override
    public void setValueAt(Object value, int row, int col) {
        if (col == COL_SELECTED) {
            selected[row] = (Boolean) value;
        }
        fireTableCellUpdated(row, col);
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        switch (columnIndex) {
            case COL_NAME:
                return solvers.get(rowIndex).getName();
            case COL_VERSION:
                return solvers.get(rowIndex).getVersion();
            case COL_DESCRIPTION:
                return solvers.get(rowIndex).getDescription();
            case COL_CATEGORIES:
                return categories == null ? "" : (categories[rowIndex] == null ? "" : categories[rowIndex]);
            case COL_SELECTED:
                return selected[rowIndex];
            default:
                return "";
        }
    }

    /**
     * Sets the selection of the solver with the given id according to value
     * @param solverId the solver id
     * @param value the selection value
     */
    public void setSolverSelected(int solverId, boolean value) {
        for (int i = 0; i < solvers.size(); i++) {
            if (solvers.get(i).getId() == solverId) {
                selected[i] = value;
                this.fireTableCellUpdated(i, 5);
                break;
            }
        }
    }

    /**
     * Returns the solver represented by the row
     * @param row row index
     * @return a solver
     */
    public Solver getSolver(int row) {
        return solvers.get(row);
    }

    /**
     * Checks if the solver in this row is selected
     * @param row the row index
     * @return true, iff the solver in this row is selected
     */
    public boolean isSelected(int row) {
        return selected[row];
    }

    /**
     * Sets the selection of the solver in this row according to <code>sel</code>
     * @param row the row index
     * @param sel the selection value
     */
    public void setSelected(int row, boolean sel) {
        selected[row] = sel;
        fireTableRowsUpdated(row, row);
    }
}
