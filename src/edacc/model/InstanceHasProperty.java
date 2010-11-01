/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edacc.model;

/**
 *
 * @author dgall
 */
public class InstanceHasProperty extends BaseModel implements IntegerPKModel {

    private int id;
    private Instance instance;
    private Property instanceProperty;
    private String value;

    protected InstanceHasProperty(Instance instance, Property instanceProperty, String value) {
        this.instance = instance;
        this.instanceProperty = instanceProperty;
        this.value = value;
    }
    
    public String getValue() {
        return value;
    }

    public Instance getInstance() {
        return instance;
    }

    public Property getProperty() {
        return instanceProperty;
    }

    @Override
    public int getId() {
        return id;
    }

    protected void setId(int id) {
        this.id = id;
    }

    public void setValue(String value) {
        this.value = value;
        if (this.isSaved())
            this.setModified();
    }
}
