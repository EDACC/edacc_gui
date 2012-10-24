/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * EDACCVerifiersCreateEditDialog.java
 *
 * Created on Mar 13, 2012, 6:23:02 PM
 */
package edacc;

import edacc.manageDB.FileInputStreamList;
import edacc.manageDB.Util;
import edacc.manageDB.VerifierParameterTableModel;
import edacc.model.Verifier;
import edacc.model.VerifierParameter;
import java.awt.Color;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.SequenceInputStream;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import javax.swing.InputVerifier;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 *
 * @author simon
 */
public class EDACCVerifiersCreateEditDialog extends javax.swing.JDialog {

    private List<Verifier> otherVerifiers;
    private Verifier verifier;
    private VerifierParameterTableModel model;
    private VerifierParameter currentParameter;
    private File[] files;
    private boolean cancelled;

    /** Creates new form EDACCVerifiersCreateEditDialog */
    public EDACCVerifiersCreateEditDialog(java.awt.Frame parent, boolean modal, List<Verifier> otherVerifiers) {
        super(parent, modal);
        initComponents();

        this.setTitle("Create Verifier");
        this.verifier = new Verifier();
        this.currentParameter = null;
        model = new VerifierParameterTableModel();
        model.setVerifier(verifier);
        tableParameters.setModel(model);
        tableParameters.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (e.getSource() == tableParameters.getSelectionModel() && tableParameters.getRowSelectionAllowed()) {
                    int row = tableParameters.getSelectedRow();
                    if (row < 0 || row >= tableParameters.getRowCount()) {
                        return;
                    }
                    row = tableParameters.convertRowIndexToModel(tableParameters.getSelectedRow());
                    showParameter(row);
                }
            }
        });
        cancelled = true;
        this.otherVerifiers = otherVerifiers;
    }

    public EDACCVerifiersCreateEditDialog(java.awt.Frame parent, boolean modal, List<Verifier> otherVerifiers, Verifier verifier) {
        this(parent, modal, otherVerifiers);

        this.setTitle("Edit Verifier");
        this.verifier = verifier;
        txtName.setText(verifier.getName());
        txtDescription.setText(verifier.getDescription());
        txtRunCommand.setText(verifier.getRunCommand());
        txtRunPath.setText(verifier.getRunPath());
        lblMd5.setText(verifier.getMd5());

        model.setVerifier(verifier);
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public Verifier getVerifier() {
        return verifier;
    }

    private void showInvalidParameterNameError(boolean show) {
        if (show) {
            // set the color of the TextField to a nice red
            txtParametersName.setBackground(new Color(255, 102, 102));
        } else {
            txtParametersName.setBackground(Color.white);
        }
    }

    private void showParameterDetails(VerifierParameter currentParameter) {
        boolean enabled = false;

        if (currentParameter != null) {
            enabled = true;
            txtParametersName.setText(currentParameter.getName());
            txtParametersOrder.setText(Integer.toString(currentParameter.getOrder()));
            txtParametersPrefix.setText(currentParameter.getPrefix());
            txtParametersDefaultValue.setText(currentParameter.getDefaultValue());
            chkHasNoValue.setSelected(!currentParameter.getHasValue());
            chkMandatory.setSelected(currentParameter.isMandatory());
            chkSpace.setSelected(currentParameter.getSpace());
            chkAttachToPrevious.setSelected(currentParameter.isAttachToPrevious());
            txtParametersName.getInputVerifier().shouldYieldFocus(txtParametersName);
        } else {
            txtParametersName.setText("");
            txtParametersOrder.setText("");
            txtParametersPrefix.setText("");
            txtParametersDefaultValue.setText("");
            chkHasNoValue.setSelected(false);
            chkMandatory.setSelected(false);
            chkSpace.setSelected(false);
            chkAttachToPrevious.setSelected(false);
            showInvalidParameterNameError(false);
        }
        txtParametersName.setEnabled(enabled);
        txtParametersPrefix.setEnabled(enabled);
        txtParametersOrder.setEnabled(enabled);
        txtParametersDefaultValue.setEnabled(enabled);
        chkHasNoValue.setEnabled(enabled);
        chkMandatory.setEnabled(enabled);
        chkSpace.setEnabled(enabled);
        chkAttachToPrevious.setEnabled(enabled);
    }

    private void showParameter(int index) {
        currentParameter = model.getParameter(index);
        if (currentParameter != null) {
            showParameterDetails(currentParameter);
        }
    }

    private void parameterChanged() {
        int selectedRow = tableParameters.getSelectedRow();

        if (selectedRow == -1) {
            return;
        }
        if (tableParameters.getSelectedRowCount() > 1) {
            return;
        }
        selectedRow = tableParameters.convertRowIndexToModel(selectedRow);
        VerifierParameter p = model.getParameter(selectedRow);

        String name = txtParametersName.getText();
        String order = txtParametersOrder.getText();
        String defaultValue = txtParametersDefaultValue.getText();
        String prefix = txtParametersPrefix.getText();
        boolean hasValue = !chkHasNoValue.isSelected();
        boolean mandatory = chkMandatory.isSelected();
        boolean space = chkSpace.isSelected();
        boolean attachToPrevious = chkAttachToPrevious.isSelected();

        if (p.getName() != null && p.getName().equals(name)
                && order != null && order.equals(Integer.toString(p.getOrder()))
                && p.getDefaultValue() != null && p.getDefaultValue().equals(defaultValue)
                && p.getPrefix() != null && p.getPrefix().equals(prefix)
                && p.getHasValue() == hasValue
                && p.isMandatory() == mandatory
                && p.getSpace() == space
                && p.isAttachToPrevious() == attachToPrevious) {
            return;
        }

        p.setName(name);
        try {
            p.setOrder(Integer.parseInt(order));
        } catch (NumberFormatException e) {
            if (!order.equals("")) {
                txtParametersOrder.setText(Integer.toString(p.getOrder()));
            }
        }
        p.setDefaultValue(defaultValue);
        p.setPrefix(prefix);
        p.setHasValue(hasValue);
        p.setMandatory(mandatory);
        p.setSpace(space);
        p.setAttachToPrevious(attachToPrevious);
        model.fireTableRowsUpdated(selectedRow, selectedRow);
        // show error message if necessary
        txtParametersName.getInputVerifier().shouldYieldFocus(txtParametersName);
    }

    private boolean parameterExists(String name) {
        for (VerifierParameter p : model.getParameters()) {
            if (p != currentParameter && p.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }

    private void newParam() {
        VerifierParameter p = new VerifierParameter();
        p.setOrder(model.getHighestOrder() + 1);
        model.addParameter(p);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panelParameters = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tableParameters = new javax.swing.JTable();
        jPanel2 = new javax.swing.JPanel();
        jlParametersName = new javax.swing.JLabel();
        txtParametersName = new javax.swing.JTextField();
        jlParametersPrefix = new javax.swing.JLabel();
        txtParametersPrefix = new javax.swing.JTextField();
        jlParametersOrder = new javax.swing.JLabel();
        txtParametersOrder = new javax.swing.JTextField();
        chkHasNoValue = new javax.swing.JCheckBox();
        chkMandatory = new javax.swing.JCheckBox();
        chkSpace = new javax.swing.JCheckBox();
        jlParametersDefaultValue = new javax.swing.JLabel();
        txtParametersDefaultValue = new javax.swing.JTextField();
        chkAttachToPrevious = new javax.swing.JCheckBox();
        panelParametersButons = new javax.swing.JPanel();
        btnAdd = new javax.swing.JButton();
        btnAccept = new javax.swing.JButton();
        btnDismiss = new javax.swing.JButton();
        btnRemove = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        txtName = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        txtDescription = new javax.swing.JTextArea();
        jPanel3 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        txtRunCommand = new javax.swing.JTextField();
        txtRunPath = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        lblMd5 = new javax.swing.JLabel();
        btnChangeBinary = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setName("Form"); // NOI18N

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(edacc.EDACCApp.class).getContext().getResourceMap(EDACCVerifiersCreateEditDialog.class);
        panelParameters.setBorder(javax.swing.BorderFactory.createTitledBorder(resourceMap.getString("panelParameters.border.title"))); // NOI18N
        panelParameters.setName("panelParameters"); // NOI18N

        jScrollPane1.setMinimumSize(new java.awt.Dimension(0, 0));
        jScrollPane1.setName("jScrollPane1"); // NOI18N
        jScrollPane1.setPreferredSize(new java.awt.Dimension(0, 0));

        tableParameters.setAutoCreateRowSorter(true);
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

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        jPanel2.setName("jPanel2"); // NOI18N

        jlParametersName.setText(resourceMap.getString("jlParametersName.text")); // NOI18N
        jlParametersName.setName("jlParametersName"); // NOI18N

        txtParametersName.setToolTipText(resourceMap.getString("txtParametersName.toolTipText")); // NOI18N
        txtParametersName.setInputVerifier(new ParameterNameVerifier());
        txtParametersName.setName("txtParametersName"); // NOI18N
        txtParametersName.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtParametersNameparameterChangedOnFocusLost(evt);
            }
        });
        txtParametersName.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtParametersNameparameterChangedOnKeyReleased(evt);
            }
        });

        jlParametersPrefix.setText(resourceMap.getString("jlParametersPrefix.text")); // NOI18N
        jlParametersPrefix.setName("jlParametersPrefix"); // NOI18N

        txtParametersPrefix.setToolTipText(resourceMap.getString("txtParametersPrefix.toolTipText")); // NOI18N
        txtParametersPrefix.setName("txtParametersPrefix"); // NOI18N
        txtParametersPrefix.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtParametersPrefixparameterChangedOnFocusLost(evt);
            }
        });
        txtParametersPrefix.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtParametersPrefixparameterChangedOnKeyReleased(evt);
            }
        });

        jlParametersOrder.setText(resourceMap.getString("jlParametersOrder.text")); // NOI18N
        jlParametersOrder.setName("jlParametersOrder"); // NOI18N

        txtParametersOrder.setToolTipText(resourceMap.getString("txtParametersOrder.toolTipText")); // NOI18N
        txtParametersOrder.setName("txtParametersOrder"); // NOI18N
        txtParametersOrder.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtParametersOrderparameterChangedOnFocusLost(evt);
            }
        });
        txtParametersOrder.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtParametersOrderparameterChangedOnKeyReleased(evt);
            }
        });

        chkHasNoValue.setText(resourceMap.getString("chkHasNoValue.text")); // NOI18N
        chkHasNoValue.setToolTipText(resourceMap.getString("chkHasNoValue.toolTipText")); // NOI18N
        chkHasNoValue.setName("chkHasNoValue"); // NOI18N
        chkHasNoValue.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkHasNoValueActionPerformed(evt);
            }
        });

        chkMandatory.setText(resourceMap.getString("chkMandatory.text")); // NOI18N
        chkMandatory.setToolTipText(resourceMap.getString("chkMandatory.toolTipText")); // NOI18N
        chkMandatory.setName("chkMandatory"); // NOI18N
        chkMandatory.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkMandatoryActionPerformed(evt);
            }
        });

        chkSpace.setText(resourceMap.getString("chkSpace.text")); // NOI18N
        chkSpace.setToolTipText(resourceMap.getString("chkSpace.toolTipText")); // NOI18N
        chkSpace.setName("chkSpace"); // NOI18N
        chkSpace.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkSpaceActionPerformed(evt);
            }
        });

        jlParametersDefaultValue.setText(resourceMap.getString("jlParametersDefaultValue.text")); // NOI18N
        jlParametersDefaultValue.setName("jlParametersDefaultValue"); // NOI18N

        txtParametersDefaultValue.setName("txtParametersDefaultValue"); // NOI18N
        txtParametersDefaultValue.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtParametersDefaultValueparameterChangedOnFocusLost(evt);
            }
        });
        txtParametersDefaultValue.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtParametersDefaultValueparameterChangedOnKeyReleased(evt);
            }
        });

        chkAttachToPrevious.setText(resourceMap.getString("chkAttachToPrevious.text")); // NOI18N
        chkAttachToPrevious.setToolTipText(resourceMap.getString("chkAttachToPrevious.toolTipText")); // NOI18N
        chkAttachToPrevious.setName("chkAttachToPrevious"); // NOI18N
        chkAttachToPrevious.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkAttachToPreviousActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(chkHasNoValue)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(chkMandatory)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(chkSpace)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(chkAttachToPrevious))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jlParametersDefaultValue)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED))
                            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jlParametersName, javax.swing.GroupLayout.DEFAULT_SIZE, 85, Short.MAX_VALUE)
                                .addComponent(jlParametersPrefix, javax.swing.GroupLayout.DEFAULT_SIZE, 85, Short.MAX_VALUE)
                                .addComponent(jlParametersOrder, javax.swing.GroupLayout.DEFAULT_SIZE, 85, Short.MAX_VALUE)))
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtParametersName, javax.swing.GroupLayout.DEFAULT_SIZE, 509, Short.MAX_VALUE)
                            .addComponent(txtParametersPrefix, javax.swing.GroupLayout.DEFAULT_SIZE, 509, Short.MAX_VALUE)
                            .addComponent(txtParametersDefaultValue, javax.swing.GroupLayout.DEFAULT_SIZE, 509, Short.MAX_VALUE)
                            .addComponent(txtParametersOrder, javax.swing.GroupLayout.DEFAULT_SIZE, 509, Short.MAX_VALUE))))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jlParametersName)
                    .addComponent(txtParametersName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jlParametersPrefix, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtParametersPrefix, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jlParametersOrder)
                    .addComponent(txtParametersOrder, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jlParametersDefaultValue)
                    .addComponent(txtParametersDefaultValue, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(chkHasNoValue)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(chkSpace)
                            .addComponent(chkAttachToPrevious))
                        .addComponent(chkMandatory))))
        );

        panelParametersButons.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        panelParametersButons.setName("panelParametersButons"); // NOI18N

        btnAdd.setText(resourceMap.getString("btnAdd.text")); // NOI18N
        btnAdd.setName("btnAdd"); // NOI18N
        btnAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddActionPerformed(evt);
            }
        });

        btnAccept.setText(resourceMap.getString("btnAccept.text")); // NOI18N
        btnAccept.setName("btnAccept"); // NOI18N
        btnAccept.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAcceptActionPerformed(evt);
            }
        });

        btnDismiss.setText(resourceMap.getString("btnDismiss.text")); // NOI18N
        btnDismiss.setName("btnDismiss"); // NOI18N
        btnDismiss.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDismissActionPerformed(evt);
            }
        });

        btnRemove.setText(resourceMap.getString("btnRemove.text")); // NOI18N
        btnRemove.setName("btnRemove"); // NOI18N
        btnRemove.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRemoveActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelParametersButonsLayout = new javax.swing.GroupLayout(panelParametersButons);
        panelParametersButons.setLayout(panelParametersButonsLayout);
        panelParametersButonsLayout.setHorizontalGroup(
            panelParametersButonsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelParametersButonsLayout.createSequentialGroup()
                .addComponent(btnAdd)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnRemove)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 318, Short.MAX_VALUE)
                .addComponent(btnDismiss)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnAccept))
        );

        panelParametersButonsLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {btnAccept, btnAdd, btnDismiss, btnRemove});

        panelParametersButonsLayout.setVerticalGroup(
            panelParametersButonsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelParametersButonsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(btnAdd)
                .addComponent(btnAccept)
                .addComponent(btnDismiss)
                .addComponent(btnRemove))
        );

        javax.swing.GroupLayout panelParametersLayout = new javax.swing.GroupLayout(panelParameters);
        panelParameters.setLayout(panelParametersLayout);
        panelParametersLayout.setHorizontalGroup(
            panelParametersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(panelParametersButons, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 626, Short.MAX_VALUE)
        );
        panelParametersLayout.setVerticalGroup(
            panelParametersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelParametersLayout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 178, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelParametersButons, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(resourceMap.getString("jPanel1.border.title"))); // NOI18N
        jPanel1.setName("jPanel1"); // NOI18N

        jLabel3.setText(resourceMap.getString("jLabel3.text")); // NOI18N
        jLabel3.setName("jLabel3"); // NOI18N

        txtName.setText(resourceMap.getString("txtName.text")); // NOI18N
        txtName.setName("txtName"); // NOI18N

        jLabel4.setText(resourceMap.getString("jLabel4.text")); // NOI18N
        jLabel4.setName("jLabel4"); // NOI18N

        jScrollPane2.setName("jScrollPane2"); // NOI18N

        txtDescription.setColumns(20);
        txtDescription.setRows(5);
        txtDescription.setName("txtDescription"); // NOI18N
        jScrollPane2.setViewportView(txtDescription);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4)
                    .addComponent(jLabel3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtName, javax.swing.GroupLayout.DEFAULT_SIZE, 539, Short.MAX_VALUE)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 539, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(txtName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addGap(82, 82, 82))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 85, Short.MAX_VALUE)
                        .addContainerGap())))
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(resourceMap.getString("jPanel3.border.title"))); // NOI18N
        jPanel3.setName("jPanel3"); // NOI18N

        jLabel5.setText(resourceMap.getString("jLabel5.text")); // NOI18N
        jLabel5.setName("jLabel5"); // NOI18N

        jLabel6.setText(resourceMap.getString("jLabel6.text")); // NOI18N
        jLabel6.setName("jLabel6"); // NOI18N

        txtRunCommand.setText(resourceMap.getString("txtRunCommand.text")); // NOI18N
        txtRunCommand.setName("txtRunCommand"); // NOI18N

        txtRunPath.setText(resourceMap.getString("txtRunPath.text")); // NOI18N
        txtRunPath.setName("txtRunPath"); // NOI18N

        jLabel7.setText(resourceMap.getString("jLabel7.text")); // NOI18N
        jLabel7.setName("jLabel7"); // NOI18N

        lblMd5.setText(resourceMap.getString("lblMd5.text")); // NOI18N
        lblMd5.setName("lblMd5"); // NOI18N

        btnChangeBinary.setText(resourceMap.getString("btnChangeBinary.text")); // NOI18N
        btnChangeBinary.setName("btnChangeBinary"); // NOI18N
        btnChangeBinary.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnChangeBinaryActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel5)
                            .addComponent(jLabel6)
                            .addComponent(jLabel7))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblMd5)
                            .addComponent(txtRunPath, javax.swing.GroupLayout.DEFAULT_SIZE, 527, Short.MAX_VALUE)
                            .addComponent(txtRunCommand, javax.swing.GroupLayout.DEFAULT_SIZE, 527, Short.MAX_VALUE)))
                    .addComponent(btnChangeBinary, javax.swing.GroupLayout.Alignment.TRAILING))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(txtRunCommand, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(txtRunPath, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(lblMd5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnChangeBinary))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(panelParameters, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelParameters, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void txtParametersNameparameterChangedOnFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtParametersNameparameterChangedOnFocusLost
        parameterChanged();
	}//GEN-LAST:event_txtParametersNameparameterChangedOnFocusLost

    private void txtParametersNameparameterChangedOnKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtParametersNameparameterChangedOnKeyReleased
        parameterChanged();
	}//GEN-LAST:event_txtParametersNameparameterChangedOnKeyReleased

    private void txtParametersPrefixparameterChangedOnFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtParametersPrefixparameterChangedOnFocusLost
        parameterChanged();
	}//GEN-LAST:event_txtParametersPrefixparameterChangedOnFocusLost

    private void txtParametersPrefixparameterChangedOnKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtParametersPrefixparameterChangedOnKeyReleased
        parameterChanged();
	}//GEN-LAST:event_txtParametersPrefixparameterChangedOnKeyReleased

    private void txtParametersOrderparameterChangedOnFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtParametersOrderparameterChangedOnFocusLost
        parameterChanged();
	}//GEN-LAST:event_txtParametersOrderparameterChangedOnFocusLost

    private void txtParametersOrderparameterChangedOnKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtParametersOrderparameterChangedOnKeyReleased
        parameterChanged();
	}//GEN-LAST:event_txtParametersOrderparameterChangedOnKeyReleased

    private void chkHasNoValueActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkHasNoValueActionPerformed
        parameterChanged();
	}//GEN-LAST:event_chkHasNoValueActionPerformed

    private void chkMandatoryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkMandatoryActionPerformed
        parameterChanged();
	}//GEN-LAST:event_chkMandatoryActionPerformed

    private void chkSpaceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkSpaceActionPerformed
        parameterChanged();
	}//GEN-LAST:event_chkSpaceActionPerformed

    private void txtParametersDefaultValueparameterChangedOnFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtParametersDefaultValueparameterChangedOnFocusLost
        parameterChanged();
	}//GEN-LAST:event_txtParametersDefaultValueparameterChangedOnFocusLost

    private void txtParametersDefaultValueparameterChangedOnKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtParametersDefaultValueparameterChangedOnKeyReleased
        parameterChanged();
	}//GEN-LAST:event_txtParametersDefaultValueparameterChangedOnKeyReleased

    private void chkAttachToPreviousActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkAttachToPreviousActionPerformed
        parameterChanged();
	}//GEN-LAST:event_chkAttachToPreviousActionPerformed

    private void btnAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddActionPerformed
        newParam();
        int selIndex = tableParameters.getRowCount() - 1;
        tableParameters.getSelectionModel().setSelectionInterval(selIndex, selIndex);
        //   tableParameters.updateUI();
        txtParametersName.requestFocus();
    }//GEN-LAST:event_btnAddActionPerformed

    private void btnRemoveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRemoveActionPerformed
        if (tableParameters.getSelectedRow() == -1) {
            JOptionPane.showMessageDialog(this,
                    "No parameter selected!",
                    "Warning",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        int selectedIndex = tableParameters.getSelectedRow();
        VerifierParameter p = model.getParameter(tableParameters.convertRowIndexToModel(selectedIndex));
        model.remove(p);
        currentParameter = null;

        // try select the parameter which stood on row over the deleted param
        if (selectedIndex >= tableParameters.getRowCount()) {
            selectedIndex--;
        }
        VerifierParameter selected = null;
        tableParameters.clearSelection();
        if (selectedIndex >= 0) {
            selected = model.getParameter(tableParameters.convertRowIndexToModel(selectedIndex));
            tableParameters.getSelectionModel().setSelectionInterval(selectedIndex, selectedIndex);
        }
        showParameterDetails(selected);
    }//GEN-LAST:event_btnRemoveActionPerformed

    private void btnAcceptActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAcceptActionPerformed
        Set<String> parameterNames = new HashSet<String>();
        for (VerifierParameter p : model.getParameters()) {
            if (parameterNames.contains(p.getName())) {
                JOptionPane.showMessageDialog(this, "Error: parameters must have unique names.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            parameterNames.add(p.getName());
        }

        if (verifier.isNew()) {
            if ((files == null || files.length == 0) && (verifier.getFiles() == null || verifier.getFiles().length == 0)) {
                JOptionPane.showMessageDialog(this, "No binary file selected.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (files != null && files.length != 0) {
                verifier.setFiles(files);
                verifier.setMd5(lblMd5.getText());
            }
            verifier.setName(txtName.getText());
            verifier.setDescription(txtDescription.getText());
            verifier.setRunCommand(txtRunCommand.getText());
            verifier.setRunPath(txtRunPath.getText());
            
            verifier.setParameters(model.getParameters());
        } else {
            Map<Integer, VerifierParameter> modelParams = new HashMap<Integer, VerifierParameter>();
            for (VerifierParameter p : model.getParameters()) {
                if (p.isSaved() || p.isModified()) {
                    modelParams.put(p.getId(), p);
                }
            }
            for (VerifierParameter p : verifier.getParameters()) {
                if (modelParams.containsKey(p.getId())) {
                    p.assign(modelParams.get(p.getId()));
                } else {
                    p.setDeleted();
                }
            }
            for (VerifierParameter p : model.getParameters()) {
                if (p.isNew())
                    verifier.getParameters().add(p);
            }
            if (files != null && files.length != 0) {
                verifier.setFiles(files);
                verifier.setMd5(lblMd5.getText());
            }
            verifier.setName(txtName.getText());
            verifier.setDescription(txtDescription.getText());
            verifier.setRunCommand(txtRunCommand.getText());
            verifier.setRunPath(txtRunPath.getText());
        }
        cancelled = false;
        setVisible(false);
    }//GEN-LAST:event_btnAcceptActionPerformed

    private void btnDismissActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDismissActionPerformed
        setVisible(false);
    }//GEN-LAST:event_btnDismissActionPerformed

    private void addBinary(File[] binary) throws NoSuchAlgorithmException, IOException {
        if (binary.length == 0) {
            return;
        }
        Arrays.sort(binary);
        try {
            FileInputStreamList is = new FileInputStreamList(binary);
            SequenceInputStream seq = new SequenceInputStream(is);
            String md5 = Util.calculateMD5(seq);
            boolean hasDuplicates = false;
            for (Verifier v : otherVerifiers) {
                if (md5.equals(v.getMd5())) {
                    hasDuplicates = true;
                    break;
                }
            }
            if (hasDuplicates) {
                if (JOptionPane.showConfirmDialog(this,
                        "There already exists a verifier binary with the same "
                        + "checksum. Do you want to add this binary anyway?",
                        "Duplicate Verifier Binary",
                        JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE)
                        == JOptionPane.NO_OPTION) {
                    return;
                }
            }

            EDACCVerifierBinaryDialog dialog = new EDACCVerifierBinaryDialog(EDACCApp.getApplication().getMainFrame(), true, binary);
            dialog.setName("EDACCVerifierBinaryDialog");
            EDACCApp.getApplication().show(dialog);
            if (!dialog.isCancelled()) {
                files = binary;
                txtRunCommand.setText(dialog.getRunCommand());
                txtRunPath.setText(dialog.getRunPath());
                lblMd5.setText(md5);
            }
        } catch (NoSuchElementException e) {
            JOptionPane.showMessageDialog(this, "You have to choose some files!", "No files chosen!", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void btnChangeBinaryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnChangeBinaryActionPerformed
        JFileChooser binaryFileChooser = null;

        if (binaryFileChooser == null) {
            binaryFileChooser = new JFileChooser();
            binaryFileChooser.setMultiSelectionEnabled(true);
            binaryFileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        }

        if (binaryFileChooser.showDialog(this, "Add Verifier") == JFileChooser.APPROVE_OPTION) {
            try {
                addBinary(binaryFileChooser.getSelectedFiles());
            } catch (FileNotFoundException ex) {
                JOptionPane.showMessageDialog(this,
                        "The binary of the solver couldn't be found: " + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                        "An error occured while adding the binary of the solver: " + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_btnChangeBinaryActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAccept;
    private javax.swing.JButton btnAdd;
    private javax.swing.JButton btnChangeBinary;
    private javax.swing.JButton btnDismiss;
    private javax.swing.JButton btnRemove;
    private javax.swing.JCheckBox chkAttachToPrevious;
    private javax.swing.JCheckBox chkHasNoValue;
    private javax.swing.JCheckBox chkMandatory;
    private javax.swing.JCheckBox chkSpace;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel jlParametersDefaultValue;
    private javax.swing.JLabel jlParametersName;
    private javax.swing.JLabel jlParametersOrder;
    private javax.swing.JLabel jlParametersPrefix;
    private javax.swing.JLabel lblMd5;
    private javax.swing.JPanel panelParameters;
    private javax.swing.JPanel panelParametersButons;
    private javax.swing.JTable tableParameters;
    private javax.swing.JTextArea txtDescription;
    private javax.swing.JTextField txtName;
    private javax.swing.JTextField txtParametersDefaultValue;
    private javax.swing.JTextField txtParametersName;
    private javax.swing.JTextField txtParametersOrder;
    private javax.swing.JTextField txtParametersPrefix;
    private javax.swing.JTextField txtRunCommand;
    private javax.swing.JTextField txtRunPath;
    // End of variables declaration//GEN-END:variables

    /**
     * Verifies the input of the Parameter name TextField.
     */
    class ParameterNameVerifier extends InputVerifier {

        @Override
        public boolean verify(JComponent input) {
            String text = ((JTextField) input).getText();
            try {
                return !text.equals("") && !parameterExists(text);
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
}
