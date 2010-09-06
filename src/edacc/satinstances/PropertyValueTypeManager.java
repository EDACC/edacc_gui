/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edacc.satinstances;

import java.util.Vector;

/**
 *
 * @author dgall
 */
public class PropertyValueTypeManager {

    private static PropertyValueTypeManager instance;

    private PropertyValueTypeManager() { }

    public static PropertyValueTypeManager getInstance() {
        if (instance == null)
            instance = new PropertyValueTypeManager();
        return instance;
    }

    public PropertyValueType<?> getPropertyValueTypeByName(String name) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public Vector<PropertyValueType> getAll(){
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
