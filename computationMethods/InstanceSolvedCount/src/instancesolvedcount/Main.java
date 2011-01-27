package instancesolvedcount;
import edacc.model.ExperimentResult;
import edacc.model.ExperimentResultResultCode;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.HashSet;
/**
 *
 * @author simon
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        ObjectInputStream is = new ObjectInputStream(System.in);
        ArrayList<ExperimentResult> er = (ArrayList<ExperimentResult>) is.readUnshared();
        HashSet<Integer> hs = new HashSet<Integer>();
        for (ExperimentResult e : er) {
            if (e.getResultCode().getValue() ==  ExperimentResultResultCode.SAT.getValue())
                hs.add(e.getSolverConfigId());
        }
        System.out.println(hs.size());
    }

}
