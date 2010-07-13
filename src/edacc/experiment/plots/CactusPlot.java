package edacc.experiment.plots;

import edacc.experiment.ExperimentController;
import edacc.model.ExperimentResult;
import edacc.model.ExperimentResultDAO;
import edacc.model.Instance;
import edacc.model.InstanceDAO;
import edacc.model.SolverConfiguration;
import edacc.model.SolverConfigurationDAO;
import edacc.model.SolverDAO;
import java.sql.SQLException;
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
    private static final String[] colors = {"red", "green", "blue", "darkgoldenrod1", "darkolivegreen", "darkorchid", "deeppink", "darkgreen", "blue4"};
    private Dependency[] dependencies;
    private JComboBox comboRun;
    private InstanceSelector instanceSelector;
    private SolverConfigurationSelector solverConfigurationSelector;
    
    public CactusPlot(ExperimentController expController) {
        super(expController);
        comboRun = new JComboBox();
        instanceSelector = new InstanceSelector();
        solverConfigurationSelector = new SolverConfigurationSelector();
        dependencies = new Dependency[] {
            new Dependency("Solvers", solverConfigurationSelector),
            new Dependency("Instances", instanceSelector),
            new Dependency("Plot for run", comboRun)
        };
    }

    public Dependency[] getDependencies() {
        return dependencies;
    }

    public void plot(Rengine engine) throws SQLException, DependencyException {
        if (!(comboRun.getSelectedItem() instanceof Integer)) {
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
        for (Instance i: instances) {
            selectedInstanceIds.add(i.getId());
        }
        int run = (Integer) comboRun.getSelectedItem();
        SolverInfos[] solver = new SolverInfos[solverConfigs.size()];
        double max_y = 0;
        for (int i = 0; i < solver.length; i++) {
            SolverConfiguration sc = solverConfigs.get(i);
            Vector<ExperimentResult> results = ExperimentResultDAO.getAllBySolverConfigurationAndRunAndStatusOrderByTime(sc, run, 1);
            
            for (int k = results.size()-1; k >= 0; k--) {
                if (!selectedInstanceIds.contains(results.get(k).getInstanceId())) {
                    results.remove(k);
                }
            }

            solver[i] = new SolverInfos();
            solver[i].name = SolverDAO.getById(sc.getSolver_id()).getName();
            solver[i].xs = new int[results.size()+1];
            solver[i].ys = new double[results.size()+1];
            
            int k = 1;
            solver[i].xs[0] = 0;
            solver[i].ys[0] = 0;
            for (ExperimentResult r : results) {
                solver[i].xs[k] = k;
                solver[i].ys[k] = r.getTime();
                if (r.getTime() > max_y) max_y = r.getTime();
                k++;
            }
        }
        max_y = max_y * 1.05;
        int max_x = (int)(selectedInstanceIds.size() * 1.1)+1;//(int) (ExperimentResultDAO.getAllInstanceIdsByExperimentId(expController.getActiveExperiment().getId()).size()*1.1);
        engine.eval("plot(c(), c(), type='p', col='red', las=1, xlim=c(0,"+max_x+"), ylim=c(0,"+max_y+"), xaxs='i', yaxs='i', xlab='', ylab='', cex.main=1.5)");
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
            engine.eval("plot(xs,ys,type='p',col=color,pch="+colNum+",xlim=c(0,"+max_x+"),ylim=c(0,"+max_y+"), xaxs='i', yaxs='i', axes=False, xlab='', ylab='', cex.main=1.5)");
            engine.eval("par(new=1)");
            // plot lines
            engine.eval("plot(xs, ys, type='l', col=color, lty=1, xlim=c(0,"+max_x+"),ylim=c(0,"+max_y+"),xaxs='i', yaxs='i', axes=False, xlab='', ylab='', cex.main=1.5)");
            engine.eval("par(new=1)");
            colNum = (colNum +1)% colors.length;
        }

        engine.eval("mtext('number of solved instances', side=1, line=3, cex=1.2)");
        engine.eval("mtext('CPU Time (s)', side=2, padj=0, line=3, cex=1.2)");
        engine.eval("mtext('Number of instances solved within a given amount of time', padj=1, side=3, line=3, cex=1.7)");
        String[] lnames = new String[solver.length];
        int[] pchs = new int[solver.length];
        int[] lty = new int[solver.length+1];
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

    public void loadDefaultValues() throws SQLException {
        comboRun.removeAllItems();
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
}
