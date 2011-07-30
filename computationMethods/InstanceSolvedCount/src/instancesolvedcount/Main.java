package instancesolvedcount;

import edacc.model.ExperimentResult;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.HashSet;

/**
 *
 * @author simon
 */
public class Main {
    public static final int SAT = 11;
    public static final int UNSAT = 10;
    public static final String help =
            "Instance Property Calculator from Experiment Results\n"
            + "----------------------------------------------------\n"
            + "Expects an object stream containing an array list of experiment results.\n"
            + "Usage: java -jar InstanceSolvedCount.jar [Option]\n"
            + " Option:\n"
            + "  --help    prints this help\n"
            + "  --solved  prints how often this instance has been solved by different solver configs (ids)\n"
            + "  --used    prints how often this instance has been used by different solver configs (ids)\n";

    public static void printHelp() {
        System.out.println(help);
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        if (args.length != 1) {
            printHelp();
        } else {
            if ("--help".equals(args[0])) {
                printHelp();
            } else if ("--solved".equals(args[0]) || "--used".equals(args[0])) {
                ObjectInputStream is = new ObjectInputStream(System.in);
                ArrayList<ExperimentResult> er = (ArrayList<ExperimentResult>) is.readUnshared();
                if ("--solved".equals(args[0])) {
                    HashSet<Integer> hs = new HashSet<Integer>();
                    for (ExperimentResult e : er) {
                        if (e.getResultCode().getResultCode() == SAT || e.getResultCode().getResultCode() == UNSAT) {
                            hs.add(e.getSolverConfigId());
                        }
                    }
                    System.out.println(hs.size());
                } else if ("--used".equals(args[0])) {
                    HashSet<Integer> hs = new HashSet<Integer>();
                    for (ExperimentResult e : er) {
                        hs.add(e.getSolverConfigId());
                    }
                    System.out.println(hs.size());
                }
            }
        }
    }
}
