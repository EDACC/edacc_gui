package edacc.experiment;

import edacc.model.SolverConfiguration;
import java.util.HashSet;
import javax.swing.RowFilter;
import javax.swing.RowFilter.Entry;

/**
 *
 * @author simon
 */
public class SolverConfigurationTableRowFilter extends RowFilter<SolverConfigurationTableModel, Integer> {
    HashSet<Integer> solverBinaryIds;
    HashSet<Integer> excludeExperiments;
    public SolverConfigurationTableRowFilter() {
        super();
        solverBinaryIds = new HashSet<Integer>();
        excludeExperiments = new HashSet<Integer>();
    }
    
    @Override
    public boolean include(Entry<? extends SolverConfigurationTableModel, ? extends Integer> entry) {
        SolverConfiguration sc = entry.getModel().getSolverConfigurationAt(entry.getIdentifier());
        return !excludeExperiments.contains(sc.getExperiment_id()) && solverBinaryIds.contains(sc.getSolverBinary().getId());
    }

    public void excludeExperiment(int id) {
        excludeExperiments.add(id);
    }
    
    public void clearExcludedExperiments() {
        excludeExperiments.clear();
    }
    
    public void addSolverBinaryId(int id) {
        solverBinaryIds.add(id);
    }
    
    public void clearSolverBinaryIds() {
        solverBinaryIds.clear();
    }
    
}
