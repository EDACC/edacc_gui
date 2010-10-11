package edacc.model;

import java.sql.Time;
import java.util.HashMap;

public class ExperimentResult extends BaseModel {
    public static final int SOLVER_OUTPUT = 0;
    public static final int LAUNCHER_OUTPUT = 1;
    public static final int WATCHER_OUTPUT = 2;
    public static final int VERIFIER_OUTPUT = 3;
    private int id;
    private int run;
    private ExperimentResultStatus status;
    private ExperimentResultResultCode resultCode;
    private int seed;
    private float resultTime;
    private int SolverConfigId;
    private int ExperimentId;
    private int InstanceId;
    private Time runningTime;

    private String solverOutputFilename;
    private String launcherOutputFilename;
    private String watcherOutputFilename;
    private String verifierOutputFilename;

    private int solverExitCode;
    private int watcherExitCode;
    private int verifierExitCode;

    private int computeQueue;

    private HashMap<Integer, ExperimentResultHasSolverProperty> propertyValues;
    
    protected ExperimentResult() {
    }

    protected ExperimentResult(int run, int status, int seed, float resultTime, int SolverConfigId, int ExperimentId, int InstanceId) {
        this.run = run;
        this.status = ExperimentResultStatus.getExperimentResultStatus(status);
        this.seed = seed;
        this.resultTime = resultTime;
        this.SolverConfigId = SolverConfigId;
        this.ExperimentId = ExperimentId;
        this.InstanceId = InstanceId;
        String filename = "results/" + this.ExperimentId + "_" + this.SolverConfigId + "_" + this.InstanceId + "_R" + this.run + ".res";
        this.solverOutputFilename = filename + ".solver";
        this.launcherOutputFilename = filename + ".launcher";
        this.watcherOutputFilename = filename + ".watcher";
        this.verifierOutputFilename = filename + ".verifier";
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

    public ExperimentResultStatus getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = ExperimentResultStatus.getExperimentResultStatus(status);
        if (this.isSaved()) {
            this.setModified();
        }
    }

    public float getResultTime() {
        return resultTime;
    }

    public void setResultTime(float resultTime) {
        this.resultTime = resultTime;
        if (this.isSaved()) {
            this.setModified();
        }
    }

    public Time getRunningTime() {
        return runningTime;
    }

    public void setRunningTime(Time maxTimeLeft) {
        this.runningTime = maxTimeLeft;
    }

    public int getComputeQueue() {
        return computeQueue;
    }

    public void setComputeQueue(int computeQueue) {
        this.computeQueue = computeQueue;
    }

    public String getLauncherOutputFilename() {
        return launcherOutputFilename;
    }

    public void setLauncherOutputFilename(String launcherOutputFilename) {
        this.launcherOutputFilename = launcherOutputFilename;
    }

    public int getSolverExitCode() {
        return solverExitCode;
    }

    public void setSolverExitCode(int solverExitCode) {
        this.solverExitCode = solverExitCode;
    }

    public String getSolverOutputFilename() {
        return solverOutputFilename;
    }

    public void setSolverOutputFilename(String solverOutputFilename) {
        this.solverOutputFilename = solverOutputFilename;
    }

    public int getVerifierExitCode() {
        return verifierExitCode;
    }

    public void setVerifierExitCode(int verifierExitCode) {
        this.verifierExitCode = verifierExitCode;
    }

    public String getVerifierOutputFilename() {
        return verifierOutputFilename;
    }

    public void setVerifierOutputFilename(String verifierOutputFilename) {
        this.verifierOutputFilename = verifierOutputFilename;
    }

    public int getWatcherExitCode() {
        return watcherExitCode;
    }

    public void setWatcherExitCode(int watcherExitCode) {
        this.watcherExitCode = watcherExitCode;
    }

    public String getWatcherOutputFilename() {
        return watcherOutputFilename;
    }

    public void setWatcherOutputFilename(String watcherOutputFilename) {
        this.watcherOutputFilename = watcherOutputFilename;
    }

    public ExperimentResultResultCode getResultCode() {
        return resultCode;
    }

    public void setResultCode(int resultCode) {
        this.resultCode = ExperimentResultResultCode.getExperimentResultResultCode(resultCode);
    }

    public HashMap<Integer, ExperimentResultHasSolverProperty> getPropertyValues() {
        return propertyValues;
    }

    public void setPropertyValues(HashMap<Integer, ExperimentResultHasSolverProperty> propertyValues) {
        this.propertyValues = propertyValues;
    }
}
