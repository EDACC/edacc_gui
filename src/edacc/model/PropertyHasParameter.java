/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edacc.model;

/**
 *
 * @author rretz
 */
public class PropertyHasParameter extends BaseModel implements IntegerPKModel{
    private int id;
    private Property property;
    private String parameter;

    @Override
    public int getId() {
        return id;
    }

    public void setId(int id){
        this.id = id;
        if (this.isSaved())
            this.setModified();
    }

    public String getParameter() {
        return parameter;
    }

    public void setParameter(String parameter) {
        this.parameter = parameter;
        if (this.isSaved())
            this.setModified();
    }

    public Property getProperty() {
        return property;
    }

    public void setProperty(Property property) {
        this.property = property;
        if (this.isSaved())
            this.setModified();
    }


}
