/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edacc.model;

/**
 *
 * @author dgall
 */
public class SolverNotInDBException extends Exception {

    public SolverNotInDBException(Solver solver) {
        super("The solver " + solver.getName() + " isn't in the database.");
    }

}
