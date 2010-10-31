package edacc.experiment.plots;

import edacc.experiment.ExperimentController;
import edacc.model.ExperimentResult;
import edacc.model.ExperimentResultStatus;
import edacc.model.Instance;
import edacc.model.SolverConfiguration;
import edacc.model.SolverDAO;
import edacc.model.Property;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
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
    private static final ExperimentResultStatus[] statusCodes = new ExperimentResultStatus[] { ExperimentResultStatus.SUCCESSFUL };
    private static JComboBox comboRun, comboProperty;
    private static InstanceSelector instanceSelector;
    private static SolverConfigurationSelector solverConfigurationSelector;
    private Integer run;
    private Property property;
    private ArrayList<Instance> instances;
    private ArrayList<SolverConfiguration> solverConfigs;
    private HashSet<Integer> selectedInstanceIds;

    public CactusPlot(ExperimentController expController) {
        super(expController);
    }

    public static Dependency[] getDependencies() {
        if (comboRun == null) {
            comboRun = new JComboBox();
        }
        if (comboProperty == null) {
            comboProperty = new JComboBox();
        }
        if (instanceSelector == null) {
            instanceSelector = new InstanceSelector();
        }
        if (solverConfigurationSelector == null) {
            solverConfigurationSelector = new SolverConfigurationSelector();
        }
        return new Dependency[]{
                    new Dependency("Solvers", solverConfigurationSelector),
                    new Dependency("Instances", instanceSelector),
                    new Dependency("Property", comboProperty),
                    new Dependency("Plot for run", comboRun)
                };
    }

    public static void loadDefaultValues(ExperimentController expController) throws Exception {
        comboRun.removeAllItems();
        comboRun.addItem(AVERAGE_TEXT);
        comboRun.addItem(MEDIAN_TEXT);
        comboRun.addItem(ALLRUNS_TEXT);
        for (Integer i = 0; i < expController.getActiveExperiment().getNumRuns(); i++) {
            comboRun.addItem(i);
        }
        comboProperty.removeAllItems();
        for (Property sp : expController.getResultProperties()) {
            comboProperty.addItem(sp);
        }
        instanceSelector.setInstances(expController.getInstances());
        instanceSelector.btnSelectAll();
        solverConfigurationSelector.setSolverConfigurations(expController.getSolverConfigurations());
        solverConfigurationSelector.btnSelectAll();
    }

    @Override
    public void plot(Rengine engine, ArrayList<PointInformation> pointInformations) throws Exception {
        if (run == null || property == null || instances == null || solverConfigs == null || selectedInstanceIds == null) {
            run = -4;
            if (AVERAGE_TEXT.equals(comboRun.getSelectedItem())) {
                run = AVERAGE;
            } else if (MEDIAN_TEXT.equals(comboRun.getSelectedItem())) {
                run = MEDIAN;
            } else if (ALLRUNS_TEXT.equals(comboRun.getSelectedItem())) {
                run = ALLRUNS;
            } else if (comboRun.getSelectedItem() instanceof Integer) {
                run = (Integer) comboRun.getSelectedItem();
            }
            if (run == -4) {
                throw new DependencyException("You have to select a run.");
            }
            instances = instanceSelector.getSelectedInstances();
            if (instances == null || instances.isEmpty()) {
                throw new DependencyException("You have to select instances in order to plot.");
            }
            solverConfigs = solverConfigurationSelector.getSelectedSolverConfigurations();
            if (solverConfigs.isEmpty()) {
                throw new DependencyException("You have to select solvers in order to plot.");
            }
            if (!(comboProperty.getSelectedItem() instanceof Property)) {
                throw new DependencyException("You have to select a property.");
            }
            property = (Property) comboProperty.getSelectedItem();
            selectedInstanceIds = new HashSet<Integer>();
            for (Instance i : instances) {
                selectedInstanceIds.add(i.getId());
            }
        }
        expController.updateExperimentResults();
        SolverInfos[] solver = new SolverInfos[solverConfigs.size()];
        double max_y = 0;
        int max_x = 0;
        for (int i = 0; i < solver.length; i++) {
            SolverConfiguration sc = solverConfigs.get(i);
            ArrayList<Double> resultValues = new ArrayList<Double>();
            int k = 0;
            for (Integer instanceId : selectedInstanceIds) {
                if (run == ALLRUNS) {
                    ArrayList<ExperimentResult> tmp = expController.getResults(sc.getId(), instanceId, statusCodes);
                    for (ExperimentResult er : tmp) {
                        Double value = expController.getValue(er, property);
                        if (value != null) {
                            resultValues.add(value);
                        }
                    }
                } else if (run == MEDIAN || run == AVERAGE) {
                    ArrayList<ExperimentResult> results = expController.getResults(sc.getId(), instanceId, statusCodes);
                    Double value;
                    if (run == MEDIAN) {
                        value = getMedian(results, property);
                    } else {
                        value = getAverage(results, property);
                    }
                    if (value != null) {
                        resultValues.add(value);
                    }
                } else {
                    ExperimentResult res = expController.getResult(sc.getId(), instanceId, run);
                    if (res == null) {
                        continue;
                    }
                    Double value = expController.getValue(res, property);
                    if (value == null) {
                        continue;
                    }
                    resultValues.add(value);
                }
            }
            Collections.sort(resultValues);

            solver[i] = new SolverInfos();
            solver[i].name = SolverDAO.getById(sc.getSolver_id()).getName();
            solver[i].xs = new int[resultValues.size() + 1];
            solver[i].ys = new double[resultValues.size() + 1];

            k = 1;
            solver[i].xs[0] = 0;
            solver[i].ys[0] = 0;
            for (Double value : resultValues) {
                solver[i].xs[k] = k;
                solver[i].ys[k] = value;
                if (value > max_y) {
                    max_y = value;
                }
                k++;
            }
            if (resultValues.size() > max_x) {
                max_x = resultValues.size();
            }
        }
        max_y = max_y * 1.05;
        max_x = (int) (max_x * 1.1) + 1;
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
        engine.eval("mtext('" + property.getName() + "', side=2, padj=0, line=3, cex=1.2)");
        engine.eval("mtext('Number of instances solved within a given amount of " + property.getName() + "', padj=1, side=3, line=3, cex=1.7)");
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

    public static String getTitle() {
        return "Number of instances solved within a given amount of a property";
    }

    @Override
    public String getPlotTitle() {
        return "Cactus Plot (" + expController.getActiveExperiment().getName() + ")";
    }

    @Override
    public void updateDependencies() {
        if (run == null || property == null || instances == null || solverConfigs == null) {
            return;
        }
        if (run == AVERAGE) {
            comboRun.setSelectedItem(AVERAGE_TEXT);
        } else if (run == MEDIAN) {
            comboRun.setSelectedItem(MEDIAN_TEXT);
        } else if (run == ALLRUNS) {
            comboRun.setSelectedItem(ALLRUNS_TEXT);
        } else {
            comboRun.setSelectedItem(run);
        }
        comboProperty.setSelectedItem(property);
        instanceSelector.setSelectedInstances(instances);
        solverConfigurationSelector.setSelectedSolverConfigurations(solverConfigs);
    }
}
