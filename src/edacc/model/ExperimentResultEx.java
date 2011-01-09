package edacc.model;

import java.sql.Timestamp;

/**
 *
 * @author simon
 */
public class ExperimentResultEx extends ExperimentResult {

    private byte[] watcherOutput, launcherOutput, verifierOutput, solverOutput;
    private Timestamp startTime;

    protected ExperimentResultEx(int run, int priority, int status, ExperimentResultResultCode resultCode, int seed, float resultTime, int SolverConfigId, int ExperimentId, int InstanceId, Timestamp startTime, byte[] solverOutput, byte[] launcherOutput, byte[] watcherOutput, byte[] verifierOutput) {
        super(run, priority, status, seed, resultTime, SolverConfigId, ExperimentId, InstanceId);
        this.resultCode = resultCode;
        this.startTime = startTime;
        this.solverOutput = solverOutput;
        this.launcherOutput = launcherOutput;
        this.watcherOutput = watcherOutput;
        this.verifierOutput = verifierOutput;
    }

    public byte[] getLauncherOutput() {
        return launcherOutput;
    }

    public void setLauncherOutput(byte[] launcherOutput) {
        this.launcherOutput = launcherOutput;
        if (isSaved()) {
            setModified();
        }
    }

    public byte[] getSolverOutput() {
        return solverOutput;
    }

    public void setSolverOutput(byte[] solverOutput) {
        this.solverOutput = solverOutput;
        if (isSaved()) {
            setModified();
        }
    }

    public byte[] getVerifierOutput() {
        return verifierOutput;
    }

    public void setVerifierOutput(byte[] verifierOutput) {
        this.verifierOutput = verifierOutput;
        if (isSaved()) {
            setModified();
        }
    }

    public byte[] getWatcherOutput() {
        return watcherOutput;
    }

    public void setWatcherOutput(byte[] watcherOutput) {
        this.watcherOutput = watcherOutput;
        if (isSaved()) {
            setModified();
        }
    }

    public Timestamp getStartTime() {
        return startTime;
    }

    public void setStartTime(Timestamp startTime) {
        this.startTime = startTime;
        if (isSaved()) {
            setModified();
        }
    }
}
