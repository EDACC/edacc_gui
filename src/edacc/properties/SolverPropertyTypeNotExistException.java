/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edacc.properties;

/**
 * This exception is thrown if the requested SolverPropertyType does not exists.
 * @author rretz
 */
public class SolverPropertyTypeNotExistException extends Exception{

    public SolverPropertyTypeNotExistException() {
        super("Used SolverPropertyType not exist.");
    }

}
