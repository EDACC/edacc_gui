/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edacc.model;

/**
 *
 * @author dgall
 */
public class SolverIsInExperimentException extends Exception {

    public SolverIsInExperimentException(Solver solver) {
        super ("Solver " + solver.getName() + " is used in an experiment!");
    }

}
