/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edacc.manageDB;

import edacc.EDACCApp;
import edacc.EDACCManageDBMode;
import edacc.model.DatabaseConnector;
import edacc.model.NoConnectionToDBException;
import edacc.model.Parameter;
import edacc.model.ParameterDAO;
import edacc.model.Solver;
import edacc.parametergrapheditor.ParameterGraphEditor;
import java.sql.SQLException;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.xml.bind.JAXBException;

/**
 *
 * @author gregor
 */
public class ManageDBParameters implements Observer {

    private EDACCManageDBMode gui;
    private ParameterTableModel parameterTableModel;
    private Parameter currentParameter;


    public ManageDBParameters(EDACCManageDBMode gui, ParameterTableModel parameterTableModel){
        this.gui = gui;
        this.parameterTableModel = parameterTableModel;
        DatabaseConnector.getInstance().addObserver(this);
    }

    /**
     * Loads all parameters of the given solvers to the parameter table model.
     * @param solvers
     */
    public void loadParametersOfSolvers(Vector<Solver> solvers) throws SQLException {
        parameterTableModel.clear();
        for (Solver s : solvers) {
            for (Parameter p : ParameterDAO.getParameterFromSolverId(s.getId())) {
                parameterTableModel.addParameter(s, p);
            }
        }
    }

    public void saveParameters(Solver s) throws NoConnectionToDBException, SQLException, JAXBException{
        for(Parameter p : parameterTableModel.getParamtersOfSolver(s)){
            ParameterDAO.saveParameterForSolver(s, p);
        }
    }

    public void newParam() {
        Parameter p = new Parameter();
        p.setOrder(parameterTableModel.getHighestOrder() + 1);
        parameterTableModel.addParameter(p);
    }

    public Parameter addParameter(Solver s) {
        Parameter p = new Parameter();
        parameterTableModel.addParameter(s, p);
        return p;
    }

    public void setCurrentSolver(Solver currentSolver) {
        parameterTableModel.setCurrentSolver(currentSolver);      
    }

    void showParameter(int index) {
        currentParameter = parameterTableModel.getParameter(index);
        if (currentParameter != null) {
            gui.showParameterDetails(currentParameter);
        }
    }

    /**
     * removes the parameters of the current solver.
     */
    public void removeParameters() {
        parameterTableModel.removeParametersOfSolver(parameterTableModel.getCurrentSolver());
        currentParameter = null;
    }

    /**
     * Removes the parameters of a specified solver.
     * @param s
     */
    public void removeParameters(Solver s) {
        parameterTableModel.removeParametersOfSolver(s);
        if (s == parameterTableModel.getCurrentSolver())
            currentParameter = null;
    }

    public void removeParameter(Parameter param) throws SQLException {
        parameterTableModel.remove(param);
        ParameterDAO.delete(param);
        currentParameter = null;
    }

    /**
     * Adds the default parameter "instance" for Solver s.
     * In future, this method will maybe add some more default parameters, each
     * solver should have.
     * @param s
     */
    public void addDefaultParameters(Solver s) {
        Parameter p = new Parameter();
        p.setName("instance");
        p.setPrefix("--instance");
        parameterTableModel.addParameter(s, p);
    }

    /**
     * Checks, if a parameter with the given name already exists for the
     * current solver. Won't check the DB for performance reasons!
     * @param name
     * @return
     * @throws NoConnectionToDBException
     * @throws SQLException
     */
    public boolean parameterExists(String name) throws NoConnectionToDBException, SQLException {
        Vector<String> parameterNames = new Vector<String>();
        for (Parameter p : parameterTableModel.getParametersOfCurrentSolver())
            if (p != currentParameter)
                parameterNames.add(p.getName());
        return parameterNames.contains(name); //|| ParameterDAO.parameterExistsForSolver(name, parameterTableModel.getCurrentSolver());
    }

    /**
     * Checks, if a parameter with the given prefix already exists for the
     * current solver. Won't check the DB for performance reasons!
     * @param prefix
     * @return
     * @throws NoConnectionToDBException
     * @throws SQLException
     */
    public boolean parameterPrefixExists(String prefix) throws NoConnectionToDBException, SQLException {
        Vector<String> parameterPrefixes = new Vector<String>();
        for (Parameter p : parameterTableModel.getParametersOfCurrentSolver())
            if (p != currentParameter)
                parameterPrefixes.add(p.getPrefix());
        return parameterPrefixes.contains(prefix); 
    }

    @Override
    public void update(Observable o, Object arg) {
        parameterTableModel.clear();
    }

    void rehash(Solver s, Vector<Parameter> params) {
        parameterTableModel.removeParametersOfSolver(s);
        for (Parameter p : params) {
            parameterTableModel.addParameter(s, p);
        }
    }

    Vector<Parameter> getParametersOfSolver(Solver s) {
        return parameterTableModel.getParamtersOfSolver(s);
    }

    public void showParameterGraphEditor() throws SQLException, JAXBException {
        Solver solver = parameterTableModel.getCurrentSolver();        
        if (solver.isSaved()) {
            ParameterGraphEditor dialog = new ParameterGraphEditor(EDACCApp.getApplication().getMainFrame(), true, solver);
            dialog.setLocationRelativeTo(EDACCApp.getApplication().getMainFrame());
            EDACCApp.getApplication().show(dialog);
        } else {
            JOptionPane.showMessageDialog(EDACCApp.getApplication().getMainFrame(), "You have to save the solver before editing the parameter graph.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void moveUp(int[] selectedIndices, JTable paramTable) {
        if (selectedIndices == null || selectedIndices.length < 1)
            return;
        int minIndex = selectedIndices[0]; // index of the first selected element
        int maxIndex = selectedIndices[selectedIndices.length - 1]; // index of the last selected element
        Parameter first = parameterTableModel.getParameter(minIndex); // first selected element
        Parameter last = parameterTableModel.getParameter(maxIndex); // last selected element
        int aboveIndex = -1;
        Parameter above = null;
        try { // try to find element above the first selected element
            aboveIndex = paramTable.convertRowIndexToModel(paramTable.convertRowIndexToView(minIndex) - 1);
            above = parameterTableModel.getParameter(aboveIndex);
        } catch (IndexOutOfBoundsException e) { // first element is already the highest element
            return; // => do nothing
        }

        
        int diff; // difference between first parameter and the parameter above the selected block
        while ((diff = first.getOrder() - above.getOrder()) == 0) { // if difference equals zero, nothing would happen => find next higher element with smaller order
            try {
                aboveIndex = paramTable.convertRowIndexToModel(paramTable.convertRowIndexToView(aboveIndex) - 1);
                above = parameterTableModel.getParameter(aboveIndex);
            } catch (IndexOutOfBoundsException e) { // found element is the highest element
                above = null;
                break;
            }
        }
        if (above != null) { 
            // switch orders of first parameter and the parameter above the block
            int lastOrder = last.getOrder();
            first.setOrder(above.getOrder());
            above.setOrder(lastOrder);
        } else { // no element above the first selected element with smaller order found => decrease order
            first.setOrder(first.getOrder() - 1);
            diff = 1;
        }
        if (selectedIndices.length >= 2)
            for (int i = 1; i < selectedIndices.length; i++) {
                Parameter p = parameterTableModel.getParameter(selectedIndices[i]);
                p.setOrder(p.getOrder() - diff);
            }
        parameterTableModel.fireTableDataChanged();
    }

    public void moveDown(int[] selectedIndices, JTable paramTable) {
        if (selectedIndices == null || selectedIndices.length < 1)
            return;
        int minIndex = selectedIndices[0]; // index of the first selected element
        int maxIndex = selectedIndices[selectedIndices.length - 1]; // index of the last selected element
        Parameter first = parameterTableModel.getParameter(minIndex); // first selected element
        Parameter last = parameterTableModel.getParameter(maxIndex); // last selected element
        int belowIndex = -1;
        Parameter below = null;
        try { // try to find element above the first selected element
            belowIndex = paramTable.convertRowIndexToModel(paramTable.convertRowIndexToView(maxIndex) + 1);
            below = parameterTableModel.getParameter(belowIndex);
        } catch (IndexOutOfBoundsException e) { // first element is already the highest element
            return; // => do nothing
        }
        
        int diff; // difference between first parameter and the parameter above the selected block
        while ((diff = last.getOrder() - below.getOrder()) == 0) { // if difference equals zero, nothing would happen => find next higher element with smaller order
            try {
                belowIndex = paramTable.convertRowIndexToModel(paramTable.convertRowIndexToView(belowIndex) + 1);
                below = parameterTableModel.getParameter(belowIndex);
            } catch (IndexOutOfBoundsException e) { // found element is the highest element
                below = null;
                break;
            }
        }
        if (below != null) {
            // switch orders of first parameter and the parameter above the block
            int firstOrder = first.getOrder();
            below.setOrder(firstOrder);
        } else { // no element above the first selected element with smaller order found => decrease order
            first.setOrder(first.getOrder() - 1);
            diff = 1;
        }
        for (int i = 0; i < selectedIndices.length; i++) {
            Parameter p = parameterTableModel.getParameter(selectedIndices[i]);
            p.setOrder(p.getOrder() - diff);
        }
        parameterTableModel.fireTableDataChanged();
    }
}
