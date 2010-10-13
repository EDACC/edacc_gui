package edacc.experiment.plots;

import edacc.experiment.ExperimentController;
import edacc.model.ExperimentDAO;
import edacc.model.ExperimentResult;
import edacc.model.Instance;
import edacc.model.InstanceDAO;
import edacc.model.SolverConfiguration;
import edacc.model.SolverProperty;
import java.sql.SQLException;
import java.util.ArrayList;
import javax.swing.JComboBox;
import org.rosuda.JRI.Rengine;

/**
 *
 * @author simon
 */
public class RTDPlot extends Plot {

    private static JComboBox combo1, combo2, comboProperty, comboInstance;
    public SolverConfiguration sc1, sc2;
    public Instance instance;
    public SolverProperty property;
    private String infos, title;

    public RTDPlot(ExperimentController expController) {
        super(expController);
    }

    public static Dependency[] getDependencies() {
        if (combo1 == null) {
            combo1 = new JComboBox();
        }
        if (combo2 == null) {
            combo2 = new JComboBox();
        }
        if (comboProperty == null) {
            comboProperty = new JComboBox();
        }
        if (comboInstance == null) {
            comboInstance = new JComboBox();
        }
        return new Dependency[]{
                    new Dependency("First solver", combo1),
                    new Dependency("Second solver", combo2),
                    new Dependency("Property", comboProperty),
                    new Dependency("Instance", comboInstance)
                };
    }

    public static void loadDefaultValues(ExperimentController expController) throws Exception {
        combo1.removeAllItems();
        combo2.removeAllItems();
        comboInstance.removeAllItems();
        for (SolverConfiguration solConfig : ExperimentDAO.getSolverConfigurationsInExperiment(expController.getActiveExperiment())) {
            combo1.addItem(solConfig);
            combo2.addItem(solConfig);
        }

        for (Instance i : InstanceDAO.getAllByExperimentId(expController.getActiveExperiment().getId())) {
            comboInstance.addItem(i);
        }
        for (SolverProperty p : getSolverProperties()) {
            comboProperty.addItem(p);
        }
    }

    @Override
    public void plot(Rengine engine, ArrayList<PointInformation> pointInformations) throws Exception {
        if (sc1 == null || sc2 == null || instance == null || property == null) {
            if (!(combo1.getSelectedItem() instanceof SolverConfiguration) || !(combo2.getSelectedItem() instanceof SolverConfiguration)) {
                throw new DependencyException("You have to select two solvers.");
            }
            if (!(comboInstance.getSelectedItem() instanceof Instance)) {
                throw new DependencyException("You have to select an instance.");
            }
            if (!(comboProperty.getSelectedItem() instanceof SolverProperty)) {
                throw new DependencyException("You have to select a property.");
            }
            sc1 = (SolverConfiguration) combo1.getSelectedItem();
            sc2 = (SolverConfiguration) combo2.getSelectedItem();
            instance = (Instance) comboInstance.getSelectedItem();
            property = (SolverProperty) comboProperty.getSelectedItem();
        }
        initializeResults();
        title = "Property distribution comparison on " + instance + " (" + expController.getActiveExperiment().getName() + ")";
        infos = null;
        ArrayList<ExperimentResult> results1 = getResults(sc1.getId(), instance.getId());
        ArrayList<ExperimentResult> results2 = getResults(sc2.getId(), instance.getId());

        double max_x = 0;
        ArrayList<Double> resultsDouble1 = new ArrayList<Double>();
        ArrayList<Double> resultsDouble2 = new ArrayList<Double>();
        int firstNoResult = 0;
        int secondNoResult = 0;
        // TODO: continue fix!
        for (int i = 0; i < results1.size(); i++) {
            Double tmp = getValue(results1.get(i), property);
            if (tmp == null) {
                firstNoResult++;
                continue;
            }
            resultsDouble1.add(tmp);
            if (tmp > max_x) {
                max_x = tmp;
            }
        }
        for (int i = 0; i < results2.size(); i++) {
            Double tmp = getValue(results2.get(i), property);
            if (tmp == null) {
                secondNoResult++;
                continue;
            }
            resultsDouble2.add(tmp);
            if (tmp > max_x) {
                max_x = tmp;
            }
        }
        double[] resultsDoubleArray1 = new double[resultsDouble1.size()];
        double[] resultsDoubleArray2 = new double[resultsDouble2.size()];
        for (int i = 0; i < resultsDoubleArray1.length; i++) {
            resultsDoubleArray1[i] = resultsDouble1.get(i);
        }
        for (int i = 0; i < resultsDoubleArray2.length; i++) {
            resultsDoubleArray2[i] = resultsDouble2.get(i);
        }
        engine.assign("results1", resultsDoubleArray1);
        engine.assign("results2", resultsDoubleArray2);
        engine.assign("legendNames", new String[]{sc1.toString(), sc2.toString()});
        // plot without data to create the frame
        engine.eval("plot(c(), c(), type='p', col='red', las=1, xlim=c(0," + max_x + ") , ylim=c(-0.05,1.05), xaxs='i', yaxs='i', xlab='', ylab='', cex.main=1.5)");
        engine.eval("par(new=1)");

        // plot the two distributions
        engine.eval("plot(ecdf(results1),"
                + "main='',"
                + "xlab='', ylab='', xaxs='i', yaxs='i', las=1, col='red',"
                + "xlim=c(0.0," + max_x + "), ylim=c(-0.05,1.05))");
        engine.eval("par(new=1)");
        engine.eval("plot(ecdf(results2),"
                + "main='',"
                + "xlab='', ylab='', xaxs='i', yaxs='i', las=1, col='blue',"
                + "xlim=c(0.0," + max_x + "), ylim=c(-0.05,1.05))");

        // plot labels and axes
        engine.eval("mtext('" + property.getName() + "', side=1, line=3, cex=1.2)");                      // bottom axis label
        engine.eval("mtext('P(solve within x seconds)', side=2, padj=0, line=3, cex=1.2)"); // left axis label
        engine.eval("mtext('RTD Comparison', padj=1, side=3, line=3, cex=1.7)");            // plot title

        // plot legend
        engine.eval("legend('bottomright',"
                + "legend=legendNames,"
                + "col=c('red', 'blue'),"
                + "pch=c(0,1), lty=1)");
        infos = htmlHeader;
        double[] kolmogorow = Statistics.kolmogorowSmirnow2sampleTest(engine, "results1", "results2");
        infos += "<h2>Kolmogorow-Smirnow two-sample test</h2>";
        if (kolmogorow != null) {
            infos += "H0: RTD1 = RTD2<br>"
                    + "H1: RTD1 != RTD2<br>"
                    + "Statistic: " + kolmogorow[0] + "<br>"
                    + "p-value: " + kolmogorow[1] + " (two-sided)<br>";
        } else {
        }
        double[] wilcox = Statistics.wilcoxTest(engine, "results1", "results2");
        if (wilcox != null) {
            infos += "<h2>Mann-Whitney-U Test (Wilcoxon rank sum test)</h2>"
                    + "H0: RTD1 = RTD2<br>"
                    + "H1: RTD1 != RTD2<br>"
                    + "Statistic: " + wilcox[0] + "<br>"
                    + "p-value: " + wilcox[1] + " (two-sided)<br>";
        } else {
        }
        // TODO: warnings
        infos += htmlFooter;
    }

    @Override
    public String getAdditionalInformations() {
        return infos;
    }

    @Override
    public String getPlotTitle() {
        return title;
    }

    public static String getTitle() {
        return "Property distribution comparison of two solvers on an instance";
    }
}
