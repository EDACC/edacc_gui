/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edacc.model;

/**
 *
 * @author rretz
 */
public class ExperimentResultHasSolverProperty extends BaseModel implements IntegerPKModel {

    private int id;
    private ExperimentResult expResult;
    private SolverProperty solvProperty;
    private String value;

    public ExperimentResult getExpResult() {
        return expResult;
    }

    public void setExpResult(ExperimentResult expResult) {
        this.expResult = expResult;
        if (this.isSaved())
            this.setModified();
    }

    public SolverProperty getSolvProperty() {
        return solvProperty;
    }

    public void setSolvProperty(SolverProperty solvProperty) {
        this.solvProperty = solvProperty;
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
