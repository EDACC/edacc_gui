/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edacc.model;

/**
 * This exception is thrown if the requested ExperimentResult object does not exist in the database.
 * @author rretz
 */
class ExperimentResultNotInDBException extends Exception{

    public ExperimentResultNotInDBException() {
        super("Requested ResultProperty object does not exist in the database!");
    }

}
