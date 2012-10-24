/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edacc.manageDB;
/**
 *
 * @author rretz
 */
public class NoInstancesToSaveException extends Exception{

    public NoInstancesToSaveException() {
        this("There is no instance to save in the table.");
    }

     public NoInstancesToSaveException(String msg) {
         super(msg);
    }
}
