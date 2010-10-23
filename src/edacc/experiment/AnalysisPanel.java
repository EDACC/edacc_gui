/*
 * AnalysisPanel.java
 *
 * Created on 29.06.2010, 10:43:45
 */
package edacc.experiment;

import edacc.experiment.plots.*;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import javax.swing.JComboBox;
import javax.swing.JLabel;

/**
 *
 * @author simon
 */
public class AnalysisPanel extends javax.swing.JPanel {

    public JComboBox comboType;
    private JLabel jLabel1;
    private java.awt.GridBagConstraints gridBagConstraints;
    private AnalysisBottomPanel bottom;
    private ExperimentController expController;
    private Dependency[][] dependencies;
    private static Class<Plot>[] plotClasses = (Class<Plot>[]) new Class<?>[]{
                BoxPlot.class, ScatterOnePropertyTwoSolvers.class,
                ScatterTwoPropertiesOneSolver.class, ScatterInstancePropertySolverProperty.class, 
                CactusPlot.class, KernelDensityPlot.class,
                RTDPlot.class, RTDsPlot.class,
                ProbabilisticDomination.class
            };

    /** Creates new form AnalysisPanel */
    public AnalysisPanel(ExperimentController controller) {
        initComponents();
        this.expController = controller;
        bottom = new AnalysisBottomPanel(this);
        jLabel1 = new javax.swing.JLabel();
        jLabel1.setText("Plot type:");
        comboType = new javax.swing.JComboBox();
        comboType.setModel(new javax.swing.DefaultComboBoxModel(new String[]{}));
        for (Class<Plot> plotClass : plotClasses) {
            comboType.addItem(new ComboTypeEntry(plotClass));
        }
        dependencies = new Dependency[plotClasses.length][];
        for (int i = 0; i < plotClasses.length; i++) {
            try {
                dependencies[i] = (Dependency[]) plotClasses[i].getMethod("getDependencies", new Class[]{}).invoke(null);
            } catch (Exception e) {
                javax.swing.JOptionPane.showMessageDialog(null, e.getMessage(), "Error while generating form", javax.swing.JOptionPane.ERROR_MESSAGE);
            }
        }

        comboType.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (comboType.getSelectedItem() instanceof ComboTypeEntry) {
                    ComboTypeEntry cte = (ComboTypeEntry) comboType.getSelectedItem();
                    try {
                        initialize();
                        cte.plotClass.getMethod("loadDefaultValues", new Class[]{ExperimentController.class}).invoke(null, expController);
                        initializePlotType(dependencies[comboType.getSelectedIndex()]);

                    } catch (Exception ex) {
                        javax.swing.JOptionPane.showMessageDialog(null, ex.getMessage(), "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
                        initialize();
                    }
                }
            }
        });

    }

    public void initialize() {
        this.removeAll();
        setLayout(new java.awt.GridBagLayout());

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.8;
        gridBagConstraints.weighty = 0.001;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(comboType, gridBagConstraints);

        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.05;
        gridBagConstraints.weighty = 0.001;
        gridBagConstraints.insets = new java.awt.Insets(8, 3, 3, 3);
        add(jLabel1, gridBagConstraints);
    }

    public void initializePlotType(Dependency[] dependencies) throws SQLException {
        for (Dependency dependency : dependencies) {
            gridBagConstraints.gridx = 0;
            gridBagConstraints.gridy++;
            JLabel label = new JLabel(dependency.getDescription() + ":");
            gridBagConstraints.weightx = 0.05;
            gridBagConstraints.insets = new java.awt.Insets(8, 3, 3, 3);

            add(label, gridBagConstraints);
            gridBagConstraints.gridx = 1;
            gridBagConstraints.weightx = 0.8;
            gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
            add(dependency.getGuiObject(), gridBagConstraints);
        }
        gridBagConstraints.gridy++;
        gridBagConstraints.gridx = 0;
        gridBagConstraints.weighty = 0.5;
        gridBagConstraints.weightx = 1.;
        gridBagConstraints.gridwidth = 2;
        add(bottom, gridBagConstraints);
        this.invalidate();
        this.repaint();
    }

    public Plot getSelectedPlot() {
        try {
            if (comboType.getSelectedItem() instanceof ComboTypeEntry) {
                ComboTypeEntry cte = (ComboTypeEntry) comboType.getSelectedItem();
                return (Plot) cte.plotClass.getConstructor(ExperimentController.class).newInstance(expController);
            }
        } catch (Exception e) {
            // TODO: error!
        }
        return null;
    }

    /**
     * Tries to set the class of the plot as the current plot type.
     * @param plot
     * @return false, iff setting the plot type failed (plot didn't exist)
     */
    public boolean setSelectedPlot(Plot plot) {
        for (int i = 0; i < comboType.getItemCount(); i++) {
            if (comboType.getItemAt(i) instanceof ComboTypeEntry) {
                ComboTypeEntry cte = (ComboTypeEntry) comboType.getItemAt(i);
                if (cte.plotClass == plot.getClass()) {
                    comboType.setSelectedIndex(i);
                    return true;
                }
            }
        }
        return false;
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setName("Form"); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 619, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 379, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables

    class ComboTypeEntry {

        public Class<?> plotClass;

        public ComboTypeEntry(Class<?> plotClass) {
            this.plotClass = plotClass;
        }

        @Override
        public String toString() {
            try {
                return (String) plotClass.getDeclaredMethod("getTitle", new Class[]{}).invoke(null, new Object[]{});
            } catch (Exception e) {
                return "Error while initializing";
            }
        }
    }
}
