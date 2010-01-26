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
    private ParameterTableModel parmeterTableModel;
    private Parameter currentParam;


    public ManageDBParameters(EDACCManageDBMode gui, ParameterTableModel parameterTableModel){
        this.gui = gui;
        this.parmeterTableModel = parameterTableModel;
    }

    public void saveParameters(Solver s) throws NoConnectionToDBException, SQLException{
        for(Parameter p : parmeterTableModel.getParamters()){
            ParameterDAO.saveParameterForSolver(s, p);
        }
    }

}
