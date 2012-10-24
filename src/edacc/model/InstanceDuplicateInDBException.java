/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edacc.model;

import java.util.ArrayList;

/**
 *
 * @author rretz
 */
public class InstanceDuplicateInDBException extends Exception {
    private ArrayList<Instance> duplicates;

    public InstanceDuplicateInDBException(ArrayList<Instance> duplicates) {
        this.duplicates = duplicates;
    }
    
    public ArrayList<Instance> getDuplicates(){
        return duplicates;
    }
    
}
