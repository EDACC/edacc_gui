/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edacc.model;

/**
 *
 * @author dgall
 */
public class InstanceClass extends BaseModel implements IntegerPKModel {

    private int instanceClassID;
    private String name;
    private String description;
    private boolean source;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getId() {
        return getInstanceClassID();
    }

    public int getInstanceClassID() {
        return instanceClassID;
    }

    public void setInstanceClassID(int instanceClassID) {
        this.instanceClassID = instanceClassID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isSource() {
        return source;
    }

    public void setSource(boolean source) {
        this.source = source;
    }

    
}
