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

    /** Creates the verifier config entry table model */
    public VerifierParameterTableModel() {
        this.parameterInstances = new VerifierParameterInstance[0];
        this.parameters = new VerifierParameter[0];
    }

    /**
     * Sets the parameters for this model
     * @param parameters the parameters <code>ArrayList</code>
     */
    public void setParameters(List<VerifierParameter> parameters) {
        this.parameters = new VerifierParameter[parameters.size()];
        this.parameterInstances = new VerifierParameterInstance[parameters.size()];
        for (int i = 0; i < parameters.size(); i++) {
            this.parameters[i] = parameters.get(i);
            if (parameters.get(i).isMandatory()) {
                parameterInstances[i] = new VerifierParameterInstance();
                parameterInstances[i].setParameter_id(parameters.get(i).getId());
                parameterInstances[i].setValue(parameters.get(i).getDefaultValue() == null ? "" : parameters.get(i).getDefaultValue());
            } else {
                parameterInstances[i] = null;
            }
        }
        this.fireTableDataChanged();
    }

    /**
     * checks if some parameter instances have been changed or there are new parameter instances.
     * @return <code>true</code>, iff some data has been changed
     */
    public boolean isModified() {
        for (int i = 0; i < parameterInstances.length; i++) {
            if (parameterInstances[i].isModified() || parameterInstances[i].isNew() || parameterInstances[i].isDeleted()) {
                return true;
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
    public void assignParameterInstances(List<VerifierParameterInstance> params) {
        if (params == null) {
            for (int i = 0; i < parameterInstances.length; i++) {
                parameterInstances[i] = null;
            }
        } else {
            // TODO: performance
            for (int i = 0; i < params.size(); i++) {
                for (int j = 0; j < parameters.length; j++) {
                    if (!params.get(i).isDeleted() && parameters[j].getId() == params.get(i).getParameter_id()) {
                        if (parameterInstances[j] == null) {
                            parameterInstances[j] = new VerifierParameterInstance();
                        }
                        parameterInstances[j].assign(params.get(i));
                        parameterInstances[j].setSaved();
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
            for (String s : edacc.experiment.Util.constSolverParameters) {
                if (s.equals(parameters[row].getName().toLowerCase())) {
                    return false;
                }
            }
            return parameterInstances[row] != null && !parameterInstances[row].isDeleted();
        }
        return false;
    }

    @Override
    public void setValueAt(Object value, int row, int col) {
        if (col == 0) {
            boolean val = (Boolean) value;
            if (val) {
                if (parameterInstances[row] == null) {
                    parameterInstances[row] = new VerifierParameterInstance();
                    parameterInstances[row].setValue(parameters[row].getDefaultValue() == null ? "" : parameters[row].getDefaultValue());
                    parameterInstances[row].setParameter_id(parameters[row].getId());
                } else {
                    parameterInstances[row].setModified();
                }
            } else {
                if (parameterInstances[row].isNew()) {
                    parameterInstances[row] = null;
                } else {
                    parameterInstances[row].setDeleted();
                }
            }
        } else if (col == 3) {
            parameterInstances[row].setValue((String) value);
        }
        this.fireTableRowsUpdated(row, row);
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        switch (columnIndex) {
            case 0:
                return isSelected(rowIndex);
            case 1:
                return parameters[rowIndex].getName();
            case 2:
                return parameters[rowIndex].getPrefix() == null ? "" : parameters[rowIndex].getPrefix();
            case 3:
                if (parameters[rowIndex].getHasValue()) {
                    return parameterInstances[rowIndex] == null ? "" : parameterInstances[rowIndex].getValue();
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
            if (pi != null && !pi.isDeleted()) {
                res.add(pi);
            }
        }
        return res;
    }

    public boolean isSelected(int row) {
        return !(parameterInstances[row] == null || parameterInstances[row].isDeleted());
    }

    public VerifierParameter getParameterAt(int row) {
        return parameters[row];
    }

    public String getValueAt(int row) {
        return parameterInstances[row].getValue();
    }
}
