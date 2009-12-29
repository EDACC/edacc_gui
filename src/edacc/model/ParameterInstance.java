package edacc.model;

public class ParameterInstance extends BaseModel {
    private int parameter_id;
    private int solver_config_id;
    private String value;
    
    public int getParameter_id() {
        return parameter_id;
    }

    public void setParameter_id(int parameter_id) {
        this.parameter_id = parameter_id;
    }

    public int getSolver_config_id() {
        return solver_config_id;
    }

    public void setSolver_config_id(int solver_config_id) {
        this.solver_config_id = solver_config_id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

}
