package edacc.experiment;

import edacc.EDACCFilter;
import edacc.experiment.ExperimentUpdateThread.ExperimentStatus;
import edacc.model.Experiment;
import edacc.model.ExperimentDAO;
import edacc.model.ExperimentDAO.StatusCount;
import edacc.model.StatusCode;
import edacc.util.Pair;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import javax.swing.RowFilter.Entry;
import javax.swing.SwingWorker;

/**
 * The experiment update thread.<br/>
 * Updates the running, finished, failed and not started counts in the experiment table every 10 seconds.
 * @author simon
 */
public class ExperimentUpdateThread extends SwingWorker<Void, ExperimentStatus> {

    private final int MODIFIED_EXPERIMENTS_UPDATE_INTERVAL = 2000;
    private final int EXPERIMENTS_UPDATE_INTERVAL = 20000;
    private ExperimentTableModel model;
    private EDACCFilter filter;
    private final HashSet<Integer> modifiedExperimentListContains = new HashSet<Integer>();
    private final LinkedList<ModifiedExperiment> modifiedExperiments = new LinkedList<ModifiedExperiment>();

    /**
     * Creates a new experiment update thread.
     * @param model the model to be used
     */
    public ExperimentUpdateThread(ExperimentTableModel model, EDACCFilter filter) {
        super();
        this.model = model;
        this.filter = filter;
    }

    @Override
    @SuppressWarnings("SleepWhileInLoop")
    protected Void doInBackground() throws Exception {
        int sleep_count = EXPERIMENTS_UPDATE_INTERVAL;
        LinkedList<Experiment> experiments = new LinkedList<Experiment>();
        while (!this.isCancelled()) {
            if (sleep_count >= EXPERIMENTS_UPDATE_INTERVAL) {
                for (int i = 0; i < model.getRowCount(); i++) {
                    synchronized (modifiedExperiments) {
                        final int row = i;
                        Experiment exp = model.getExperimentAt(row);
                        if (filter.include(new Entry<ExperimentTableModel, Integer>() {

                            @Override
                            public ExperimentTableModel getModel() {
                                return model;
                            }

                            @Override
                            public int getValueCount() {
                                return model.getColumnCount();
                            }

                            @Override
                            public Object getValue(int index) {
                                return model.getValueAt(row, index);
                            }

                            @Override
                            public Integer getIdentifier() {
                                return row;
                            }
                        }) && !modifiedExperimentListContains.contains(exp.getId())) {
                            experiments.add(exp);
                        }
                    }
                }
                sleep_count = 0;
            }

            while (true) {
                Experiment exp = null;
                synchronized (modifiedExperiments) {
                    if (!modifiedExperiments.isEmpty()) {
                        ModifiedExperiment mexp = modifiedExperiments.getFirst();
                        if (mexp.toBeUpdated()) {
                            modifiedExperiments.removeFirst();
                            modifiedExperimentListContains.remove(mexp.exp.getId());
                            experiments.push(mexp.exp);
                        }
                    }
                }
                if (experiments.isEmpty())
                    break;
                
                exp = experiments.poll();
                checkExperiment(exp);
                if (this.isCancelled()) {
                    break;
                }
            }
            if (this.isCancelled()) {
                break;
            }
            Thread.sleep(1000);
            sleep_count += 1000;
        }
        return null;
    }

    private void checkExperiment(Experiment exp) throws SQLException {
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
        Pair<Integer, Boolean> pa = ExperimentDAO.getPriorityActiveByExperiment(exp);
        if (pa != null) {
            publish(new ExperimentStatus(exp, count, finished, running, failed, not_started, pa.getFirst(), pa.getSecond()));
        }
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
                    if (status.experiment.getPriority() != status.priority) {
                        model.setValueAt(status.priority, i, ExperimentTableModel.COL_PRIORITY);
                        modified = true;
                    }
                    if (status.experiment.isActive() != status.active) {
                        model.setValueAt(status.active, i, ExperimentTableModel.COL_ACTIVE);
                        modified = true;
                    }
                    modified |= status.running > 0;
                    if (modified) {
                        synchronized (modifiedExperiments) {
                            if (!modifiedExperimentListContains.contains(status.experiment.getId())) {
                                modifiedExperiments.add(new ModifiedExperiment(status.experiment));
                                modifiedExperimentListContains.add(status.experiment.getId());
                            }
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
        int priority;
        boolean active;

        ExperimentStatus(Experiment exp, int count, int finished, int running, int failed, int not_started, int priority, boolean active) {
            this.experiment = exp;
            this.count = count;
            this.finished = finished;
            this.running = running;
            this.failed = failed;
            this.not_started = not_started;
            this.priority = priority;
            this.active = active;
        }
    }

    private class ModifiedExperiment {

        private Date updateTime;
        private Experiment exp;

        public ModifiedExperiment(Experiment exp) {
            this.exp = exp;
            this.updateTime = new Date(new Date().getTime() + MODIFIED_EXPERIMENTS_UPDATE_INTERVAL);
        }

        public boolean toBeUpdated() {
            return new Date().after(updateTime);
        }
    }
}
