/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edacc.model;

import edacc.satinstances.Property;
import edacc.satinstances.PropertyInput;
import edacc.satinstances.PropertyValueType;

/**
 *
 * @author rretz
 */
public class ResultProperty implements Property{

   private String name;
   private String prefix;
   private String description;


    public void setName(String name){
        this.name = name;
    }
    
    @Override
    public String getName() {
        return name;
    }

    @Override
    public Object computeProperty(PropertyInput f) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public PropertyValueType<?> getPropertyValueType() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     *
     * @return the prefix of the ResultProperty
     */
    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
