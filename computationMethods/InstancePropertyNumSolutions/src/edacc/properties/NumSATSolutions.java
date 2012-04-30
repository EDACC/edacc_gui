package edacc.properties;

import edacc.model.ExperimentResult;
import edacc.model.ExperimentResultDAO;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.Blob;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author simon
 */
public class NumSATSolutions extends InstanceComputationMethod {

    @Override
    public String calculateProperty(int id) throws Exception {
        List<ExperimentResult> results = ExperimentResultDAO.getAllByInstanceId(id);
        HashMap<HashMap<Integer, Integer>, List<HashMap<Integer, Integer>>> solutions = new HashMap<HashMap<Integer, Integer>, List<HashMap<Integer, Integer>>>();
        for (ExperimentResult result : results) {
            // SAT answer
            if (result.getResultCode().getResultCode() == 11) {
                Blob b = ExperimentResultDAO.getSolverOutput(result);
                if (b == null) {
                    continue;
                }
                BufferedReader br = new BufferedReader(new InputStreamReader(b.getBinaryStream()));
                String line;
                HashMap<Integer, Integer> solution = new HashMap<Integer, Integer>();
                while ((line = br.readLine()) != null) {
                    // timestamp
                    String tmpar[] = line.split("\t");
                    if (tmpar.length < 2)
                        continue;
                    line = tmpar[1];
                    // parse solution
                    if (line.startsWith("v ")) {
                        String values[] = line.split(" ");
                        for (int i = 1; i < values.length; i++) {
                            int val = Integer.parseInt(values[i]);
                            solution.put(Math.abs(val), val);
                        }
                    }
                }
                System.out.println("SOLUTION SIZE: " + solution.size());
                List<HashMap<Integer, Integer>> list = solutions.get(solution);
                if (list == null) {
                    list = new ArrayList<HashMap<Integer, Integer>>();
                    list.add(solution);
                    solutions.put(solution, list);
                } else {
                    boolean found = false;
                    for (HashMap<Integer, Integer> m : list) {
                        if (m.equals(solution)) {
                            found = true;
                            break;
                        }
                    }
                    if (!found) {
                        list.add(solution);
                    }
                }
            }
        }
        
        int res = 0;
        for (List<HashMap<Integer, Integer>> list : solutions.values()) {
            res += list.size();
        }
        return String.valueOf(res);
    }
}
