/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edacc.manageDB;

import edacc.model.Solver;
import edacc.model.SolverBinaries;
import edacc.model.SolverBinariesModel;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractListModel;

/**
 *
 * @author dgall
 */
public class SolverBinariesListModel extends AbstractListModel {

    private SolverBinaries solverBin;

    public SolverBinariesListModel(SolverBinaries solverBin) {
        super();
        this.solverBin = solverBin;
    }

    @Override
    public int getSize() {
        return solverBin.getBinaryFiles().length;
    }

    @Override
    public Object getElementAt(int index) {
        return solverBin.getBinaryFiles()[index];
    }

}
