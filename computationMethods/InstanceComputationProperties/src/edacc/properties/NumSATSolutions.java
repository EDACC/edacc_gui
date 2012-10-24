package edacc.properties;

import edacc.model.ExperimentResult;
import edacc.model.ExperimentResultDAO;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.Blob;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

/**
 *
 * @author simon
 */
public class NumSATSolutions extends InstanceComputationMethod {

    Integer expId;

    public NumSATSolutions(String[] args) {
        super(args);
        if (args.length == 0) {
            expId = null;
        } else {
            if ("--expid".equals(args[0])) {
                expId = Integer.valueOf(args[1]);
            }
        }
    }

    @Override
    public String calculateProperty(int id) throws Exception {
        List<ExperimentResult> results = ExperimentResultDAO.getAllByInstanceId(id);
        HashSet<HashMap<Integer, Integer>> solutions = new HashSet<HashMap<Integer, Integer>>();
        for (ExperimentResult result : results) {
            if (expId != null && result.getExperimentId() != expId) {
                continue;
            }
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
                    if (tmpar.length < 2) {
                        continue;
                    }
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
                solutions.add(solution);
            }
        }
        return String.valueOf(solutions.size());
    }
}
