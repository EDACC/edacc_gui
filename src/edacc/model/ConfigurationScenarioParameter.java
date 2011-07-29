package edacc.model;

public class ConfigurationScenarioParameter extends BaseModel implements Comparable<ConfigurationScenarioParameter> {

    private int idConfigurationScenario;
    private int idParameter;
    private boolean configurable;
    private String fixedValue;
    private Parameter parameter;

    public ConfigurationScenarioParameter() {
        setNew();
    }

    public ConfigurationScenarioParameter(boolean configurable, String fixedValue, Parameter parameter) {
        this();
        this.configurable = configurable;
        this.fixedValue = fixedValue;
        this.parameter = parameter;
        this.idParameter = parameter.getId();
    }

    public boolean isConfigurable() {
        return configurable;
    }

    public void setConfigurable(boolean configurable) {
        if (this.configurable != configurable) {
            if (isSaved()) {
                setModified();
            }
        }
        this.configurable = configurable;
    }

    public String getFixedValue() {
        return fixedValue;
    }

    public void setFixedValue(String fixedValue) {
        this.fixedValue = fixedValue;
        if (isSaved()) {
            setModified();
        }
    }

    public int getIdConfigurationScenario() {
        return idConfigurationScenario;
    }

    protected void setIdConfigurationScenario(int idConfigurationScenario) {
        this.idConfigurationScenario = idConfigurationScenario;
    }

    public int getIdParameter() {
        return idParameter;
    }

    protected void setIdParameter(int idParameter) {
        this.idParameter = idParameter;
    }

    public Parameter getParameter() {
        return parameter;
    }

    protected void setParameter(Parameter parameter) {
        this.parameter = parameter;
    }

    @Override
    public int compareTo(ConfigurationScenarioParameter o) {
        return this.getParameter().getName().compareTo(o.getParameter().getName());
    }
}
