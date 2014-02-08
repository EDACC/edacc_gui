/*
 * EDACCGridSettingsView.java
 *
 * Created on Nov 26, 2009, 7:54:46 PM
 */
package edacc;

import edacc.model.DBEmptyException;
import edacc.model.DBVersionException;
import edacc.model.DBVersionUnknownException;
import edacc.model.DatabaseConnector;
import edacc.model.NoConnectionToDBException;
import edacc.model.TaskRunnable;
import edacc.model.Tasks;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import org.jdesktop.application.ApplicationContext;

/**
 *
 * @author Daniel D.
 */
public class EDACCDatabaseSettingsView extends javax.swing.JDialog {

    private final String connection_settings_filename = "connection_details.xml";

    /** Creates new form EDACCGridSettingsView */
    public EDACCDatabaseSettingsView(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        //btnConnect.requestFocus();
        getRootPane().setDefaultButton(btnConnect);
        try {
            loadDatabaseSettings(null);
            reloadSessionNames();
            txtSessionName.setText("");
        } catch (IOException ex) {
            Logger.getLogger(EDACCDatabaseSettingsView.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void loadDatabaseSettings(String sessionName) throws IOException {
        String ext = "";
        if (sessionName != null) {
            ext = "[" + sessionName + "]";
        }
        ApplicationContext ctxt = EDACCApp.getApplication().getContext();
        Map<String, String> map = (Map<String, String>) ctxt.getLocalStorage().load(connection_settings_filename);
        if (map != null) {
            if (map.containsKey("hostname" + ext)) {
                this.txtHostname.setText(map.get("hostname" + ext));
            }
            if (map.containsKey("database" + ext)) {
                this.txtDatabase.setText(map.get("database" + ext));
            }
            if (map.containsKey("port" + ext)) {
                this.txtPort.setText(map.get("port"));
            }
            if (map.containsKey("username" + ext)) {
                this.txtUsername.setText(map.get("username" + ext));
            }
            if (map.containsKey("max_connections" + ext)) {
                this.txtMaxConnections.setText(map.get("max_connections"));
            }
            if (map.containsKey("secured_connection" + ext)) {
                this.chkUseSSL.setSelected(map.get("secured_connection" + ext).equalsIgnoreCase("true"));
            }
            if (map.containsKey("use_compression" + ext)) {
                this.chkCompress.setSelected(map.get("use_compression" + ext).equalsIgnoreCase("true"));
            }
            if (map.containsKey("save_password" + ext)) {
                this.chkSavePassword.setSelected(map.get("save_password" + ext).equalsIgnoreCase("true"));
            }
            if (this.chkSavePassword.isSelected() && map.containsKey("password" + ext)) {
                this.txtPassword.setText(map.get("password" + ext));
            }
        }
        txtSessionName.setText(sessionName);
    }
    
    private void saveDatabaseSettings(String sessionName) throws IOException {
        String ext = "";
        if (sessionName != null) {
            ext = "[" + sessionName + "]";
        }
        ApplicationContext ctxt = EDACCApp.getApplication().getContext();
        Map<String, String> map = (Map<String, String>) ctxt.getLocalStorage().load(connection_settings_filename);
        map.put("hostname" + ext, txtHostname.getText());
        map.put("database" + ext, txtDatabase.getText());
        map.put("port" + ext, txtPort.getText());
        map.put("username" + ext, txtUsername.getText());
        map.put("max_connections" + ext, txtMaxConnections.getText());
        map.put("secured_connection" + ext, chkUseSSL.isSelected() ? "true" : "false");
        map.put("use_compression" + ext, chkCompress.isSelected() ? "true" : "false");
        map.put("save_password" + ext, chkSavePassword.isSelected() ? "true" : "false");
        if (chkSavePassword.isSelected()) {
            map.put("password" + ext, txtPassword.getText());
        } else {
            map.put("password" + ext, "");
        }
        ctxt.getLocalStorage().save(map, connection_settings_filename);
    }
    
    private void removeSavedDatabaseSession(String sessionName) throws IOException {
        String ext = "";
        if (sessionName != null) {
            ext = "[" + sessionName + "]";
        }
        ApplicationContext ctxt = EDACCApp.getApplication().getContext();
        Map<String, String> map = (Map<String, String>) ctxt.getLocalStorage().load(connection_settings_filename);
        map.remove("hostname" + ext);
        map.remove("database" + ext);
        map.remove("port" + ext);
        map.remove("username" + ext);
        map.remove("max_connections" + ext);
        map.remove("secured_connection" + ext);
        map.remove("use_compression" + ext);
        map.remove("save_password" + ext);
        map.remove("password" + ext);
        ctxt.getLocalStorage().save(map, connection_settings_filename);
    }
    
    private void reloadSessionNames() throws IOException {
        ApplicationContext ctxt = EDACCApp.getApplication().getContext();
        Map<String, String> map = (Map<String, String>) ctxt.getLocalStorage().load(connection_settings_filename);
        List<String> sessions = new LinkedList<String>();
        for (String key : map.keySet()) {
            if (key.startsWith("hostname") && key.length() >= 10) {
                String name = key.substring(9, key.length()-1);
                sessions.add(name);
            }
        }
        Collections.sort(sessions);
        listSavedSessions.setListData(sessions.toArray());
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        btnConnect = new javax.swing.JButton();
        btnCancel = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        txtPassword = new javax.swing.JPasswordField();
        lblPort = new javax.swing.JLabel();
        lblHostname = new javax.swing.JLabel();
        txtHostname = new javax.swing.JTextField();
        lblDatabase = new javax.swing.JLabel();
        txtDatabase = new javax.swing.JTextField();
        lblUsername = new javax.swing.JLabel();
        txtUsername = new javax.swing.JTextField();
        txtPort = new javax.swing.JTextField();
        lblPassword = new javax.swing.JLabel();
        chkUseSSL = new javax.swing.JCheckBox();
        lblPassword1 = new javax.swing.JLabel();
        chkCompress = new javax.swing.JCheckBox();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        txtMaxConnections = new javax.swing.JTextField();
        chkSavePassword = new javax.swing.JCheckBox();
        btnSaveSession = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        txtSessionName = new javax.swing.JTextField();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        listSavedSessions = new javax.swing.JList();
        btnLoadSession = new javax.swing.JButton();
        btnRemoveSession = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(edacc.EDACCApp.class).getContext().getResourceMap(EDACCDatabaseSettingsView.class);
        setTitle(resourceMap.getString("Form.title")); // NOI18N
        setIconImage(null);
        setMinimumSize(new java.awt.Dimension(400, 220));
        setName("Form"); // NOI18N
        setResizable(false);

        btnConnect.setText(resourceMap.getString("btnConnect.text")); // NOI18N
        btnConnect.setToolTipText(resourceMap.getString("btnConnect.toolTipText")); // NOI18N
        btnConnect.setName("btnConnect"); // NOI18N
        btnConnect.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnConnectActionPerformed(evt);
            }
        });
        btnConnect.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                btnConnectKeyPressed(evt);
            }
        });

        btnCancel.setText(resourceMap.getString("btnCancel.text")); // NOI18N
        btnCancel.setToolTipText(resourceMap.getString("btnCancel.toolTipText")); // NOI18N
        btnCancel.setName("btnCancel"); // NOI18N
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

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(resourceMap.getString("jPanel1.border.title"))); // NOI18N
        jPanel1.setName("jPanel1"); // NOI18N

        txtPassword.setText(resourceMap.getString("txtPassword.text")); // NOI18N
        txtPassword.setToolTipText(resourceMap.getString("txtPassword.toolTipText")); // NOI18N
        txtPassword.setName("txtPassword"); // NOI18N
        txtPassword.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtPasswordKeyPressed(evt);
            }
        });

        lblPort.setLabelFor(txtPort);
        lblPort.setText(resourceMap.getString("lblPort.text")); // NOI18N
        lblPort.setMaximumSize(new java.awt.Dimension(100, 17));
        lblPort.setMinimumSize(new java.awt.Dimension(100, 17));
        lblPort.setName("lblPort"); // NOI18N
        lblPort.setPreferredSize(new java.awt.Dimension(100, 17));

        lblHostname.setLabelFor(txtHostname);
        lblHostname.setText(resourceMap.getString("lblHostname.text")); // NOI18N
        lblHostname.setMaximumSize(new java.awt.Dimension(100, 17));
        lblHostname.setMinimumSize(new java.awt.Dimension(100, 17));
        lblHostname.setName("lblHostname"); // NOI18N
        lblHostname.setPreferredSize(new java.awt.Dimension(100, 17));

        txtHostname.setText(resourceMap.getString("txtHostname.text")); // NOI18N
        txtHostname.setToolTipText(resourceMap.getString("txtHostname.toolTipText")); // NOI18N
        txtHostname.setName("txtHostname"); // NOI18N
        txtHostname.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtHostnameKeyPressed(evt);
            }
        });

        lblDatabase.setLabelFor(txtDatabase);
        lblDatabase.setText(resourceMap.getString("lblDatabase.text")); // NOI18N
        lblDatabase.setName("lblDatabase"); // NOI18N
        lblDatabase.setPreferredSize(new java.awt.Dimension(100, 17));

        txtDatabase.setText(resourceMap.getString("txtDatabase.text")); // NOI18N
        txtDatabase.setToolTipText(resourceMap.getString("txtDatabase.toolTipText")); // NOI18N
        txtDatabase.setName("txtDatabase"); // NOI18N
        txtDatabase.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtDatabaseKeyPressed(evt);
            }
        });

        lblUsername.setLabelFor(txtUsername);
        lblUsername.setText(resourceMap.getString("lblUsername.text")); // NOI18N
        lblUsername.setName("lblUsername"); // NOI18N
        lblUsername.setPreferredSize(new java.awt.Dimension(100, 17));

        txtUsername.setText(resourceMap.getString("txtUsername.text")); // NOI18N
        txtUsername.setToolTipText(resourceMap.getString("txtUsername.toolTipText")); // NOI18N
        txtUsername.setName("txtUsername"); // NOI18N
        txtUsername.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtUsernameKeyPressed(evt);
            }
        });

        txtPort.setText(resourceMap.getString("txtPort.text")); // NOI18N
        txtPort.setToolTipText(resourceMap.getString("txtPort.toolTipText")); // NOI18N
        txtPort.setName("txtPort"); // NOI18N
        txtPort.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtPortKeyPressed(evt);
            }
        });

        lblPassword.setLabelFor(txtPassword);
        lblPassword.setText(resourceMap.getString("lblPassword.text")); // NOI18N
        lblPassword.setName("lblPassword"); // NOI18N
        lblPassword.setPreferredSize(new java.awt.Dimension(100, 17));

        chkUseSSL.setText(resourceMap.getString("chkUseSSL.text")); // NOI18N
        chkUseSSL.setName("chkUseSSL"); // NOI18N

        lblPassword1.setLabelFor(txtPassword);
        lblPassword1.setText(resourceMap.getString("lblPassword1.text")); // NOI18N
        lblPassword1.setName("lblPassword1"); // NOI18N
        lblPassword1.setPreferredSize(new java.awt.Dimension(100, 17));

        chkCompress.setText(resourceMap.getString("chkCompress.text")); // NOI18N
        chkCompress.setName("chkCompress"); // NOI18N

        jLabel1.setText(resourceMap.getString("jLabel1.text")); // NOI18N
        jLabel1.setName("jLabel1"); // NOI18N

        jLabel2.setText(resourceMap.getString("jLabel2.text")); // NOI18N
        jLabel2.setName("jLabel2"); // NOI18N

        txtMaxConnections.setText(resourceMap.getString("txtMaxConnections.text")); // NOI18N
        txtMaxConnections.setName("txtMaxConnections"); // NOI18N
        txtMaxConnections.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtMaxConnectionsKeyPressed(evt);
            }
        });

        chkSavePassword.setText(resourceMap.getString("chkSavePassword.text")); // NOI18N
        chkSavePassword.setName("chkSavePassword"); // NOI18N

        btnSaveSession.setText(resourceMap.getString("btnSaveSession.text")); // NOI18N
        btnSaveSession.setName("btnSaveSession"); // NOI18N
        btnSaveSession.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveSessionActionPerformed(evt);
            }
        });

        jLabel3.setText(resourceMap.getString("jLabel3.text")); // NOI18N
        jLabel3.setName("jLabel3"); // NOI18N

        txtSessionName.setText(resourceMap.getString("txtSessionName.text")); // NOI18N
        txtSessionName.setName("txtSessionName"); // NOI18N

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(lblHostname, javax.swing.GroupLayout.DEFAULT_SIZE, 108, Short.MAX_VALUE)
                                .addComponent(lblDatabase, 0, 0, Short.MAX_VALUE)
                                .addComponent(lblUsername, 0, 0, Short.MAX_VALUE)
                                .addComponent(lblPassword, 0, 0, Short.MAX_VALUE))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED))
                        .addGroup(jPanel1Layout.createSequentialGroup()
                            .addComponent(jLabel2)
                            .addGap(26, 26, 26))
                        .addGroup(jPanel1Layout.createSequentialGroup()
                            .addComponent(lblPassword1, 0, 0, Short.MAX_VALUE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED))
                        .addGroup(jPanel1Layout.createSequentialGroup()
                            .addComponent(jLabel1)
                            .addGap(61, 61, 61)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addGap(42, 42, 42)))
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(txtSessionName)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnSaveSession, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(chkSavePassword)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(txtHostname, javax.swing.GroupLayout.PREFERRED_SIZE, 219, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(32, 32, 32)
                                .addComponent(lblPort, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 24, Short.MAX_VALUE)
                                .addComponent(txtPort, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(txtUsername)
                            .addComponent(txtPassword)
                            .addComponent(txtDatabase, javax.swing.GroupLayout.DEFAULT_SIZE, 359, Short.MAX_VALUE)
                            .addComponent(chkCompress)
                            .addComponent(chkUseSSL)
                            .addComponent(txtMaxConnections))))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lblHostname, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtHostname, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblPort, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtPort, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lblDatabase, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtDatabase, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lblUsername, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtUsername, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(6, 6, 6)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lblPassword, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtPassword, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkSavePassword)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(txtMaxConnections, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblPassword1, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkUseSSL))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(chkCompress))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnSaveSession)
                    .addComponent(jLabel3)
                    .addComponent(txtSessionName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel1Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {lblDatabase, lblHostname, lblPassword, lblUsername});

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(resourceMap.getString("jPanel2.border.title"))); // NOI18N
        jPanel2.setName("jPanel2"); // NOI18N

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        listSavedSessions.setName("listSavedSessions"); // NOI18N
        listSavedSessions.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                listSavedSessionsMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(listSavedSessions);

        btnLoadSession.setText(resourceMap.getString("btnLoadSession.text")); // NOI18N
        btnLoadSession.setName("btnLoadSession"); // NOI18N
        btnLoadSession.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLoadSessionActionPerformed(evt);
            }
        });

        btnRemoveSession.setText(resourceMap.getString("btnRemoveSession.text")); // NOI18N
        btnRemoveSession.setName("btnRemoveSession"); // NOI18N
        btnRemoveSession.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRemoveSessionActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(88, Short.MAX_VALUE)
                .addComponent(btnRemoveSession)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnLoadSession, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 240, Short.MAX_VALUE)
        );

        jPanel2Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {btnLoadSession, btnRemoveSession});

        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 219, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnLoadSession)
                    .addComponent(btnRemoveSession)))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(btnCancel, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 575, Short.MAX_VALUE)
                        .addComponent(btnConnect, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 271, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnCancel)
                    .addComponent(btnConnect))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelActionPerformed
        this.setVisible(false);
        this.dispose();
    }//GEN-LAST:event_btnCancelActionPerformed

    private void btnConnectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnConnectActionPerformed
        Tasks.startTask(new TaskRunnable() {

            @Override
            public void run(Tasks task) {
                try {
                    try {
                        saveDatabaseSettings(null);
                    } catch (IOException ex) {
                        // couldn't save connection settings, doesn't really matter
                        Logger.getLogger(EDACCDatabaseSettingsView.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    DatabaseConnector.getInstance().connect(txtHostname.getText(), Integer.parseInt(txtPort.getText()), txtUsername.getText(), txtDatabase.getText(), txtPassword.getText(), chkUseSSL.isSelected(), chkCompress.isSelected(), Integer.parseInt(txtMaxConnections.getText()));

                    SwingUtilities.invokeLater(new Runnable() {

                        @Override
                        public void run() {
                            EDACCDatabaseSettingsView.this.setVisible(false);
                        }
                    });
                } catch (ClassNotFoundException e) {
                    SwingUtilities.invokeLater(new Runnable() {

                        @Override
                        public void run() {
                            JOptionPane.showMessageDialog(EDACCDatabaseSettingsView.this, "Couldn't find the MySQL jdbc driver Connector-J. Make sure it's in your Java class path", "Database driver not found", JOptionPane.ERROR_MESSAGE);
                        }
                    });

                } catch (final SQLException e) {
                    SwingUtilities.invokeLater(new Runnable() {

                        @Override
                        public void run() {
                            JOptionPane.showMessageDialog(EDACCDatabaseSettingsView.this, "Couldn't connect to the database: \n\n" + e.getMessage(), "Connection error", JOptionPane.ERROR_MESSAGE);
                        }
                    });

                } catch (final DBVersionException e) {
                    SwingUtilities.invokeLater(new Runnable() {

                        @Override
                        public void run() {
                            handleDBVersionException(e);
                        }
                    });

                } catch (final DBVersionUnknownException e) {
                    SwingUtilities.invokeLater(new Runnable() {

                        @Override
                        public void run() {
                            handleDBVersionException(e);
                        }
                    });
                } catch (final DBEmptyException e) {
                    SwingUtilities.invokeLater(new Runnable() {

                        @Override
                        public void run() {
                            handleDBVersionException(e);
                        }
                    });
                }

            }
        });
    }//GEN-LAST:event_btnConnectActionPerformed

    private void handleDBVersionException(Exception ex) {
        if (ex instanceof DBEmptyException) {
            if (JOptionPane.showConfirmDialog(this,
                    "It seems that there are no tables in the database. Do you want to create them?",
                    "Warning!",
                    JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION) {
                Tasks.startTask(new TaskRunnable() {

                    @Override
                    public void run(Tasks task) {
                        try {
                            DatabaseConnector.getInstance().createDBSchema(task);
                        } catch (NoConnectionToDBException ex1) {
                            JOptionPane.showMessageDialog(Tasks.getTaskView(),
                                    "Couldn't generate the EDACC tables: No connection to database. Please connect to a database first.",
                                    "Error!", JOptionPane.ERROR_MESSAGE);
                            DatabaseConnector.getInstance().disconnect();
                        } catch (SQLException ex) {
                            JOptionPane.showMessageDialog(Tasks.getTaskView(),
                                    "An error occured while trying to generate the EDACC tables: " + ex.getMessage(),
                                    "Error!", JOptionPane.ERROR_MESSAGE);
                            DatabaseConnector.getInstance().disconnect();
                        } catch (IOException ex) {
                            JOptionPane.showMessageDialog(Tasks.getTaskView(),
                                    "An error occured while trying to generate the EDACC tables: " + ex.getMessage(),
                                    "Error!", JOptionPane.ERROR_MESSAGE);
                            DatabaseConnector.getInstance().disconnect();
                        }
                    }
                });

            } else {
                DatabaseConnector.getInstance().disconnect();
            }
            return;
        }


        boolean updateModel = false;
        if (ex instanceof DBVersionUnknownException) {
            int userinput = JOptionPane.showConfirmDialog(this, "The version of the database model is unknown. If you created the database\ntables with EDACC 0.2 or EDACC 0.3, you can update the database model\nnow to version " + ((DBVersionUnknownException) ex).localVersion + ".\nDo you want to update the database model?", "Unknown Database Model Version", JOptionPane.YES_NO_OPTION);
            if (userinput == 0) {
                // update model
                updateModel = true;
            } else {
                // don't update model
            }
        } else if (ex instanceof DBVersionException) {
            int currentVersion = ((DBVersionException) ex).currentVersion;
            int localVersion = ((DBVersionException) ex).localVersion;
            if (currentVersion > localVersion) {
                JOptionPane.showMessageDialog(this, "The version of the database model is too new. Please update you EDACC application.", "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                int userinput = JOptionPane.showConfirmDialog(this, "The version of the database model is " + currentVersion + " which is older\nthan the database model version supported by this application.\nDo you want to update the database model?", "Database Model Version", JOptionPane.YES_NO_OPTION);
                if (userinput == 0) {
                    // update model
                    updateModel = true;
                } else {
                    // don't update model
                }
            }
        }
        if (!updateModel) {
            DatabaseConnector.getInstance().disconnect();
        } else {
            Tasks.startTask(new TaskRunnable() {

                @Override
                public void run(Tasks task) {
                    try {
                        DatabaseConnector.getInstance().updateDBModel(task);
                        SwingUtilities.invokeLater(new Runnable() {

                            @Override
                            public void run() {
                                EDACCDatabaseSettingsView.this.setVisible(false);
                            }
                        });
                    } catch (final Exception ex1) {
                        DatabaseConnector.getInstance().disconnect();
                        SwingUtilities.invokeLater(new Runnable() {

                            @Override
                            public void run() {
                                JOptionPane.showMessageDialog(EDACCDatabaseSettingsView.this, "Error while updating database model:\n\n" + ex1.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                            }
                        });
                    }
                }
            });
        }
    }

    private void txtHostnameKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtHostnameKeyPressed
//        if (evt.getKeyCode() == KeyEvent.VK_TAB) {
//            txtHostname.select(0, 0);
//            txtPort.requestFocus();
//            txtPort.selectAll();
//        }
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) // connect to DB
        {
            evt.consume();
            btnConnectActionPerformed(null);
        }

    }//GEN-LAST:event_txtHostnameKeyPressed

    private void txtPortKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtPortKeyPressed
//        if (evt.getKeyCode() == KeyEvent.VK_TAB) {
//            txtPort.select(0, 0);
//            txtDatabase.requestFocus();
//            txtDatabase.selectAll();
//        }
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) // connect to DB
        {
            evt.consume();
            btnConnectActionPerformed(null);
        }
    }//GEN-LAST:event_txtPortKeyPressed

    private void txtDatabaseKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtDatabaseKeyPressed
//        if (evt.getKeyCode() == KeyEvent.VK_TAB) {
//            txtDatabase.select(0, 0);
//            txtUsername.requestFocus();
//            txtUsername.selectAll();
//        }
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) // connect to DB
        {
            evt.consume();
            btnConnectActionPerformed(null);
        }
    }//GEN-LAST:event_txtDatabaseKeyPressed

    private void txtUsernameKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtUsernameKeyPressed
//        if (evt.getKeyCode() == KeyEvent.VK_TAB) {
//            txtUsername.select(0, 0);
//            txtPassword.requestFocus();
//            txtPassword.selectAll();
//        }
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) // connect to DB
        {
            evt.consume();
            btnConnectActionPerformed(null);
        }
    }//GEN-LAST:event_txtUsernameKeyPressed

    private void txtPasswordKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtPasswordKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) // connect to DB
        {
            evt.consume();
            btnConnectActionPerformed(null);
        }
    }//GEN-LAST:event_txtPasswordKeyPressed

    private void btnConnectKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_btnConnectKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) // connect to DB
        {
            evt.consume();
            btnConnectActionPerformed(null);
        }
    }//GEN-LAST:event_btnConnectKeyPressed

    private void btnCancelKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_btnCancelKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) // connect to DB
        {
            evt.consume();
            btnCancelActionPerformed(null);
        }

    }//GEN-LAST:event_btnCancelKeyPressed

    private void txtMaxConnectionsKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtMaxConnectionsKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) // connect to DB
        {
            evt.consume();
            btnConnectActionPerformed(null);
        }
    }//GEN-LAST:event_txtMaxConnectionsKeyPressed

    private void listSavedSessionsMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_listSavedSessionsMouseClicked
        if (evt.getClickCount() > 1) {
            btnLoadSessionActionPerformed(null);
            btnConnectActionPerformed(null);
        }
    }//GEN-LAST:event_listSavedSessionsMouseClicked

    private void btnSaveSessionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveSessionActionPerformed
        if (!txtSessionName.getText().equals("")) {
            try {
                saveDatabaseSettings(txtSessionName.getText());
                reloadSessionNames();
            } catch (IOException ex) {
                Logger.getLogger(EDACCDatabaseSettingsView.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_btnSaveSessionActionPerformed

    private void btnLoadSessionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLoadSessionActionPerformed
        if (listSavedSessions.getSelectedIndex() != -1) {
            Object sel = listSavedSessions.getModel().getElementAt(listSavedSessions.getSelectedIndex());
            if (sel instanceof String) {
                try {
                    loadDatabaseSettings((String) sel);
                } catch (IOException ex) {
                    Logger.getLogger(EDACCDatabaseSettingsView.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }//GEN-LAST:event_btnLoadSessionActionPerformed

    private void btnRemoveSessionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRemoveSessionActionPerformed
        if (listSavedSessions.getSelectedIndex() != -1) {
            Object sel = listSavedSessions.getModel().getElementAt(listSavedSessions.getSelectedIndex());
            if (sel instanceof String) {
                try {
                    removeSavedDatabaseSession((String) sel);
                    reloadSessionNames();
                } catch (IOException ex) {
                    Logger.getLogger(EDACCDatabaseSettingsView.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }//GEN-LAST:event_btnRemoveSessionActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnConnect;
    private javax.swing.JButton btnLoadSession;
    private javax.swing.JButton btnRemoveSession;
    private javax.swing.JButton btnSaveSession;
    private javax.swing.JCheckBox chkCompress;
    private javax.swing.JCheckBox chkSavePassword;
    private javax.swing.JCheckBox chkUseSSL;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblDatabase;
    private javax.swing.JLabel lblHostname;
    private javax.swing.JLabel lblPassword;
    private javax.swing.JLabel lblPassword1;
    private javax.swing.JLabel lblPort;
    private javax.swing.JLabel lblUsername;
    private javax.swing.JList listSavedSessions;
    private javax.swing.JTextField txtDatabase;
    private javax.swing.JTextField txtHostname;
    private javax.swing.JTextField txtMaxConnections;
    private javax.swing.JPasswordField txtPassword;
    private javax.swing.JTextField txtPort;
    private javax.swing.JTextField txtSessionName;
    private javax.swing.JTextField txtUsername;
    // End of variables declaration//GEN-END:variables
}
