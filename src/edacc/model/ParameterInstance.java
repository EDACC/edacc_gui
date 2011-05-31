package edacc.model;

public class ParameterInstance extends BaseModel {
    private int parameter_id;
    private SolverConfiguration solverConfiguration;
    private String value;
    
    public int getParameter_id() {
        return parameter_id;
    }

    public void setParameter_id(int parameter_id) {
        this.parameter_id = parameter_id;
        if (this.isSaved()) {
            this.setModified();
        }
    }

    public SolverConfiguration getSolverConfiguration() {
        return solverConfiguration;
    }

    public void setSolverConfiguration(SolverConfiguration solverConfiguration) {
        this.solverConfiguration = solverConfiguration;
        if (this.isSaved()) {
            this.setModified();
        }
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
        if (this.isSaved()) {
            this.setModified();
        }
    }

}
