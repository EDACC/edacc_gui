package edacc.model;

import java.io.Serializable;
import java.util.ArrayList;

public class ConfigurationScenario extends BaseModel implements IntegerPKModel, Serializable {

    private int id;
    private int idExperiment;
    private int idSolverBinary;
    private ArrayList<ConfigurationScenarioParameter> parameters;
    private Course course;
    
    public ConfigurationScenario() {
        super();
        parameters = new ArrayList<ConfigurationScenarioParameter>();
    }

    protected ConfigurationScenario(ConfigurationScenario scenario) {
        this();
        id = scenario.id;
        idExperiment = scenario.idExperiment;
        idSolverBinary = scenario.idSolverBinary;
        parameters = new ArrayList<ConfigurationScenarioParameter>();
        for (ConfigurationScenarioParameter p : scenario.parameters) {
            parameters.add(new ConfigurationScenarioParameter(p.isConfigurable(), p.getFixedValue(), p.getParameter()));
        }
        course = new Course(scenario.course);
    }

    @Override
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdExperiment() {
        return idExperiment;
    }

    public void setIdExperiment(int idExperiment) {
        this.idExperiment = idExperiment;
    }

    public ArrayList<ConfigurationScenarioParameter> getParameters() {
        return parameters;
    }

    protected void setParameters(ArrayList<ConfigurationScenarioParameter> parameters) {
        this.parameters = parameters;
    }

    public int getIdSolverBinary() {
        return idSolverBinary;
    }
    
    protected void setCourse(Course course) {
        if (this.course != null) {
            throw new IllegalArgumentException("Configuration Scenario already has a course.");
        }
        this.course = course;
    }
    
    public Course getCourse() {
        return course;
    }

    public void setIdSolverBinary(int idSolverBinary) {
        if (this.idSolverBinary != idSolverBinary) {
            if (isSaved()) {
                setModified();
            }
        }
        this.idSolverBinary = idSolverBinary;
    }
}
