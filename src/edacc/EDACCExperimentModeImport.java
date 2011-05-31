/*
 * EDACCExperimentModeImport.java
 *
 * Created on 28.05.2011, 13:58:10
 */
package edacc;

import edacc.experiment.ExperimentTableModel;
import edacc.experiment.InstanceTableModel;
import edacc.experiment.SolverConfigurationTableModel;
import edacc.experiment.Util;
import edacc.model.Experiment;
import edacc.model.ExperimentDAO;
import edacc.model.Instance;
import edacc.model.InstanceDAO;
import edacc.model.SolverConfiguration;
import edacc.model.SolverConfigurationDAO;
import edacc.model.TaskRunnable;
import edacc.model.Tasks;
import java.awt.Color;
import java.awt.Component;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Vector;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

/**
 *
 * @author simon
 */
public class EDACCExperimentModeImport extends javax.swing.JDialog {

    private ExperimentTableModel expTableModel;
    private SolverConfigurationTableModel solverConfigTableModel;
    private InstanceTableModel instanceTableModel;
    private HashMap<Integer, ArrayList<Instance>> instances;
    private HashMap<Integer, ArrayList<SolverConfiguration>> solverConfigs;
    private Experiment experiment;
    private HashSet<Integer> instanceIds;
    private HashSet<Integer> solverConfigIds;
    private TableCellRenderer defaultBooleanCellRenderer;
    private boolean cancelled;
    private ArrayList<SolverConfiguration> selectedSolverConfigs;
    private ArrayList<Instance> selectedInstances;
    private boolean importFinishedRuns;

    /** Creates new form EDACCExperimentModeImport */
    public EDACCExperimentModeImport(java.awt.Frame parent, boolean modal, Experiment experiment) {
        super(parent, modal);
        initComponents();
        cancelled = true;
        this.experiment = experiment;

        expTableModel = new ExperimentTableModel(true);
        solverConfigTableModel = new SolverConfigurationTableModel();
        instanceTableModel = new InstanceTableModel();

        tblExperiments.setModel(expTableModel);
        tblSolverConfigs.setModel(solverConfigTableModel);
        tblInstances.setModel(instanceTableModel);


        instances = new HashMap<Integer, ArrayList<Instance>>();
        solverConfigs = new HashMap<Integer, ArrayList<SolverConfiguration>>();

        instanceIds = new HashSet<Integer>();
        solverConfigIds = new HashSet<Integer>();

        defaultBooleanCellRenderer = tblExperiments.getDefaultRenderer(Boolean.class);

        tblExperiments.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

            @Override
            public void valueChanged(ListSelectionEvent e) {
                ArrayList<SolverConfiguration> scs = new ArrayList<SolverConfiguration>();
                ArrayList<Instance> inst = new ArrayList<Instance>();
                HashSet<Integer> instanceIds = new HashSet<Integer>();
                for (int viewRow : tblExperiments.getSelectedRows()) {
                    int row = tblExperiments.convertRowIndexToModel(viewRow);
                    Experiment exp = expTableModel.getExperimentAt(row);
                    ArrayList<SolverConfiguration> listScs = solverConfigs.get(exp.getId());
                    ArrayList<Instance> listInst = instances.get(exp.getId());
                    if (listScs != null) {
                        scs.addAll(listScs);
                    }
                    if (listInst != null) {
                        for (Instance i : listInst) {
                            if (!instanceIds.contains(i.getId())) {
                                inst.add(i);
                                instanceIds.add(i.getId());
                            }
                        }
                    }
                }
                solverConfigTableModel.setSolverConfigurations(scs);
                instanceTableModel.setInstances(inst, false, false);
                solverConfigTableModel.fireTableDataChanged();
                instanceTableModel.fireTableDataChanged();
            }
        });

        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer() {

            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c;
                if (value instanceof Boolean) {
                    c = defaultBooleanCellRenderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                } else {
                    c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                }
                boolean inExperiment = false;
                if (table == tblSolverConfigs) {
                    SolverConfiguration sc = solverConfigTableModel.getSolverConfigurationAt(tblSolverConfigs.convertRowIndexToModel(row));
                    if (solverConfigIds.contains(sc.getId())) {
                        inExperiment = true;
                    }
                } else if (table == tblInstances) {
                    Instance i = instanceTableModel.getInstanceAt(tblInstances.convertRowIndexToModel(row));
                    if (instanceIds.contains(i.getId())) {
                        inExperiment = true;
                    }
                }
                if (isSelected) {
                    c.setBackground(table.getSelectionBackground());
                } else {
                    if (inExperiment) {
                        c.setBackground(Color.orange);
                    } else {
                        c.setBackground(table.getBackground());
                    }
                }
                return c;
            }
        };

        tblInstances.setDefaultRenderer(String.class, renderer);
        tblInstances.setDefaultRenderer(Boolean.class, renderer);
        tblInstances.setDefaultRenderer(Integer.class, renderer);
        tblInstances.setDefaultRenderer(Float.class, renderer);
        tblInstances.setDefaultRenderer(Double.class, renderer);

        tblSolverConfigs.setDefaultRenderer(String.class, renderer);
        tblSolverConfigs.setDefaultRenderer(Boolean.class, renderer);
        tblSolverConfigs.setDefaultRenderer(Integer.class, renderer);
        tblSolverConfigs.setDefaultRenderer(Float.class, renderer);
        tblSolverConfigs.setDefaultRenderer(Double.class, renderer);
    }

    public void initializeData() {
        Tasks.startTask(new TaskRunnable() {

            @Override
            public void run(Tasks task) {
                try {
                    ArrayList<Experiment> exps = new ArrayList<Experiment>();
                    exps.addAll(ExperimentDAO.getAll());
                    for (Experiment e : exps) {
                        if (e.getId() == experiment.getId()) {
                            exps.remove(e);
                            break;
                        }
                    }
                    expTableModel.setExperiments(exps);
                    Vector<Instance> tmpInstances = new Vector<Instance>();
                    tmpInstances.addAll(InstanceDAO.getAll());

                    for (Experiment exp : exps) {
                        ArrayList<Instance> tmp = new ArrayList<Instance>();
                        tmp.addAll(InstanceDAO.getAllByExperimentId(exp.getId()));
                        instances.put(exp.getId(), tmp);
                    }

                    ArrayList<SolverConfiguration> tmpSolverConfigs = SolverConfigurationDAO.getAll();
                    for (SolverConfiguration sc : tmpSolverConfigs) {
                        ArrayList<SolverConfiguration> list = solverConfigs.get(sc.getExperiment_id());
                        if (list == null) {
                            list = new ArrayList<SolverConfiguration>();
                            solverConfigs.put(sc.getExperiment_id(), list);
                        }
                        list.add(sc);
                    }

                    for (Instance i : InstanceDAO.getAllByExperimentId(experiment.getId())) {
                        instanceIds.add(i.getId());
                    }

                    for (SolverConfiguration solverConfig : SolverConfigurationDAO.getSolverConfigurationByExperimentId(experiment.getId())) {
                        for (SolverConfiguration sc : tmpSolverConfigs) {
                            if (solverConfig.hasEqualSemantics(sc)) {
                                solverConfigIds.add(sc.getId());
                            }
                        }
                    }
                    SwingUtilities.invokeLater(new Runnable() {

                        @Override
                        public void run() {
                            tblExperiments.selectAll();
                            Util.updateTableColumnWidth(tblExperiments);
                            Util.updateTableColumnWidth(tblInstances);
                            Util.updateTableColumnWidth(tblSolverConfigs);
                        }
                    });
                } catch (final Exception ex) {
                    SwingUtilities.invokeLater(new Runnable() {

                        @Override
                        public void run() {
                            JOptionPane.showMessageDialog(EDACCExperimentModeImport.this, "Error while loading the import dialog: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                            EDACCExperimentModeImport.this.cancelled = true;
                            EDACCExperimentModeImport.this.setVisible(false);
                        }
                        
                    });
                }
            }
        });
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        splitExperiment = new javax.swing.JSplitPane();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblExperiments = new javax.swing.JTable();
        jSplitPane2 = new javax.swing.JSplitPane();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblSolverConfigs = new javax.swing.JTable();
        jScrollPane3 = new javax.swing.JScrollPane();
        tblInstances = new javax.swing.JTable();
        btnImport = new javax.swing.JButton();
        chkImportFinishedRuns = new javax.swing.JCheckBox();
        btnCancel = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(edacc.EDACCApp.class).getContext().getResourceMap(EDACCExperimentModeImport.class);
        setTitle(resourceMap.getString("Form.title")); // NOI18N
        setName("Form"); // NOI18N

        splitExperiment.setDividerLocation(150);
        splitExperiment.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        splitExperiment.setName("splitExperiment"); // NOI18N

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        tblExperiments.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        tblExperiments.setName("tblExperiments"); // NOI18N
        jScrollPane1.setViewportView(tblExperiments);

        splitExperiment.setLeftComponent(jScrollPane1);

        jSplitPane2.setDividerLocation(300);
        jSplitPane2.setName("jSplitPane2"); // NOI18N

        jScrollPane2.setName("jScrollPane2"); // NOI18N

        tblSolverConfigs.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        tblSolverConfigs.setName("tblSolverConfigs"); // NOI18N
        jScrollPane2.setViewportView(tblSolverConfigs);

        jSplitPane2.setLeftComponent(jScrollPane2);

        jScrollPane3.setName("jScrollPane3"); // NOI18N

        tblInstances.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        tblInstances.setName("tblInstances"); // NOI18N
        jScrollPane3.setViewportView(tblInstances);

        jSplitPane2.setRightComponent(jScrollPane3);

        splitExperiment.setBottomComponent(jSplitPane2);

        btnImport.setText(resourceMap.getString("btnImport.text")); // NOI18N
        btnImport.setName("btnImport"); // NOI18N
        btnImport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnImportActionPerformed(evt);
            }
        });

        chkImportFinishedRuns.setText(resourceMap.getString("chkImportFinishedRuns.text")); // NOI18N
        chkImportFinishedRuns.setName("chkImportFinishedRuns"); // NOI18N

        btnCancel.setText(resourceMap.getString("btnCancel.text")); // NOI18N
        btnCancel.setName("btnCancel"); // NOI18N
        btnCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelActionPerformed(evt);
            }
        });

        jLabel1.setText(resourceMap.getString("jLabel1.text")); // NOI18N
        jLabel1.setName("jLabel1"); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(splitExperiment, javax.swing.GroupLayout.DEFAULT_SIZE, 633, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addContainerGap(217, Short.MAX_VALUE))
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnCancel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 358, Short.MAX_VALUE)
                .addComponent(chkImportFinishedRuns)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnImport)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(splitExperiment, javax.swing.GroupLayout.DEFAULT_SIZE, 424, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btnImport)
                        .addComponent(chkImportFinishedRuns))
                    .addComponent(btnCancel))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnImportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnImportActionPerformed
        selectedSolverConfigs = solverConfigTableModel.getSelectedSolverConfigurations();
        selectedInstances = instanceTableModel.getSelectedInstances();
        cancelled = false;
        importFinishedRuns = chkImportFinishedRuns.isSelected();
        setVisible(false);
    }//GEN-LAST:event_btnImportActionPerformed

    private void btnCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelActionPerformed
        cancelled = true;
        setVisible(false);
    }//GEN-LAST:event_btnCancelActionPerformed

    public boolean isCancelled() {
        return cancelled;
    }

    public boolean getImportFinishedRuns() {
        return importFinishedRuns;
    }

    public ArrayList<Instance> getSelectedInstances() {
        return selectedInstances;
    }

    public ArrayList<SolverConfiguration> getSelectedSolverConfigs() {
        return selectedSolverConfigs;
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnImport;
    private javax.swing.JCheckBox chkImportFinishedRuns;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JSplitPane jSplitPane2;
    private javax.swing.JSplitPane splitExperiment;
    private javax.swing.JTable tblExperiments;
    private javax.swing.JTable tblInstances;
    private javax.swing.JTable tblSolverConfigs;
    // End of variables declaration//GEN-END:variables
}
