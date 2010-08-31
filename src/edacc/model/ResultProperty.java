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
public class ResultProperty extends BaseModel implements IntegerPKModel, Property{

   private int id;
   private String name;
   private String prefix;
   private String description;
   private PropertyValueType valueType;

    public void setValueType(PropertyValueType valueType) {
        this.valueType = valueType;
        if (this.isSaved())
            this.setModified();
    }


    public void setName(String name){
        if (this.isSaved())
            this.setModified();
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
        if (this.isSaved())
            this.setModified();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
        if (this.isSaved())
            this.setModified();
    }

    @Override
    public int getId() {
        return id;
    }

    public void setId(int id){
        this.id = id;
        if (this.isSaved())
            this.setModified();

    }

}
