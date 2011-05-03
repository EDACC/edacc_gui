package edacc.experiment;

import edacc.model.ParameterInstance;
import edacc.model.ParameterInstanceDAO;
import edacc.model.SolverConfiguration;
import edacc.model.SolverDAO;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author simon
 */
public class SolverConfigurationTableModel extends AbstractTableModel {

    public static final int COL_SEL = 0;
    public static final int COL_SOLVER = 1;
    public static final int COL_NAME = 2;
    public static final int COL_PARAMETERS = 3;
    private String[] columns = {"Selected", "Solver", "Solver Configuration", "Parameters"};
    public boolean[] selected;
    private ArrayList<SolverConfiguration> solverConfigurations;
    private HashMap<Integer, ArrayList<ParameterInstance>> parameterInstances;

    @Override
    public int getRowCount() {
        return solverConfigurations == null ? 0 : solverConfigurations.size();
    }

    @Override
    public int getColumnCount() {
        return columns.length;
    }

    /**
     * Returns all parameter instances for that job
     * @param row
     * @return null, if there was an error
     */
    public ArrayList<ParameterInstance> getParameters(int row) {
        try {
            SolverConfiguration sc = solverConfigurations.get(row);
            ArrayList<ParameterInstance> params = parameterInstances.get(sc.getId());
            if (params == null) {
                params = ParameterInstanceDAO.getBySolverConfigId(sc.getId());
                parameterInstances.put(sc.getId(), params);
            }
            return params;
        } catch (Exception e) {
            return null;
        }
    }

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
            case COL_PARAMETERS:
                return Util.getParameterString(getParameters(rowIndex));
        }
        return "";
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        if (solverConfigurations == null || solverConfigurations.isEmpty()) {
            return String.class;
        } else {
            return getValueAt(0, columnIndex).getClass();
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
    }

    public void setSolverConfigurations(ArrayList<SolverConfiguration> solverConfigurations) {
        parameterInstances = new HashMap<Integer, ArrayList<ParameterInstance>>();
        selected = new boolean[solverConfigurations.size()];
        for (int i = 0; i < selected.length; i++) {
            selected[i] = false;
        }
        this.solverConfigurations = solverConfigurations;
    }

    public ArrayList<SolverConfiguration> getSelectedSolverConfigurations() {
        ArrayList<SolverConfiguration> res = new ArrayList<SolverConfiguration>();
        for (int i = 0; i < solverConfigurations.size(); i++) {
            if (selected[i]) {
                res.add(solverConfigurations.get(i));
            }
        }
        return res;
    }
}
