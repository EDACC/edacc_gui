package edacc.model;

import java.util.ArrayList;

public class ConfigurationScenario extends BaseModel implements IntegerPKModel {

    private int id;
    private int idExperiment;
    private int idSolverBinary;
    private ArrayList<ConfigurationScenarioParameter> parameters;
    private Course course;
    
    public ConfigurationScenario() {
        parameters = new ArrayList<ConfigurationScenarioParameter>();
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
