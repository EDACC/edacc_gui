package edacc.model;

import java.io.Serializable;

/**
 *
 * @author simon
 */
public class ExperimentResultResultCode implements Serializable {

    private static final long serialVersionUID = -235236236L;

    public static final ExperimentResultResultCode SAT = new ExperimentResultResultCode(11);
    public static final ExperimentResultResultCode UNSAT = new ExperimentResultResultCode(10);
    public static final ExperimentResultResultCode UNKNOWN = new ExperimentResultResultCode(0);
    public static final ExperimentResultResultCode WA = new ExperimentResultResultCode(-1);
    public static final ExperimentResultResultCode LIMIT_CPUTIME = new ExperimentResultResultCode(-21);
    public static final ExperimentResultResultCode LIMIT_WALLCLOCKTIME = new ExperimentResultResultCode(-22);
    public static final ExperimentResultResultCode LIMIT_MEMORY = new ExperimentResultResultCode(-23);
    public static final ExperimentResultResultCode LIMIT_STACKSIZE = new ExperimentResultResultCode(-24);
    public static final ExperimentResultResultCode LIMIT_OUTPUTSIZE = new ExperimentResultResultCode(-25);

    private static final ExperimentResultResultCode[] constants = new ExperimentResultResultCode[] {SAT, UNSAT, UNKNOWN,
                                WA,LIMIT_CPUTIME,LIMIT_WALLCLOCKTIME,LIMIT_MEMORY,LIMIT_STACKSIZE,LIMIT_OUTPUTSIZE};

    int value;
    private ExperimentResultResultCode (int code) {
        value = code;
    }

    public static ExperimentResultResultCode getExperimentResultResultCode(int code) {
        for (ExperimentResultResultCode errc : constants) {
            if (errc.value == code) {
                return errc;
            }
        }
        return new ExperimentResultResultCode(code);
    }

    @Override
    public String toString() {
        if (value == 11) {
            return "satisfiable";
        } else if (value == 10) {
            return "unsatisfiable";
        } else if (value == 0) {
            return "unknown";
        } else if (value == -1) {
            return "wrong answer";
        } else if (value == -21) {
            return "cpu time limit exceeded";
        } else if (value == -22) {
            return "wall clock time limit exceeded";
        } else if (value == -23) {
            return "memory limit exceeded";
        } else if (value == -24) {
            return "stack size limit exceeded";
        } else if (value == -25) {
            return "output size limit exceeded";
        } else if (value <= -301 && value >= -330) {
            int sig = -(value+300);
            return "linux signal: " + sig;
        } else return "-";
    }



    public int getValue() {
        return value;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ExperimentResultResultCode other = (ExperimentResultResultCode) obj;
        if (this.value != other.value) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + this.value;
        return hash;
    }
}
