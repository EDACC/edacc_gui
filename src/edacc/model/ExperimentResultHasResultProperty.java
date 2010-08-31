/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edacc.model;

/**
 *
 * @author rretz
 */
public class ExperimentResultHasResultProperty extends BaseModel implements IntegerPKModel {

    private int id;
    private ExperimentResult expResult;
    private ResultProperty resProperty;
    private String value;

    public ExperimentResult getExpResult() {
        return expResult;
    }

    public void setExpResult(ExperimentResult expResult) {
        this.expResult = expResult;
        if (this.isSaved())
            this.setModified();
    }

    public ResultProperty getResProperty() {
        return resProperty;
    }

    public void setResProperty(ResultProperty resProperty) {
        this.resProperty = resProperty;
        if (this.isSaved())
            this.setModified();
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
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
