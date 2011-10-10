package edacc.experiment;

import edacc.experiment.ExperimentUpdateThread.ExperimentStatus;
import edacc.model.Experiment;
import edacc.model.ExperimentDAO;
import edacc.model.ExperimentDAO.StatusCount;
import edacc.model.StatusCode;
import java.util.ArrayList;
import java.util.HashMap;
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
    private HashMap<Integer, Experiment> modifiedExperiments;

    /**
     * Creates a new experiment update thread.
     * @param model the model to be used
     */
    public ExperimentUpdateThread(ExperimentTableModel model) {
        super();
        this.model = model;
        modifiedExperiments = new HashMap<Integer, Experiment>();
    }

    @Override
    @SuppressWarnings("SleepWhileInLoop")
    protected Void doInBackground() throws Exception {
        int sleep_count = 10000;
        while (!this.isCancelled()) {
            LinkedList<Experiment> experiments;
            if (sleep_count >= 10000) {
                experiments = ExperimentDAO.getAll();
                sleep_count = 0;
            } else {
                experiments = new LinkedList<Experiment>();
                synchronized (modifiedExperiments) {
                    experiments.addAll(modifiedExperiments.values());
                    modifiedExperiments.clear();
                }
            }
            for (Experiment exp : experiments) {
                ArrayList<StatusCount> statusCount = ExperimentDAO.getJobCountForExperiment(exp);
                int running = 0;
                int finished = 0;
                int failed = 0;
                int not_started = 0;
                int count = 0;
                for (StatusCount stat : statusCount) {
                    count += stat.getCount();
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
                publish(new ExperimentStatus(exp, count, finished, running, failed, not_started));
            }
            Thread.sleep(2000);
            sleep_count += 2000;
        }
        return null;
    }

    @Override
    protected void process(List<ExperimentStatus> chunks) {
        for (ExperimentStatus status : chunks) {
            for (int i = 0; i < model.getRowCount(); i++) {
                if (model.getExperimentAt(i) == status.experiment) {
                    boolean modified = false;
                    if (model.getNumRunsAt(i) == null || model.getNumRunsAt(i) != status.count) {
                        model.setNumRunsAt(i, status.count);
                        modified = true;
                    }
                    if (model.getFailedAt(i) == null || model.getFailedAt(i) != status.failed) {
                        model.setFailedAt(i, status.failed);
                        modified = true;
                    }
                    if (model.getFinishedAt(i) == null || model.getFinishedAt(i) != status.finished) {
                        model.setFinishedAt(i, status.finished);
                        modified = true;
                    }
                    if (model.getNotStartedAt(i) == null || model.getNotStartedAt(i) != status.not_started) {
                        model.setNotStartedAt(i, status.not_started);
                        modified = true;
                    }
                    if (model.getRunningAt(i) == null || model.getRunningAt(i) != status.running) {
                        model.setRunningAt(i, status.running);
                        modified = true;
                    }
                    if (modified) {
                        synchronized (modifiedExperiments) {
                            modifiedExperiments.put(status.experiment.getId(), status.experiment);
                        }
                    }
                }
            }
        }
    }

    class ExperimentStatus {

        Experiment experiment;
        int count;
        int finished;
        int running;
        int failed;
        int not_started;

        ExperimentStatus(Experiment exp, int count, int finished, int running, int failed, int not_started) {
            this.experiment = exp;
            this.count = count;
            this.finished = finished;
            this.running = running;
            this.failed = failed;
            this.not_started = not_started;
        }
    }
}
