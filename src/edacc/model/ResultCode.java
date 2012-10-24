package edacc.model;

import java.io.Serializable;

/**
 *
 * @author simon
 */
public class ResultCode extends BaseModel implements Serializable {

    private static final long serialVersionUID = -235236236L;

  /*  public static final ResultCode SAT = new ResultCode(11);
    public static final ResultCode UNSAT = new ResultCode(10);*/
    public static final ResultCode UNKNOWN = new ResultCode(0, "unknown");
   /* public static final ResultCode WA = new ResultCode(-1);
    public static final ResultCode LIMIT_CPUTIME = new ResultCode(-21);
    public static final ResultCode LIMIT_WALLCLOCKTIME = new ResultCode(-22);
    public static final ResultCode LIMIT_MEMORY = new ResultCode(-23);
    public static final ResultCode LIMIT_STACKSIZE = new ResultCode(-24);
    public static final ResultCode LIMIT_OUTPUTSIZE = new ResultCode(-25);

    private static final ResultCode[] constants = new ResultCode[] {SAT, UNSAT, UNKNOWN,
                                WA,LIMIT_CPUTIME,LIMIT_WALLCLOCKTIME,LIMIT_MEMORY,LIMIT_STACKSIZE,LIMIT_OUTPUTSIZE};*/
    public static final ResultCode[] CONST = new ResultCode[] {UNKNOWN};

    private int resultCode;
    private String description;

    protected ResultCode (int resultCode, String description) {
        this.resultCode = resultCode;
        this.description = description;
    }

    @Override
    public String toString() {
        /*if (value == 11) {
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
        } else return "-";*/
        return description;
    }

    public String getDescription() {
        return description;
    }

    public int getResultCode() {
        return resultCode;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ResultCode other = (ResultCode) obj;
        if (this.resultCode != other.resultCode) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + this.resultCode;
        return hash;
    }
    
    public boolean isCorrect() {
        return String.valueOf(resultCode).startsWith("1");
    }
}
