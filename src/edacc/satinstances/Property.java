/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edacc.satinstances;

/**
 * An interface every instance propery must implement for computing the property
 * value.
 * @author dgall
 */
public interface Property {

    /**
     *
     * @return the name of the property.
     */
    public String getName();

    /**
     * @return the description of the property.
     */
    public String getDescription();

    /**
     *
     * @param f
     * @return the value of the property for a given instance.
     */
    public Object computeProperty(PropertyInput f);

    public PropertyValueType<?> getPropertyValueType();

}
