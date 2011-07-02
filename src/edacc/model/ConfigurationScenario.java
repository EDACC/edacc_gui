package edacc.model;

import java.util.ArrayList;

public class ConfigurationScenario extends BaseModel implements IntegerPKModel {
    private int id;
    private int idExperiment;
    private int idSolverBinary;
    private ArrayList<ConfigurationScenarioParameter> parameters;
    
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

    public void setParameters(ArrayList<ConfigurationScenarioParameter> parameters) {
        this.parameters = parameters;
    }

    public int getIdSolverBinary() {
        return idSolverBinary;
    }

    public void setIdSolverBinary(int idSolverBinary) {
        this.idSolverBinary = idSolverBinary;
    }
}
