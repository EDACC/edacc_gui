package edacc.experiment;

import edacc.experiment.ExperimentUpdateThread.ExperimentStatus;
import edacc.model.Experiment;
import edacc.model.ExperimentDAO;
import edacc.model.StatusCode;
import java.util.LinkedList;
import java.util.List;
import javax.swing.SwingWorker;

/**
 *
 * @author simon
 */
public class ExperimentUpdateThread extends SwingWorker<Void, ExperimentStatus> {
    private ExperimentTableModel model;
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
                int running = ExperimentDAO.getJobCountForExperimentAndStatus(exp, StatusCode.RUNNING);
                publish(new ExperimentStatus(exp, running));
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
                    model.setStatusAt(i, "Running: " + status.running);
                }
            }
        }
    }

    public class ExperimentStatus {

        Experiment experiment;
        int running;

        ExperimentStatus(Experiment exp, int running) {
            this.experiment = exp;
            this.running = running;
        }
    }
}

