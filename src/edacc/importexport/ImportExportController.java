package edacc.importexport;

import edacc.model.Experiment;
import edacc.model.Instance;
import edacc.model.Solver;
import edacc.model.Verifier;
import java.util.List;

/**
 *
 * @author simon
 */
public interface ImportExportController {
    public List<Experiment> getExperiments();
    public List<Solver> getSolvers();
    public List<Instance> getInstances();
    public List<Verifier> getVerifiers();
    
    public List<Solver> getDependentSolvers(List<Experiment> experiments) throws Exception;
    public List<Instance> getDependentInstances(List<Experiment> experiments) throws Exception;
    public List<Verifier> getDependentVerifiers(List<Experiment> experiments) throws Exception;
}
