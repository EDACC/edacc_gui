/*
 * EDACCExperimentModeNewSolver.java
 *
 * Created on 14.06.2010, 11:47:44
 */
package edacc;

import edacc.experiment.Util;
import edacc.experiment.VerifierParameterTableModel;
import edacc.model.Experiment;
import edacc.model.Verifier;
import edacc.model.VerifierConfiguration;
import edacc.model.VerifierParameter;
import edacc.model.VerifierParameterInstance;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;

/**
 *
 * @author balint
 */
public class EDACCExperimentModeNewExp extends javax.swing.JDialog {
    
    public String expName;
    public String expDesc;
    public Integer solverOutputPreserveFirst, solverOutputPreserveLast;
    public Integer watcherOutputPreserveFirst, watcherOutputPreserveLast;
    public Integer verifierOutputPreserveFirst, verifierOutputPreserveLast;
    public Experiment.Cost defaultCost;
    public VerifierConfiguration verifierConfig;
    public boolean isConfigurationExp;
    public boolean canceled;
    private VerifierParameterTableModel verifierParameterTableModel;

    /** Creates new form EDACCExperimentModeNewExp */
    public EDACCExperimentModeNewExp(java.awt.Frame parent, boolean modal, List<Verifier> verifiers) {
        super(parent, modal);
        initComponents();
        verifierParameterTableModel = new VerifierParameterTableModel();
        tblVerifierParameters.setModel(verifierParameterTableModel);
        
        tblVerifierParameters.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
        tblVerifierParameters.setRowSelectionAllowed(false);
        tblVerifierParameters.setCellSelectionEnabled(false);
        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer() {
            
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                JLabel lbl = (JLabel) comp;
                lbl.setEnabled((Boolean) verifierParameterTableModel.getValueAt(row, 0));
                return comp;
            }
        };
        
        DefaultTableCellRenderer booleanRenderer = new DefaultTableCellRenderer() {
            
            protected JCheckBox checkBox;
            
            {
                checkBox = new JCheckBox();
                checkBox.setHorizontalAlignment(SwingConstants.CENTER);
                checkBox.setBackground(Color.white);
            }
            
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                checkBox.setBackground(comp.getBackground());
                checkBox.setEnabled(!verifierParameterTableModel.getParameters().get(row).isMandatory());
                checkBox.setSelected((Boolean) value);
                return checkBox;
            }
        };
        
        tblVerifierParameters.setDefaultRenderer(String.class, renderer);
        tblVerifierParameters.setDefaultRenderer(Integer.class, renderer);
        tblVerifierParameters.setDefaultRenderer(Boolean.class, booleanRenderer);
        
        this.pack();
        this.txtExperimentName.requestFocus();
        this.chkLimitSolverOutputActionPerformed(null);
        this.chkLimitWatcherOutputActionPerformed(null);
        this.chkLimitVerifierOutputActionPerformed(null);
        this.getRootPane().setDefaultButton(this.btnCreateExperiment);
        comboDefaultCost.removeAllItems();
        for (Experiment.Cost cost : Experiment.Cost.values()) {
            comboDefaultCost.addItem(cost);
        }
        comboVerifierBinary.removeAllItems();
        comboVerifierBinary.addItem((Verifier) null);
        for (Verifier v : verifiers) {
            comboVerifierBinary.addItem(v);
        }
    }
    
    public EDACCExperimentModeNewExp(java.awt.Frame parent, boolean modal, List<Verifier> verifiers, String expName, String expDescription, boolean configurationExp, Experiment.Cost defaultCost, Integer solverOutputPreserveFirst, Integer solverOutputPreserveLast, Integer watcherOutputPreserveFirst, Integer watcherOutputPreserveLast, Integer verifierOutputPreserveFirst, Integer verifierOutputPreserveLast, VerifierConfiguration verifierConfig) {
        this(parent, modal, verifiers);
        
        txtExperimentName.setText(expName);
        txtExperimentDescription.setText(expDescription);
        this.isConfigurationExp = configurationExp;
        chkConfigurationExp.setSelected(configurationExp);
        chkConfigurationExp.setEnabled(false);
        btnCreateExperiment.setText("Save");
        if (solverOutputPreserveFirst != null && solverOutputPreserveLast != null) {
            chkLimitSolverOutput.setSelected(true);
            if (solverOutputPreserveFirst < 0 || solverOutputPreserveLast < 0) {
                solverOutputPreserveFirst = -solverOutputPreserveFirst;
                solverOutputPreserveLast = -solverOutputPreserveLast;
                comboSolverOutputUnit.setSelectedItem("lines");
            } else {
                comboSolverOutputUnit.setSelectedItem("bytes");
            }
            txtSolverOutputPreserveFirst.setText("" + solverOutputPreserveFirst);
            txtSolverOutputPreserveLast.setText("" + solverOutputPreserveLast);
        }
        if (watcherOutputPreserveLast != null && watcherOutputPreserveLast != null) {
            chkLimitWatcherOutput.setSelected(true);
            if (watcherOutputPreserveFirst < 0 || watcherOutputPreserveLast < 0) {
                watcherOutputPreserveFirst = -watcherOutputPreserveFirst;
                watcherOutputPreserveLast = -watcherOutputPreserveLast;
                comboWatcherOutputUnit.setSelectedItem("lines");
            } else {
                comboWatcherOutputUnit.setSelectedItem("bytes");
            }
            txtWatcherOutputPreserveFirst.setText("" + watcherOutputPreserveFirst);
            txtWatcherOutputPreserveLast.setText("" + watcherOutputPreserveLast);
        }
        if (verifierOutputPreserveFirst != null && verifierOutputPreserveLast != null) {
            chkLimitVerifierOutput.setSelected(true);
            if (verifierOutputPreserveFirst < 0 || verifierOutputPreserveLast < 0) {
                verifierOutputPreserveFirst = -verifierOutputPreserveFirst;
                verifierOutputPreserveLast = -verifierOutputPreserveLast;
                comboVerifierOutputUnit.setSelectedItem("lines");
            } else {
                comboVerifierOutputUnit.setSelectedItem("bytes");
            }
            txtVerifierOutputPreserveFirst.setText("" + verifierOutputPreserveFirst);
            txtVerifierOutputPreserveLast.setText("" + verifierOutputPreserveLast);
        }
        this.chkLimitSolverOutputActionPerformed(null);
        this.chkLimitWatcherOutputActionPerformed(null);
        this.chkLimitVerifierOutputActionPerformed(null);
        
        comboDefaultCost.setSelectedItem(defaultCost);
        
        this.verifierConfig = verifierConfig;
        if (verifierConfig != null) {
            comboVerifierBinary.setSelectedItem(verifierConfig.getVerifier());
            verifierParameterTableModel.assignParameterInstances(verifierConfig.getParameterInstances());
        }
        
        this.pack();
        this.setTitle("Edit experiment");
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lblExperimentName = new javax.swing.JLabel();
        txtExperimentName = new javax.swing.JTextField();
        lblExperimentDescription = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        txtExperimentDescription = new javax.swing.JTextArea();
        btnCreateExperiment = new javax.swing.JButton();
        btnCancel = new javax.swing.JButton();
        chkConfigurationExp = new javax.swing.JCheckBox();
        jLabel1 = new javax.swing.JLabel();
        chkLimitVerifierOutput = new javax.swing.JCheckBox();
        txtVerifierOutputPreserveLast = new javax.swing.JTextField();
        txtVerifierOutputPreserveFirst = new javax.swing.JTextField();
        lblVerifierOutputPreserveLast = new javax.swing.JLabel();
        lblVerifierOutputPreserveFirst = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        lblWatcherOutputPreserveLast = new javax.swing.JLabel();
        txtWatcherOutputPreserveLast = new javax.swing.JTextField();
        txtWatcherOutputPreserveFirst = new javax.swing.JTextField();
        lblWatcherOutputPreserveFirst = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        chkLimitWatcherOutput = new javax.swing.JCheckBox();
        lblSolverOutputPreserveLast = new javax.swing.JLabel();
        txtSolverOutputPreserveLast = new javax.swing.JTextField();
        lblSolverOutputPreserveFirst = new javax.swing.JLabel();
        txtSolverOutputPreserveFirst = new javax.swing.JTextField();
        chkLimitSolverOutput = new javax.swing.JCheckBox();
        jLabel10 = new javax.swing.JLabel();
        lblLimitSolverOutputIn = new javax.swing.JLabel();
        comboSolverOutputUnit = new javax.swing.JComboBox();
        lblLimitWatcherOutputIn = new javax.swing.JLabel();
        comboWatcherOutputUnit = new javax.swing.JComboBox();
        lblLimitVerifierOutputIn = new javax.swing.JLabel();
        comboVerifierOutputUnit = new javax.swing.JComboBox();
        jLabel2 = new javax.swing.JLabel();
        comboDefaultCost = new javax.swing.JComboBox();
        jPanel1 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        comboVerifierBinary = new javax.swing.JComboBox();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblVerifierParameters = new javax.swing.JTable();
        jLabel3 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(edacc.EDACCApp.class).getContext().getResourceMap(EDACCExperimentModeNewExp.class);
        setTitle(resourceMap.getString("Form.title")); // NOI18N
        setMinimumSize(new java.awt.Dimension(400, 250));
        setName("Form"); // NOI18N
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowActivated(java.awt.event.WindowEvent evt) {
                formWindowActivated(evt);
            }
            public void windowClosed(java.awt.event.WindowEvent evt) {
                formWindowClosed(evt);
            }
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        lblExperimentName.setText(resourceMap.getString("lblExperimentName.text")); // NOI18N
        lblExperimentName.setName("lblExperimentName"); // NOI18N

        txtExperimentName.setToolTipText(resourceMap.getString("txtExperimentName.toolTipText")); // NOI18N
        txtExperimentName.setName("txtExperimentName"); // NOI18N

        lblExperimentDescription.setText(resourceMap.getString("lblExperimentDescription.text")); // NOI18N
        lblExperimentDescription.setName("lblExperimentDescription"); // NOI18N

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        txtExperimentDescription.setColumns(20);
        txtExperimentDescription.setRows(5);
        txtExperimentDescription.setToolTipText(resourceMap.getString("txtExperimentDescription.toolTipText")); // NOI18N
        txtExperimentDescription.setName("txtExperimentDescription"); // NOI18N
        jScrollPane1.setViewportView(txtExperimentDescription);

        btnCreateExperiment.setText(resourceMap.getString("btnCreateExperiment.text")); // NOI18N
        btnCreateExperiment.setToolTipText(resourceMap.getString("btnCreateExperiment.toolTipText")); // NOI18N
        btnCreateExperiment.setName("btnCreateExperiment"); // NOI18N
        btnCreateExperiment.setPreferredSize(new java.awt.Dimension(80, 25));
        btnCreateExperiment.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCreateExperimentActionPerformed(evt);
            }
        });
        btnCreateExperiment.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                btnCreateExperimentKeyPressed(evt);
            }
        });

        btnCancel.setText(resourceMap.getString("btnCancel.text")); // NOI18N
        btnCancel.setToolTipText(resourceMap.getString("btnCancel.toolTipText")); // NOI18N
        btnCancel.setName("btnCancel"); // NOI18N
        btnCancel.setPreferredSize(new java.awt.Dimension(80, 25));
        btnCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelActionPerformed(evt);
            }
        });
        btnCancel.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                btnCancelKeyPressed(evt);
            }
        });

        chkConfigurationExp.setText(resourceMap.getString("chkConfigurationExp.text")); // NOI18N
        chkConfigurationExp.setName("chkConfigurationExp"); // NOI18N

        jLabel1.setText(resourceMap.getString("jLabel1.text")); // NOI18N
        jLabel1.setName("jLabel1"); // NOI18N

        chkLimitVerifierOutput.setText(resourceMap.getString("chkLimitVerifierOutput.text")); // NOI18N
        chkLimitVerifierOutput.setName("chkLimitVerifierOutput"); // NOI18N
        chkLimitVerifierOutput.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkLimitVerifierOutputActionPerformed(evt);
            }
        });

        txtVerifierOutputPreserveLast.setText(resourceMap.getString("txtVerifierOutputPreserveLast.text")); // NOI18N
        txtVerifierOutputPreserveLast.setName("txtVerifierOutputPreserveLast"); // NOI18N

        txtVerifierOutputPreserveFirst.setText(resourceMap.getString("txtVerifierOutputPreserveFirst.text")); // NOI18N
        txtVerifierOutputPreserveFirst.setName("txtVerifierOutputPreserveFirst"); // NOI18N

        lblVerifierOutputPreserveLast.setText(resourceMap.getString("lblVerifierOutputPreserveLast.text")); // NOI18N
        lblVerifierOutputPreserveLast.setName("lblVerifierOutputPreserveLast"); // NOI18N

        lblVerifierOutputPreserveFirst.setText(resourceMap.getString("lblVerifierOutputPreserveFirst.text")); // NOI18N
        lblVerifierOutputPreserveFirst.setName("lblVerifierOutputPreserveFirst"); // NOI18N

        jLabel4.setText(resourceMap.getString("jLabel4.text")); // NOI18N
        jLabel4.setName("jLabel4"); // NOI18N

        lblWatcherOutputPreserveLast.setText(resourceMap.getString("lblWatcherOutputPreserveLast.text")); // NOI18N
        lblWatcherOutputPreserveLast.setName("lblWatcherOutputPreserveLast"); // NOI18N

        txtWatcherOutputPreserveLast.setText(resourceMap.getString("txtWatcherOutputPreserveLast.text")); // NOI18N
        txtWatcherOutputPreserveLast.setName("txtWatcherOutputPreserveLast"); // NOI18N

        txtWatcherOutputPreserveFirst.setText(resourceMap.getString("txtWatcherOutputPreserveFirst.text")); // NOI18N
        txtWatcherOutputPreserveFirst.setName("txtWatcherOutputPreserveFirst"); // NOI18N

        lblWatcherOutputPreserveFirst.setText(resourceMap.getString("lblWatcherOutputPreserveFirst.text")); // NOI18N
        lblWatcherOutputPreserveFirst.setName("lblWatcherOutputPreserveFirst"); // NOI18N

        jLabel7.setText(resourceMap.getString("jLabel7.text")); // NOI18N
        jLabel7.setName("jLabel7"); // NOI18N

        chkLimitWatcherOutput.setText(resourceMap.getString("chkLimitWatcherOutput.text")); // NOI18N
        chkLimitWatcherOutput.setName("chkLimitWatcherOutput"); // NOI18N
        chkLimitWatcherOutput.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkLimitWatcherOutputActionPerformed(evt);
            }
        });

        lblSolverOutputPreserveLast.setText(resourceMap.getString("lblSolverOutputPreserveLast.text")); // NOI18N
        lblSolverOutputPreserveLast.setName("lblSolverOutputPreserveLast"); // NOI18N

        txtSolverOutputPreserveLast.setText(resourceMap.getString("txtSolverOutputPreserveLast.text")); // NOI18N
        txtSolverOutputPreserveLast.setName("txtSolverOutputPreserveLast"); // NOI18N

        lblSolverOutputPreserveFirst.setText(resourceMap.getString("lblSolverOutputPreserveFirst.text")); // NOI18N
        lblSolverOutputPreserveFirst.setName("lblSolverOutputPreserveFirst"); // NOI18N

        txtSolverOutputPreserveFirst.setText(resourceMap.getString("txtSolverOutputPreserveFirst.text")); // NOI18N
        txtSolverOutputPreserveFirst.setName("txtSolverOutputPreserveFirst"); // NOI18N

        chkLimitSolverOutput.setText(resourceMap.getString("chkLimitSolverOutput.text")); // NOI18N
        chkLimitSolverOutput.setName("chkLimitSolverOutput"); // NOI18N
        chkLimitSolverOutput.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkLimitSolverOutputActionPerformed(evt);
            }
        });

        jLabel10.setText(resourceMap.getString("jLabel10.text")); // NOI18N
        jLabel10.setName("jLabel10"); // NOI18N

        lblLimitSolverOutputIn.setText(resourceMap.getString("lblLimitSolverOutputIn.text")); // NOI18N
        lblLimitSolverOutputIn.setName("lblLimitSolverOutputIn"); // NOI18N

        comboSolverOutputUnit.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "lines", "bytes" }));
        comboSolverOutputUnit.setName("comboSolverOutputUnit"); // NOI18N

        lblLimitWatcherOutputIn.setText(resourceMap.getString("lblLimitWatcherOutputIn.text")); // NOI18N
        lblLimitWatcherOutputIn.setName("lblLimitWatcherOutputIn"); // NOI18N

        comboWatcherOutputUnit.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "lines", "bytes" }));
        comboWatcherOutputUnit.setName("comboWatcherOutputUnit"); // NOI18N

        lblLimitVerifierOutputIn.setText(resourceMap.getString("lblLimitVerifierOutputIn.text")); // NOI18N
        lblLimitVerifierOutputIn.setName("lblLimitVerifierOutputIn"); // NOI18N

        comboVerifierOutputUnit.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "lines", "bytes" }));
        comboVerifierOutputUnit.setName("comboVerifierOutputUnit"); // NOI18N

        jLabel2.setText(resourceMap.getString("jLabel2.text")); // NOI18N
        jLabel2.setName("jLabel2"); // NOI18N

        comboDefaultCost.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        comboDefaultCost.setName("comboDefaultCost"); // NOI18N

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        jPanel1.setName("jPanel1"); // NOI18N

        jLabel5.setText(resourceMap.getString("jLabel5.text")); // NOI18N
        jLabel5.setName("jLabel5"); // NOI18N

        comboVerifierBinary.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        comboVerifierBinary.setName("comboVerifierBinary"); // NOI18N
        comboVerifierBinary.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comboVerifierBinaryActionPerformed(evt);
            }
        });

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(resourceMap.getString("jPanel2.border.title"))); // NOI18N
        jPanel2.setName("jPanel2"); // NOI18N

        jScrollPane2.setName("jScrollPane2"); // NOI18N

        tblVerifierParameters.setModel(new javax.swing.table.DefaultTableModel(
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
        tblVerifierParameters.setName("tblVerifierParameters"); // NOI18N
        jScrollPane2.setViewportView(tblVerifierParameters);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 539, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 140, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(comboVerifierBinary, 0, 507, Short.MAX_VALUE))
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(comboVerifierBinary, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel3.setText(resourceMap.getString("jLabel3.text")); // NOI18N
        jLabel3.setName("jLabel3"); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(btnCancel, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnCreateExperiment, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(lblWatcherOutputPreserveLast)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addComponent(jLabel4)
                            .addComponent(lblWatcherOutputPreserveFirst)
                            .addComponent(jLabel7)
                            .addComponent(lblSolverOutputPreserveLast)
                            .addComponent(lblSolverOutputPreserveFirst)
                            .addComponent(jLabel10)
                            .addComponent(lblExperimentName)
                            .addComponent(lblExperimentDescription)
                            .addComponent(lblVerifierOutputPreserveFirst)
                            .addComponent(lblVerifierOutputPreserveLast)
                            .addComponent(jLabel2)
                            .addComponent(jLabel3))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 563, Short.MAX_VALUE)
                            .addComponent(comboDefaultCost, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                .addComponent(chkLimitWatcherOutput)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(lblLimitWatcherOutputIn)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(comboWatcherOutputUnit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                .addComponent(chkLimitVerifierOutput)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(lblLimitVerifierOutputIn)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(comboVerifierOutputUnit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                .addComponent(chkConfigurationExp)
                                .addGap(38, 38, 38))
                            .addComponent(txtWatcherOutputPreserveLast, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 563, Short.MAX_VALUE)
                            .addComponent(txtWatcherOutputPreserveFirst, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 563, Short.MAX_VALUE)
                            .addComponent(txtSolverOutputPreserveLast, javax.swing.GroupLayout.DEFAULT_SIZE, 563, Short.MAX_VALUE)
                            .addComponent(txtSolverOutputPreserveFirst, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 563, Short.MAX_VALUE)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                .addComponent(chkLimitSolverOutput)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(lblLimitSolverOutputIn)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(comboSolverOutputUnit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(txtExperimentName, javax.swing.GroupLayout.DEFAULT_SIZE, 563, Short.MAX_VALUE)
                            .addComponent(txtVerifierOutputPreserveFirst, javax.swing.GroupLayout.DEFAULT_SIZE, 563, Short.MAX_VALUE)
                            .addComponent(txtVerifierOutputPreserveLast, javax.swing.GroupLayout.DEFAULT_SIZE, 563, Short.MAX_VALUE)
                            .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {btnCancel, btnCreateExperiment});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblExperimentName)
                    .addComponent(txtExperimentName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblExperimentDescription)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(comboDefaultCost, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(chkLimitSolverOutput)
                        .addComponent(jLabel10))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(lblLimitSolverOutputIn)
                        .addComponent(comboSolverOutputUnit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblSolverOutputPreserveFirst)
                    .addComponent(txtSolverOutputPreserveFirst, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtSolverOutputPreserveLast, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblSolverOutputPreserveLast))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel7)
                        .addComponent(chkLimitWatcherOutput))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(lblLimitWatcherOutputIn)
                        .addComponent(comboWatcherOutputUnit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtWatcherOutputPreserveFirst, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblWatcherOutputPreserveFirst))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblWatcherOutputPreserveLast)
                    .addComponent(txtWatcherOutputPreserveLast, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel4)
                        .addComponent(chkLimitVerifierOutput))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(lblLimitVerifierOutputIn)
                        .addComponent(comboVerifierOutputUnit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtVerifierOutputPreserveFirst, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblVerifierOutputPreserveFirst))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtVerifierOutputPreserveLast, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblVerifierOutputPreserveLast))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel1)
                    .addComponent(chkConfigurationExp))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnCreateExperiment, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnCancel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnCreateExperimentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCreateExperimentActionPerformed
        solverOutputPreserveFirst = null;
        solverOutputPreserveLast = null;
        watcherOutputPreserveFirst = null;
        watcherOutputPreserveLast = null;
        verifierOutputPreserveFirst = null;
        verifierOutputPreserveLast = null;
        
        this.expName = this.txtExperimentName.getText();
        this.expDesc = this.txtExperimentDescription.getText();
        boolean limitCheck = true;
        try {
            if (chkLimitSolverOutput.isSelected()) {
                limitCheck &= Util.verifyNumber_geq(txtSolverOutputPreserveFirst, 0);
                solverOutputPreserveFirst = Integer.parseInt(txtSolverOutputPreserveFirst.getText());
                limitCheck &= Util.verifyNumber_geq(txtSolverOutputPreserveLast, 0);
                solverOutputPreserveLast = Integer.parseInt(txtSolverOutputPreserveLast.getText());
                if ("lines".equals(comboSolverOutputUnit.getSelectedItem())) {
                    solverOutputPreserveFirst = -solverOutputPreserveFirst;
                    solverOutputPreserveLast = -solverOutputPreserveLast;
                }
            }
            if (chkLimitWatcherOutput.isSelected()) {
                limitCheck &= Util.verifyNumber_geq(txtWatcherOutputPreserveFirst, 0);
                watcherOutputPreserveFirst = Integer.parseInt(txtWatcherOutputPreserveFirst.getText());
                limitCheck &= Util.verifyNumber_geq(txtWatcherOutputPreserveLast, 0);
                watcherOutputPreserveLast = Integer.parseInt(txtWatcherOutputPreserveLast.getText());
                if ("lines".equals(comboWatcherOutputUnit.getSelectedItem())) {
                    watcherOutputPreserveFirst = -watcherOutputPreserveFirst;
                    watcherOutputPreserveLast = -watcherOutputPreserveLast;
                }
            }
            if (chkLimitVerifierOutput.isSelected()) {
                limitCheck &= Util.verifyNumber_geq(txtVerifierOutputPreserveFirst, 0);
                verifierOutputPreserveFirst = Integer.parseInt(txtVerifierOutputPreserveFirst.getText());
                limitCheck &= Util.verifyNumber_geq(txtVerifierOutputPreserveLast, 0);
                verifierOutputPreserveLast = Integer.parseInt(txtVerifierOutputPreserveLast.getText());
                if ("lines".equals(comboVerifierOutputUnit.getSelectedItem())) {
                    verifierOutputPreserveFirst = -verifierOutputPreserveFirst;
                    verifierOutputPreserveLast = -verifierOutputPreserveLast;
                }
            }
        } catch (NumberFormatException ex) {
            limitCheck = false;
        }
        if (!limitCheck) {
            JOptionPane.showMessageDialog(this, "Could not verify limits.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        isConfigurationExp = chkConfigurationExp.isSelected();
        defaultCost = comboDefaultCost.getSelectedItem() instanceof Experiment.Cost ? (Experiment.Cost) comboDefaultCost.getSelectedItem() : Experiment.Cost.resultTime;
        if (verifierConfig == null) {
            Verifier verifier = (Verifier) comboVerifierBinary.getSelectedItem();
            if (verifier != null) {
                verifierConfig = new VerifierConfiguration();
                verifierConfig.setVerifier(verifier);
                verifierConfig.setParameterInstances(verifierParameterTableModel.getParameterInstances());
            }
        } else {
            Verifier verifier = (Verifier) comboVerifierBinary.getSelectedItem();
            if (verifier != null) {
                if (this.verifierConfig.getVerifier() != verifier) {
                    verifierConfig.setVerifier(verifier);
                    for (int i = verifierConfig.getParameterInstances().size() - 1; i >= 0; i--) {
                        VerifierParameterInstance pi = verifierConfig.getParameterInstances().get(i);
                        if (pi.isSaved() || pi.isModified()) {
                            pi.setDeleted();
                        } else if (pi.isNew()) {
                            verifierConfig.getParameterInstances().remove(i);
                        }
                    }
                    verifierConfig.getParameterInstances().addAll(verifierParameterTableModel.getParameterInstances());
                } else {
                    Map<Integer, VerifierParameterInstance> pis = new HashMap<Integer, VerifierParameterInstance>();
                    for (VerifierParameterInstance pi : verifierParameterTableModel.getParameterInstances()) {
                        pis.put(pi.getParameter_id(), pi);
                    }
                    for (int i = verifierConfig.getParameterInstances().size() - 1; i >= 0; i--) {
                        VerifierParameterInstance pi = verifierConfig.getParameterInstances().get(i);
                        VerifierParameterInstance modelPi = pis.get(pi.getParameter_id());
                        if (modelPi == null) {
                            if (pi.isNew()) {
                                verifierConfig.getParameterInstances().remove(i);
                            } else {
                                pi.setDeleted();
                            }
                        } else {
                            pi.assign(modelPi);
                            if (pi.isDeleted()) {
                                pi.setModified();
                            }
                            pis.remove(pi.getParameter_id());
                        }
                    }
                    verifierConfig.getParameterInstances().addAll(pis.values());
                }
            } else {
                verifierConfig.setDeleted();
            }
        }
        this.canceled = false;
        this.setVisible(false);
    }//GEN-LAST:event_btnCreateExperimentActionPerformed
    
    private void btnCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelActionPerformed
        this.canceled = true;
        this.txtExperimentDescription.setText("");
        this.txtExperimentName.setText("");
        this.setVisible(false);
}//GEN-LAST:event_btnCancelActionPerformed
    
    private void formWindowActivated(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowActivated
        this.expName = "";
        this.expDesc = "";
    }//GEN-LAST:event_formWindowActivated
    
    private void formWindowClosed(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosed
        this.canceled = true;
    }//GEN-LAST:event_formWindowClosed
    
    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        this.canceled = true;
    }//GEN-LAST:event_formWindowClosing
    
    private void btnCreateExperimentKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_btnCreateExperimentKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) //create Experiment
        {
            this.btnCreateExperimentActionPerformed(null);
        }
    }//GEN-LAST:event_btnCreateExperimentKeyPressed
    
    private void btnCancelKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_btnCancelKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) //create Experiment
        {
            this.btnCancelActionPerformed(null);
        }
    }//GEN-LAST:event_btnCancelKeyPressed
    
    private void chkLimitSolverOutputActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkLimitSolverOutputActionPerformed
        if (chkLimitSolverOutput.isSelected()) {
            txtSolverOutputPreserveFirst.setVisible(true);
            txtSolverOutputPreserveLast.setVisible(true);
            lblSolverOutputPreserveFirst.setVisible(true);
            lblSolverOutputPreserveLast.setVisible(true);
            lblLimitSolverOutputIn.setVisible(true);
            comboSolverOutputUnit.setVisible(true);
        } else {
            txtSolverOutputPreserveFirst.setVisible(false);
            txtSolverOutputPreserveLast.setVisible(false);
            lblSolverOutputPreserveFirst.setVisible(false);
            lblSolverOutputPreserveLast.setVisible(false);
            lblLimitSolverOutputIn.setVisible(false);
            comboSolverOutputUnit.setVisible(false);
        }
    }//GEN-LAST:event_chkLimitSolverOutputActionPerformed
    
    private void chkLimitWatcherOutputActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkLimitWatcherOutputActionPerformed
        if (chkLimitWatcherOutput.isSelected()) {
            txtWatcherOutputPreserveFirst.setVisible(true);
            txtWatcherOutputPreserveLast.setVisible(true);
            lblWatcherOutputPreserveFirst.setVisible(true);
            lblWatcherOutputPreserveLast.setVisible(true);
            lblLimitWatcherOutputIn.setVisible(true);
            comboWatcherOutputUnit.setVisible(true);
        } else {
            txtWatcherOutputPreserveFirst.setVisible(false);
            txtWatcherOutputPreserveLast.setVisible(false);
            lblWatcherOutputPreserveFirst.setVisible(false);
            lblWatcherOutputPreserveLast.setVisible(false);
            lblLimitWatcherOutputIn.setVisible(false);
            comboWatcherOutputUnit.setVisible(false);
        }
    }//GEN-LAST:event_chkLimitWatcherOutputActionPerformed
    
    private void chkLimitVerifierOutputActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkLimitVerifierOutputActionPerformed
        if (chkLimitVerifierOutput.isSelected()) {
            txtVerifierOutputPreserveFirst.setVisible(true);
            txtVerifierOutputPreserveLast.setVisible(true);
            lblVerifierOutputPreserveFirst.setVisible(true);
            lblVerifierOutputPreserveLast.setVisible(true);
            lblLimitVerifierOutputIn.setVisible(true);
            comboVerifierOutputUnit.setVisible(true);
        } else {
            txtVerifierOutputPreserveFirst.setVisible(false);
            txtVerifierOutputPreserveLast.setVisible(false);
            lblVerifierOutputPreserveFirst.setVisible(false);
            lblVerifierOutputPreserveLast.setVisible(false);
            lblLimitVerifierOutputIn.setVisible(false);
            comboVerifierOutputUnit.setVisible(false);
        }
    }//GEN-LAST:event_chkLimitVerifierOutputActionPerformed
    
    private void comboVerifierBinaryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comboVerifierBinaryActionPerformed
        Verifier v = (Verifier) comboVerifierBinary.getSelectedItem();
        if (v == null) {
            verifierParameterTableModel.setParameters(new LinkedList<VerifierParameter>());
        } else {
            verifierParameterTableModel.setParameters(v.getParameters());
        }
    }//GEN-LAST:event_comboVerifierBinaryActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnCreateExperiment;
    private javax.swing.JCheckBox chkConfigurationExp;
    private javax.swing.JCheckBox chkLimitSolverOutput;
    private javax.swing.JCheckBox chkLimitVerifierOutput;
    private javax.swing.JCheckBox chkLimitWatcherOutput;
    private javax.swing.JComboBox comboDefaultCost;
    private javax.swing.JComboBox comboSolverOutputUnit;
    private javax.swing.JComboBox comboVerifierBinary;
    private javax.swing.JComboBox comboVerifierOutputUnit;
    private javax.swing.JComboBox comboWatcherOutputUnit;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lblExperimentDescription;
    private javax.swing.JLabel lblExperimentName;
    private javax.swing.JLabel lblLimitSolverOutputIn;
    private javax.swing.JLabel lblLimitVerifierOutputIn;
    private javax.swing.JLabel lblLimitWatcherOutputIn;
    private javax.swing.JLabel lblSolverOutputPreserveFirst;
    private javax.swing.JLabel lblSolverOutputPreserveLast;
    private javax.swing.JLabel lblVerifierOutputPreserveFirst;
    private javax.swing.JLabel lblVerifierOutputPreserveLast;
    private javax.swing.JLabel lblWatcherOutputPreserveFirst;
    private javax.swing.JLabel lblWatcherOutputPreserveLast;
    private javax.swing.JTable tblVerifierParameters;
    private javax.swing.JTextArea txtExperimentDescription;
    private javax.swing.JTextField txtExperimentName;
    private javax.swing.JTextField txtSolverOutputPreserveFirst;
    private javax.swing.JTextField txtSolverOutputPreserveLast;
    private javax.swing.JTextField txtVerifierOutputPreserveFirst;
    private javax.swing.JTextField txtVerifierOutputPreserveLast;
    private javax.swing.JTextField txtWatcherOutputPreserveFirst;
    private javax.swing.JTextField txtWatcherOutputPreserveLast;
    // End of variables declaration//GEN-END:variables
}
