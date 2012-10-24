package edacc.properties;

import edacc.model.ExperimentResult;
import edacc.model.ExperimentResultDAO;
import edacc.model.SolverConfiguration;
import edacc.model.SolverConfigurationDAO;

/**
 *
 * @author simon
 */
public class BestSolver extends InstanceComputationMethod {

    private Integer expId = null;
    public BestSolver(String[] args) {
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
    public String calculateProperty(int instanceId) throws Exception {
        float minTime = Float.MAX_VALUE;
        int scid = -1;
        for (ExperimentResult er : ExperimentResultDAO.getAllByInstanceId(instanceId)) {
            if (expId != null && er.getExperimentId() != expId) {
                continue;
            }
            if (er.getResultCode().isCorrect()) {
                if (er.getResultTime() < minTime) {
                    minTime = er.getResultTime();
                    scid = er.getSolverConfigId();
                }
            }
        }
        if (scid == -1) {
            return "";
        } else {
            SolverConfiguration sc = SolverConfigurationDAO.getSolverConfigurationById(scid);
            return sc.getName() + " (" + sc.getId() + ")";
        }
    }
    
}
