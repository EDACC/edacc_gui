/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edacc.model;

/**
 * This exception is thrown if the requested ExperimentResultHasResultProperty object does not exist in the database.
 * @author Robert
 */
public class ExpResultHasSolvPropertyNotInDBException extends Exception{

    public ExpResultHasSolvPropertyNotInDBException() {
        super("Requested ExperimentResultHasResultProperty object does not exist in the database!");
    }

}
