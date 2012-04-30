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

    @Override
    public String calculateProperty(int instanceId) throws Exception {
        float minTime = Float.MAX_VALUE;
        int scid = -1;
        for (ExperimentResult er : ExperimentResultDAO.getAllByInstanceId(instanceId)) {
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
