/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edacc.manageDB;

import edacc.model.Solver;
import edacc.model.SolverDAO;
import java.util.LinkedList;
import java.util.Vector;
import javax.swing.AbstractListModel;

/**
 *
 * @author dgall
 */
public class SolverListModel extends AbstractListModel {

    private LinkedList<Solver> solvers;

    public SolverListModel(LinkedList<Solver> solvers) {
        this.solvers = (LinkedList<Solver>) solvers.clone();
    }
    
    @Override
    public int getSize() {
        return solvers.size();
    }

    @Override
    public Object getElementAt(int index) {
        return solvers.get(index);
    }

    public void removeSolver(Solver s) {
        solvers.remove(s);
    }

}
