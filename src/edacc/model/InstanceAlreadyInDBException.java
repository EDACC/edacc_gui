/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edacc.model;

/**
 *
 * @author rretz
 */
public class InstanceAlreadyInDBException extends Exception{
    private Instance duplicate;

    public Instance getDuplicate() {
        return duplicate;
    }
    
    public InstanceAlreadyInDBException() {
    }

    InstanceAlreadyInDBException(Instance duplicate) {
        this.duplicate = duplicate;
    }
    

}
