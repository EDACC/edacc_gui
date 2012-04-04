/*
 * EDACCImportExport.java
 *
 * Created on 12.02.2012, 22:24:01
 */
package edacc;

import edacc.importexport.ExperimentSelectionTableModel;
import edacc.experiment.InstanceTableModel;
import edacc.experiment.SolverTableModel;
import edacc.importexport.ExportController;
import edacc.importexport.ExportSummaryPanel;
import edacc.importexport.ImportController;
import edacc.importexport.ImportExportController;
import edacc.importexport.ImportSummaryPanel;
import edacc.importexport.InstanceFixedSelectionTableModel;
import edacc.importexport.SolverFixedSelectionTableModel;
import edacc.importexport.VerifierFixedSelectionTableModel;
import edacc.model.Instance;
import edacc.model.InstanceClassMustBeSourceException;
import edacc.model.Solver;
import edacc.model.TaskRunnable;
import edacc.model.Tasks;
import edacc.model.Verifier;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.zip.ZipException;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileFilter;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableRowSorter;

/**
 *
 * @author simon
 */
public class EDACCImportExport extends javax.swing.JPanel {
    
    private ImportExportController controller;
    private ExperimentSelectionTableModel expTableModel;
    private SolverFixedSelectionTableModel solTableModel;
    private InstanceFixedSelectionTableModel insTableModel;
    private VerifierFixedSelectionTableModel verifierTableModel;
    private EDACCFilter instanceFilter;
    private EDACCFilter experimentFilter;
    private EDACCFilter solverFilter;
    private JPanel panelSummary;

    /** Creates new form EDACCImportExport */
    public EDACCImportExport() throws SQLException, InstanceClassMustBeSourceException, IOException {
        initComponents();
        panelSummary = new ExportSummaryPanel();
        pnlSummaryComponent.setLayout(new BorderLayout());
        pnlSummaryComponent.add(panelSummary, BorderLayout.CENTER);
        controller = new ExportController();
        fillTables();
    }
    
    public EDACCImportExport(File file) throws ZipException, IOException, ClassNotFoundException {
        initComponents();
        controller = new ImportController(file);
        panelSummary = new ImportSummaryPanel((ImportController) controller);
        pnlSummaryComponent.setLayout(new BorderLayout());
        pnlSummaryComponent.add(panelSummary, BorderLayout.CENTER);
        fillTables();
    }
    
    private void fillTables() {
        tblExperiments.setDefaultRenderer(char.class, new DefaultTableCellRenderer() {
            
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                lbl.setHorizontalAlignment(JLabel.CENTER);
                return lbl;
            }
        });
        tblSolvers.setDefaultRenderer(Boolean.class, new BooleanRenderer());
        tblInstances.setDefaultRenderer(Boolean.class, new BooleanRenderer());
        tblVerifiers.setDefaultRenderer(Boolean.class, new BooleanRenderer());
        expTableModel = new ExperimentSelectionTableModel();
        solTableModel = new SolverFixedSelectionTableModel();
        insTableModel = new InstanceFixedSelectionTableModel();
        verifierTableModel = new VerifierFixedSelectionTableModel();
        
        tblExperiments.setModel(expTableModel);
        tblSolvers.setModel(solTableModel);
        tblInstances.setModel(insTableModel);
        tblVerifiers.setModel(verifierTableModel);
        tblExperiments.setRowSorter(new TableRowSorter<ExperimentSelectionTableModel>(expTableModel));
        tblSolvers.setRowSorter(new TableRowSorter<SolverFixedSelectionTableModel>(solTableModel));
        tblInstances.setRowSorter(new TableRowSorter<InstanceFixedSelectionTableModel>(insTableModel));
        tblVerifiers.setRowSorter(new TableRowSorter<VerifierFixedSelectionTableModel>(verifierTableModel));
        expTableModel.setExperiments(controller.getExperiments());
        solTableModel.setSolvers(controller.getSolvers());
        insTableModel.setInstances(controller.getInstances(), false, false);
        verifierTableModel.setVerifiers(controller.getVerifiers());
        
        edacc.experiment.Util.addSpaceSelection(tblExperiments, expTableModel.getColumnCount() - 1);
        edacc.experiment.Util.addSpaceSelection(tblSolvers, SolverTableModel.COL_SELECTED);
        edacc.experiment.Util.addSpaceSelection(tblInstances, InstanceTableModel.COL_SELECTED);
        edacc.experiment.Util.addSpaceSelection(tblVerifiers, VerifierFixedSelectionTableModel.COL_SELECTED);
        
        experimentFilter = new EDACCFilter(EDACCApp.getApplication().getMainFrame(), true, tblExperiments, true);
        instanceFilter = new EDACCFilter(EDACCApp.getApplication().getMainFrame(), true, tblInstances, true);
        solverFilter = new EDACCFilter(EDACCApp.getApplication().getMainFrame(), true, tblSolvers, true);
        experimentFilter.setName("EDACCImportExport.experimentFilter");
        instanceFilter.setName("EDACCImportExport.instanceFilter");
        solverFilter.setName("EDACCImportExport.solverFilter");
        
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        tabbedPane = new javax.swing.JTabbedPane();
        pnlExperiments = new javax.swing.JPanel();
        lblExperimentFilterStatus = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblExperiments = new JTableTooltipInformation();
        btnExperimentNext = new javax.swing.JButton();
        btnFilterExperiments = new javax.swing.JButton();
        pnlSolvers = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblSolvers = new JTableTooltipInformation();
        btnFilterSolvers = new javax.swing.JButton();
        btnSolversNext = new javax.swing.JButton();
        btnSolversPrevious = new javax.swing.JButton();
        lblFilterSolverText = new javax.swing.JLabel();
        pnlInstances = new javax.swing.JPanel();
        lblInstancesFilterText = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        tblInstances = new JTableTooltipInformation();
        btnInstancesNext = new javax.swing.JButton();
        btnInstancesPrevious = new javax.swing.JButton();
        btnInstancesFilter = new javax.swing.JButton();
        pnlVerifiers = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        tblVerifiers = new javax.swing.JTable();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        pnlSummary = new javax.swing.JPanel();
        pnlSummaryComponent = new javax.swing.JPanel();
        btnFinish = new javax.swing.JButton();
        btnSummaryPrevious = new javax.swing.JButton();

        setName("Form"); // NOI18N

        tabbedPane.setName("tabbedPane"); // NOI18N
        tabbedPane.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                tabbedPaneStateChanged(evt);
            }
        });

        pnlExperiments.setName("pnlExperiments"); // NOI18N

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(edacc.EDACCApp.class).getContext().getResourceMap(EDACCImportExport.class);
        lblExperimentFilterStatus.setText(resourceMap.getString("lblExperimentFilterStatus.text")); // NOI18N
        lblExperimentFilterStatus.setName("lblExperimentFilterStatus"); // NOI18N

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        tblExperiments.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        tblExperiments.setName("tblExperiments"); // NOI18N
        jScrollPane1.setViewportView(tblExperiments);

        btnExperimentNext.setText(resourceMap.getString("btnExperimentNext.text")); // NOI18N
        btnExperimentNext.setName("btnExperimentNext"); // NOI18N
        btnExperimentNext.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExperimentNextActionPerformed(evt);
            }
        });

        btnFilterExperiments.setText(resourceMap.getString("btnFilterExperiments.text")); // NOI18N
        btnFilterExperiments.setName("btnFilterExperiments"); // NOI18N
        btnFilterExperiments.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFilterExperimentsActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnlExperimentsLayout = new javax.swing.GroupLayout(pnlExperiments);
        pnlExperiments.setLayout(pnlExperimentsLayout);
        pnlExperimentsLayout.setHorizontalGroup(
            pnlExperimentsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlExperimentsLayout.createSequentialGroup()
                .addComponent(lblExperimentFilterStatus)
                .addContainerGap(674, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlExperimentsLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnFilterExperiments)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 540, Short.MAX_VALUE)
                .addComponent(btnExperimentNext)
                .addContainerGap())
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 674, Short.MAX_VALUE)
        );

        pnlExperimentsLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {btnExperimentNext, btnFilterExperiments});

        pnlExperimentsLayout.setVerticalGroup(
            pnlExperimentsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlExperimentsLayout.createSequentialGroup()
                .addComponent(lblExperimentFilterStatus)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 455, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlExperimentsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnExperimentNext)
                    .addComponent(btnFilterExperiments))
                .addContainerGap())
        );

        tabbedPane.addTab(resourceMap.getString("pnlExperiments.TabConstraints.tabTitle"), pnlExperiments); // NOI18N

        pnlSolvers.setName("pnlSolvers"); // NOI18N

        jScrollPane2.setName("jScrollPane2"); // NOI18N

        tblSolvers.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        tblSolvers.setName("tblSolvers"); // NOI18N
        jScrollPane2.setViewportView(tblSolvers);

        btnFilterSolvers.setText(resourceMap.getString("btnFilterSolvers.text")); // NOI18N
        btnFilterSolvers.setName("btnFilterSolvers"); // NOI18N

        btnSolversNext.setText(resourceMap.getString("btnSolversNext.text")); // NOI18N
        btnSolversNext.setName("btnSolversNext"); // NOI18N
        btnSolversNext.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSolversNextActionPerformed(evt);
            }
        });

        btnSolversPrevious.setText(resourceMap.getString("btnSolversPrevious.text")); // NOI18N
        btnSolversPrevious.setName("btnSolversPrevious"); // NOI18N
        btnSolversPrevious.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSolversPreviousActionPerformed(evt);
            }
        });

        lblFilterSolverText.setText(resourceMap.getString("lblFilterSolverText.text")); // NOI18N
        lblFilterSolverText.setName("lblFilterSolverText"); // NOI18N

        javax.swing.GroupLayout pnlSolversLayout = new javax.swing.GroupLayout(pnlSolvers);
        pnlSolvers.setLayout(pnlSolversLayout);
        pnlSolversLayout.setHorizontalGroup(
            pnlSolversLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlSolversLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnFilterSolvers)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 429, Short.MAX_VALUE)
                .addComponent(btnSolversPrevious)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnSolversNext)
                .addContainerGap())
            .addGroup(pnlSolversLayout.createSequentialGroup()
                .addComponent(lblFilterSolverText)
                .addContainerGap())
            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 674, Short.MAX_VALUE)
        );

        pnlSolversLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {btnFilterSolvers, btnSolversNext, btnSolversPrevious});

        pnlSolversLayout.setVerticalGroup(
            pnlSolversLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlSolversLayout.createSequentialGroup()
                .addComponent(lblFilterSolverText)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 455, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlSolversLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnFilterSolvers)
                    .addComponent(btnSolversNext)
                    .addComponent(btnSolversPrevious))
                .addContainerGap())
        );

        tabbedPane.addTab(resourceMap.getString("pnlSolvers.TabConstraints.tabTitle"), pnlSolvers); // NOI18N

        pnlInstances.setName("pnlInstances"); // NOI18N

        lblInstancesFilterText.setText(resourceMap.getString("lblInstancesFilterText.text")); // NOI18N
        lblInstancesFilterText.setName("lblInstancesFilterText"); // NOI18N

        jScrollPane3.setName("jScrollPane3"); // NOI18N

        tblInstances.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        tblInstances.setName("tblInstances"); // NOI18N
        jScrollPane3.setViewportView(tblInstances);

        btnInstancesNext.setText(resourceMap.getString("btnInstancesNext.text")); // NOI18N
        btnInstancesNext.setName("btnInstancesNext"); // NOI18N
        btnInstancesNext.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnInstancesNextActionPerformed(evt);
            }
        });

        btnInstancesPrevious.setText(resourceMap.getString("btnInstancesPrevious.text")); // NOI18N
        btnInstancesPrevious.setName("btnInstancesPrevious"); // NOI18N
        btnInstancesPrevious.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnInstancesPreviousActionPerformed(evt);
            }
        });

        btnInstancesFilter.setText(resourceMap.getString("btnInstancesFilter.text")); // NOI18N
        btnInstancesFilter.setName("btnInstancesFilter"); // NOI18N

        javax.swing.GroupLayout pnlInstancesLayout = new javax.swing.GroupLayout(pnlInstances);
        pnlInstances.setLayout(pnlInstancesLayout);
        pnlInstancesLayout.setHorizontalGroup(
            pnlInstancesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlInstancesLayout.createSequentialGroup()
                .addComponent(lblInstancesFilterText)
                .addContainerGap(674, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlInstancesLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnInstancesFilter)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 429, Short.MAX_VALUE)
                .addComponent(btnInstancesPrevious)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnInstancesNext)
                .addContainerGap())
            .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 674, Short.MAX_VALUE)
        );

        pnlInstancesLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {btnInstancesFilter, btnInstancesNext, btnInstancesPrevious});

        pnlInstancesLayout.setVerticalGroup(
            pnlInstancesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlInstancesLayout.createSequentialGroup()
                .addComponent(lblInstancesFilterText)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 455, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlInstancesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnInstancesNext)
                    .addComponent(btnInstancesPrevious)
                    .addComponent(btnInstancesFilter))
                .addContainerGap())
        );

        tabbedPane.addTab(resourceMap.getString("pnlInstances.TabConstraints.tabTitle"), pnlInstances); // NOI18N

        pnlVerifiers.setName("pnlVerifiers"); // NOI18N

        jScrollPane4.setName("jScrollPane4"); // NOI18N

        tblVerifiers.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        tblVerifiers.setName("tblVerifiers"); // NOI18N
        jScrollPane4.setViewportView(tblVerifiers);

        jButton1.setText(resourceMap.getString("jButton1.text")); // NOI18N
        jButton1.setName("jButton1"); // NOI18N
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setText(resourceMap.getString("jButton2.text")); // NOI18N
        jButton2.setName("jButton2"); // NOI18N
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnlVerifiersLayout = new javax.swing.GroupLayout(pnlVerifiers);
        pnlVerifiers.setLayout(pnlVerifiersLayout);
        pnlVerifiersLayout.setHorizontalGroup(
            pnlVerifiersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlVerifiersLayout.createSequentialGroup()
                .addContainerGap(512, Short.MAX_VALUE)
                .addComponent(jButton2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton1)
                .addContainerGap())
            .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 674, Short.MAX_VALUE)
        );

        pnlVerifiersLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jButton1, jButton2});

        pnlVerifiersLayout.setVerticalGroup(
            pnlVerifiersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlVerifiersLayout.createSequentialGroup()
                .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 461, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlVerifiersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton1)
                    .addComponent(jButton2))
                .addContainerGap())
        );

        tabbedPane.addTab(resourceMap.getString("pnlVerifiers.TabConstraints.tabTitle"), pnlVerifiers); // NOI18N

        pnlSummary.setName("pnlSummary"); // NOI18N

        pnlSummaryComponent.setName("pnlSummaryComponent"); // NOI18N

        javax.swing.GroupLayout pnlSummaryComponentLayout = new javax.swing.GroupLayout(pnlSummaryComponent);
        pnlSummaryComponent.setLayout(pnlSummaryComponentLayout);
        pnlSummaryComponentLayout.setHorizontalGroup(
            pnlSummaryComponentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 674, Short.MAX_VALUE)
        );
        pnlSummaryComponentLayout.setVerticalGroup(
            pnlSummaryComponentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 461, Short.MAX_VALUE)
        );

        btnFinish.setText(resourceMap.getString("btnFinish.text")); // NOI18N
        btnFinish.setName("btnFinish"); // NOI18N
        btnFinish.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFinishActionPerformed(evt);
            }
        });

        btnSummaryPrevious.setText(resourceMap.getString("btnSummaryPrevious.text")); // NOI18N
        btnSummaryPrevious.setName("btnSummaryPrevious"); // NOI18N
        btnSummaryPrevious.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSummaryPreviousActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnlSummaryLayout = new javax.swing.GroupLayout(pnlSummary);
        pnlSummary.setLayout(pnlSummaryLayout);
        pnlSummaryLayout.setHorizontalGroup(
            pnlSummaryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlSummaryLayout.createSequentialGroup()
                .addContainerGap(512, Short.MAX_VALUE)
                .addComponent(btnSummaryPrevious)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnFinish)
                .addContainerGap())
            .addComponent(pnlSummaryComponent, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pnlSummaryLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {btnFinish, btnSummaryPrevious});

        pnlSummaryLayout.setVerticalGroup(
            pnlSummaryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlSummaryLayout.createSequentialGroup()
                .addComponent(pnlSummaryComponent, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlSummaryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnFinish)
                    .addComponent(btnSummaryPrevious))
                .addContainerGap())
        );

        tabbedPane.addTab(resourceMap.getString("pnlSummary.TabConstraints.tabTitle"), pnlSummary); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(tabbedPane, javax.swing.GroupLayout.DEFAULT_SIZE, 679, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(tabbedPane, javax.swing.GroupLayout.DEFAULT_SIZE, 529, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btnExperimentNextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExperimentNextActionPerformed
        tabbedPane.setSelectedIndex(1);
    }//GEN-LAST:event_btnExperimentNextActionPerformed
    
    private void btnSolversPreviousActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSolversPreviousActionPerformed
        tabbedPane.setSelectedIndex(0);
    }//GEN-LAST:event_btnSolversPreviousActionPerformed
    
    private void btnSolversNextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSolversNextActionPerformed
        tabbedPane.setSelectedIndex(2);
    }//GEN-LAST:event_btnSolversNextActionPerformed
    
    private void validateSolverSelection() throws Exception {
        List<Solver> depSolvers = controller.getDependentSolvers(expTableModel.getSelectedExperiments());
        
        solTableModel.clearFixedSolvers();
        for (Solver s : depSolvers) {
            solTableModel.setSolverFixed(s.getId(), true);
        }
    }
    
    private void validateInstanceSelection() throws Exception {
        List<Instance> depInstances = controller.getDependentInstances(expTableModel.getSelectedExperiments());
        
        insTableModel.clearFixedInstances();
        for (Instance s : depInstances) {
            insTableModel.setInstanceFixed(s.getId(), true);
        }
    }
    
    private void validateVerifierSelection() throws Exception {
        List<Verifier> depVerifiers = controller.getDependentVerifiers(expTableModel.getSelectedExperiments());
        
        verifierTableModel.clearFixedVerifiers();
        for (Verifier v : depVerifiers) {
            verifierTableModel.setVerifierFixed(v.getId(), true);
        }
    }
    
    private void handleException(final Throwable ex) {
        SwingUtilities.invokeLater(new Runnable() {
            
            @Override
            public void run() {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(EDACCApp.getApplication().getMainFrame(), "Error while loading data: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
    
    private void tabbedPaneStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_tabbedPaneStateChanged
        Tasks.startTask(new TaskRunnable() {
            
            @Override
            public void run(Tasks task) {
                if (tabbedPane.getSelectedIndex() == 0) {
                    // experiments tab
                    SwingUtilities.invokeLater(new Runnable() {
                        
                        @Override
                        public void run() {
                            edacc.experiment.Util.updateTableColumnWidth(tblExperiments, 1000);
                        }
                    });
                    
                } else if (tabbedPane.getSelectedIndex() == 1) {
                    // solvers tab
                    SwingUtilities.invokeLater(new Runnable() {
                        
                        @Override
                        public void run() {
                            edacc.experiment.Util.updateTableColumnWidth(tblSolvers, 1000);
                        }
                    });
                    try {
                        validateSolverSelection();
                    } catch (Exception ex) {
                        handleException(ex);
                        return;
                    }
                } else if (tabbedPane.getSelectedIndex() == 2) {
                    // instances tab
                    SwingUtilities.invokeLater(new Runnable() {
                        
                        @Override
                        public void run() {
                            edacc.experiment.Util.updateTableColumnWidth(tblInstances, 1000);
                        }
                    });
                    try {
                        validateInstanceSelection();
                    } catch (Exception ex) {
                        handleException(ex);
                        return;
                    }
                } else if (tabbedPane.getSelectedIndex() == 3) {
                    // verifiers tab
                    SwingUtilities.invokeLater(new Runnable() {
                        
                        @Override
                        public void run() {
                            edacc.experiment.Util.updateTableColumnWidth(tblVerifiers, 1000);
                        }
                    });
                    try {
                        validateVerifierSelection();
                    } catch (Exception ex) {
                        handleException(ex);
                        return;
                    }
                } else if (tabbedPane.getSelectedIndex() == 4) {
                    // summary tab
                    try {
                        validateSolverSelection();
                        validateInstanceSelection();
                        validateVerifierSelection();
                    } catch (Exception ex) {
                        handleException(ex);
                        SwingUtilities.invokeLater(new Runnable() {
                            
                            @Override
                            public void run() {
                                tabbedPane.setSelectedIndex(0);
                            }
                        });
                        return;
                    }
                    SwingUtilities.invokeLater(new Runnable() {
                        
                        @Override
                        public void run() {
                            if (panelSummary instanceof ImportSummaryPanel) {
                                final ImportSummaryPanel panel = (ImportSummaryPanel) panelSummary;
                                panel.setExperimentCount(expTableModel.getSelectedExperiments().size());
                                panel.setSolvers(solTableModel.getSelectedSolvers());
                                panel.setInstanceCount(insTableModel.getSelectedInstances().size());
                                panel.setVerifiers(verifierTableModel.getSelectedVerifiers());
                            } else if (panelSummary instanceof ExportSummaryPanel) {
                                ExportSummaryPanel panel = (ExportSummaryPanel) panelSummary;
                                panel.setExperimentCount(expTableModel.getSelectedExperiments().size());
                                panel.setSolverCount(solTableModel.getSelectedSolvers().size());
                                panel.setInstanceCount(insTableModel.getSelectedInstances().size());
                                panel.setVerifierCount(verifierTableModel.getSelectedVerifiers().size());
                            }
                        }
                    });
                }
            }
        });
        
    }//GEN-LAST:event_tabbedPaneStateChanged
    
    private void btnInstancesNextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnInstancesNextActionPerformed
        tabbedPane.setSelectedIndex(3);
    }//GEN-LAST:event_btnInstancesNextActionPerformed
    
    private void btnInstancesPreviousActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnInstancesPreviousActionPerformed
        tabbedPane.setSelectedIndex(1);
    }//GEN-LAST:event_btnInstancesPreviousActionPerformed
    
    private void btnSummaryPreviousActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSummaryPreviousActionPerformed
        tabbedPane.setSelectedIndex(2);
    }//GEN-LAST:event_btnSummaryPreviousActionPerformed
    
    private void btnFinishActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFinishActionPerformed
        if (controller instanceof ExportController) {
            final ExportController c = (ExportController) controller;
            
            String filename = ((ExportSummaryPanel) panelSummary).getExportFilename();
            if (filename.equals("")) {
                JOptionPane.showMessageDialog(EDACCApp.getApplication().getMainFrame(), "Filename cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (!filename.endsWith(".edacc")) {
                filename += ".edacc";
            }
            final File file = new File(filename);
            if (file.exists()) {
                int input = JOptionPane.showConfirmDialog(EDACCApp.getApplication().getMainFrame(), "File " + file.getName() + " exists. Overwrite?", "File exists", JOptionPane.YES_NO_OPTION);
                if (input != JOptionPane.YES_OPTION) {
                    return;
                }
            }
            Tasks.startTask(new TaskRunnable() {
                
                @Override
                public void run(Tasks task) {
                    try {
                        c.export(task, file, expTableModel.getSelectedExperiments(), solTableModel.getSelectedSolvers(), insTableModel.getSelectedInstances(), verifierTableModel.getSelectedVerifiers());
                        SwingUtilities.invokeLater(new Runnable() {
                            
                            @Override
                            public void run() {
                                JOptionPane.showMessageDialog(EDACCApp.getApplication().getMainFrame(), "Export was successful!", "Information", JOptionPane.INFORMATION_MESSAGE);
                            }
                        });
                        
                    } catch (Exception ex) {
                        handleException(ex);
                    }
                }
            });
        } else if (controller instanceof ImportController) {
            final ImportController c = (ImportController) controller;
            ImportSummaryPanel panel = (ImportSummaryPanel) panelSummary;
            if (!panel.validateInput()) {
                JOptionPane.showMessageDialog(EDACCApp.getApplication().getMainFrame(), "Invalid input.", "Invalid input", JOptionPane.ERROR_MESSAGE);
                return;
            }
            final HashMap<Integer, Solver> solverMap = panel.getSolverMap();
            final HashMap<Integer, String> nameMap = panel.getNameMap();
            
            final HashMap<Integer, Verifier> verifierMap = panel.getVerifierMap();
            final HashMap<Integer, String> verifierNameMap = panel.getVerifierNameMap();
            
            Tasks.startTask(new TaskRunnable() {
                
                @Override
                public void run(Tasks task) {
                    try {
                        c.importData(task, expTableModel.getSelectedExperiments(), solTableModel.getSelectedSolvers(), insTableModel.getSelectedInstances(), verifierTableModel.getSelectedVerifiers(), solverMap, nameMap, verifierMap, verifierNameMap);
                        SwingUtilities.invokeLater(new Runnable() {
                            
                            @Override
                            public void run() {
                                JOptionPane.showMessageDialog(EDACCApp.getApplication().getMainFrame(), "Import was successful!", "Information", JOptionPane.INFORMATION_MESSAGE);
                            }
                        });
                    } catch (Exception ex) {
                        handleException(ex);
                    }
                }
            });
            
        }
    }//GEN-LAST:event_btnFinishActionPerformed
    
    private void btnFilterExperimentsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFilterExperimentsActionPerformed
        EDACCApp.getApplication().show(experimentFilter);
        expTableModel.fireTableDataChanged();
        updateExperimentFilterStatus();
    }//GEN-LAST:event_btnFilterExperimentsActionPerformed
    
    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        tabbedPane.setSelectedIndex(4);
    }//GEN-LAST:event_jButton1ActionPerformed
    
    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        tabbedPane.setSelectedIndex(2);
    }//GEN-LAST:event_jButton2ActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnExperimentNext;
    private javax.swing.JButton btnFilterExperiments;
    private javax.swing.JButton btnFilterSolvers;
    private javax.swing.JButton btnFinish;
    private javax.swing.JButton btnInstancesFilter;
    private javax.swing.JButton btnInstancesNext;
    private javax.swing.JButton btnInstancesPrevious;
    private javax.swing.JButton btnSolversNext;
    private javax.swing.JButton btnSolversPrevious;
    private javax.swing.JButton btnSummaryPrevious;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JLabel lblExperimentFilterStatus;
    private javax.swing.JLabel lblFilterSolverText;
    private javax.swing.JLabel lblInstancesFilterText;
    private javax.swing.JPanel pnlExperiments;
    private javax.swing.JPanel pnlInstances;
    private javax.swing.JPanel pnlSolvers;
    private javax.swing.JPanel pnlSummary;
    private javax.swing.JPanel pnlSummaryComponent;
    private javax.swing.JPanel pnlVerifiers;
    private javax.swing.JTabbedPane tabbedPane;
    private javax.swing.JTable tblExperiments;
    private javax.swing.JTable tblInstances;
    private javax.swing.JTable tblSolvers;
    private javax.swing.JTable tblVerifiers;
    // End of variables declaration//GEN-END:variables

    public static File getImportFile() {
        final JFileChooser fc = new JFileChooser();
        
        fc.setFileFilter(new EDACCFileFilter());
        fc.showOpenDialog(EDACCApp.getApplication().getMainFrame());
        
        return fc.getSelectedFile();
    }
    
    public static File getExportFile() {
        final JFileChooser fc = new JFileChooser();
        
        fc.setFileFilter(new EDACCFileFilter());
        fc.showSaveDialog(EDACCApp.getApplication().getMainFrame());
        
        if (fc.getSelectedFile() == null) {
            return null;
        }
        
        String filename = fc.getSelectedFile().getAbsolutePath();
        if (!filename.endsWith(".edacc")) {
            filename += ".edacc";
        }
        
        return new File(filename);
    }
    
    private void updateExperimentFilterStatus() {
        lblExperimentFilterStatus.setForeground(Color.red);
        String status = "";
        if (experimentFilter.hasFiltersApplied()) {
            status += "This list of experiments has filters applied to it. Use the filter button below to modify. Showing " + tblExperiments.getRowCount() + " experiments.";
        }
        lblExperimentFilterStatus.setText(status);
    }
    
    private class BooleanRenderer extends DefaultTableCellRenderer {
        
        private JCheckBox checkbox;
        
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            
            if (value instanceof Boolean) {
                if (checkbox == null) {
                    checkbox = new JCheckBox();
                    checkbox.setHorizontalAlignment(JCheckBox.CENTER);
                }
                checkbox.setBackground(c.getBackground());
                checkbox.setForeground(c.getForeground());
                checkbox.setSelected((Boolean) value);
                boolean fixed = false;
                
                if (table.getModel() instanceof SolverFixedSelectionTableModel) {
                    if (((SolverFixedSelectionTableModel) table.getModel()).isFixed(table.convertRowIndexToModel(row))) {
                        fixed = true;
                    }
                }
                if (table.getModel() instanceof InstanceFixedSelectionTableModel) {
                    if (((InstanceFixedSelectionTableModel) table.getModel()).isFixed(table.convertRowIndexToModel(row))) {
                        fixed = true;
                    }
                }
                if (table.getModel() instanceof VerifierFixedSelectionTableModel) {
                    if (((VerifierFixedSelectionTableModel) table.getModel()).isFixed(table.convertRowIndexToModel(row))) {
                        fixed = true;
                    }
                }
                checkbox.setEnabled(!fixed);
                checkbox.repaint();
                c = checkbox;
            }
            return c;
        }
    }
    
    private static class EDACCFileFilter extends FileFilter {
        
        @Override
        public boolean accept(File f) {
            if (f.isDirectory()) {
                return true;
            }
            if (f.getAbsolutePath().endsWith(".edacc")) {
                return true;
            }
            return false;
        }
        
        @Override
        public String getDescription() {
            return "EDACC Exported Files [.edacc]";
        }
    }
}
