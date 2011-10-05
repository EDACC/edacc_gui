package edacc.experiment.tabs.solver;

import edacc.experiment.tabs.solver.gui.EDACCSolverConfigEntry;
import edacc.model.SolverBinaries;

/**
 *
 * @author simon
 */
public class EDACCSolverConfigEntryAdapter implements EDACCSolverConfigEntryListener {

    @Override
    public void onNameChanged(EDACCSolverConfigEntry entry, String oldName, String newName) {
    }

    @Override
    public void onSeedGroupChanged(EDACCSolverConfigEntry entry, int oldSeedGroup, int newSeedGroup) {
    }

    @Override
    public void onHintChanged(EDACCSolverConfigEntry entry, String oldHint, String newHint) {
    }

    @Override
    public void onSolverBinaryChanged(EDACCSolverConfigEntry entry, SolverBinaries oldSolverBinary, SolverBinaries newSolverBinary) {
    }

    @Override
    public void onParametersChanged(EDACCSolverConfigEntry entry) {
    }

    @Override
    public void onReplicateRequest(EDACCSolverConfigEntry entry) {
    }

    @Override
    public void onRemoveRequest(EDACCSolverConfigEntry entry) {
    }

    @Override
    public void onMassReplicationRequest(EDACCSolverConfigEntry entry) {
    }
    
}
