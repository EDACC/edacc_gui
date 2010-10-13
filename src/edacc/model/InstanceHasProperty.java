/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edacc.model;

import edacc.satinstances.InstancePropertyManager;
import java.io.IOException;
import java.sql.SQLException;

/**
 *
 * @author dgall
 */
public class InstanceHasProperty extends BaseModel implements IntegerPKModel {

    private int id;
    private Instance instance;
    private InstanceProperty instanceProperty;
    private String value;

    protected InstanceHasProperty(Instance instance, InstanceProperty instanceProperty, String value) {
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

    public InstanceProperty getInstanceProperty() {
        return instanceProperty;
    }

    @Override
    public int getId() {
        return id;
    }

    protected void setId(int id) {
        this.id = id;
    }
}
