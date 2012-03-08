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
public class InstancesNotFoundException extends Exception {
    ArrayList<String[]> instanceError;

    public ArrayList<String[]> getInstanceError() {
        return instanceError;
    }
    
    public InstancesNotFoundException(ArrayList<String[]> instanceError) {
        super();
        this.instanceError = instanceError;
    }
    
}
