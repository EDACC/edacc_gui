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
    private StatusCode status;
    protected ResultCode resultCode;
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
    private int CPUTimeLimit;
    private int memoryLimit;
    private int wallClockTimeLimit;
    private int stackSizeLimit;
    private String computeNode;
    private String computeNodeIP;
    private transient HashMap<Integer, ExperimentResultHasProperty> propertyValues = null;
    private Timestamp datemodified;
    private Integer idClient;

    protected ExperimentResult() {
        super();
    }

    protected ExperimentResult(int run, int priority, int computeQueue, StatusCode status, int seed, ResultCode resultCode, float resultTime, int SolverConfigId, int ExperimentId, int InstanceId, Timestamp startTime, int cpuTimeLimit, int memoryLimit, int wallClockTimeLimit, int stackSizeLimit) {
        this();
        this.run = run;
        this.priority = priority;
        this.computeQueue = computeQueue;
        this.status = status;
        this.seed = seed;
        this.resultCode = resultCode;
        this.resultTime = resultTime;
        this.SolverConfigId = SolverConfigId;
        this.ExperimentId = ExperimentId;
        this.InstanceId = InstanceId;
        this.startTime = startTime;
        this.CPUTimeLimit = cpuTimeLimit;
        this.memoryLimit = memoryLimit;
        this.wallClockTimeLimit = wallClockTimeLimit;
        this.stackSizeLimit = stackSizeLimit;
    }
    
    protected ExperimentResult(ExperimentResult er) {
        this();
        id = er.id;
        run = er.run;
        priority = er.priority;
        computeQueue = er.computeQueue;
        computeNode = er.computeNode;
        computeNodeIP = er.computeNodeIP;
        status = er.status;
        seed = er.seed;
        resultCode = er.resultCode;
        resultTime = er.resultTime;
        SolverConfigId = er.SolverConfigId;
        ExperimentId = er.ExperimentId;
        InstanceId = er.InstanceId;
        startTime = er.startTime;
        CPUTimeLimit = er.CPUTimeLimit;
        memoryLimit = er.memoryLimit;
        wallClockTimeLimit = er.wallClockTimeLimit;
        stackSizeLimit = er.stackSizeLimit;
        solverExitCode = er.solverExitCode;
        verifierExitCode = er.verifierExitCode;
        watcherExitCode = er.watcherExitCode;
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

    public StatusCode getStatus() {
        return status;
    }

    protected void setStatus(StatusCode status) {
        this.status = status;
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

    public ResultCode getResultCode() {
        return resultCode;
    }

    protected void setResultCode(ResultCode resultCode) {
        this.resultCode = resultCode;
    }

    public HashMap<Integer, ExperimentResultHasProperty> getPropertyValues() {
        if (propertyValues == null) {
            propertyValues = new HashMap<Integer, ExperimentResultHasProperty>();
        }
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

    public int getCPUTimeLimit() {
        return CPUTimeLimit;
    }

    public void setCPUTimeLimit(int CPUTimeLimit) {
        this.CPUTimeLimit = CPUTimeLimit;
        if (this.isSaved()) {
            this.setModified();
        }
    }

    public int getMemoryLimit() {
        return memoryLimit;
    }

    public void setMemoryLimit(int memoryLimit) {
        this.memoryLimit = memoryLimit;
        if (this.isSaved()) {
            this.setModified();
        }
    }

    public int getStackSizeLimit() {
        return stackSizeLimit;
    }

    public void setStackSizeLimit(int stackSizeLimit) {
        this.stackSizeLimit = stackSizeLimit;
        if (this.isSaved()) {
            this.setModified();
        }
    }

    public int getWallClockTimeLimit() {
        return wallClockTimeLimit;
    }

    public void setWallClockTimeLimit(int wallClockTimeLimit) {
        this.wallClockTimeLimit = wallClockTimeLimit;
        if (this.isSaved()) {
            this.setModified();
        }
    }

    public String getComputeNode() {
        return computeNode;
    }

    protected void setComputeNode(String computeNode) {
        this.computeNode = computeNode;
    }

    public String getComputeNodeIP() {
        return computeNodeIP;
    }

    protected void setComputeNodeIP(String computeNodeIP) {
        this.computeNodeIP = computeNodeIP;
    }

    public Integer getIdClient() {
        return idClient;
    }

    protected void setIdClient(Integer idClient) {
        this.idClient = idClient;
    }
}
