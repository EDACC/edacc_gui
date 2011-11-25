package edacc.model;

import java.sql.Timestamp;

/**
 *
 * @author simon
 */
public class ExperimentResultEx extends ExperimentResult {

    private byte[] watcherOutput, launcherOutput, verifierOutput, solverOutput;
    
    protected ExperimentResultEx(int run, int priority, int computeQueue, StatusCode status, ResultCode resultCode, int seed, float resultTime, int SolverConfigId, int ExperimentId, int InstanceId, Timestamp startTime, int cpuTimeLimit, int memoryLimit, int wallClockTimeLimit, int stackSizeLimit, byte[] solverOutput, byte[] launcherOutput, byte[] watcherOutput, byte[] verifierOutput) {
        super(run, priority, computeQueue, status, seed, resultCode, resultTime, SolverConfigId, ExperimentId, InstanceId, startTime, cpuTimeLimit, memoryLimit, wallClockTimeLimit, stackSizeLimit);
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
}
