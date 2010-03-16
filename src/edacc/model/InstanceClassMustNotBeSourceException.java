/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edacc.model;

/**
 *
 * @author dgall
 */
class InstanceClassMustNotBeSourceException extends Exception {

    public InstanceClassMustNotBeSourceException() {
        super("The source instance class must have the source flag set on false!");
    }

}
