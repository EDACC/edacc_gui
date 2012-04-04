/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edacc.model;

import java.sql.SQLException;

/**
 * This exception is thrown if the requested ExperimentResult object does not exist in the database.
 * @author rretz
 */
public class ExperimentResultNotInDBException extends SQLException{

    public ExperimentResultNotInDBException() {
        super("Requested ResultProperty object does not exist in the database!");
    }

}
