/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edacc.manageDB;

import edacc.model.Parameter;
import java.util.Vector;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author gregor
 */
public class ParameterTableModel extends AbstractTableModel{
    private final int ORDER = 0;
    private final int NAME = 1;
    private final int PREFIX = 2;

    private String[] colums = {"Order", "Name", "Prefix"};
    private Vector<Parameter> parameters;

    public ParameterTableModel(){
        parameters = new Vector<Parameter>();
    }

    public void removeParameter(Vector<Parameter> params){
        parameters.removeAll(params);
    }

    public void remove(Parameter param){
        parameters.remove(param);
    }

    public int getRowCount() {
        return parameters.size();
    }

    public int getColumnCount() {
        return colums.length;
    }

    @Override
    public String getColumnName(int col){
        return colums[col];
    }

    public Object getValueAt(int rowIndex, int columIndex) {
        Parameter p = parameters.get(rowIndex);
        switch(columIndex){
            case ORDER:
                return p.getOrder();
            case NAME:
                return p.getName();
            case PREFIX:
                return p.getPrefix();
        }
        return null;
    }

    public void addParameter(Parameter param){
        parameters.add(param);
    }

    public Vector<Parameter> getParamters(){
        return (Vector<Parameter>) parameters.clone();
    }

    public Parameter getParameter(int rowIndex){
        if(rowIndex >= 0 && rowIndex < parameters.size())
            return parameters.get(rowIndex);
        else
            return null;
    }

}
