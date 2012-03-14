/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edacc.experiment;

import edacc.model.VerifierParameter;
import edacc.model.VerifierParameterInstance;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author simon
 */
public class VerifierParameterTableModel extends ThreadSafeDefaultTableModel {
    
    private String[] columns = {"Selected", "Parameter Name", "Prefix", "Value", "Order"};
    private VerifierParameter[] parameters;
    private VerifierParameterInstance[] parameterInstances;
    private String[] values;
    private Boolean[] selected;

    /** Creates the solver config entry table model */
    public VerifierParameterTableModel() {
        this.parameterInstances = new VerifierParameterInstance[0];
        this.parameters = new VerifierParameter[0];
        this.values = new String[0];
    }

    /**
     * Sets the parameters for this model
     * @param parameters the parameters <code>ArrayList</code>
     */
    public void setParameters(List<VerifierParameter> parameters) {
        this.parameters = new VerifierParameter[parameters.size()];
        this.parameterInstances = new VerifierParameterInstance[parameters.size()];
        this.selected = new Boolean[parameters.size()];
        this.values = new String[parameters.size()];
        for (int i = 0; i < parameters.size(); i++) {
            this.parameters[i] = parameters.get(i);
            this.values[i] = parameters.get(i).getDefaultValue() == null ? "" : parameters.get(i).getDefaultValue();
            if (parameters.get(i).isMandatory()) {
                this.selected[i] = true;
            } else {
                this.selected[i] = false;
            }
            this.parameterInstances[i] = null;
        }
        this.fireTableDataChanged();
    }

    /**
     * checks if some parameter instances have been changed or there are new parameter instances.
     * @return <code>true</code>, iff some data has been changed
     */
    public boolean isModified() {
        for (int i = 0; i < parameterInstances.length; i++) {
            if (selected[i]) {
                if (parameterInstances[i] == null) {
                    // new parameter instance
                    return true;
                }
                if (parameters[i].getId() == parameterInstances[i].getParameter_id()) {
                    // modified parameter instance
                    if (parameters[i].getHasValue() && !values[i].equals(parameterInstances[i].getValue())) {
                        return true;
                    }
                }
            } else {
                if (parameterInstances[i] != null) {
                    // deleted parameter instance
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Sets the parameter instances for this model. Tries to find the parameter 
     * for each parameter instance and sets the value according to the parameter 
     * instance. If a parameter is found for a specific parameter instance, this
     * parameter will be selected in this model.
     * @param params parameter instances as <code>ArrayList</code>
     */
    public void setParameterInstances(List<VerifierParameterInstance> params) {
        if (params == null) {
            for (int i = 0; i < parameterInstances.length; i++) {
                parameterInstances[i] = null;
            }
        } else {
            // TODO: performance
            for (int i = 0; i < params.size(); i++) {
                for (int j = 0; j < parameters.length; j++) {
                    if (parameters[j].getId() == params.get(i).getParameter_id()) {
                        parameterInstances[j] = params.get(i);
                        this.values[j] = params.get(i).getValue();
                        this.selected[j] = true;
                    }
                }
            }
        }
        this.fireTableDataChanged();
    }

    /**
     * Tries to find the given parameter instance. If found, it will be removed from the model.
     * @param pi the parameter instance to be removed
     */
    public void removeParameterInstance(VerifierParameterInstance pi) {
        for (int i = 0; i < parameterInstances.length; i++) {
            if (parameterInstances[i] == pi) {
                parameterInstances[i] = null;
            }
        }
    }

    @Override
    public int getRowCount() {
        return parameters == null ? 0 : parameters.length;
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
        if (col == 0) {
            return !parameters[row].isMandatory();
        }
        if (col == 3 && parameters[row].getHasValue()) {
            if ("instance".equals(parameters[row].getName()) || "seed".equals(parameters[row].getName())) {
                return false;
            }
            return selected[row];
        }
        return false;
    }

    @Override
    public void setValueAt(Object value, int row, int col) {
        if (col == 0) {
            selected[row] = (Boolean) value;
            if (!selected[row]) {
                values[row] = "";
            }
        } else if (col == 3) {
            values[row] = (String) value;
        }
        this.fireTableRowsUpdated(row, row);
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        switch (columnIndex) {
            case 0:
                return selected[rowIndex];
            case 1:
                return parameters[rowIndex].getName();
            case 2:
                return parameters[rowIndex].getPrefix() == null ? "" : parameters[rowIndex].getPrefix();
            case 3:
                if (parameters[rowIndex].getHasValue()) {
                    return values[rowIndex];
                } else {
                    return "toggleable flag";
                }
            case 4:
                return parameters[rowIndex].getOrder();
            case 5:
                return parameters[rowIndex];
            case 6:
                return parameterInstances[rowIndex];
            default:
                return "";
        }
    }

    /**
     * Returns all parameters in this model
     * @return <code>ArrayList</code> of parameters
     */
    public ArrayList<VerifierParameter> getParameters() {
        ArrayList<VerifierParameter> res = new ArrayList<VerifierParameter>();
        res.addAll(Arrays.asList(parameters));
        return res;
    }

    /**
     * Returns all parameter instances in this model
     * @return <code>ArrayList</code> of parameter instances
     */
    public ArrayList<VerifierParameterInstance> getParameterInstances() {
        ArrayList<VerifierParameterInstance> res = new ArrayList<VerifierParameterInstance>();
        for (VerifierParameterInstance pi : parameterInstances) {
            if (pi != null) {
                res.add(pi);
            }
        }
        return res;
    }
    
    public boolean isSelected(int row) {
        return selected[row];
    }
    
    public VerifierParameter getParameterAt(int row) {
        return parameters[row];
    }
    
    public String getValueAt(int row) {
        return values[row];
    }
}
