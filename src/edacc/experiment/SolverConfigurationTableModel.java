package edacc.experiment;

import edacc.model.ParameterInstance;
import edacc.model.ParameterInstanceDAO;
import edacc.model.Solver;
import edacc.model.SolverConfiguration;
import edacc.model.SolverDAO;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 *
 * @author simon
 */
public class SolverConfigurationTableModel extends ThreadSafeDefaultTableModel {

    /** The index for the selected column */
    public static final int COL_SEL = 0;
    /** The index for the solver column */
    public static final int COL_SOLVER = 1;
    /** The index for the name column */
    public static final int COL_NAME = 2;
    /** The index for the hint column */
    public static final int COL_HINT = 3;
    /** The index for the cost column */
    public static final int COL_COST = 4;
    /** The index for the cost function column */
    public static final int COL_COSTFUNCTION = 5;
    /** The index for the solver binary column */
    public static final int COL_SOLVERBINARY = 6;
    /** The index for the parameters column */
    public static final int COL_PARAMETERS = 7;
    private String[] columns = {"Selected", "Solver", "Solver Configuration", "Hint", "Cost", "Cost Function", "Solver Binary", "Parameters"};
    /** The selection of the rows */
    public boolean[] selected;
    private ArrayList<SolverConfiguration> solverConfigurations;

    @Override
    public int getRowCount() {
        return solverConfigurations == null ? 0 : solverConfigurations.size();
    }

    @Override
    public int getColumnCount() {
        return columns.length;
    }

    private ArrayList<ParameterInstance> getParametersBySolverConfig(SolverConfiguration sc) {
        try {
            return ParameterInstanceDAO.getBySolverConfig(sc);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Returns the solver configuration at the specified row
     * @param row the row index
     * @return a solver configuration
     */
    public SolverConfiguration getSolverConfigurationAt(int row) {
        return solverConfigurations.get(row);
    }

    @Override
    public String getColumnName(int column) {
        return columns[column];
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        switch (columnIndex) {
            case COL_SEL:
                return selected[rowIndex];
            case COL_SOLVER:
                try {
                    return SolverDAO.getById(solverConfigurations.get(rowIndex).getSolverBinary().getIdSolver()).getName();
                } catch (SQLException ex) {
                    return "-";
                }
            case COL_NAME:
                return solverConfigurations.get(rowIndex).getName();
            case COL_HINT:
                return solverConfigurations.get(rowIndex).getHint();
            case COL_COST:
                return solverConfigurations.get(rowIndex).getCost();
            case COL_COSTFUNCTION:
                return solverConfigurations.get(rowIndex).getCost_function();
            case COL_SOLVERBINARY:
                return solverConfigurations.get(rowIndex).getSolverBinary().toString();
            case COL_PARAMETERS:
                Solver solver = null;
                try {
                    solver = SolverDAO.getById(solverConfigurations.get(rowIndex).getSolverBinary().getIdSolver());
                } catch (Exception ex) {
                    return "";
                }
                SolverConfiguration sc = solverConfigurations.get(rowIndex);
                return Util.getSolverParameterString(getParametersBySolverConfig(sc), solver);
        }
        return "";
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        switch (columnIndex) {
            case COL_SEL:
                return Boolean.class;
            case COL_SOLVER:
                return String.class;
            case COL_NAME:
                return String.class;
            case COL_COST:
                return Float.class;
            case COL_COSTFUNCTION:
                return String.class;
            case COL_SOLVERBINARY:
                return String.class;
            case COL_PARAMETERS:
                return String.class;
            default:
                return String.class;
        }
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return columnIndex == 0 ? true : false;
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        if (columnIndex == 0) {
            selected[rowIndex] = (Boolean) aValue;
        }
        fireTableCellUpdated(rowIndex, columnIndex);
    }

    /**
     * Sets the solver configurations for this model
     * @param solverConfigurations an <code>ArrayList</code> of solver configurations used for this model
     */
    public void setSolverConfigurations(ArrayList<SolverConfiguration> solverConfigurations) {
        selected = new boolean[solverConfigurations.size()];
        for (int i = 0; i < selected.length; i++) {
            selected[i] = false;
        }
        this.solverConfigurations = solverConfigurations;
        this.fireTableDataChanged();
    }

    /**
     * Returns the selected solver configurations as an <code>ArrayList</code>
     * @return an <code>ArrayList</code> of solver configurations
     */
    public ArrayList<SolverConfiguration> getSelectedSolverConfigurations() {
        ArrayList<SolverConfiguration> res = new ArrayList<SolverConfiguration>();
        for (int i = 0; i < solverConfigurations.size(); i++) {
            if (selected[i]) {
                res.add(solverConfigurations.get(i));
            }
        }
        return res;
    }
    
    public boolean isSelected(int rowIndex) {
        return selected[rowIndex];
    }
}
