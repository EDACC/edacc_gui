/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edacc.manageDB;

/**
 *
 * @author dgall
 */
class SolverAlreadyInDBException extends Exception {

    public SolverAlreadyInDBException() {
        super ("You are trying to add a duplicate solver!");
    }

}
