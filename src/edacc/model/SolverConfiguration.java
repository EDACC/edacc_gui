package edacc.model;

public class SolverConfiguration extends BaseModel implements IntegerPKModel {
    private int solver_id;
    private int experiment_id;
    private int id;
    private int seed_group;

    public int getSeed_group() {
        return seed_group;
    }

    public void setSeed_group(int seed_group) {
        this.seed_group = seed_group;
    }

    public int getExperiment_id() {
        return experiment_id;
    }

    public void setExperiment_id(int experiment_id) {
        this.experiment_id = experiment_id;
    }

    public int getId() {
        return id;
    }

    protected void setId(int id) {
        this.id = id;
    }

    public int getSolver_id() {
        return solver_id;
    }

    public void setSolver_id(int solver_id) {
        this.solver_id = solver_id;
    }

}