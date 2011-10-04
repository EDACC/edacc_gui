package edacc.experiment.tabs.solver;

import edacc.model.Solver;

/**
 *
 * @author simon
 */
public interface SolverConfigurationEntryModelListener {
    public void onDataChanged();
    public void onEntryChanged(SolverConfigurationEntry entry);
}
