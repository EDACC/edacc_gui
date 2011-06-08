package edacc.experiment;

import edacc.model.SolverConfigCache;
import javax.swing.SwingWorker;

/**
 *
 * @author simon
 */
public class SolverConfigUpdateThread extends SwingWorker<Void, Void> {
    private SolverConfigCache cache;
    
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
