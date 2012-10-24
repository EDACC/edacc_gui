/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edacc.model;

/**
 * Is thrown if an duplicate instance is found in the database with the same name as the instance to add.
 * @author rretz
 */
public class InstanceDuplicateNameException extends Exception {

    public InstanceDuplicateNameException() {
        super();
    }
    
}
