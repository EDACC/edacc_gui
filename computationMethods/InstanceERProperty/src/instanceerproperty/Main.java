package instanceerproperty;

import edacc.model.ExperimentResult;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author simon
 */
public class Main {

    public static final int SAT = 11;
    public static final int UNSAT = 10;
    public static final int UNKNOWN = 0;
    public static final String help =
            "Instance Property Calculator from Experiment Results\n"
            + "----------------------------------------------------\n"
            + "Expects an object stream containing an array list of experiment results.\n"
            + "Usage: java -jar InstanceERProperty.jar [Option] [--expid <id>]\n"
            + " Option:\n"
            + "  --help       prints this help\n"
            + "  --mintime    prints the minimum time needed to solve this instance\n"
            + "  --avgtime    prints the average time needed to solve this instance\n"
            + "  --mediantime prints the median time needed to solver this instance\n"
            + "  --status     prints status of the instance: SAT/UNSAT/UNKNOWN\n"
            + "  --solved     prints how often this instance was solved\n"
            + "  --used       prints how often this instance was used\n"
            + "  --expid      if specified, use results only from the experiment with that id\n";

    public static void printHelp() {
        System.out.println(help);
    }

    public static Float getAvgTime(ArrayList<ExperimentResult> er, Integer expId) {
        float res = 0.f;
        int count = 0;
        for (ExperimentResult e : er) {
            if (expId != null && e.getExperimentId() != expId) {
                continue;
            }
            if (e.getResultCode().isCorrect()) {
                res += e.getResultTime();
                count++;
            }
        }
        if (count == 0) {
            return null;
        } else {
            return (res / (float) count);
        }
    }

    public static Float getMinTime(ArrayList<ExperimentResult> er, Integer expId) {
        Float res = null;
        for (ExperimentResult e : er) {
            if (expId != null && e.getExperimentId() != expId) {
                continue;
            }
            if (e.getResultCode().isCorrect()) {
                if (res == null || res > e.getResultTime()) {
                    res = e.getResultTime();
                }
            }
        }
        return res;
    }

    public static Float getMedianTime(ArrayList<ExperimentResult> er, Integer expId) {
        List<Float> resultTimes = new ArrayList<Float>();
        for (ExperimentResult e : er) {
            if (expId != null && e.getExperimentId() != expId) {
                continue;
            }
            if (e.getResultCode().isCorrect()) {
                resultTimes.add(e.getResultTime());
            } else {
                if (e.getCPUTimeLimit() > 0) {
                    resultTimes.add(Float.valueOf(e.getCPUTimeLimit()));
                }
            }
        }
        if (resultTimes.isEmpty()) {
            return null;
        } else {
            return resultTimes.get(resultTimes.size() / 2);
        }
    }

    public static String getStatus(ArrayList<ExperimentResult> er, Integer expId) {
        int status = UNKNOWN;
        for (ExperimentResult e : er) {
            if (expId != null && e.getExperimentId() != expId) {
                continue;
            }
            if (e.getResultCode().getResultCode() == SAT) {
                status = SAT;
                break;
            } else if (e.getResultCode().getResultCode() == UNSAT) {
                if (status == UNKNOWN) {
                    status = UNSAT;
                }
            }
        }
        if (status == UNKNOWN) {
            return "UNKNOWN";
        } else if (status == SAT) {
            return "SAT";
        } else if (status == UNSAT) {
            return "UNSAT";
        }
        return "";
    }

    public static int getSolvedCount(ArrayList<ExperimentResult> er, Integer expId) {
        int res = 0;
        for (ExperimentResult e : er) {
            if (expId != null && e.getExperimentId() != expId) {
                continue;
            }
            if (e.getResultCode().isCorrect()) {
                res++;
            }
        }
        return res;
    }

    public static int getUsedCount(ArrayList<ExperimentResult> er, Integer expId) {
        int res = 0;
        for (ExperimentResult e : er) {
            if (expId != null && e.getExperimentId() != expId) {
                continue;
            }
            res++;
        }
        return res;
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {
        if (args.length < 1) {
            printHelp();
        } else {
            if ("--help".equals(args[0])) {
                printHelp();
            } else {
                Integer expId = null;
                if (args.length > 1 && "--expid".equals(args[1])) {
                    expId = Integer.parseInt(args[2]);
                }
                ObjectInputStream is = new ObjectInputStream(System.in);
                ArrayList<ExperimentResult> er = (ArrayList<ExperimentResult>) is.readUnshared();
                if ("--mintime".equals(args[0])) {
                    Float res = getMinTime(er, expId);
                    if (res != null) {
                        System.out.println(res);
                    } else {
                        System.out.println("");
                    }
                } else if ("--avgtime".equals(args[0])) {
                    Float res = getAvgTime(er, expId);
                    if (res != null) {
                        System.out.println(res);
                    } else {
                        System.out.println("");
                    }
                } else if ("--mediantime".equals(args[0])) {
                    Float res = getMedianTime(er, expId);
                    if (res != null) {
                        System.out.println(res);
                    } else {
                        System.out.println("");
                    }
                } else if ("--status".equals(args[0])) {
                    String res = getStatus(er, expId);
                    System.out.println(res);
                } else if ("--solved".equals(args[0])) {
                    int res = getSolvedCount(er, expId);
                    System.out.println(res);
                } else if ("--used".equals(args[0])) {
                    int res = getUsedCount(er, expId);
                    System.out.println(res);
                } else {
                    printHelp();
                }
            }
        }
    }
}
