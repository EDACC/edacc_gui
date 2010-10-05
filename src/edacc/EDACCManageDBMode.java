/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * EDACCManageDBMode.java
 * @author
 * Created on 03.01.2010, 16:02:23
 */
package edacc;

import edacc.events.TaskEvents;
import edacc.manageDB.*;
import edacc.model.InstanceNotInDBException;
import edacc.model.InstanceClass;
import edacc.model.InstanceSourceClassHasInstance;
import edacc.model.MD5CheckFailedException;
import edacc.model.NoConnectionToDBException;
import edacc.model.Parameter;
import edacc.model.Solver;
import edacc.model.Tasks;
import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.InputVerifier;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.table.*;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.TableRowSorter;
import org.jdesktop.application.Action;

/**
 *
 * @author rretz
 */
public class EDACCManageDBMode extends javax.swing.JPanel implements TaskEvents {

    public boolean unsavedChanges;
    public ManageDBInstances manageDBInstances;
    public InstanceTableModel instanceTableModel;
    public InstanceClassTableModel instanceClassTableModel;
    public TableRowSorter<InstanceTableModel> sorter;
    private ManageDBSolvers manageDBSolvers;
    private SolverTableModel solverTableModel;
    private ManageDBParameters manageDBParameters;
    private ParameterTableModel parameterTableModel;
    public EDACCCreateEditInstanceClassDialog createInstanceClassDialog;
    public EDACCAddNewInstanceSelectClassDialog addInstanceDialog;
    public EDACCManageDBInstanceFilter instanceFilter;

    public EDACCManageDBMode() {
        initComponents();
        unsavedChanges = false;

        manageDBInstances = new ManageDBInstances(this, panelManageDBInstances,
                jFileChooserManageDBInstance, jFileChooserManageDBExportInstance, tableInstances);

        // initialize instance table
        instanceTableModel = new InstanceTableModel();
        tableInstances.setModel(instanceTableModel);
        sorter = new TableRowSorter<InstanceTableModel>(instanceTableModel);
        tableInstances.setRowSorter(sorter);
        tableInstances.getSelectionModel().addListSelectionListener(new InstanceTableSelectionListener(tableInstances, manageDBInstances));

        // initialize instance class table
        instanceClassTableModel = new InstanceClassTableModel(tableInstances);
        tableInstanceClass.setModel(instanceClassTableModel);
        tableInstanceClass.getSelectionModel().addListSelectionListener(new InstanceClassTableSelectionListener(tableInstanceClass, manageDBInstances));        
        tableInstanceClass.getColumnModel().getColumn(2).setCellRenderer(new DefaultTableCellRenderer() {

            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                lbl.setHorizontalAlignment(JLabel.CENTER);
                return lbl;
            }
        });

        // initialize solver table
        solverTableModel = new SolverTableModel();
        manageDBSolvers = new ManageDBSolvers(this, solverTableModel);
        tableSolver.setModel(solverTableModel);
        tableSolver.setRowSorter(new TableRowSorter<SolverTableModel>(solverTableModel));

        // initialize parameter table
        parameterTableModel = new ParameterTableModel();
        manageDBParameters = new ManageDBParameters(this, parameterTableModel);
        tableParameters.setModel(parameterTableModel);
        tableParameters.setRowSorter(new TableRowSorter<ParameterTableModel>(parameterTableModel));

        tableSolver.getSelectionModel().addListSelectionListener(new SolverTableSelectionListener(tableSolver, manageDBSolvers));
        tableParameters.getSelectionModel().addListSelectionListener(new ParameterTableSelectionListener(tableParameters, manageDBParameters));
        showSolverDetails(null);
        tableParameters.setDefaultRenderer(tableParameters.getColumnClass(2), new DefaultTableCellRenderer() {

            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                lbl.setHorizontalAlignment(JLabel.CENTER);
                return lbl;
            }
        });
        //TODO: FontMetrics verwenden!!!
        
        //tableParameters.getColumnModel().getColumn(0).setMaxWidth(metric.stringWidth(tableParameters.getModel().getColumnName(0))+10);
        //tableParameters.getColumnModel().getColumn(0).setMinWidth(metric.stringWidth(tableParameters.getModel().getColumnName(0))+5);
        tableParameters.getColumnModel().getColumn(3).setMaxWidth(50);
        tableInstanceClass.getColumnModel().getColumn(2).setMaxWidth(55);
        tableInstanceClass.getColumnModel().getColumn(3).setMaxWidth(55);
        tableInstanceClass.getColumnModel().getColumn(2).setMinWidth(40);
        tableInstanceClass.getColumnModel().getColumn(3).setMinWidth(40);
        this.jSplitPane2.setDividerLocation(-1);

    }

    void initialize() throws NoConnectionToDBException, SQLException {
        manageDBSolvers.loadSolvers();
        manageDBParameters.loadParametersOfSolvers(solverTableModel.getSolvers());
        manageDBInstances.loadInstanceClasses();

        unsavedChanges = false;
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jFileChooserManageDBInstance = new javax.swing.JFileChooser();
        jFileChooserManageDBExportInstance = new javax.swing.JFileChooser();
        manageDBPane = new javax.swing.JTabbedPane();
        panelManageDBSolver = new javax.swing.JPanel();
        jSplitPane2 = new javax.swing.JSplitPane();
        panelParametersOverall = new javax.swing.JPanel();
        panelParametersButons = new javax.swing.JPanel();
        btnParametersDelete = new javax.swing.JButton();
        btnParametersNew = new javax.swing.JButton();
        panelParameters = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tableParameters = new javax.swing.JTable();
        jPanel2 = new javax.swing.JPanel();
        jlParametersName = new javax.swing.JLabel();
        tfParametersName = new javax.swing.JTextField();
        jlParametersPrefix = new javax.swing.JLabel();
        tfParametersPrefix = new javax.swing.JTextField();
        jlParametersOrder = new javax.swing.JLabel();
        tfParametersOrder = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        chkHasNoValue = new javax.swing.JCheckBox();
        panelSolverOverall = new javax.swing.JPanel();
        panelSolverButtons = new javax.swing.JPanel();
        btnSolverDelete = new javax.swing.JButton();
        btnSolverNew = new javax.swing.JButton();
        btnSolverExport = new javax.swing.JButton();
        panelSolver = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tableSolver = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        jlSolverName = new javax.swing.JLabel();
        tfSolverName = new javax.swing.JTextField();
        jlSolverDescription = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        taSolverDescription = new javax.swing.JTextArea();
        jlSolverBinary = new javax.swing.JLabel();
        btnSolverAddBinary = new javax.swing.JButton();
        jlSolverCode = new javax.swing.JLabel();
        btnSolverAddCode = new javax.swing.JButton();
        tfSolverAuthors = new javax.swing.JTextField();
        tfSolverVersion = new javax.swing.JTextField();
        jlSolverAuthors = new javax.swing.JLabel();
        jlSolverVersion = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        btnSolverSaveToDB = new javax.swing.JButton();
        btnSolverRefresh = new javax.swing.JButton();
        panelManageDBInstances = new javax.swing.JPanel();
        jSplitPane1 = new javax.swing.JSplitPane();
        panelInstanceClass = new javax.swing.JPanel();
        panelButtonsInstanceClass = new javax.swing.JPanel();
        btnNewInstanceClass = new javax.swing.JButton();
        btnEditInstanceClass = new javax.swing.JButton();
        btnSelectAllInstanceClasses = new javax.swing.JButton();
        btnRemoveInstanceClass = new javax.swing.JButton();
        panelInstanceClassTable = new javax.swing.JScrollPane();
        tableInstanceClass = new javax.swing.JTable();
        panelInstance = new javax.swing.JPanel();
        panelInstanceTable = new javax.swing.JScrollPane();
        tableInstances = new javax.swing.JTable();
        panelButtonsInstances = new javax.swing.JPanel();
        btnAddInstances = new javax.swing.JButton();
        btnRemoveInstances = new javax.swing.JButton();
        btnFilterInstances = new javax.swing.JButton();
        btnExportInstances = new javax.swing.JButton();
        btnAddToClass = new javax.swing.JButton();
        btnRemoveFromClass = new javax.swing.JButton();
        btnAddInstances1 = new javax.swing.JButton();
        lblFilterStatus = new javax.swing.JLabel();

        jFileChooserManageDBInstance.setFileSelectionMode(javax.swing.JFileChooser.FILES_AND_DIRECTORIES);
        jFileChooserManageDBInstance.setName("jFileChooserManageDBInstance"); // NOI18N

        jFileChooserManageDBExportInstance.setFileSelectionMode(javax.swing.JFileChooser.DIRECTORIES_ONLY);
        jFileChooserManageDBExportInstance.setName("jFileChooserManageDBExportInstance"); // NOI18N

        setMinimumSize(new java.awt.Dimension(0, 0));
        setName("Form"); // NOI18N
        setPreferredSize(new java.awt.Dimension(500, 591));

        manageDBPane.setMinimumSize(new java.awt.Dimension(0, 0));
        manageDBPane.setName("manageDBPane"); // NOI18N
        manageDBPane.setRequestFocusEnabled(false);

        panelManageDBSolver.setName("panelManageDBSolver"); // NOI18N
        panelManageDBSolver.setPreferredSize(new java.awt.Dimension(0, 0));

        jSplitPane2.setDividerLocation(0.6);
        jSplitPane2.setResizeWeight(0.5);
        jSplitPane2.setName("jSplitPane2"); // NOI18N

        panelParametersOverall.setName("panelParametersOverall"); // NOI18N
        panelParametersOverall.setPreferredSize(new java.awt.Dimension(0, 0));

        panelParametersButons.setName("panelParametersButons"); // NOI18N

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(edacc.EDACCApp.class).getContext().getResourceMap(EDACCManageDBMode.class);
        btnParametersDelete.setText(resourceMap.getString("btnParametersDelete.text")); // NOI18N
        btnParametersDelete.setToolTipText(resourceMap.getString("btnParametersDelete.toolTipText")); // NOI18N
        btnParametersDelete.setName("btnParametersDelete"); // NOI18N
        btnParametersDelete.setPreferredSize(new java.awt.Dimension(81, 25));
        btnParametersDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnParametersDeleteActionPerformed(evt);
            }
        });

        btnParametersNew.setText(resourceMap.getString("btnParametersNew.text")); // NOI18N
        btnParametersNew.setToolTipText(resourceMap.getString("btnParametersNew.toolTipText")); // NOI18N
        btnParametersNew.setName("btnParametersNew"); // NOI18N
        btnParametersNew.setPreferredSize(new java.awt.Dimension(81, 25));
        btnParametersNew.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnParametersNewActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelParametersButonsLayout = new javax.swing.GroupLayout(panelParametersButons);
        panelParametersButons.setLayout(panelParametersButonsLayout);
        panelParametersButonsLayout.setHorizontalGroup(
            panelParametersButonsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelParametersButonsLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnParametersNew, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnParametersDelete, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        panelParametersButonsLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {btnParametersDelete, btnParametersNew});

        panelParametersButonsLayout.setVerticalGroup(
            panelParametersButonsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelParametersButonsLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(panelParametersButonsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnParametersDelete, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnParametersNew, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        panelParameters.setBorder(javax.swing.BorderFactory.createTitledBorder(resourceMap.getString("panelParameters.border.title"))); // NOI18N
        panelParameters.setName("panelParameters"); // NOI18N
        panelParameters.setPreferredSize(new java.awt.Dimension(0, 0));

        jScrollPane1.setMinimumSize(new java.awt.Dimension(0, 0));
        jScrollPane1.setName("jScrollPane1"); // NOI18N
        jScrollPane1.setPreferredSize(new java.awt.Dimension(0, 0));

        tableParameters.setModel(new javax.swing.table.DefaultTableModel(
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
        tableParameters.setToolTipText(resourceMap.getString("tableParameters.toolTipText")); // NOI18N
        tableParameters.setName("tableParameters"); // NOI18N
        tableParameters.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane1.setViewportView(tableParameters);

        jPanel2.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jPanel2.setName("jPanel2"); // NOI18N

        jlParametersName.setText(resourceMap.getString("jlParametersName.text")); // NOI18N
        jlParametersName.setName("jlParametersName"); // NOI18N

        tfParametersName.setText(resourceMap.getString("tfParametersName.text")); // NOI18N
        tfParametersName.setToolTipText(resourceMap.getString("tfParametersName.toolTipText")); // NOI18N
        tfParametersName.setInputVerifier(new ParameterNameVerifier());
        tfParametersName.setName("tfParametersName"); // NOI18N
        tfParametersName.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                parameterChangedOnFocusLost(evt);
            }
        });
        tfParametersName.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                parameterChangedOnKeyReleased(evt);
            }
        });

        jlParametersPrefix.setText(resourceMap.getString("jlParametersPrefix.text")); // NOI18N
        jlParametersPrefix.setName("jlParametersPrefix"); // NOI18N

        tfParametersPrefix.setText(resourceMap.getString("tfParametersPrefix.text")); // NOI18N
        tfParametersPrefix.setToolTipText(resourceMap.getString("tfParametersPrefix.toolTipText")); // NOI18N
        tfParametersPrefix.setName("tfParametersPrefix"); // NOI18N
        tfParametersPrefix.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                parameterChangedOnFocusLost(evt);
            }
        });
        tfParametersPrefix.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                parameterChangedOnKeyReleased(evt);
            }
        });

        jlParametersOrder.setText(resourceMap.getString("jlParametersOrder.text")); // NOI18N
        jlParametersOrder.setName("jlParametersOrder"); // NOI18N

        tfParametersOrder.setText(resourceMap.getString("tfParametersOrder.text")); // NOI18N
        tfParametersOrder.setToolTipText(resourceMap.getString("tfParametersOrder.toolTipText")); // NOI18N
        tfParametersOrder.setName("tfParametersOrder"); // NOI18N
        tfParametersOrder.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                parameterChangedOnFocusLost(evt);
            }
        });
        tfParametersOrder.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                parameterChangedOnKeyReleased(evt);
            }
        });

        jLabel1.setText(resourceMap.getString("jLabel1.text")); // NOI18N
        jLabel1.setToolTipText(resourceMap.getString("jLabel1.toolTipText")); // NOI18N
        jLabel1.setName("jLabel1"); // NOI18N

        chkHasNoValue.setText(resourceMap.getString("chkHasNoValue.text")); // NOI18N
        chkHasNoValue.setToolTipText(resourceMap.getString("chkHasNoValue.toolTipText")); // NOI18N
        chkHasNoValue.setName("chkHasNoValue"); // NOI18N
        chkHasNoValue.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                chkHasNoValueStateChanged(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jlParametersName, javax.swing.GroupLayout.DEFAULT_SIZE, 81, Short.MAX_VALUE)
                    .addComponent(jlParametersPrefix, javax.swing.GroupLayout.DEFAULT_SIZE, 81, Short.MAX_VALUE)
                    .addComponent(jlParametersOrder, javax.swing.GroupLayout.DEFAULT_SIZE, 81, Short.MAX_VALUE)
                    .addComponent(jLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(tfParametersName, javax.swing.GroupLayout.DEFAULT_SIZE, 37, Short.MAX_VALUE)
                    .addComponent(tfParametersPrefix, javax.swing.GroupLayout.DEFAULT_SIZE, 37, Short.MAX_VALUE)
                    .addComponent(tfParametersOrder, javax.swing.GroupLayout.DEFAULT_SIZE, 37, Short.MAX_VALUE)
                    .addComponent(chkHasNoValue, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        jPanel2Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jLabel1, jlParametersName, jlParametersOrder, jlParametersPrefix});

        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jlParametersName)
                    .addComponent(tfParametersName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jlParametersPrefix, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tfParametersPrefix, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jlParametersOrder)
                    .addComponent(tfParametersOrder, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addComponent(chkHasNoValue)))
        );

        javax.swing.GroupLayout panelParametersLayout = new javax.swing.GroupLayout(panelParameters);
        panelParameters.setLayout(panelParametersLayout);
        panelParametersLayout.setHorizontalGroup(
            panelParametersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelParametersLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelParametersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 152, Short.MAX_VALUE))
                .addContainerGap())
        );
        panelParametersLayout.setVerticalGroup(
            panelParametersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelParametersLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 216, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        javax.swing.GroupLayout panelParametersOverallLayout = new javax.swing.GroupLayout(panelParametersOverall);
        panelParametersOverall.setLayout(panelParametersOverallLayout);
        panelParametersOverallLayout.setHorizontalGroup(
            panelParametersOverallLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelParametersOverallLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelParametersOverallLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(panelParametersButons, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(panelParameters, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 188, Short.MAX_VALUE))
                .addContainerGap())
        );
        panelParametersOverallLayout.setVerticalGroup(
            panelParametersOverallLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelParametersOverallLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panelParameters, javax.swing.GroupLayout.DEFAULT_SIZE, 392, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelParametersButons, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jSplitPane2.setRightComponent(panelParametersOverall);
        panelParametersOverall.getAccessibleContext().setAccessibleName(resourceMap.getString("panelParameters.AccessibleContext.accessibleName")); // NOI18N

        panelSolverOverall.setName("panelSolverOverall"); // NOI18N
        panelSolverOverall.setPreferredSize(new java.awt.Dimension(500, 489));

        panelSolverButtons.setName("panelSolverButtons"); // NOI18N

        btnSolverDelete.setText(resourceMap.getString("btnSolverDelete.text")); // NOI18N
        btnSolverDelete.setToolTipText(resourceMap.getString("btnSolverDelete.toolTipText")); // NOI18N
        btnSolverDelete.setName("btnSolverDelete"); // NOI18N
        btnSolverDelete.setPreferredSize(new java.awt.Dimension(81, 25));
        btnSolverDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSolverDeleteActionPerformed(evt);
            }
        });

        btnSolverNew.setText(resourceMap.getString("btnNew.text")); // NOI18N
        btnSolverNew.setToolTipText(resourceMap.getString("btnNew.toolTipText")); // NOI18N
        btnSolverNew.setMaximumSize(new java.awt.Dimension(81, 25));
        btnSolverNew.setMinimumSize(new java.awt.Dimension(81, 25));
        btnSolverNew.setName("btnNew"); // NOI18N
        btnSolverNew.setPreferredSize(new java.awt.Dimension(81, 25));
        btnSolverNew.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSolverNewActionPerformed(evt);
            }
        });

        btnSolverExport.setText(resourceMap.getString("exportSolver.text")); // NOI18N
        btnSolverExport.setToolTipText(resourceMap.getString("exportSolver.toolTipText")); // NOI18N
        btnSolverExport.setName("exportSolver"); // NOI18N
        btnSolverExport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExport(evt);
            }
        });

        javax.swing.GroupLayout panelSolverButtonsLayout = new javax.swing.GroupLayout(panelSolverButtons);
        panelSolverButtons.setLayout(panelSolverButtonsLayout);
        panelSolverButtonsLayout.setHorizontalGroup(
            panelSolverButtonsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelSolverButtonsLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnSolverNew, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnSolverDelete, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnSolverExport, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        panelSolverButtonsLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {btnSolverDelete, btnSolverExport, btnSolverNew});

        panelSolverButtonsLayout.setVerticalGroup(
            panelSolverButtonsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelSolverButtonsLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelSolverButtonsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(btnSolverNew, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnSolverDelete, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnSolverExport))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        panelSolver.setBorder(javax.swing.BorderFactory.createTitledBorder(resourceMap.getString("panelSolver.border.title"))); // NOI18N
        panelSolver.setAutoscrolls(true);
        panelSolver.setName("panelSolver"); // NOI18N

        jScrollPane2.setToolTipText(resourceMap.getString("jScrollPane2.toolTipText")); // NOI18N
        jScrollPane2.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        jScrollPane2.setAutoscrolls(true);
        jScrollPane2.setEnabled(false);
        jScrollPane2.setMinimumSize(new java.awt.Dimension(0, 0));
        jScrollPane2.setName("jScrollPane2"); // NOI18N
        jScrollPane2.setPreferredSize(new java.awt.Dimension(0, 0));

        tableSolver.setAutoCreateRowSorter(true);
        tableSolver.setName("tableSolver"); // NOI18N
        tableSolver.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane2.setViewportView(tableSolver);

        jPanel1.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jPanel1.setName("jPanel1"); // NOI18N
        jPanel1.setPreferredSize(new java.awt.Dimension(0, 0));

        jlSolverName.setText(resourceMap.getString("jlSolverName.text")); // NOI18N
        jlSolverName.setName("jlSolverName"); // NOI18N

        tfSolverName.setText(resourceMap.getString("tfSolverName.text")); // NOI18N
        tfSolverName.setToolTipText(resourceMap.getString("tfSolverName.toolTipText")); // NOI18N
        tfSolverName.setName("tfSolverName"); // NOI18N
        tfSolverName.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                solverChangedOnFocusLost(evt);
            }
        });
        tfSolverName.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                solverChangedOnKey(evt);
            }
        });

        jlSolverDescription.setText(resourceMap.getString("jlSolverDescription.text")); // NOI18N
        jlSolverDescription.setName("jlSolverDescription"); // NOI18N

        jScrollPane3.setName("jScrollPane3"); // NOI18N

        taSolverDescription.setColumns(20);
        taSolverDescription.setLineWrap(true);
        taSolverDescription.setRows(5);
        taSolverDescription.setToolTipText(resourceMap.getString("taSolverDescription.toolTipText")); // NOI18N
        taSolverDescription.setName("taSolverDescription"); // NOI18N
        taSolverDescription.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                solverChangedOnFocusLost(evt);
            }
        });
        taSolverDescription.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                solverChangedOnKey(evt);
            }
        });
        jScrollPane3.setViewportView(taSolverDescription);

        jlSolverBinary.setText(resourceMap.getString("jlSolverBinary.text")); // NOI18N
        jlSolverBinary.setName("jlSolverBinary"); // NOI18N

        btnSolverAddBinary.setText(resourceMap.getString("btnSolverAddBinary.text")); // NOI18N
        btnSolverAddBinary.setToolTipText(resourceMap.getString("btnSolverAddBinary.toolTipText")); // NOI18N
        btnSolverAddBinary.setName("btnSolverAddBinary"); // NOI18N
        btnSolverAddBinary.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSolverAddBinaryActionPerformed(evt);
            }
        });

        jlSolverCode.setText(resourceMap.getString("jlSolverCode.text")); // NOI18N
        jlSolverCode.setName("jlSolverCode"); // NOI18N

        btnSolverAddCode.setText(resourceMap.getString("btnSolverAddCode.text")); // NOI18N
        btnSolverAddCode.setToolTipText(resourceMap.getString("btnSolverAddCode.toolTipText")); // NOI18N
        btnSolverAddCode.setName("btnSolverAddCode"); // NOI18N
        btnSolverAddCode.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSolverAddCodeActionPerformed(evt);
            }
        });

        tfSolverAuthors.setText(resourceMap.getString("tfSolverAuthors.text")); // NOI18N
        tfSolverAuthors.setName("tfSolverAuthors"); // NOI18N
        tfSolverAuthors.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                solverChangedOnFocusLost(evt);
            }
        });
        tfSolverAuthors.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                solverChangedOnKey(evt);
            }
        });

        tfSolverVersion.setText(resourceMap.getString("tfSolverVersion.text")); // NOI18N
        tfSolverVersion.setName("tfSolverVersion"); // NOI18N
        tfSolverVersion.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                solverChangedOnFocusLost(evt);
            }
        });
        tfSolverVersion.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                solverChangedOnKey(evt);
            }
        });

        jlSolverAuthors.setText(resourceMap.getString("jlSolverAuthors.text")); // NOI18N
        jlSolverAuthors.setName("jlSolverAuthors"); // NOI18N

        jlSolverVersion.setText(resourceMap.getString("jlSolverVersion.text")); // NOI18N
        jlSolverVersion.setName("jlSolverVersion"); // NOI18N

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jlSolverName, javax.swing.GroupLayout.DEFAULT_SIZE, 91, Short.MAX_VALUE)
                            .addComponent(jlSolverDescription, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jlSolverAuthors))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 120, Short.MAX_VALUE)
                            .addComponent(tfSolverName, javax.swing.GroupLayout.DEFAULT_SIZE, 120, Short.MAX_VALUE)
                            .addComponent(tfSolverAuthors, javax.swing.GroupLayout.DEFAULT_SIZE, 120, Short.MAX_VALUE)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jlSolverBinary)
                            .addComponent(jlSolverVersion))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnSolverAddBinary)
                            .addComponent(tfSolverVersion, javax.swing.GroupLayout.DEFAULT_SIZE, 120, Short.MAX_VALUE)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jlSolverCode)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnSolverAddCode)))
                .addContainerGap())
        );

        jPanel1Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {btnSolverAddBinary, btnSolverAddCode});

        jPanel1Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jlSolverBinary, jlSolverCode, jlSolverDescription, jlSolverName});

        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tfSolverName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jlSolverName))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jlSolverDescription, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(tfSolverAuthors, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jlSolverAuthors))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tfSolverVersion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jlSolverVersion))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnSolverAddBinary)
                    .addComponent(jlSolverBinary))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnSolverAddCode)
                    .addComponent(jlSolverCode))
                .addContainerGap(15, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout panelSolverLayout = new javax.swing.GroupLayout(panelSolver);
        panelSolver.setLayout(panelSolverLayout);
        panelSolverLayout.setHorizontalGroup(
            panelSolverLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelSolverLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelSolverLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 239, Short.MAX_VALUE)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 239, Short.MAX_VALUE))
                .addContainerGap())
        );
        panelSolverLayout.setVerticalGroup(
            panelSolverLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelSolverLayout.createSequentialGroup()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 83, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 262, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jScrollPane2.getAccessibleContext().setAccessibleParent(manageDBPane);

        javax.swing.GroupLayout panelSolverOverallLayout = new javax.swing.GroupLayout(panelSolverOverall);
        panelSolverOverall.setLayout(panelSolverOverallLayout);
        panelSolverOverallLayout.setHorizontalGroup(
            panelSolverOverallLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelSolverOverallLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelSolverOverallLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(panelSolver, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(panelSolverButtons, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        panelSolverOverallLayout.setVerticalGroup(
            panelSolverOverallLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelSolverOverallLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panelSolver, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelSolverButtons, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jSplitPane2.setLeftComponent(panelSolverOverall);

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        jPanel3.setName("jPanel3"); // NOI18N

        btnSolverSaveToDB.setText(resourceMap.getString("btnSolverSaveToDB.text")); // NOI18N
        btnSolverSaveToDB.setToolTipText(resourceMap.getString("btnSolverSaveToDB.toolTipText")); // NOI18N
        btnSolverSaveToDB.setName("btnSolverSaveToDB"); // NOI18N
        btnSolverSaveToDB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSolverSaveToDBActionPerformed(evt);
            }
        });

        btnSolverRefresh.setText(resourceMap.getString("btnSolverRefresh.text")); // NOI18N
        btnSolverRefresh.setToolTipText(resourceMap.getString("btnSolverRefresh.toolTipText")); // NOI18N
        btnSolverRefresh.setName("btnSolverRefresh"); // NOI18N
        btnSolverRefresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSolverRefreshActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnSolverRefresh, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 300, Short.MAX_VALUE)
                .addComponent(btnSolverSaveToDB)
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(btnSolverRefresh)
                .addComponent(btnSolverSaveToDB))
        );

        javax.swing.GroupLayout panelManageDBSolverLayout = new javax.swing.GroupLayout(panelManageDBSolver);
        panelManageDBSolver.setLayout(panelManageDBSolverLayout);
        panelManageDBSolverLayout.setHorizontalGroup(
            panelManageDBSolverLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelManageDBSolverLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelManageDBSolverLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jSplitPane2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 510, Short.MAX_VALUE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        panelManageDBSolverLayout.setVerticalGroup(
            panelManageDBSolverLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelManageDBSolverLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jSplitPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 469, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        manageDBPane.addTab("Solvers", panelManageDBSolver);

        panelManageDBInstances.setName("panelManageDBInstances"); // NOI18N
        panelManageDBInstances.setPreferredSize(new java.awt.Dimension(0, 0));

        jSplitPane1.setDividerLocation(0.6);
        jSplitPane1.setResizeWeight(0.4);
        jSplitPane1.setName("jSplitPane1"); // NOI18N

        panelInstanceClass.setBorder(javax.swing.BorderFactory.createTitledBorder(resourceMap.getString("panelInstanceClass.border.title"))); // NOI18N
        panelInstanceClass.setName("panelInstanceClass"); // NOI18N
        panelInstanceClass.setPreferredSize(new java.awt.Dimension(0, 0));

        panelButtonsInstanceClass.setName("panelButtonsInstanceClass"); // NOI18N

        btnNewInstanceClass.setText(resourceMap.getString("btnNewInstanceClass.text")); // NOI18N
        btnNewInstanceClass.setToolTipText(resourceMap.getString("btnNewInstanceClass.toolTipText")); // NOI18N
        btnNewInstanceClass.setName("btnNewInstanceClass"); // NOI18N
        btnNewInstanceClass.setPreferredSize(new java.awt.Dimension(89, 25));
        btnNewInstanceClass.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNewInstanceClassActionPerformed(evt);
            }
        });

        btnEditInstanceClass.setText(resourceMap.getString("btnEditInstanceClass.text")); // NOI18N
        btnEditInstanceClass.setToolTipText(resourceMap.getString("btnEditInstanceClass.toolTipText")); // NOI18N
        btnEditInstanceClass.setEnabled(false);
        btnEditInstanceClass.setName("btnEditInstanceClass"); // NOI18N
        btnEditInstanceClass.setPreferredSize(new java.awt.Dimension(89, 25));
        btnEditInstanceClass.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEditInstanceClassActionPerformed(evt);
            }
        });

        btnSelectAllInstanceClasses.setText(resourceMap.getString("btnSelectAllInstanceClasses.text")); // NOI18N
        btnSelectAllInstanceClasses.setToolTipText(resourceMap.getString("btnSelectAllInstanceClasses.toolTipText")); // NOI18N
        btnSelectAllInstanceClasses.setName("btnSelectAllInstanceClasses"); // NOI18N
        btnSelectAllInstanceClasses.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSelectAllInstanceClassesActionPerformed(evt);
            }
        });

        btnRemoveInstanceClass.setText(resourceMap.getString("btnRemoveInstanceClass.text")); // NOI18N
        btnRemoveInstanceClass.setToolTipText(resourceMap.getString("btnRemoveInstanceClass.toolTipText")); // NOI18N
        btnRemoveInstanceClass.setEnabled(false);
        btnRemoveInstanceClass.setName("btnRemoveInstanceClass"); // NOI18N
        btnRemoveInstanceClass.setPreferredSize(new java.awt.Dimension(89, 25));
        btnRemoveInstanceClass.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRemoveInstanceClassActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelButtonsInstanceClassLayout = new javax.swing.GroupLayout(panelButtonsInstanceClass);
        panelButtonsInstanceClass.setLayout(panelButtonsInstanceClassLayout);
        panelButtonsInstanceClassLayout.setHorizontalGroup(
            panelButtonsInstanceClassLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelButtonsInstanceClassLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnNewInstanceClass, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnEditInstanceClass, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnRemoveInstanceClass, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnSelectAllInstanceClasses)
                .addContainerGap())
        );

        panelButtonsInstanceClassLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {btnEditInstanceClass, btnNewInstanceClass, btnRemoveInstanceClass});

        panelButtonsInstanceClassLayout.setVerticalGroup(
            panelButtonsInstanceClassLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelButtonsInstanceClassLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelButtonsInstanceClassLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnSelectAllInstanceClasses)
                    .addComponent(btnNewInstanceClass, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnEditInstanceClass, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnRemoveInstanceClass, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        panelInstanceClassTable.setToolTipText(resourceMap.getString("panelInstanceClassTable.toolTipText")); // NOI18N
        panelInstanceClassTable.setName("panelInstanceClassTable"); // NOI18N

        tableInstanceClass.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null},
                {null, null},
                {null, null},
                {null, null}
            },
            new String [] {
                "Class", "Select"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Boolean.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        tableInstanceClass.setFocusTraversalPolicyProvider(true);
        tableInstanceClass.setName("tableInstanceClass"); // NOI18N
        tableInstanceClass.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tableInstanceClass.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tableInstanceClassMouseClicked(evt);
            }
        });
        panelInstanceClassTable.setViewportView(tableInstanceClass);
        tableInstanceClass.getColumnModel().getColumn(0).setHeaderValue(resourceMap.getString("tableInstanceClass.columnModel.title0")); // NOI18N
        tableInstanceClass.getColumnModel().getColumn(1).setHeaderValue(resourceMap.getString("tableInstanceClass.columnModel.title1")); // NOI18N

        javax.swing.GroupLayout panelInstanceClassLayout = new javax.swing.GroupLayout(panelInstanceClass);
        panelInstanceClass.setLayout(panelInstanceClassLayout);
        panelInstanceClassLayout.setHorizontalGroup(
            panelInstanceClassLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelInstanceClassLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelInstanceClassLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(panelButtonsInstanceClass, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(panelInstanceClassTable, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 384, Short.MAX_VALUE))
                .addContainerGap())
        );
        panelInstanceClassLayout.setVerticalGroup(
            panelInstanceClassLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelInstanceClassLayout.createSequentialGroup()
                .addComponent(panelInstanceClassTable, javax.swing.GroupLayout.DEFAULT_SIZE, 429, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(panelButtonsInstanceClass, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jSplitPane1.setLeftComponent(panelInstanceClass);

        panelInstance.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createTitledBorder(resourceMap.getString("panelInstance.border.border.title")))); // NOI18N
        panelInstance.setName("panelInstance"); // NOI18N
        panelInstance.setPreferredSize(new java.awt.Dimension(0, 0));

        panelInstanceTable.setName("panelInstanceTable"); // NOI18N

        tableInstances.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "Name", "numAtoms", "numClauses", "ratio", "maxClauseLength"
            }
        ));
        tableInstances.setToolTipText(resourceMap.getString("tableInstances.toolTipText")); // NOI18N
        tableInstances.setMaximumSize(new java.awt.Dimension(2147483647, 8000));
        tableInstances.setName("tableInstances"); // NOI18N
        tableInstances.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tableInstancesMouseClicked(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                tableInstancesMousePressed(evt);
            }
        });
        panelInstanceTable.setViewportView(tableInstances);
        tableInstances.getColumnModel().getColumn(0).setHeaderValue(resourceMap.getString("tableInstances.columnModel.title0")); // NOI18N
        tableInstances.getColumnModel().getColumn(1).setHeaderValue(resourceMap.getString("tableInstances.columnModel.title1")); // NOI18N
        tableInstances.getColumnModel().getColumn(2).setHeaderValue(resourceMap.getString("tableInstances.columnModel.title2")); // NOI18N
        tableInstances.getColumnModel().getColumn(3).setHeaderValue(resourceMap.getString("tableInstances.columnModel.title3")); // NOI18N
        tableInstances.getColumnModel().getColumn(4).setHeaderValue(resourceMap.getString("tableInstances.columnModel.title4")); // NOI18N

        panelButtonsInstances.setName("panelButtonsInstances"); // NOI18N

        btnAddInstances.setText(resourceMap.getString("btnAddInstances.text")); // NOI18N
        btnAddInstances.setToolTipText(resourceMap.getString("btnAddInstances.toolTipText")); // NOI18N
        btnAddInstances.setName("btnAddInstances"); // NOI18N
        btnAddInstances.setPreferredSize(new java.awt.Dimension(83, 25));
        btnAddInstances.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddInstancesActionPerformed(evt);
            }
        });

        btnRemoveInstances.setText(resourceMap.getString("btnRemoveInstances.text")); // NOI18N
        btnRemoveInstances.setToolTipText(resourceMap.getString("btnRemoveInstances.toolTipText")); // NOI18N
        btnRemoveInstances.setEnabled(false);
        btnRemoveInstances.setName("btnRemoveInstances"); // NOI18N
        btnRemoveInstances.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRemoveInstancesActionPerformed(evt);
            }
        });

        btnFilterInstances.setText(resourceMap.getString("btnFilterInstances.text")); // NOI18N
        btnFilterInstances.setToolTipText(resourceMap.getString("btnFilterInstances.toolTipText")); // NOI18N
        btnFilterInstances.setName("btnFilterInstances"); // NOI18N
        btnFilterInstances.setPreferredSize(new java.awt.Dimension(83, 25));
        btnFilterInstances.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFilterInstancesActionPerformed(evt);
            }
        });

        btnExportInstances.setText(resourceMap.getString("btnExportInstances.text")); // NOI18N
        btnExportInstances.setToolTipText(resourceMap.getString("btnExportInstances.toolTipText")); // NOI18N
        btnExportInstances.setEnabled(false);
        btnExportInstances.setName("btnExportInstances"); // NOI18N
        btnExportInstances.setPreferredSize(new java.awt.Dimension(83, 25));
        btnExportInstances.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExportInstancesActionPerformed(evt);
            }
        });

        btnAddToClass.setText(resourceMap.getString("btnAddToClass.text")); // NOI18N
        btnAddToClass.setToolTipText(resourceMap.getString("btnAddToClass.toolTipText")); // NOI18N
        btnAddToClass.setEnabled(false);
        btnAddToClass.setName("btnAddToClass"); // NOI18N
        btnAddToClass.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddToClassActionPerformed(evt);
            }
        });

        btnRemoveFromClass.setToolTipText(resourceMap.getString("btnRemoveFromClass.toolTipText")); // NOI18N
        btnRemoveFromClass.setActionCommand(resourceMap.getString("btnRemoveFromClass.actionCommand")); // NOI18N
        btnRemoveFromClass.setEnabled(false);
        btnRemoveFromClass.setLabel(resourceMap.getString("btnRemoveFromClass.label")); // NOI18N
        btnRemoveFromClass.setName("btnRemoveFromClass"); // NOI18N
        btnRemoveFromClass.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRemoveFromClassActionPerformed(evt);
            }
        });

        btnAddInstances1.setText(resourceMap.getString("btnAddInstances1.text")); // NOI18N
        btnAddInstances1.setToolTipText(resourceMap.getString("btnAddInstances1.toolTipText")); // NOI18N
        btnAddInstances1.setName("btnAddInstances1"); // NOI18N
        btnAddInstances1.setPreferredSize(new java.awt.Dimension(83, 25));
        btnAddInstances1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddInstances1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelButtonsInstancesLayout = new javax.swing.GroupLayout(panelButtonsInstances);
        panelButtonsInstances.setLayout(panelButtonsInstancesLayout);
        panelButtonsInstancesLayout.setHorizontalGroup(
            panelButtonsInstancesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelButtonsInstancesLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelButtonsInstancesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelButtonsInstancesLayout.createSequentialGroup()
                        .addComponent(btnAddToClass, javax.swing.GroupLayout.PREFERRED_SIZE, 151, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btnRemoveFromClass))
                    .addGroup(panelButtonsInstancesLayout.createSequentialGroup()
                        .addComponent(btnAddInstances, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnRemoveInstances)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnAddInstances1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnFilterInstances, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnExportInstances, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())))
        );

        panelButtonsInstancesLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {btnAddInstances, btnRemoveInstances});

        panelButtonsInstancesLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {btnAddToClass, btnRemoveFromClass});

        panelButtonsInstancesLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {btnExportInstances, btnFilterInstances});

        panelButtonsInstancesLayout.setVerticalGroup(
            panelButtonsInstancesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelButtonsInstancesLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelButtonsInstancesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnAddInstances, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnRemoveInstances)
                    .addComponent(btnExportInstances, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnFilterInstances, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnAddInstances1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(panelButtonsInstancesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnAddToClass)
                    .addComponent(btnRemoveFromClass))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        lblFilterStatus.setText(resourceMap.getString("lblFilterStatus.text")); // NOI18N
        lblFilterStatus.setName("lblFilterStatus"); // NOI18N

        javax.swing.GroupLayout panelInstanceLayout = new javax.swing.GroupLayout(panelInstance);
        panelInstance.setLayout(panelInstanceLayout);
        panelInstanceLayout.setHorizontalGroup(
            panelInstanceLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelInstanceLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelInstanceLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(panelInstanceTable, javax.swing.GroupLayout.DEFAULT_SIZE, 471, Short.MAX_VALUE)
                    .addComponent(panelButtonsInstances, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblFilterStatus, javax.swing.GroupLayout.DEFAULT_SIZE, 471, Short.MAX_VALUE)))
        );
        panelInstanceLayout.setVerticalGroup(
            panelInstanceLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelInstanceLayout.createSequentialGroup()
                .addComponent(lblFilterStatus)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelInstanceTable, javax.swing.GroupLayout.DEFAULT_SIZE, 374, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(panelButtonsInstances, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jSplitPane1.setRightComponent(panelInstance);

        javax.swing.GroupLayout panelManageDBInstancesLayout = new javax.swing.GroupLayout(panelManageDBInstances);
        panelManageDBInstances.setLayout(panelManageDBInstancesLayout);
        panelManageDBInstancesLayout.setHorizontalGroup(
            panelManageDBInstancesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelManageDBInstancesLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jSplitPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 932, Short.MAX_VALUE)
                .addContainerGap())
        );
        panelManageDBInstancesLayout.setVerticalGroup(
            panelManageDBInstancesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelManageDBInstancesLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jSplitPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 530, Short.MAX_VALUE))
        );

        manageDBPane.addTab("Instances", panelManageDBInstances);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(manageDBPane, javax.swing.GroupLayout.DEFAULT_SIZE, 480, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(manageDBPane, javax.swing.GroupLayout.DEFAULT_SIZE, 569, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    public void test(Tasks task) {
        task.setStatus("lalalaaaaaa");
        System.out.println("test");
    }
    public void btnAddInstancesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddInstancesActionPerformed

        //Starts the dialog at which the user has to choose a instance source class or the autogeneration.

        try {
            if (addInstanceDialog == null) {
                JFrame mainFrame = EDACCApp.getApplication().getMainFrame();
                this.addInstanceDialog = new EDACCAddNewInstanceSelectClassDialog(mainFrame, true);
                this.addInstanceDialog.setLocationRelativeTo(mainFrame);
            } 
            addInstanceDialog.refresh();
            EDACCApp.getApplication().show(this.addInstanceDialog);
            InstanceClass input = this.addInstanceDialog.getInput();
            int searchDepth = this.addInstanceDialog.getSearchDepth();

            //if the user doesn't cancel the dialog above, the fileChooser is shown.
            if (input != null) {
                //When the user choos autogenerate only directorys can be choosen, else files and directorys.
                if (input.getName().equals("")) {
                    jFileChooserManageDBInstance.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                } else {
                    jFileChooserManageDBInstance.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
                }

                int returnVal = jFileChooserManageDBInstance.showOpenDialog(panelManageDBInstances);
                File ret = jFileChooserManageDBInstance.getSelectedFile();
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    Tasks.startTask("addInstances", new Class[]{edacc.model.InstanceClass.class, java.io.File.class, edacc.model.Tasks.class, int.class}, new Object[]{input, ret, null, searchDepth}, manageDBInstances, EDACCManageDBMode.this);
                }
            }
            input = null;
            unsavedChanges = true;

        } catch (NoConnectionToDBException ex) {
            Logger.getLogger(EDACCManageDBMode.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(EDACCManageDBMode.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.instanceTableModel.fireTableDataChanged();
        this.instanceClassTableModel.fireTableDataChanged();
    }//GEN-LAST:event_btnAddInstancesActionPerformed

    private void btnRemoveInstancesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRemoveInstancesActionPerformed
        if(tableInstances.getSelectedRows().length == 0){
             JOptionPane.showMessageDialog(panelManageDBInstances,
                "No instances selected.",
                "Warning",
                JOptionPane.WARNING_MESSAGE);
        } else {
            try {
                manageDBInstances.removeInstances(tableInstances.getSelectedRows());
            } catch (NoConnectionToDBException ex) {
                Logger.getLogger(EDACCManageDBMode.class.getName()).log(Level.SEVERE, null, ex);
            } catch (SQLException ex) {
                Logger.getLogger(EDACCManageDBMode.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        this.tableInstances.requestFocus();
       
    }//GEN-LAST:event_btnRemoveInstancesActionPerformed

    private void btnFilterInstancesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFilterInstancesActionPerformed
        manageDBInstances.addFilter();
    }//GEN-LAST:event_btnFilterInstancesActionPerformed

    private void btnSolverSaveToDBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSolverSaveToDBActionPerformed
        try {
            manageDBSolvers.saveSolvers();
            for (Solver s : solverTableModel.getSolvers()) {
                manageDBParameters.saveParameters(s);
            }
            unsavedChanges = false;
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(panelManageDBInstances,
                    "Solvers cannot be saved. There is a problem with the Database: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        } catch (FileNotFoundException ex) {
            JOptionPane.showMessageDialog(panelManageDBInstances,
                    "Solvers cannot be saved because a file couldn't be found: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        } catch (NoSolverBinarySpecifiedException ex) {
            JOptionPane.showMessageDialog(panelManageDBInstances,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        } catch (NoSolverNameSpecifiedException ex) {
            JOptionPane.showMessageDialog(panelManageDBInstances,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(panelManageDBInstances,
                    "IO exception while reading solver data from the filesystem" + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnSolverSaveToDBActionPerformed

    private void btnSolverNewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSolverNewActionPerformed
        manageDBSolvers.newSolver();
        tableSolver.getSelectionModel().setSelectionInterval(tableSolver.getRowCount() - 1, tableSolver.getRowCount() - 1);
        tableSolver.updateUI();
        unsavedChanges = true;
        tfSolverName.requestFocus();
    }//GEN-LAST:event_btnSolverNewActionPerformed
    JFileChooser binaryFileChooser;
    private void btnSolverAddBinaryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSolverAddBinaryActionPerformed
        try {
            if (binaryFileChooser == null) {
                binaryFileChooser = new JFileChooser();
            }
            if (binaryFileChooser.showDialog(this, "Add Solver Binary") == JFileChooser.APPROVE_OPTION) {
                manageDBSolvers.addSolverBinary(binaryFileChooser.getSelectedFile());
                unsavedChanges = true;
            }
        } catch (FileNotFoundException ex) {
            JOptionPane.showMessageDialog(panelManageDBInstances,
                    "The binary of the solver couldn't be found: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(panelManageDBInstances,
                    "An error occured while adding the binary of the solver: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
        tableSolver.updateUI();
    }//GEN-LAST:event_btnSolverAddBinaryActionPerformed

    private void btnParametersNewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnParametersNewActionPerformed
        manageDBParameters.newParam();
        tableParameters.getSelectionModel().setSelectionInterval(tableParameters.getRowCount() - 1, tableParameters.getRowCount() - 1);
        tableParameters.updateUI();
        unsavedChanges = true;
        this.tfParametersName.requestFocus();
    }//GEN-LAST:event_btnParametersNewActionPerformed

    /**
     * Handles the key released events of the textfields "solver name" and "solver description".
     * @param evt
     */
    private void solverChangedOnKey(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_solverChangedOnKey
        solverChanged();
    }//GEN-LAST:event_solverChangedOnKey

    /**
     * Applies the solver name and description and updates the UI of the table.
     */
    private void solverChanged() {
        manageDBSolvers.applySolver(tfSolverName.getText(), taSolverDescription.getText(), tfSolverAuthors.getText(), tfSolverVersion.getText());
        tableSolver.updateUI();
        unsavedChanges = true;
    }
    private JFileChooser codeFileChooser;
    private void btnSolverAddCodeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSolverAddCodeActionPerformed
        try {
            if (codeFileChooser == null) {
                codeFileChooser = new JFileChooser();
                codeFileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
                codeFileChooser.setMultiSelectionEnabled(true);
            }
            if (codeFileChooser.showDialog(this, "Choose code") == JFileChooser.APPROVE_OPTION) {
                manageDBSolvers.addSolverCode(codeFileChooser.getSelectedFiles());
                unsavedChanges = true;
            }
        } catch (FileNotFoundException ex) {
            JOptionPane.showMessageDialog(panelManageDBInstances,
                    "The code of the solver couldn't be found: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
        tableSolver.updateUI();
    }//GEN-LAST:event_btnSolverAddCodeActionPerformed

    /**
     * Handles the focus lost event of the solver textfields "name" and "description".
     * @param evt
     */
    private void solverChangedOnFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_solverChangedOnFocusLost
        solverChanged();
    }//GEN-LAST:event_solverChangedOnFocusLost

    private void btnSolverDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSolverDeleteActionPerformed
        // show warning first
        final int userAnswer = JOptionPane.showConfirmDialog(panelSolver,
                "The selected solvers will be deleted. Do you wish to continue?",
                "Delete selected solvers",
                JOptionPane.YES_NO_OPTION);
        if (userAnswer == JOptionPane.NO_OPTION) {
            return;
        }

        int[] rows = tableSolver.getSelectedRows();
        LinkedList<Solver> selectedSolvers = new LinkedList<Solver>();
        int lastSelectedIndex = -1;
        for (int i : rows) {
            selectedSolvers.add(solverTableModel.getSolver(tableSolver.convertRowIndexToModel(i)));
            lastSelectedIndex = i;
        }
        if (selectedSolvers.isEmpty()) {
            JOptionPane.showMessageDialog(panelSolver, "No solver selected!", "Warning", JOptionPane.WARNING_MESSAGE);
        } else {
            while (!selectedSolvers.isEmpty()) { // are there remaining solvers to delete?
                try {
                    Solver s = selectedSolvers.removeFirst();
                    manageDBSolvers.removeSolver(s);
                    manageDBParameters.removeParameters(s);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this,
                            "An error occured while deleting a solver: " + ex.getMessage(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                } finally {
                   
                    tableSolver.getSelectionModel().clearSelection();
                    solverTableModel.fireTableDataChanged();
                    tableSolver.updateUI();
                    tableParameters.updateUI();

                    // try to select the solver which stood one row over the last deleted solver
                    if (lastSelectedIndex >= tableSolver.getRowCount())
                        lastSelectedIndex = tableSolver.getRowCount() - 1;
                    if (lastSelectedIndex >= 0)
                        tableSolver.getSelectionModel().setSelectionInterval(lastSelectedIndex, lastSelectedIndex);
                }
            }
        }
        tfSolverName.setText("");
        taSolverDescription.setText("");
        tfSolverAuthors.setText("");
        tfSolverVersion.setText("");
    }//GEN-LAST:event_btnSolverDeleteActionPerformed
    private void btnExportInstancesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExportInstancesActionPerformed

        if (tableInstances.getSelectedRowCount() == 0) {
            JOptionPane.showMessageDialog(panelManageDBInstances,
                    "No instances are selected. ",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        } else {
            int returnVal = jFileChooserManageDBExportInstance.showOpenDialog(panelManageDBInstances);
            String path = jFileChooserManageDBExportInstance.getSelectedFile().getAbsolutePath();
            Tasks.startTask("exportInstances", new Class[]{int[].class, String.class, edacc.model.Tasks.class}, new Object[]{tableInstances.getSelectedRows(), path, null}, manageDBInstances, EDACCManageDBMode.this);
        }

    }//GEN-LAST:event_btnExportInstancesActionPerformed

    private void btnSelectAllInstanceClassesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSelectAllInstanceClassesActionPerformed
        
        if (btnSelectAllInstanceClasses.getText() == null ? "Show all" == null : btnSelectAllInstanceClasses.getText().equals("Show all")) {
            Tasks.startTask("SelectAllInstanceClass", new Class[]{edacc.model.Tasks.class}, new Object[]{null}, manageDBInstances, EDACCManageDBMode.this);
            this.btnSelectAllInstanceClasses.setText("Show none");
        } else {
            this.manageDBInstances.DeselectAllInstanceClass();
            this.btnSelectAllInstanceClasses.setText("Show all");
        }
        instanceTableModel.fireTableDataChanged();
        instanceClassTableModel.fireTableDataChanged();

    }//GEN-LAST:event_btnSelectAllInstanceClassesActionPerformed

    private void btnEditInstanceClassActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditInstanceClassActionPerformed
            if(tableInstanceClass.getSelectedRow() == -1){
                    JOptionPane.showMessageDialog(panelManageDBInstances,
                    "Please select an instance class to edit!",
                    "Warning",
                    JOptionPane.WARNING_MESSAGE);
            }else {
                manageDBInstances.EditInstanceClass(instanceClassTableModel, tableInstanceClass.convertRowIndexToModel(tableInstanceClass.getSelectedRow()));
                instanceClassTableModel.fireTableDataChanged();
            }

    }//GEN-LAST:event_btnEditInstanceClassActionPerformed

    private void btnParametersDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnParametersDeleteActionPerformed
        if (tableParameters.getSelectedRow() == -1) {
            JOptionPane.showMessageDialog(this,
                    "No parameters selected!",
                    "Warning",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        int selectedIndex = tableParameters.getSelectedRow();
        Parameter p = parameterTableModel.getParameter(tableParameters.convertRowIndexToModel(selectedIndex));
        try {
            manageDBParameters.removeParameter(p);
        } catch (NoConnectionToDBException ex) {
            JOptionPane.showMessageDialog(panelManageDBInstances,
                    "No connection to database: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(panelManageDBInstances,
                    "SQL-Exception while deleting parameter: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
        // try select the parameter which stood on row over the deleted param
        if (selectedIndex >= tableParameters.getRowCount())
            selectedIndex--;
        Parameter selected = null;
        tableParameters.clearSelection();
        if (selectedIndex >= 0) {
            selected = parameterTableModel.getParameter(tableParameters.convertRowIndexToModel(selectedIndex));
            tableParameters.getSelectionModel().setSelectionInterval(selectedIndex, selectedIndex);
        }
        
        showParameterDetails(
                selected);
        tableParameters.updateUI();
    }//GEN-LAST:event_btnParametersDeleteActionPerformed

    private void btnSolverRefreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSolverRefreshActionPerformed
        //if (this.unsavedChanges)
        if ((JOptionPane.showConfirmDialog(this,
                "This will reload all data from DB. You are going to lose all your unsaved changes. Do you wish to continue?",
                "Warning!",
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION)) {
            try {
                int row = tableSolver.getSelectedRow();
                manageDBSolvers.loadSolvers();
                manageDBParameters.loadParametersOfSolvers(solverTableModel.getSolvers());
                tableSolver.updateUI();
                panelSolverOverall.updateUI();
                tableParameters.updateUI();
                tableSolver.clearSelection();
                unsavedChanges = false;
            } catch (NoConnectionToDBException ex) {
                JOptionPane.showMessageDialog(panelManageDBInstances,
                        "No connection to database: " + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(panelManageDBInstances,
                        "SQL-Exception while refreshing tables: " + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_btnSolverRefreshActionPerformed

    private void btnNewInstanceClassActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNewInstanceClassActionPerformed
        manageDBInstances.addInstanceClasses();
        tableInstanceClass.updateUI();
        unsavedChanges = true;
    }//GEN-LAST:event_btnNewInstanceClassActionPerformed
    private void parameterChangedOnFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_parameterChangedOnFocusLost
        parameterChanged();
    }//GEN-LAST:event_parameterChangedOnFocusLost
    private void btnAddToClassActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddToClassActionPerformed
        try {
            int[] selectedRowsInstance = tableInstances.getSelectedRows();
            for (int i = 0; i < selectedRowsInstance.length; i++) {
                selectedRowsInstance[i] = tableInstances.convertRowIndexToModel(selectedRowsInstance[i]);
            }
            manageDBInstances.addInstancesToClass(selectedRowsInstance);
            unsavedChanges = true;
            tableInstances.requestFocus();
            if (instanceTableModel.getRowCount() != 0) {
                tableInstances.addRowSelectionInterval(0, 0);
            }
        } catch (IOException ex) {
            Logger.getLogger(EDACCManageDBMode.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btnAddToClassActionPerformed
    private void btnRemoveFromClassActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRemoveFromClassActionPerformed
       int[] selectedRowsInstanceClass = tableInstanceClass.getSelectedRows();
       for(int i = 0; i < selectedRowsInstanceClass.length; i++){
           selectedRowsInstanceClass[i] = tableInstanceClass.convertRowIndexToModel(selectedRowsInstanceClass[i]);
       }
        manageDBInstances.RemoveInstanceFromInstanceClass(tableInstances.getSelectedRows(), selectedRowsInstanceClass);
        this.instanceTableModel.fireTableDataChanged();
        if(instanceTableModel.getRowCount() != 0){
           this.tableInstances.addRowSelectionInterval(0, 0);
        }      
    }//GEN-LAST:event_btnRemoveFromClassActionPerformed
    private JFileChooser exportFileChooser;
    private void btnExport(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExport
        if (exportFileChooser == null) {
            exportFileChooser = new JFileChooser();
            exportFileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        }

        if (exportFileChooser.showDialog(this, "Export code and binary of selected solvers to directory") == JFileChooser.APPROVE_OPTION) {
            int[] rows = tableSolver.getSelectedRows();

            for (int i : rows) {
                try {
                    manageDBSolvers.exportSolver(solverTableModel.getSolver(tableSolver.convertRowIndexToModel(i)), exportFileChooser.getSelectedFile());
                    manageDBSolvers.exportSolverCode(solverTableModel.getSolver(tableSolver.convertRowIndexToModel(i)), exportFileChooser.getSelectedFile());
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this,
                            "An error occured while exporting solver \"" + solverTableModel.getSolver(tableSolver.convertRowIndexToModel(i)).getName() + "\": " + ex.getMessage(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        }

    }//GEN-LAST:event_btnExport

    private void parameterChangedOnKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_parameterChangedOnKeyReleased
        parameterChanged();
    }//GEN-LAST:event_parameterChangedOnKeyReleased

    private void chkHasNoValueStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_chkHasNoValueStateChanged
        parameterChanged();
    }//GEN-LAST:event_chkHasNoValueStateChanged

    private void tableInstancesMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tableInstancesMouseClicked

    }//GEN-LAST:event_tableInstancesMouseClicked

    private void btnRemoveInstanceClassActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRemoveInstanceClassActionPerformed
          if(tableInstanceClass.getSelectedRows().length == 0){
             JOptionPane.showMessageDialog(panelManageDBInstances,
                "No instances selected.",
                "Warning",
                JOptionPane.WARNING_MESSAGE);
          } else{
                try {
                    int tmp = tableInstanceClass.getRowCount();
                    int select = tableInstanceClass.getSelectedRows()[0] - 1;
                    manageDBInstances.RemoveInstanceClass(tableInstanceClass.getSelectedRows());
                    instanceClassTableModel.fireTableDataChanged();
                    if(tableInstanceClass.getRowCount() != 0 && select >= 0 && tmp != tableInstanceClass.getRowCount()){
                        this.tableInstanceClass.addRowSelectionInterval(select, select);
                    }else
                        this.tableInstanceClass.addRowSelectionInterval(select + 1, select + 1);
                } catch (SQLException ex){
                    Logger.getLogger(EDACCManageDBMode.class.getName()).log(Level.SEVERE, null, ex);
                } catch (InstanceSourceClassHasInstance ex) {
                     JOptionPane.showMessageDialog(panelManageDBInstances,
                        "The selected instance class cannot be removed. Because it is a source class with" +
                        " related instances.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                }
          }
    }//GEN-LAST:event_btnRemoveInstanceClassActionPerformed

    private void tableInstanceClassMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tableInstanceClassMouseClicked
        if ((evt.getClickCount() == 2)&&(evt.getButton()==java.awt.event.MouseEvent.BUTTON1)) {
            this.btnEditInstanceClassActionPerformed(null);
//            if (!(Boolean)instanceClassTableModel.getValueAt(this.tableInstanceClass.getSelectedRow(), 3))
//            instanceClassTableModel.setInstanceClassSelected(this.tableInstanceClass.getSelectedRow());
//            else
//                instanceClassTableModel.setValueAt(false,this.tableInstanceClass.getSelectedRow() , 3);
        }
    }//GEN-LAST:event_tableInstanceClassMouseClicked

    private void tableInstancesMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tableInstancesMousePressed

    }//GEN-LAST:event_tableInstancesMousePressed

    private void btnAddInstances1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddInstances1ActionPerformed
        if(this.tableInstances.getSelectedRowCount() == 0){
                JOptionPane.showMessageDialog(panelManageDBInstances,
                "No instances selected.",
                "Warning",
                JOptionPane.WARNING_MESSAGE);
                return;
            }
            this.manageDBInstances.showInstanceInfoDialog(this.tableInstances.getSelectedRows());
    }//GEN-LAST:event_btnAddInstances1ActionPerformed

    private void parameterChanged() {
        final int selectedRow = tableParameters.getSelectedRow();

        if (selectedRow == -1) {
            return;
        }
        Parameter p = parameterTableModel.getParameter(selectedRow);
        p.setName(tfParametersName.getText());


        try {
            p.setOrder(Integer.parseInt(tfParametersOrder.getText()));
        } catch (NumberFormatException e) {
            if (!tfParametersOrder.getText().equals("")) {
                tfParametersOrder.setText(Integer.toString(p.getOrder()));
            }
        }
        p.setPrefix(tfParametersPrefix.getText());
        p.setHasValue(!chkHasNoValue.isSelected());
        tableParameters.updateUI();
        unsavedChanges = true;
        // show error message if necessary
        tfParametersName.getInputVerifier().shouldYieldFocus(tfParametersName);
    }

    public void showSolverDetails(Solver currentSolver) {
        boolean enabled = false;


        if (currentSolver != null) {
            enabled = true;
            tfSolverName.setText(currentSolver.getName());
            taSolverDescription.setText(currentSolver.getDescription());
            tfSolverAuthors.setText(currentSolver.getAuthors());
            tfSolverVersion.setText(currentSolver.getVersion());
            manageDBParameters.setCurrentSolver(currentSolver);
            tableParameters.updateUI();
        } else {
            tfSolverName.setText("");
            taSolverDescription.setText("");
            tfSolverAuthors.setText("");
            tfSolverVersion.setText("");
            manageDBParameters.setCurrentSolver(currentSolver);
            tableParameters.updateUI();
        }
        jlSolverName.setEnabled(enabled);
        jlSolverDescription.setEnabled(enabled);
        jlSolverAuthors.setEnabled(enabled);
        jlSolverVersion.setEnabled(enabled);
        jlSolverBinary.setEnabled(enabled);
        jlSolverCode.setEnabled(enabled);
        tfSolverName.setEnabled(enabled);
        taSolverDescription.setEnabled(enabled);
        tfSolverAuthors.setEnabled(enabled);
        tfSolverVersion.setEnabled(enabled);
        btnSolverAddBinary.setEnabled(enabled);
        btnSolverAddCode.setEnabled(enabled);
        btnSolverExport.setEnabled(enabled);


        if (currentSolver != null) {
            parameterTableModel.setCurrentSolver(currentSolver);
            parameterTableModel.fireTableDataChanged();
        }
        btnParametersNew.setEnabled(enabled);
        btnParametersDelete.setEnabled(enabled);
        tableParameters.getSelectionModel().clearSelection();
        showParameterDetails(
                null);
    }

    public void showParameterDetails(Parameter currentParameter) {
        boolean enabled = false;

        if (currentParameter != null) {
            enabled = true;
            tfParametersName.setText(currentParameter.getName());
            tfParametersOrder.setText(Integer.toString(currentParameter.getOrder()));
            tfParametersPrefix.setText(currentParameter.getPrefix());
            chkHasNoValue.setSelected(!currentParameter.getHasValue());
            tfParametersName.getInputVerifier().shouldYieldFocus(tfParametersName);
        } else {
            tfParametersName.setText("");
            tfParametersOrder.setText("");
            tfParametersPrefix.setText("");
            chkHasNoValue.setSelected(false);
            showInvalidParameterNameError(false);
            showInvalidParameterNameError(false);
        }
        tfParametersName.setEnabled(enabled);
        tfParametersPrefix.setEnabled(enabled);
        tfParametersOrder.setEnabled(enabled);
        chkHasNoValue.setEnabled(enabled);
    }

    @Action
    public void btnSaveParam() {
        if (tableParameters.getSelectedRow() == -1) {
            return;
        }
        Parameter p = parameterTableModel.getParameter(tableParameters.getSelectedRow());
        p.setName(tfParametersName.getText());
        p.setOrder(Integer.parseInt(tfParametersOrder.getText()));
        p.setPrefix(tfParametersPrefix.getText());
        parameterTableModel.fireTableDataChanged();
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAddInstances;
    private javax.swing.JButton btnAddInstances1;
    private javax.swing.JButton btnAddToClass;
    private javax.swing.JButton btnEditInstanceClass;
    private javax.swing.JButton btnExportInstances;
    private javax.swing.JButton btnFilterInstances;
    private javax.swing.JButton btnNewInstanceClass;
    private javax.swing.JButton btnParametersDelete;
    private javax.swing.JButton btnParametersNew;
    private javax.swing.JButton btnRemoveFromClass;
    private javax.swing.JButton btnRemoveInstanceClass;
    private javax.swing.JButton btnRemoveInstances;
    private javax.swing.JButton btnSelectAllInstanceClasses;
    private javax.swing.JButton btnSolverAddBinary;
    private javax.swing.JButton btnSolverAddCode;
    private javax.swing.JButton btnSolverDelete;
    private javax.swing.JButton btnSolverExport;
    private javax.swing.JButton btnSolverNew;
    private javax.swing.JButton btnSolverRefresh;
    private javax.swing.JButton btnSolverSaveToDB;
    private javax.swing.JCheckBox chkHasNoValue;
    private javax.swing.JFileChooser jFileChooserManageDBExportInstance;
    private javax.swing.JFileChooser jFileChooserManageDBInstance;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JSplitPane jSplitPane2;
    private javax.swing.JLabel jlParametersName;
    private javax.swing.JLabel jlParametersOrder;
    private javax.swing.JLabel jlParametersPrefix;
    private javax.swing.JLabel jlSolverAuthors;
    private javax.swing.JLabel jlSolverBinary;
    private javax.swing.JLabel jlSolverCode;
    private javax.swing.JLabel jlSolverDescription;
    private javax.swing.JLabel jlSolverName;
    private javax.swing.JLabel jlSolverVersion;
    private javax.swing.JLabel lblFilterStatus;
    private javax.swing.JTabbedPane manageDBPane;
    private javax.swing.JPanel panelButtonsInstanceClass;
    private javax.swing.JPanel panelButtonsInstances;
    private javax.swing.JPanel panelInstance;
    private javax.swing.JPanel panelInstanceClass;
    private javax.swing.JScrollPane panelInstanceClassTable;
    private javax.swing.JScrollPane panelInstanceTable;
    private javax.swing.JPanel panelManageDBInstances;
    private javax.swing.JPanel panelManageDBSolver;
    private javax.swing.JPanel panelParameters;
    private javax.swing.JPanel panelParametersButons;
    private javax.swing.JPanel panelParametersOverall;
    private javax.swing.JPanel panelSolver;
    private javax.swing.JPanel panelSolverButtons;
    private javax.swing.JPanel panelSolverOverall;
    private javax.swing.JTextArea taSolverDescription;
    private javax.swing.JTable tableInstanceClass;
    private javax.swing.JTable tableInstances;
    private javax.swing.JTable tableParameters;
    private javax.swing.JTable tableSolver;
    private javax.swing.JTextField tfParametersName;
    private javax.swing.JTextField tfParametersOrder;
    private javax.swing.JTextField tfParametersPrefix;
    private javax.swing.JTextField tfSolverAuthors;
    private javax.swing.JTextField tfSolverName;
    private javax.swing.JTextField tfSolverVersion;
    // End of variables declaration//GEN-END:variables

    public void onTaskStart(String methodName) {
    }

    public void onTaskFailed(String methodName, Throwable e) {

        if (methodName.equals("exportInstances")) {
            if (e instanceof IOException) {
                JOptionPane.showMessageDialog(panelManageDBInstances,
                        "The instances couldn't be written: " + e.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            } else if (e instanceof NoConnectionToDBException) {
                JOptionPane.showMessageDialog(panelManageDBInstances,
                        "No connection to database: " + e.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            } else if (e instanceof SQLException) {
                JOptionPane.showMessageDialog(panelManageDBInstances,
                        "SQL-Exception: " + e.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            } else if (e instanceof InstanceNotInDBException) {
                JOptionPane.showMessageDialog(panelManageDBInstances,
                        "There is a problem with the data consistency ",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            } else if (e instanceof MD5CheckFailedException) {
                JOptionPane.showMessageDialog(panelManageDBInstances,
                        e,
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            } else if (e instanceof NoSuchAlgorithmException) {
                JOptionPane.showMessageDialog(panelManageDBInstances,
                        "An error occured while exporting solver binary: " + e.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }


        } else if (methodName.equals("addInstances")) {
            if (e instanceof NoConnectionToDBException) {
                JOptionPane.showMessageDialog(panelManageDBInstances,
                        "No connection to database: " + e.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            } else if (e instanceof SQLException) {
                JOptionPane.showMessageDialog(panelManageDBInstances,
                        "SQL-Exception: " + e.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public void onTaskSuccessful(String methodName, Object result) {
        if (methodName.equals("addInstances")) {
            this.instanceTableModel.fireTableDataChanged();
            this.instanceClassTableModel.fireTableDataChanged();
        } else if (methodName.equals("exportInstancnes")) {
        } else if (methodName.equals("TryToRemoveInstances")) {
            this.instanceTableModel.fireTableDataChanged();
            this.instanceClassTableModel.fireTableDataChanged();
        }


    }

    public void setFilterStatus(String status) {

        lblFilterStatus.setForeground(Color.red);
        lblFilterStatus.setText(status);
        lblFilterStatus.setIcon(new ImageIcon("warning-icon.png"));
        lblFilterStatus.updateUI();
    }

    public void showInstanceClassButtons(boolean enable) {
        btnEditInstanceClass.setEnabled(enable);
        btnRemoveInstanceClass.setEnabled(enable);
    }

    public void showInstanceButtons(boolean enable) {
        btnRemoveInstances.setEnabled(enable);
        btnAddToClass.setEnabled(enable);
        btnRemoveFromClass.setEnabled(enable);
        btnExportInstances.setEnabled(enable);
    }

    /**
     * Verifies the input of the Parameter name TextField.
     */
    class ParameterNameVerifier extends InputVerifier {

        @Override
        public boolean verify(JComponent input) {
            String text = ((JTextField) input).getText();
            try {
                return !text.equals("") && !manageDBParameters.parameterExists(text);
            } catch (Exception ex) {
                return false;
            }
        }

        @Override
        public boolean shouldYieldFocus(javax.swing.JComponent input) {
            boolean valid = verify(input);
            showInvalidParameterNameError(!valid);
            return valid;
        }
    }

    private void showInvalidParameterNameError(boolean show) {
        if (show) {
            // set the color of the TextField to a nice red
            tfParametersName.setBackground(new Color(255, 102, 102));
        } else {
            tfParametersName.setBackground(Color.white);
        }
    }
}


