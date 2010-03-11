/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edacc.manageDB;

import edacc.EDACCManageDBMode;
import edacc.model.NoConnectionToDBException;
import edacc.model.Parameter;
import edacc.model.ParameterDAO;
import edacc.model.Solver;
import java.sql.SQLException;

/**
 *
 * @author gregor
 */
public class ManageDBParameters {

    private EDACCManageDBMode gui;
    private ParameterTableModel parameterTableModel;
    private Parameter currentParameter;


    public ManageDBParameters(EDACCManageDBMode gui, ParameterTableModel parameterTableModel){
        this.gui = gui;
        this.parameterTableModel = parameterTableModel;
    }

    public void saveParameters(Solver s) throws NoConnectionToDBException, SQLException{
        for(Parameter p : parameterTableModel.getParamtersOfSolver(s)){
            ParameterDAO.saveParameterForSolver(s, p);
        }
    }

    public void newParam() {
        parameterTableModel.addParameter(new Parameter());
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
    }

}
