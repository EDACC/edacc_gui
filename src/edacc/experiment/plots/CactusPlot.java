package edacc.experiment.plots;

import edacc.experiment.ExperimentController;
import edacc.model.ExperimentResult;
import edacc.model.ExperimentResultStatus;
import edacc.model.Instance;
import edacc.model.InstanceDAO;
import edacc.model.SolverConfiguration;
import edacc.model.SolverConfigurationDAO;
import edacc.model.SolverDAO;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Vector;
import javax.swing.JComboBox;
import org.rosuda.JRI.Rengine;

class SolverInfos {

    String name;
    int[] xs;
    double[] ys;
}

/**
 *
 * @author simon
 */
public class CactusPlot extends Plot {

    private Dependency[] dependencies;
    private JComboBox comboRun;
    private InstanceSelector instanceSelector;
    private SolverConfigurationSelector solverConfigurationSelector;

    public CactusPlot(ExperimentController expController) {
        super(expController);
        comboRun = new JComboBox();
        instanceSelector = new InstanceSelector();
        solverConfigurationSelector = new SolverConfigurationSelector();
        dependencies = new Dependency[]{
                    new Dependency("Solvers", solverConfigurationSelector),
                    new Dependency("Instances", instanceSelector),
                    new Dependency("Plot for run", comboRun)
                };
    }

    @Override
    public Dependency[] getDependencies() {
        return dependencies;
    }

    @Override
    public void plot(Rengine engine, Vector<PointInformation> pointInformations) throws SQLException, DependencyException {
        super.plot(engine, pointInformations);
        int run = -4;
        if ("all runs - average".equals(comboRun.getSelectedItem())) {
            run = AVERAGE;
        } else if ("all runs - median".equals(comboRun.getSelectedItem())) {
            run = MEDIAN;
        } else if ("all runs".equals(comboRun.getSelectedItem())) {
            run = ALLRUNS;
        } else if (comboRun.getSelectedItem() instanceof Integer) {
            run = (Integer) comboRun.getSelectedItem();
        }
        if (run == -4) {
            throw new DependencyException("You have to select a run.");
        }
        Vector<Instance> instances = instanceSelector.getSelectedInstances();
        if (instances == null || instances.size() == 0) {
            throw new DependencyException("You have to select instances in order to plot.");
        }
        Vector<SolverConfiguration> solverConfigs = solverConfigurationSelector.getSelectedSolverConfigurations();
        if (solverConfigs.size() == 0) {
            throw new DependencyException("You have to select solvers in order to plot.");
        }
        HashSet<Integer> selectedInstanceIds = new HashSet<Integer>();
        for (Instance i : instances) {
            selectedInstanceIds.add(i.getId());
        }

        SolverInfos[] solver = new SolverInfos[solverConfigs.size()];
        double max_y = 0;
        for (int i = 0; i < solver.length; i++) {
            SolverConfiguration sc = solverConfigs.get(i);



            Vector<Float> resultTimes = new Vector<Float>();

            int k = 0;
            for (Integer instanceId : selectedInstanceIds) {
                try {
                    if (run == ALLRUNS) {
                        Vector<ExperimentResult> tmp = getResults(sc.getId(), instanceId);
                        for (ExperimentResult er : tmp) {
                            if (er.getExperimentResultStatus().equals(ExperimentResultStatus.SUCCESSFUL)) {
                                resultTimes.add(er.getTime());
                            }
                        }
                    } else {
                      //  ExperimentResult er = getResult(sc.getId(), instanceId, run);
                      //  if (er.getExperimentResultStatus().equals(ExperimentResultStatus.SUCCESSFUL)) {
                      //      resultTimes.add(er.getTime());
                      //  }
                        // TODO: this might be senseless :-) -> will be changed with properties
                        resultTimes.add(getResultValue(sc.getId(), instanceId, run).floatValue());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            Collections.sort(resultTimes);

            solver[i] = new SolverInfos();
            solver[i].name = SolverDAO.getById(sc.getSolver_id()).getName();
            solver[i].xs = new int[resultTimes.size() + 1];
            solver[i].ys = new double[resultTimes.size() + 1];

            k = 1;
            solver[i].xs[0] = 0;
            solver[i].ys[0] = 0;
            for (Float time : resultTimes) {
                solver[i].xs[k] = k;
                solver[i].ys[k] = time;
                if (time > max_y) {
                    max_y = time;
                }
                k++;
            }
        }
        max_y = max_y * 1.05;
        int max_x;
        if (run == ALLRUNS) {
            max_x = (int) (selectedInstanceIds.size() * expController.getActiveExperiment().getNumRuns() * 1.1) + 1;
        } else {
            max_x = (int) (selectedInstanceIds.size() * 1.1) + 1;
        }
        engine.eval("plot(c(), c(), type='p', col='red', las=1, xlim=c(0," + max_x + "), ylim=c(0," + max_y + "), xaxs='i', yaxs='i', xlab='', ylab='', cex.main=1.5)");
        engine.eval("par(new=1)");
        String[] used_colors = new String[solver.length];
        int colNum = 0;
        int colCnt = 0;
        for (SolverInfos s : solver) {
            // plot points
            engine.assign("xs", s.xs);
            engine.assign("ys", s.ys);

            engine.assign("color", colors[colNum]);
            used_colors[colCnt++] = colors[colNum];
            engine.eval("plot(xs,ys,type='p',col=color,pch=" + colNum + ",xlim=c(0," + max_x + "),ylim=c(0," + max_y + "), xaxs='i', yaxs='i', axes=False, xlab='', ylab='', cex.main=1.5)");
            engine.eval("par(new=1)");
            // plot lines
            engine.eval("plot(xs, ys, type='l', col=color, lty=1, xlim=c(0," + max_x + "),ylim=c(0," + max_y + "),xaxs='i', yaxs='i', axes=False, xlab='', ylab='', cex.main=1.5)");
            engine.eval("par(new=1)");
            colNum = (colNum + 1) % colors.length;
        }

        engine.eval("mtext('number of solved instances', side=1, line=3, cex=1.2)");
        engine.eval("mtext('CPU Time (s)', side=2, padj=0, line=3, cex=1.2)");
        engine.eval("mtext('Number of instances solved within a given amount of time', padj=1, side=3, line=3, cex=1.7)");
        String[] lnames = new String[solver.length];
        int[] pchs = new int[solver.length];
        int[] lty = new int[solver.length + 1];
        for (int i = 0; i < solver.length; i++) {
            lnames[i] = solver[i].name;
            pchs[i] = i;
            lty[i] = i;
        }
        lty[solver.length] = solver.length;
        engine.assign("lnames", lnames);
        engine.assign("colors", used_colors);
        engine.assign("pchs", pchs);
        engine.assign("ltys", lty);
        engine.eval("legend(1, " + (max_y - (max_y * .3)) + ", legend=lnames, col=colors, pch=pchs, lty=ltys)");
    }

    @Override
    public String toString() {
        return "Number of instances solved within a given amount of time";
    }

    @Override
    public void loadDefaultValues() throws SQLException {
        comboRun.removeAllItems();
        comboRun.addItem("all runs - average");
        comboRun.addItem("all runs - median");
        comboRun.addItem("all runs");
        for (Integer i = 0; i < expController.getActiveExperiment().getNumRuns(); i++) {
            comboRun.addItem(i);
        }
        Vector<Instance> instances = new Vector<Instance>();
        instances.addAll(InstanceDAO.getAllByExperimentId(expController.getActiveExperiment().getId()));
        instanceSelector.setInstances(instances);
        instanceSelector.btnSelectAll();
        solverConfigurationSelector.setSolverConfigurations(SolverConfigurationDAO.getSolverConfigurationByExperimentId(expController.getActiveExperiment().getId()));
        solverConfigurationSelector.btnSelectAll();
    }

    @Override
    public String getTitle() {
        return "Cactus Plot (" + expController.getActiveExperiment().getName() + ")";
    }
}
