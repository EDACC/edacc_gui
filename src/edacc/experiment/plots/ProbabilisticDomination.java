package edacc.experiment.plots;

import edacc.experiment.AnalysisController;
import edacc.experiment.ExperimentController;
import edacc.experiment.REngineInitializationException;
import edacc.model.ExperimentResult;
import edacc.model.Instance;
import edacc.model.InstanceClassMustBeSourceException;
import edacc.model.Property;
import edacc.model.SolverConfiguration;
import edacc.model.TaskRunnable;
import edacc.model.Tasks;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.border.TitledBorder;
import org.rosuda.JRI.Rengine;

/**
 *
 * @author simon
 */
public class ProbabilisticDomination extends Plot {

    private static JComboBox comboSolver1, comboSolver2, comboProperty;
    private static InstanceSelector instanceSelector;
    private static JButton btnUpdate;
    private static JPanel pnlProbabilisticDomination;
    private static JPanel pnlFirstDominates, pnlSecondDominates, pnlCrossovers;
    private static JRadioButton[] radioButtons;
    private static ProbabilisticDomination probDom;
    private static SolverConfiguration _solver1, _solver2;
    private static ArrayList<Instance> _instances;
    private static Property _property;
    private Property property;
    private SolverConfiguration solver1, solver2;
    private Instance instance;
    private String infos, title;

    public ProbabilisticDomination(ExperimentController expController) {
        super(expController);
    }

    public static Dependency[] getDependencies() {
        if (comboSolver1 == null) {
            comboSolver1 = new JComboBox();
        }
        if (comboSolver2 == null) {
            comboSolver2 = new JComboBox();
        }
        if (comboProperty == null) {
            comboProperty = new JComboBox();
        }
        if (instanceSelector == null) {
            instanceSelector = new InstanceSelector();
        }
        if (btnUpdate == null) {
            btnUpdate = new JButton("Update");
        }
        if (pnlProbabilisticDomination == null) {
            pnlProbabilisticDomination = new JPanel(new GridBagLayout());
            pnlFirstDominates = new JPanel(new GridBagLayout());
            pnlSecondDominates = new JPanel(new GridBagLayout());
            pnlCrossovers = new JPanel(new GridBagLayout());
            GridBagConstraints c = new GridBagConstraints();
            c.gridy = 0;
            c.weightx = 1000;
            c.fill = GridBagConstraints.HORIZONTAL;
            pnlProbabilisticDomination.add(pnlFirstDominates, c);
            c.gridy++;
            pnlProbabilisticDomination.add(pnlSecondDominates, c);
            c.gridy++;
            pnlProbabilisticDomination.add(pnlCrossovers, c);
        }
        btnUpdate.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                updateProbabilisticDomination();
            }
        });
        return new Dependency[]{
                    new Dependency("First solver", comboSolver1),
                    new Dependency("Second solver", comboSolver2),
                    new Dependency("Property", comboProperty),
                    new Dependency("Instances", instanceSelector),
                    new Dependency("Update", btnUpdate),
                    new Dependency("Probabilistic Domination", pnlProbabilisticDomination)
                };
    }

    public static void loadDefaultValues(ExperimentController expController) throws SQLException, InstanceClassMustBeSourceException, IOException, Exception {
        if (probDom == null) {
            probDom = new ProbabilisticDomination(expController);
        }
        comboSolver1.removeAllItems();
        comboSolver2.removeAllItems();
        comboProperty.removeAllItems();
        for (SolverConfiguration sc : expController.getSolverConfigurations()) {
            comboSolver1.addItem(sc);
            comboSolver2.addItem(sc);
        }
        for (Property p : expController.getResultProperties()) {
            comboProperty.addItem(p);
        }
        instanceSelector.setInstances(expController.getInstances());
        instanceSelector.btnSelectAll();
    }

    public static void updateProbabilisticDomination() {
        Tasks.startTask(new TaskRunnable() {

            @Override
            public void run(Tasks task) {
                task.setOperationName("Updating probabilistic domination");
                _solver1 = (SolverConfiguration) comboSolver1.getSelectedItem();
                _solver2 = (SolverConfiguration) comboSolver2.getSelectedItem();
                _instances = instanceSelector.getSelectedInstances();
                _property = (Property) comboProperty.getSelectedItem();
                pnlFirstDominates.removeAll();
                pnlSecondDominates.removeAll();
                pnlCrossovers.removeAll();
                ArrayList<Instance> firstDominates = new ArrayList<Instance>();
                ArrayList<Instance> secondDominates = new ArrayList<Instance>();
                ArrayList<Instance> crossovers = new ArrayList<Instance>();
                task.setStatus("Loading data from database");
                try {
                    probDom.expController.getExperimentResults().updateExperimentResults();
                } catch (Exception e) {
                    // TODO: error
                    return;
                }
                task.setStatus("Calculating probabilistic domination");
                for (Instance i : _instances) {
                    int dom;
                    try {
                        dom = probDom.probabilisticDominates(i, _solver1, _solver2, _property);
                    } catch (Exception e) {
                        continue;
                    }

                    if (dom == 1) {
                        firstDominates.add(i);
                    } else if (dom == -1) {
                        secondDominates.add(i);
                    } else {
                        crossovers.add(i);
                    }
                }
                pnlFirstDominates.setBorder(new TitledBorder("Instances where " + _solver1 + " prob. dominates " + _solver2 + " (" + firstDominates.size() + ")"));
                pnlSecondDominates.setBorder(new TitledBorder("Instances where " + _solver2 + " prob. dominates " + _solver1 + " (" + secondDominates.size() + ")"));
                pnlCrossovers.setBorder(new TitledBorder("Instances with crossovers (" + crossovers.size() + ")"));
                ButtonGroup buttonGroup = new ButtonGroup();
                radioButtons = new JRadioButton[firstDominates.size() + secondDominates.size() + crossovers.size()];
                int buttonIdx = 0;
                JRadioButton radioButton;
                GridBagConstraints c = new GridBagConstraints();
                c.gridy = 0;
                c.weightx = 10000;
                c.fill = GridBagConstraints.HORIZONTAL;
                for (Instance i : firstDominates) {
                    radioButton = new JRadioButton(i.getName());
                    radioButtons[buttonIdx++] = radioButton;
                    buttonGroup.add(radioButton);
                    pnlFirstDominates.add(radioButton, c);
                    c.gridy++;
                }
                c.gridy = 0;
                for (Instance i : secondDominates) {
                    radioButton = new JRadioButton(i.getName());
                    radioButtons[buttonIdx++] = radioButton;
                    buttonGroup.add(radioButton);
                    pnlSecondDominates.add(radioButton, c);
                    c.gridy++;
                }
                c.gridy = 0;
                for (Instance i : crossovers) {
                    radioButton = new JRadioButton(i.getName());
                    radioButtons[buttonIdx++] = radioButton;
                    buttonGroup.add(radioButton);
                    pnlCrossovers.add(radioButton, c);
                    c.gridy++;
                }
                pnlProbabilisticDomination.revalidate();
            }
        }, true);

    }

    public int probabilisticDominates(Instance instance, SolverConfiguration sc1, SolverConfiguration sc2, Property prop) throws REngineInitializationException {
        ArrayList<ExperimentResult> results1 = expController.getExperimentResults().getResults(sc1.getId(), instance.getId());
        ArrayList<ExperimentResult> results2 = expController.getExperimentResults().getResults(sc2.getId(), instance.getId());
        ArrayList<Double> resultsDouble1 = new ArrayList<Double>();
        ArrayList<Double> resultsDouble2 = new ArrayList<Double>();
        for (ExperimentResult res : results1) {
            Double value = expController.getValue(res, prop);
            if (value == null) {
                // TODO: ...
                continue;
            }
            resultsDouble1.add(value);
        }
        for (ExperimentResult res : results2) {
            Double value = expController.getValue(res, prop);
            if (value == null) {
                // TODO: ...
                continue;
            }
            resultsDouble2.add(value);
        }
        Rengine engine = AnalysisController.getRengine();
        double[] resultsDoubleArray1 = new double[resultsDouble1.size()];
        for (int i = 0; i < resultsDouble1.size(); i++) {
            resultsDoubleArray1[i] = resultsDouble1.get(i);
        }
        double[] resultsDoubleArray2 = new double[resultsDouble2.size()];
        for (int i = 0; i < resultsDouble2.size(); i++) {
            resultsDoubleArray2[i] = resultsDouble2.get(i);
        }

        engine.assign("values1", resultsDoubleArray1);
        engine.assign("values2", resultsDoubleArray2);
        engine.eval("ecdf1 <- ecdf(values1)");
        engine.eval("ecdf2 <- ecdf(values2)");
        double[] ecdf1 = engine.eval("ecdf1(c(values1,values2))").asDoubleArray();
        double[] ecdf2 = engine.eval("ecdf2(c(values1,values2))").asDoubleArray();
        int result = 0;
        for (int i = 0; i < ecdf1.length; i++) {
            if (ecdf1[i] < ecdf2[i]) {
                if (result == 1) {
                    result = 0;
                    break;
                }
                result = -1;
            } else if (ecdf1[i] > ecdf2[i]) {
                if (result == -1) {
                    result = 0;
                    break;
                }
                result = 1;
            }
        }
        return result;
    }

    public static String getTitle() {
        return "Probabilistic Domination";
    }

    @Override
    public String getPlotTitle() {
        return title;
    }

    @Override
    public void plot(Rengine engine, ArrayList<PointInformation> pointInformations) throws Exception {
        solver1 = _solver1;
        solver2 = _solver2;
        property = _property;
        JRadioButton radioButton = null;
        if (radioButtons == null) {
            throw new DependencyException("You have to select an instance.");
        }
        for (JRadioButton btn : radioButtons) {
            if (btn.isSelected()) {
                radioButton = btn;
                break;
            }
        }
        if (radioButton == null) {
            throw new DependencyException("You have to select an instance.");
        }
        for (Instance i : _instances) {
            if (i.getName().equals(radioButton.getText())) {
                instance = i;
                break;
            }
        }
        if (instance == null) {
            throw new DependencyException("You have to select an instance.");
        }
        RTDPlot plot = new RTDPlot(expController);
        plot.sc1 = solver1;
        plot.sc2 = solver2;
        plot.instance = instance;
        plot.property = property;
        plot.plot(engine, pointInformations);
        infos = plot.getAdditionalInformations();
        title = plot.getPlotTitle();
        // we have to create a new probDom because the plotTabView is associated
        // with this one, and needs this instance for the informations or to replot, etc.
        probDom = new ProbabilisticDomination(expController);
    }

    @Override
    public String getAdditionalInformations() {
        return infos;
    }

    @Override
    public void updateDependencies() {
        // not needed, because we use the result property distribution plot for plots
    }
}
