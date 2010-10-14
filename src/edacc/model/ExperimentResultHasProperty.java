/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edacc.model;

import java.util.Vector;

/**
 *
 * @author rretz
 */
public class ExperimentResultHasProperty extends BaseModel implements IntegerPKModel {

    private int id;
    private ExperimentResult expResult;
    private Property solvProperty;
    private Vector<String> value;

    public ExperimentResult getExpResult() {
        return expResult;
    }

    public void setExpResult(ExperimentResult expResult) {
        this.expResult = expResult;
        if (this.isSaved())
            this.setModified();
    }

    public Property getSolvProperty() {
        return solvProperty;
    }

    public void setSolvProperty(Property solvProperty) {
        this.solvProperty = solvProperty;
        if (this.isSaved())
            this.setModified();
    }

    public Vector<String> getValue() {
        return value;
    }

    public void setValue(Vector<String> value) {
        this.value = value;
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
