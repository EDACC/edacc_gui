/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edacc.manageDB;

import edacc.model.Parameter;
import edacc.model.Solver;
import java.util.HashMap;
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
    private final int HASVALUE = 3;
    private Solver currentSolver;

    private String[] colums = {"Order", "Name", "Prefix", "Boolean"};
    private HashMap<Solver, Vector<Parameter>> parameters;

    public ParameterTableModel(){
        parameters = new HashMap<Solver, Vector<Parameter>>();

    }

    /**
     * Sets the current solver the user has chosen.
     * @param solver
     */
    public void setCurrentSolver(Solver solver) {
        this.currentSolver = solver;
        if (currentSolver == null)
            return;
        Vector<Parameter> params = parameters.get(solver);
        if (params == null) {
            params = new Vector<Parameter>();
            parameters.put(solver, params);
        }

    }

    /**
     * @return the current solver.
     */
    public Solver getCurrentSolver() {
        return currentSolver;
    }
    
    public void remove(Parameter param){
        parameters.get(currentSolver).remove(param);
    }

    public int getRowCount() {
        if (currentSolver == null)
            return 0;
        Vector<Parameter> params = parameters.get(currentSolver);
        if (params == null)
            return 0;
        return params.size();
    }

    public int getColumnCount() {
        return colums.length;
    }

    @Override
    public String getColumnName(int col){
        return colums[col];
    }

    public Object getValueAt(int rowIndex, int columIndex) {
        Parameter p = parameters.get(currentSolver).get(rowIndex);
        switch(columIndex){
            case ORDER:
                return p.getOrder();
            case NAME:
                return p.getName();
            case PREFIX:
                return p.getPrefix();
            case HASVALUE:
                return p.getHasValue()?"":"\u2713";//p.getHasValue();
        }
        return null;
    }

    /**
     * Adds a parameter for the current solver. If no current solver is specified,
     * this method will do nothing!
     * @param param
     */
    public void addParameter(Parameter param) {
        if (currentSolver == null)
            return;
        addParameter(currentSolver, param);
    }

    /**
     * Adds a parameter for a solver.
     * @param solver
     * @param param
     */
    public void addParameter(Solver solver, Parameter param){
        Vector<Parameter> params = parameters.get(solver);
        if (params == null) {
            params = new Vector<Parameter>();
            parameters.put(solver, params);
        }

        params.add(param);
    }

    /**
     * returns all parameters of a solver.
     * @param s
     * @return
     */
    public Vector<Parameter> getParamtersOfSolver(Solver s){
        if (!parameters.containsKey(s)) return new Vector<Parameter>();
        return (Vector<Parameter>) parameters.get(s).clone();
    }

    /**
     * returns all parameters of the current solver.
     * @return
     */
    public Vector<Parameter> getParametersOfCurrentSolver(){
        if (currentSolver == null)
            return null;
        return getParamtersOfSolver(currentSolver);
    }
    
    public Parameter getParameter(int rowIndex){
        if(rowIndex >= 0 && rowIndex < parameters.get(currentSolver).size()) {
            return parameters.get(currentSolver).get(rowIndex);
        }
        else
            return null;
    }

    /**
     * Removes all parameters of the given solver from the table model.
     * @param s
     */
    public void removeParametersOfSolver(Solver s) {
        parameters.remove(s);
    }

    public void clear() {
        parameters.clear();
        fireTableDataChanged();
    }
}
