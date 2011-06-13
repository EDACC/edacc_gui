package edacc.experiment;

import edacc.experiment.ExperimentUpdateThread.ExperimentStatus;
import edacc.model.Experiment;
import edacc.model.ExperimentDAO;
import edacc.model.ExperimentDAO.StatusCount;
import edacc.model.StatusCode;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import javax.swing.SwingWorker;

/**
 * The experiment update thread.<br/>
 * Updates the running, finished, failed and not started counts in the experiment table every 10 seconds.
 * @author simon
 */
public class ExperimentUpdateThread extends SwingWorker<Void, ExperimentStatus> {
    private ExperimentTableModel model;
    
    /**
     * Creates a new experiment update thread.
     * @param model the model to be used
     */
    public ExperimentUpdateThread(ExperimentTableModel model) {
        super();
        this.model = model;
    }

    @Override
    @SuppressWarnings("SleepWhileInLoop")
    protected Void doInBackground() throws Exception {
        LinkedList<Experiment> experiments = ExperimentDAO.getAll();
        while (!this.isCancelled()) {
            for (Experiment exp : experiments) {
                ArrayList<StatusCount> statusCount = ExperimentDAO.getJobCountForExperiment(exp);
                int running = 0;
                int finished = 0;
                int failed = 0;
                int not_started = 0;
                for (StatusCount stat : statusCount) {
                    if (stat.getStatusCode() != StatusCode.NOT_STARTED && stat.getStatusCode() != StatusCode.RUNNING) {
                        finished += stat.getCount();
                    }
                    if (stat.getStatusCode().getStatusCode() < -1) {
                        failed += stat.getCount();
                    }
                    if (stat.getStatusCode() == StatusCode.RUNNING) {
                        running = stat.getCount();
                    }
                    if (stat.getStatusCode() == StatusCode.NOT_STARTED) {
                        not_started = stat.getCount();
                    }
                }
                publish(new ExperimentStatus(exp, finished, running, failed, not_started));
            }
            Thread.sleep(10000);
        }
        return null;
    }

    @Override
    protected void process(List<ExperimentStatus> chunks) {
        for (ExperimentStatus status: chunks) {
            for (int i = 0; i < model.getRowCount(); i++) {
                if (model.getExperimentAt(i) == status.experiment) {
                    model.setFailedAt(i, status.failed);
                    model.setFinishedAt(i, status.finished);
                    model.setNotStartedAt(i, status.not_started);
                    model.setRunningAt(i, status.running);
                }
            }
        }
    }

    class ExperimentStatus {

        Experiment experiment;
        int finished;
        int running;
        int failed;
        int not_started;
        
        ExperimentStatus(Experiment exp, int finished, int running, int failed, int not_started) {
            this.experiment = exp;
            this.finished = finished;
            this.running = running;
            this.failed = failed;
            this.not_started = not_started;
        }
    }
}

