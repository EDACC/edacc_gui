/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edacc.manageDB;

/**
 *
 * @author dgall
 */
public class NoSolverBinarySpecifiedException extends Exception {

    public NoSolverBinarySpecifiedException() {
        this ("You must specify a binary for the solver!");
    }

    public NoSolverBinarySpecifiedException(String msg) {
        super (msg);
    }

}
