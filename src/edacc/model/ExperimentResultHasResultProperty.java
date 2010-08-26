/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edacc.model;

/**
 *
 * @author rretz
 */
public class ExperimentResultHasResultProperty {
    private ExperimentResult expResult;
    private ResultProperty resProperty;
    private String value;

    public ExperimentResult getExpResult() {
        return expResult;
    }

    public void setExpResult(ExperimentResult expResult) {
        this.expResult = expResult;
    }

    public ResultProperty getResProperty() {
        return resProperty;
    }

    public void setResProperty(ResultProperty resProperty) {
        this.resProperty = resProperty;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }


}
