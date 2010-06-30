package edacc.experiment.plots;

import edacc.experiment.ExperimentController;
import edacc.model.ExperimentResult;
import edacc.model.ExperimentResultDAO;
import edacc.model.SolverConfiguration;
import edacc.model.SolverConfigurationDAO;
import edacc.model.SolverDAO;
import java.sql.SQLException;
import java.util.Vector;
import javax.swing.JTextField;
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
public class CactusPlot implements PlotInterface {
    private static final String[] colors = {"red", "green", "blue", "darkgoldenrod1", "darkolivegreen", "darkorchid", "deeppink", "darkgreen", "blue4"};
    private ExperimentController expController;
    private Dependency[] dependencies;
    private JTextField txtRun;
    public CactusPlot(ExperimentController expController) {
        this.expController = expController;
        txtRun = new JTextField();
        dependencies = new Dependency[] {
            new Dependency("Plot for run", txtRun)
        };
    }

    public Dependency[] getDependencies() {
        return dependencies;
    }

    public void plot(Rengine engine) throws SQLException, DependencyException {
        
        int run;
        try {
            run = Integer.parseInt(txtRun.getText());
        } catch (NumberFormatException ex) {
            throw new DependencyException("Expected integer for run.");
        }
        Vector<SolverConfiguration> solverConfigs = SolverConfigurationDAO.getSolverConfigurationByExperimentId(expController.getActiveExperiment().getId());
        SolverInfos[] solver = new SolverInfos[solverConfigs.size()];
        double max_y = 0;
        for (int i = 0; i < solver.length; i++) {
            SolverConfiguration sc = solverConfigs.get(i);
            Vector<ExperimentResult> results = ExperimentResultDAO.getAllBySolverConfigurationAndRunAndStatusOrderByTime(sc, run, 1);
            solver[i] = new SolverInfos();
            solver[i].name = SolverDAO.getById(sc.getSolver_id()).getName();
            solver[i].xs = new int[results.size()];
            solver[i].ys = new double[results.size()];
            
            int k = 0;
            for (ExperimentResult r : results) {
                solver[i].xs[k] = k+1;
                solver[i].ys[k] = r.getTime();
                if (r.getTime() > max_y) max_y = r.getTime();
                k++;
            }
        }
        max_y = max_y * 1.05;
        int max_x = ExperimentResultDAO.getAllInstanceIdsByExperimentId(expController.getActiveExperiment().getId()).size()+10;
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

    public void loadDefaultValues() {
        txtRun.setText("0");
    }


}
