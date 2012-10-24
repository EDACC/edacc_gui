/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edacc.manageDB;

/**
 *
 * @author balint
 */
public class NoSolverNameSpecifiedException extends Exception {
 

    public NoSolverNameSpecifiedException() {
        super ("You must specify a name for the solver!");
    }

}

