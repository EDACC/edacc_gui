/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edacc.model;

/**
 * This exception is thrown if the requested SolverProperty object does not exist in the database
 * @author rretz
 */
public class SolverPropertyNotInDBException extends Exception{

    public SolverPropertyNotInDBException() {
        super("Requested SolverProperty object does not exist in the database!");
    }

}
