package edacc.experiment.tabs.solver;

import edacc.experiment.tabs.solver.gui.EDACCSolverConfigEntry;
import edacc.model.SolverBinaries;

/**
 *
 * @author simon
 */
public interface EDACCSolverConfigEntryListener {
    public void onNameChanged(EDACCSolverConfigEntry entry, String oldName, String newName);
    public void onSeedGroupChanged(EDACCSolverConfigEntry entry, int oldSeedGroup, int newSeedGroup);
    public void onHintChanged(EDACCSolverConfigEntry entry, String oldHint, String newHint);
    public void onSolverBinaryChanged(EDACCSolverConfigEntry entry, SolverBinaries oldSolverBinary, SolverBinaries newSolverBinary);
    public void onParametersChanged(EDACCSolverConfigEntry entry);
    public void onReplicateRequest(EDACCSolverConfigEntry entry);
    public void onRemoveRequest(EDACCSolverConfigEntry entry);
    public void onMassReplicationRequest(EDACCSolverConfigEntry entry);
}
