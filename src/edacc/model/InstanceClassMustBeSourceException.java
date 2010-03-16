/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edacc.model;

/**
 *
 * @author dgall
 */
class InstanceClassMustBeSourceException extends Exception {

    public InstanceClassMustBeSourceException() {
        super("The source instance class must have the source flag set on true!");
    }

}
