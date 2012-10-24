/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edacc.satinstances;

import java.util.ArrayList;

/**
 *
 * @author rretz
 */
public class PropertyValueTypeAlreadyExistsException extends Exception {
    
    private ArrayList<String> duplicateEntry;

    public ArrayList<String> getDuplicateEntry() {
        return duplicateEntry;
    }
    
    public PropertyValueTypeAlreadyExistsException(ArrayList<String> duplicateEntry) {
        this.duplicateEntry = duplicateEntry;
    }
    
}
