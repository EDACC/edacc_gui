/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edacc.model;

import java.util.Vector;

/**
 *
 * @author dgall
 */
public class InstanceClass extends BaseModel implements IntegerPKModel {

    private int instanceClassID;
    private String name;
    private String description;
    private boolean source;
    private Vector<InstanceClass> children;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
        if (this.isSaved()) {
            this.setModified();
        }
    }

    public int getId() {
        return getInstanceClassID();
    }

    public int getInstanceClassID() {
        return instanceClassID;
    }

    public void setInstanceClassID(int instanceClassID) {
        this.instanceClassID = instanceClassID;
        if (this.isSaved()) {
            this.setModified();
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        if (this.isSaved()) {
            this.setModified();
        }
    }

    public boolean isSource() {
        return source;
    }

    public void setSource(boolean source) {
        this.source = source;
        if (this.isSaved()) {
            this.setModified();
        }
    }

    public Vector<InstanceClass> getChildren() {
        return children;
    }

    public void setChildren(Vector<InstanceClass> children) {
        this.children = children;
         if (this.isSaved()) {
            this.setModified();
        }
    }

    
}
