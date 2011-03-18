package edacc.model;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.HashMap;

public class ExperimentResult extends BaseModel implements Serializable {

    private static final long serialVersionUID = -235326236723623L;

    public static final int SOLVER_OUTPUT = 0;
    public static final int LAUNCHER_OUTPUT = 1;
    public static final int WATCHER_OUTPUT = 2;
    public static final int VERIFIER_OUTPUT = 3;
    private int id;
    private int run;
    private int priority;
    
    private ExperimentResultStatus status;
    protected ExperimentResultResultCode resultCode;
    private int seed;
    private float resultTime;
    private int SolverConfigId;
    private int ExperimentId;
    private int InstanceId;
    private int runningTime;

    private int solverExitCode;
    private int watcherExitCode;
    private int verifierExitCode;

    private int computeQueue;
    private Timestamp startTime;
    
    private transient HashMap<Integer, ExperimentResultHasProperty> propertyValues;
    private Timestamp datemodified;
    protected ExperimentResult() {
    }

    protected ExperimentResult(int run, int priority, int computeQueue, int status, int seed, ExperimentResultResultCode resultCode, float resultTime, int SolverConfigId, int ExperimentId, int InstanceId, Timestamp startTime) {
        this.run = run;
        this.priority = priority;
        this.computeQueue = computeQueue;
        this.status = ExperimentResultStatus.getExperimentResultStatus(status);
        this.seed = seed;
        this.resultCode = resultCode;
        this.resultTime = resultTime;
        this.SolverConfigId = SolverConfigId;
        this.ExperimentId = ExperimentId;
        this.InstanceId = InstanceId;
        this.startTime = startTime;
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

    protected void setExperimentId(int ExperimentId) {
        this.ExperimentId = ExperimentId;
        if (this.isSaved()) {
            this.setModified();
        }
    }

    public int getPriority() {
        return priority;
    }

    protected void setPriority(int priority) {
        this.priority = priority;
        if (this.isSaved()) {
            this.setModified();
        }
    }

    public int getInstanceId() {
        return InstanceId;
    }

    protected void setInstanceId(int InstanceId) {
        this.InstanceId = InstanceId;
        if (this.isSaved()) {
            this.setModified();
        }
    }

    public int getSolverConfigId() {
        return SolverConfigId;
    }

    protected void setSolverConfigId(int SolverConfigId) {
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

    public int getRun() {
        return run;
    }

    protected void setRun(int run) {
        this.run = run;
        if (this.isSaved()) {
            this.setModified();
        }
    }

    public int getSeed() {
        return seed;
    }

    protected void setSeed(int seed) {
        this.seed = seed;
        if (this.isSaved()) {
            this.setModified();
        }
    }

    public ExperimentResultStatus getStatus() {
        return status;
    }

    protected void setStatus(int status) {
        this.status = ExperimentResultStatus.getExperimentResultStatus(status);
        if (this.isSaved()) {
            this.setModified();
        }
    }

    public float getResultTime() {
        return resultTime;
    }

    protected void setResultTime(float resultTime) {
        this.resultTime = resultTime;
        if (this.isSaved()) {
            this.setModified();
        }
    }

    public int getRunningTime() {
        return runningTime;
    }

    protected void setRunningTime(int runningTime) {
        this.runningTime = runningTime;
    }

    public int getComputeQueue() {
        return computeQueue;
    }

    protected void setComputeQueue(int computeQueue) {
        this.computeQueue = computeQueue;
    }


    public int getSolverExitCode() {
        return solverExitCode;
    }

    protected void setSolverExitCode(int solverExitCode) {
        this.solverExitCode = solverExitCode;
    }

    public int getVerifierExitCode() {
        return verifierExitCode;
    }

    protected void setVerifierExitCode(int verifierExitCode) {
        this.verifierExitCode = verifierExitCode;
    }

    public int getWatcherExitCode() {
        return watcherExitCode;
    }

    protected void setWatcherExitCode(int watcherExitCode) {
        this.watcherExitCode = watcherExitCode;
    }

    public ExperimentResultResultCode getResultCode() {
        return resultCode;
    }

    protected void setResultCode(int resultCode) {
        this.resultCode = ExperimentResultResultCode.getExperimentResultResultCode(resultCode);
    }

    public HashMap<Integer, ExperimentResultHasProperty> getPropertyValues() {
        return propertyValues;
    }

    protected void setPropertyValues(HashMap<Integer, ExperimentResultHasProperty> propertyValues) {
        this.propertyValues = propertyValues;
    }

    public Timestamp getDatemodified() {
        return datemodified;
    }

    protected void setDatemodified(Timestamp datemodified) {
        this.datemodified = datemodified;
    }

    public Timestamp getStartTime() {
        return startTime;
    }

    protected void setStartTime(Timestamp startTime) {
        this.startTime = startTime;
        if (isSaved()) {
            setModified();
        }
    }
}
