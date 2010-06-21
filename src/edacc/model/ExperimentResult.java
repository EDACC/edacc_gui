package edacc.model;

public class ExperimentResult extends BaseModel {

    private int id;
    private int run;
    private int status;
    private int seed;
    private String resultFileName;
    private float time;
    private int statusCode;
    private int SolverConfigId;
    private int ExperimentId;
    private int InstanceId;

    protected ExperimentResult() {
    }

    protected ExperimentResult(int run, int status, int seed, float time, int statusCode, int SolverConfigId, int ExperimentId, int InstanceId) {
        this.run = run;
        this.status = status;
        this.seed = seed;
        this.time = time;
        this.statusCode = statusCode;
        this.SolverConfigId = SolverConfigId;
        this.ExperimentId = ExperimentId;
        this.InstanceId = InstanceId;
        this.resultFileName = "results/" + this.ExperimentId + "_" + this.SolverConfigId + "_" + this.InstanceId + "_R" + this.run + ".res";

    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 67 * hash + this.run;
        hash = 67 * hash + this.SolverConfigId;
        hash = 67 * hash + this.ExperimentId;
        hash = 67 * hash + this.InstanceId;
        return hash;
    }


    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ExperimentResult other = (ExperimentResult) obj;
        if (this.run != other.run) {
            return false;
        }
        if (this.SolverConfigId != other.SolverConfigId) {
            return false;
        }
        if (this.ExperimentId != other.ExperimentId) {
            return false;
        }
        if (this.InstanceId != other.InstanceId) {
            return false;
        }
        return true;
    }

    public int getExperimentId() {
        return ExperimentId;
    }

    public void setExperimentId(int ExperimentId) {
        this.ExperimentId = ExperimentId;
        if (this.isSaved()) {
            this.setModified();
        }
    }

    public int getInstanceId() {
        return InstanceId;
    }

    public void setInstanceId(int InstanceId) {
        this.InstanceId = InstanceId;
        if (this.isSaved()) {
            this.setModified();
        }
    }

    public int getSolverConfigId() {
        return SolverConfigId;
    }

    public void setSolverConfigId(int SolverConfigId) {
        this.SolverConfigId = SolverConfigId;
        if (this.isSaved()) {
            this.setModified();
        }
    }

    public int getId() {
        return id;
    }

    protected void setId(int id) {
        this.id = id;
    }

    public String getResultFileName() {
        return resultFileName;
    }

    public void setResultFileName(String resultFileName) {
        this.resultFileName = resultFileName;
        if (this.isSaved()) {
            this.setModified();
        }
    }

    public int getRun() {
        return run;
    }

    public void setRun(int run) {
        this.run = run;
        if (this.isSaved()) {
            this.setModified();
        }
    }

    public int getSeed() {
        return seed;
    }

    public void setSeed(int seed) {
        this.seed = seed;
        if (this.isSaved()) {
            this.setModified();
        }
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
        if (this.isSaved()) {
            this.setModified();
        }
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
        if (this.isSaved()) {
            this.setModified();
        }
    }

    public float getTime() {
        return time;
    }

    public void setTime(float time) {
        this.time = time;
        if (this.isSaved()) {
            this.setModified();
        }
    }

    public ExperimentResultStatus getExperimentResultStatus() {
        return ExperimentResultStatus.fromValue(status);
    }
}
