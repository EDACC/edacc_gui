/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edacc.experiment;

import edacc.model.Parameter;
import edacc.model.ParameterInstance;
import java.util.Vector;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author simon
 */
public class SolverConfigEntryTableModel extends AbstractTableModel {

    private String[] columns = {"Parameter Name", "Prefix", "Value", "Order", "Selected"};
    private Parameter[] parameters;
    private ParameterInstance[] parameterInstances;
    private String[] values;
    private Boolean[] selected;

    public SolverConfigEntryTableModel() {
        this.parameterInstances = new ParameterInstance[0];
        this.parameters = new Parameter[0];
        this.values = new String[0];
    }

    public void setParameters(Vector<Parameter> parameters) {
        this.parameters = new Parameter[parameters.size()];
        this.parameterInstances = new ParameterInstance[parameters.size()];
        this.selected = new Boolean[parameters.size()];
        this.values = new String[parameters.size()];
        for (int i = 0; i < parameters.size(); i++) {
            this.parameters[i] = parameters.get(i);
            this.values[i] = parameters.get(i).getValue();
            this.selected[i] = false;
            this.parameterInstances[i] = null;
        }
        this.fireTableDataChanged();
    }

    public void setParameterInstances(Vector<ParameterInstance> params) {
        for (int i = 0; i < params.size(); i++) {
            for (int j = 0; j < parameters.length; j++) {

                if (parameters[j].getId() == params.get(i).getParameter_id()) {
                    parameterInstances[j] = params.get(i);
                    this.values[j] = params.get(i).getValue();
                    this.selected[j] = true;
                }
            }
        }
        this.fireTableDataChanged();
    }

    public void removeParameterInstance(ParameterInstance pi) {
        for (int i = 0; i < parameterInstances.length; i++) {
            if (parameterInstances[i] == pi) {
                parameterInstances[i] = null;
            }
        }
    }

    public int getRowCount() {
        return parameters.length;
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
        return getValueAt(0, col).getClass();
    }

    @Override
    public boolean isCellEditable(int row, int col) {
        if (col == 4 || (col == 2 && ((Parameter)getValueAt(row, 5)).getHasValue())) {
            return true;
        }
        return false;
    }

    @Override
    public void setValueAt(Object value, int row, int col) {
        if (col == 4) {
            selected[row] = (Boolean) value;
        } else if (col == 2) {
            values[row] = (String) value;
        }
        fireTableCellUpdated(row, col);
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        switch (columnIndex) {
            case 0:
                return parameters[rowIndex].getName();
            case 1:
                return parameters[rowIndex].getPrefix();
            case 2:
                if (parameters[rowIndex].getHasValue()) 
                    return values[rowIndex];
                else
                    return "togglable flag";
            case 3:
                return parameters[rowIndex].getOrder();
            case 4:
                return selected[rowIndex];
            case 5:
                return parameters[rowIndex];
            case 6:
                return parameterInstances[rowIndex];
            default:
                return "";
        }
    }
}
