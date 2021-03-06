package edacc.experiment.plots;

import edacc.experiment.ExperimentController;
import edacc.model.ExperimentResult;
import edacc.model.Instance;
import edacc.model.SolverConfiguration;
import edacc.model.Property;
import java.util.ArrayList;
import java.util.LinkedList;
import javax.swing.JComboBox;
import org.rosuda.JRI.Rengine;

/**
 *
 * @author simon
 */
public class RTDsPlot extends Plot {

    private static SolverConfigurationSelector solverSelector;
    private static JComboBox comboInstance, comboProperty;
    private ArrayList<SolverConfiguration> scs;
    private Instance instance;
    private Property property;
    private String title;
    private String warning;

    public RTDsPlot(ExperimentController expController) {
        super(expController);
    }

    public static Dependency[] getDependencies() {
        if (solverSelector == null) {
            solverSelector = new SolverConfigurationSelector();
        }
        if (comboProperty == null) {
            comboProperty = new JComboBox();
        }
        if (comboInstance == null) {
            comboInstance = new JComboBox();
        }
        return new Dependency[]{
                    new Dependency("Solvers", solverSelector),
                    new Dependency("Property", comboProperty),
                    new Dependency("Instance", comboInstance)
                };
    }

    public static void loadDefaultValues(ExperimentController expController) throws Exception {
        solverSelector.setSolverConfigurations(expController.getSolverConfigurations());
        solverSelector.btnSelectAll();
        for (Instance i : expController.getInstances()) {
            comboInstance.addItem(i);
        }
        for (Property p : expController.getResultProperties()) {
            comboProperty.addItem(p);
        }
    }

    @Override
    public void plot(Rengine engine, ArrayList<PointInformation> pointInformations) throws Exception {

        if (scs == null || instance == null || property == null) {
            if (solverSelector.getSelectedSolverConfigurations().isEmpty()) {
                throw new DependencyException("You have to select solvers in order to plot.");
            }

            if (!(comboInstance.getSelectedItem() instanceof Instance)) {
                throw new DependencyException("You have to select an instance.");
            }
            if (!(comboProperty.getSelectedItem() instanceof Property)) {
                throw new DependencyException("You have to select a property.");
            }
            scs = solverSelector.getSelectedSolverConfigurations();
            instance = (Instance) comboInstance.getSelectedItem();
            property = (Property) comboProperty.getSelectedItem();
        }
        warning = null;
        expController.getExperimentResults().updateExperimentResults();
        title = "Property distribution on " + instance + " (" + expController.getActiveExperiment().getName() + ")";
        double max_x = 0.;

        ArrayList<double[]> results = new ArrayList<double[]>();
        String[] legendNames = new String[scs.size()];
        String[] legendColors = new String[scs.size()];
        int k = 0;
        LinkedList<String> warnings = new LinkedList<String>();
        for (SolverConfiguration sc : scs) {
            ArrayList<ExperimentResult> res = expController.getExperimentResults().getResults(sc.getId(), instance.getId());
            ArrayList<Double> tmp = new ArrayList<Double>();
            int noResult = 0;
            for (int i = 0; i < res.size(); i++) {
                Double value = expController.getValue(res.get(i), property);
                if (value == null) {
                    noResult++;
                    continue;
                }
                tmp.add(value);
                if (value > max_x) {
                    max_x = value;
                }
            }
            if (noResult > 0) {
                warnings.add("Solver " + sc.getName() + " has not solved or has no result property calculated for " + noResult + " runs.");
            }
            double[] tmpArray = new double[tmp.size()];
            for (int i = 0; i < tmpArray.length; i++) {
                tmpArray[i] = tmp.get(i);
            }
            results.add(tmpArray);
            legendNames[k] = sc.getName();
            legendColors[k] = colors[k % colors.length];
            k++;
        }
        // plot without data to create the frame
        engine.eval("plot(c(), c(), type='p', col='red', las=1, xlim=c(0," + max_x + ") , ylim=c(-0.05,1.05), xaxs='i', yaxs='i', xlab='', ylab='', cex.main=1.5)");
        engine.eval("par(new=1)");

        // plot the distributions
        for (int i = 0; i < results.size(); i++) {
            engine.assign("results", results.get(i));
            engine.eval("plot(ecdf(results),"
                    + "main='', col='" + legendColors[i] + "', pch=" + i + ","
                    + "xlab='', ylab='', xaxs='i', yaxs='i', las=1,"
                    + "xaxt='n', yaxt='n',"
                    + "xlim=c(0.0," + max_x + "), ylim=c(-0.05, 1.05))");
            engine.eval("par(new=1)");
        }
        // plot labels and axes
        engine.eval("mtext('"+property.getName()+"', side=1, line=3, cex=1.2)");                      // bottom axis label
        engine.eval("mtext('P(solve within x " + property.getName() + ")', side=2, padj=0, line=3, cex=1.2)"); // left axis label
        engine.eval("mtext('Runtime Distributions', padj=1, side=3, line=3, cex=1.7)");     // plot title

        // plot legend
        engine.assign("legendNames", legendNames);
        engine.assign("legendColors", legendColors);
        engine.eval("legend('bottomright', inset=.01,"
                + "legend=legendNames,"
                + "col=legendColors,"
                + "pch=c(0," + scs.size() + "), lty=1)");
        if (warnings.size() > 0) {
            warning = htmlHeader
                    + "<h2>Warning</h2>";
            for (String w: warnings) {
                warning += w + "<br>";
            }
            warning += htmlFooter;
        }
    }

    @Override
    public String getPlotTitle() {
        return title;
    }

    @Override
    public String getAdditionalInformations() {
        return warning;
    }
    
    public static String getTitle() {
        return "Property distributions of solvers on an instance";
    }

    @Override
    public void updateDependencies() {
        if (scs == null ||instance == null || property == null) {
            return;
        }
        solverSelector.setSolverConfigurations(scs);
        comboInstance.setSelectedItem(instance);
        comboProperty.setSelectedItem(property);
    }
}
