package edacc.experiment;

import edacc.model.SolverConfigCache;
import javax.swing.SwingWorker;

/**
 * The solver config update thread updates a local solver config cache every 5 sec.
 * @author simon
 */
public class SolverConfigUpdateThread extends SwingWorker<Void, Void> {
    private SolverConfigCache cache;
    
    /**
     * Creates a new solver config update thread.
     * @param cache the solver config cache to be used
     */
    public SolverConfigUpdateThread(SolverConfigCache cache) {
        this.cache = cache;
    }
    
    @Override
    @SuppressWarnings("SleepWhileInLoop")
    protected Void doInBackground() throws Exception {
        while (!this.isCancelled() && false) { // currently deactivated
            try {
                cache.synchronize();
                Thread.sleep(5000);
            } catch (Exception ex) {
                break;
            }
        }
        return null;
    }
    
}
