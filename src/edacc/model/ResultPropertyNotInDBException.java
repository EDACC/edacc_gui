/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edacc.model;

/**
 * This exception is thrown if the requested ResultProperty object does not exist in the database
 * @author rretz
 */
public class ResultPropertyNotInDBException extends Exception{

    public ResultPropertyNotInDBException() {
        super("Requested ResultProperty object does not exist in the database!");
    }

}
