/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edacc.model;

/**
 *
 * @author dgall
 */
class InstanceClassAlreadyInDBException extends Exception {

    public InstanceClassAlreadyInDBException() {
        super("Instance class already in DB!");
    }

}