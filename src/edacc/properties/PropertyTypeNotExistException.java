/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edacc.properties;

/**
 * This exception is thrown if the requested SolverPropertyType does not exists.
 * @author rretz
 */
public class PropertyTypeNotExistException extends Exception{

    public PropertyTypeNotExistException() {
        super("Used SolverPropertyType not exist.");
    }

}
