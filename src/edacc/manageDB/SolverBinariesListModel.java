/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edacc.manageDB;

import edacc.model.SolverBinaries;
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
