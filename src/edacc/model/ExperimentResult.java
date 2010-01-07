package edacc.model;

public class ExperimentResult extends BaseModel{
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

    protected ExperimentResult(int run, int status, int seed, String resultFileName, float time, int statusCode, int SolverConfigId, int ExperimentId, int InstanceId) {
        this.run = run;
        this.status = status;
        this.seed = seed;
        this.resultFileName = resultFileName;
        this.time = time;
        this.statusCode = statusCode;
        this.SolverConfigId = SolverConfigId;
        this.ExperimentId = ExperimentId;
        this.InstanceId = InstanceId;
    }

    @Override
    public int hashCode() {
        return 31 + id;
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
        if (this.id != other.id) {
            return false;
        }
        if (this.run != other.run) {
            return false;
        }
        if (this.status != other.status) {
            return false;
        }
        if (this.seed != other.seed) {
            return false;
        }
        if ((this.resultFileName == null) ? (other.resultFileName != null) : !this.resultFileName.equals(other.resultFileName)) {
            return false;
        }
        if (this.time != other.time) {
            return false;
        }
        if (this.statusCode != other.statusCode) {
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
    }

    public int getInstanceId() {
        return InstanceId;
    }

    public void setInstanceId(int InstanceId) {
        this.InstanceId = InstanceId;
    }

    public int getSolverConfigId() {
        return SolverConfigId;
    }

    public void setSolverConfigId(int SolverConfigId) {
        this.SolverConfigId = SolverConfigId;
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
    }

    public int getRun() {
        return run;
    }

    public void setRun(int run) {
        this.run = run;
    }

    public int getSeed() {
        return seed;
    }

    public void setSeed(int seed) {
        this.seed = seed;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public float getTime() {
        return time;
    }

    public void setTime(float time) {
        this.time = time;
    }




}
