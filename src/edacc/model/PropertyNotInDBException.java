/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edacc.model;

import java.sql.SQLException;

/**
 * This exception is thrown if the requested SolverProperty object does not exist in the database
 * @author rretz
 */
public class PropertyNotInDBException extends SQLException{

    public PropertyNotInDBException() {
        super("Requested SolverProperty object does not exist in the database!");
    }

}
