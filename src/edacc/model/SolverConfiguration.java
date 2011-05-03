package edacc.model;

public class SolverConfiguration extends BaseModel implements IntegerPKModel {

    private SolverBinaries solverBinary;
    private int experiment_id;
    private int id;
    private int seed_group;
    private int idx;
    private String name;

    public int getSeed_group() {
        return seed_group;
    }

    public void setSeed_group(int seed_group) {
        if (this.seed_group != seed_group && this.isSaved()) {
            this.setModified();
        }
        this.seed_group = seed_group;
    }

    public int getExperiment_id() {
        return experiment_id;
    }

    public void setExperiment_id(int experiment_id) {
        this.experiment_id = experiment_id;
        if (this.isSaved()) {
            this.setModified();
        }
    }

    @Override
    public int getId() {
        return id;
    }

    protected void setId(int id) {
        this.id = id;
    }

    public SolverBinaries getSolverBinary() {
        return solverBinary;
    }

    public void setSolverBinary(SolverBinaries solverBinary) {
        this.solverBinary = solverBinary;
        if (this.isSaved()) {
            this.setModified();
        }
    }
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (!name.equals(this.name) && this.isSaved()) {
            this.setModified();
        }
        this.name = name;
    }

    public int getIdx() {
        return idx;
    }

    public void setIdx(int idx) {
        if (this.idx != idx && this.isSaved()) {
            this.setModified();
        }
        this.idx = idx;
    }

    @Override
    public String toString() {
        return name;
    }
}
