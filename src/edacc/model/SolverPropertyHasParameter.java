/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edacc.model;

/**
 *
 * @author rretz
 */
public class SolverPropertyHasParameter extends BaseModel implements IntegerPKModel{
    private int id;
    private SolverProperty solvProperty;
    private Parameter parameter;

    @Override
    public int getId() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setId(int id){
        this.id = id;
        if (this.isSaved())
            this.setModified();
    }

    public Parameter getParameter() {
        return parameter;
    }

    public void setParameter(Parameter parameter) {
        this.parameter = parameter;
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


}
