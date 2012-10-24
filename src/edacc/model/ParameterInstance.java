package edacc.model;

import java.io.Serializable;

public class ParameterInstance extends BaseModel implements Serializable {
    private int parameter_id;
    private SolverConfiguration solverConfiguration;
    private String value;

    public ParameterInstance() {
        super();
    }
    
    public ParameterInstance(ParameterInstance pi) {
        this();
        parameter_id = pi.parameter_id;
        solverConfiguration = pi.solverConfiguration;
        value = pi.value;
    }
    
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
