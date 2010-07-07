package edacc.experiment;

import edacc.model.Parameter;
import edacc.model.ParameterDAO;
import edacc.model.ParameterInstance;
import edacc.model.ParameterInstanceDAO;
import edacc.model.SolverConfiguration;
import java.util.HashMap;
import java.util.Vector;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author simon
 */
public class SolverConfigurationTableModel extends AbstractTableModel {
    private String[] columns = {"Selected", "Solvername", "Parameters"};
    public boolean[] selected;
    private Vector<SolverConfiguration> solverConfigurations;
    private HashMap<Integer, Vector<ParameterInstance>> parameterInstances;
    
    public int getRowCount() {
        return solverConfigurations==null?0:solverConfigurations.size();
    }

    public int getColumnCount() {
        return columns.length;
    }

        /**
     * Returns all parameter instances for that job
     * @param row
     * @return null, if there was an error
     */
    public Vector<ParameterInstance> getParameters(int row) {
        try {
            SolverConfiguration sc = solverConfigurations.get(row);
            Vector<ParameterInstance> params = parameterInstances.get(sc.getId());
            if (params == null) {
                params = ParameterInstanceDAO.getBySolverConfigId(sc.getId());
                parameterInstances.put(sc.getId(), params);
            }
            return params;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Transforms the parameters obtained by getParameters to a string
     * @param row
     * @return
     */
    public String getParameterString(int row) {
        try {
            Vector<ParameterInstance> params = getParameters(row);
            if (params == null) {
                return "";
            }
            String paramString = "";

            for (ParameterInstance param : params) {
                Parameter solverParameter = ParameterDAO.getById(param.getParameter_id());
                if (solverParameter.getHasValue())
                    paramString += solverParameter.getPrefix() + " " + param.getValue();
                else
                    paramString += solverParameter.getPrefix()+ " ";

                if (params.lastElement() != param) {
                    paramString += " ";
                }
            }
            return paramString;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public String getColumnName(int column) {
        return columns[column];
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        switch (columnIndex) {
            case 0:
                return selected[rowIndex];
            case 1:
                return solverConfigurations.get(rowIndex).getName();
            case 2:
                return getParameterString(rowIndex);
        }
        return "";
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        if (solverConfigurations == null || solverConfigurations.size() == 0) {
            return String.class;
        } else {
            return getValueAt(0, columnIndex).getClass();
        }
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return columnIndex==0?true:false;
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        if (columnIndex == 0) {
            selected[rowIndex] = (Boolean) aValue;
        }
    }

    public void setSolverConfigurations(Vector<SolverConfiguration> solverConfigurations) {
        parameterInstances = new HashMap<Integer, Vector<ParameterInstance>>();
        selected = new boolean[solverConfigurations.size()];
        for (int i = 0; i < selected.length; i++)
            selected[i] = false;
        this.solverConfigurations = solverConfigurations;
    }

    public Vector<SolverConfiguration> getSelectedSolverConfigurations() {
        Vector<SolverConfiguration> res = new Vector<SolverConfiguration>();
        for (int i = 0; i < solverConfigurations.size(); i++) {
            if (selected[i]) {
                res.add(solverConfigurations.get(i));
            }
        }
        return res;
    }
}