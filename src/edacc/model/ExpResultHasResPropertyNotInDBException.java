/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edacc.model;

/**
 * This exception is thrown if the requested ExperimentResultHasResultProperty object does not exist in the database.
 * @author Robert
 */
class ExpResultHasResPropertyNotInDBException extends Exception{

    public ExpResultHasResPropertyNotInDBException() {
        super("Requested ExperimentResultHasResultProperty object does not exist in the database!");
    }

}