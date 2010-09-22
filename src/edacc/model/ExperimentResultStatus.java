package edacc.model;

/**
 *
 * @author simon
 */
public class ExperimentResultStatus {
    public static ExperimentResultStatus LAUNCHERCRASH =    new ExperimentResultStatus(-5);
    public static ExperimentResultStatus WATCHERCRASH =     new ExperimentResultStatus(-4);
    public static ExperimentResultStatus SOLVERCRASH =      new ExperimentResultStatus(-3);
    public static ExperimentResultStatus VERIFIERCRASH =    new ExperimentResultStatus(-2);
    public static ExperimentResultStatus NOTSTARTED =       new ExperimentResultStatus(-1);
    public static ExperimentResultStatus RUNNING =          new ExperimentResultStatus( 0);
    public static ExperimentResultStatus SUCCESSFUL =       new ExperimentResultStatus( 1);

    public static ExperimentResultStatus[] constants = new ExperimentResultStatus[] {LAUNCHERCRASH, WATCHERCRASH,
                                                                SOLVERCRASH, VERIFIERCRASH, NOTSTARTED, RUNNING, SUCCESSFUL};

    private int value;
    private ExperimentResultStatus (int status) {
        value = status;
    }

    public static ExperimentResultStatus getExperimentResultStatus(int status) {
        for (ExperimentResultStatus ers : constants) {
            if (ers.value == status) {
                return ers;
            }
        }
        return new ExperimentResultStatus(status);
    }

    public int getValue() {
        return value;
    }

    @Override
    public String toString() {
        switch (value) {
            case -5:
                return "launcher crash";
            case -4:
                return "watcher crash";
            case -3:
                return "solver crash";
            case -2:
                return "verifier crash";
            case -1:
                return "not started";
            case 0:
                return "running";
            case 1:
                return "finished";
            default:
                return value>1?"exceeded limit: "+value:"unknown";
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ExperimentResultStatus other = (ExperimentResultStatus) obj;
        if (this.value != other.value) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 89 * hash + this.value;
        return hash;
    }
}
