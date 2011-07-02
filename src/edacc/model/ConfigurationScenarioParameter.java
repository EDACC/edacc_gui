package edacc.model;

public class ConfigurationScenarioParameter extends BaseModel {
    private int idConfigurationScenario;
    private int idParameter;
    private boolean configurable;
    private String fixedValue;
    private Parameter parameter;

    public boolean isConfigurable() {
        return configurable;
    }

    public void setConfigurable(boolean configurable) {
        this.configurable = configurable;
    }

    public String getFixedValue() {
        return fixedValue;
    }

    public void setFixedValue(String fixedValue) {
        this.fixedValue = fixedValue;
    }

    public int getIdConfigurationScenario() {
        return idConfigurationScenario;
    }

    public void setIdConfigurationScenario(int idConfigurationScenario) {
        this.idConfigurationScenario = idConfigurationScenario;
    }

    public int getIdParameter() {
        return idParameter;
    }

    public void setIdParameter(int idParameter) {
        this.idParameter = idParameter;
    }

    public Parameter getParameter() {
        return parameter;
    }

    public void setParameter(Parameter parameter) {
        this.parameter = parameter;
    }
}
