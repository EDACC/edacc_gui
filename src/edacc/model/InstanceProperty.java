/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edacc.model;

import edacc.satinstances.PropertyValueType;
import edacc.satinstances.SATInstance;

/**
 *
 * @author dgall
 */
public abstract class InstanceProperty {

    /**
     *
     * @return the name of the property.
     */
    public abstract String getName();

    /**
     * @return the description of the property.
     */
    public abstract String getDescription();

    /**
     *
     * @param f
     * @return the value of the property for a given instance.
     */
    public abstract Object computeProperty(SATInstance f);

    public abstract PropertyValueType<?> getPropertyValueType();

}
