package edacc.experiment;

import edacc.model.SolverConfiguration;
import java.util.HashSet;
import javax.swing.RowFilter;
import javax.swing.RowFilter.Entry;

/**
 * The solver configuration table row filter can be used to filter solver configurations. <br/>
 * It supports excluding of experiments and including of solver binaries.
 * @author simon
 */
public class SolverConfigurationTableRowFilter extends RowFilter<SolverConfigurationTableModel, Integer> {
    private HashSet<Integer> solverBinaryIds;
    private HashSet<Integer> excludeExperiments;
    
    /** Creates a new solver configuration table row filter */
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

    /**
     * Exclude solver configurations with this experiment id
     * @param id the experiment id
     */
    public void excludeExperiment(int id) {
        excludeExperiments.add(id);
    }
    
    /**
     * Clears the excluded experiments.
     */
    public void clearExcludedExperiments() {
        excludeExperiments.clear();
    }
    
    /**
     * Include solver configurations with this binary id
     * @param id the binary id
     */
    public void addSolverBinaryId(int id) {
        solverBinaryIds.add(id);
    }
    
    /**
     * Clear the included solver binary ids.
     */
    public void clearSolverBinaryIds() {
        solverBinaryIds.clear();
    }
    
}
