package edacc.model;

/**
 *
 * @author simon
 */
public enum ExperimentResultStatus {
    CODE__2(-2), CODE__1(-1), CODE_0(0), CODE_1(1), CODE_2(2), CODE_3(3),UNKNOWN(4),
    NOTSTARTED(-1),RUNNING(0),SUCCESSFUL(1);
    int value;
    ExperimentResultStatus (int status) {
        value = status;
    }

    public int getValue() {
        return value;
    }

    public static ExperimentResultStatus fromValue(int status) {
        for (ExperimentResultStatus ExpStatus: ExperimentResultStatus.values()) {
            if (ExpStatus.getValue() == status) {
                return ExpStatus;
            }
        }
        return UNKNOWN;
    }

    @Override
    public String toString() {
        switch (this) {
            case CODE__2:
                return "error";
            case CODE__1:
                return "not started";
            case CODE_0:
                return "running";
            case CODE_1:
                return "finished";
            case CODE_2:
                return "terminated by ulimit";
            case CODE_3:
                return "terminated by ulimit";
            default:
                return "unknown";
        }
    }
}
