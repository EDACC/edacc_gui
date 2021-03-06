/*
 * EDACCPlotTabView.java
 *
 * Created on 24.07.2010, 17:12:03
 */
package edacc;

import edacc.events.PlotTabEvents;
import edacc.experiment.AnalysisController;
import edacc.experiment.ImageSaveFailedException;
import edacc.experiment.REngineInitializationException;
import edacc.experiment.plots.PlotPanel;
import edacc.model.TaskRunnable;
import edacc.model.Tasks;
import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Composite;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.LinkedList;
import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JWindow;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileFilter;
import javax.swing.plaf.basic.BasicButtonUI;
import org.jdesktop.application.Action;

/**
 *
 * @author simon
 */
public class EDACCPlotTabView extends javax.swing.JFrame {

    private static final String title = "Plots";
    private static final String mainWindowTitle = "Plots - main window";
    private static JWindow dragWindow = null;
    private static JWindow downArrow = null;
    private static EDACCPlotTabView dropView = null;
    private static int count = 0;
    private static LinkedList<EDACCPlotTabView> tabViews = new LinkedList<EDACCPlotTabView>();
    private static LinkedList<PlotTabEvents> listeners = new LinkedList<PlotTabEvents>();
    boolean dropBegin = false;
    int dropIdx = -1;

    /** Creates new form EDACCPlotTabView */
    public EDACCPlotTabView() {
        initComponents();
        lblCoordinates.setText("");
        this.setSize(new Dimension(800, 600));
        addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                if (EDACCPlotTabView.this == getMainTabView()) {
                    // this is the main window -> set all tab views to be invisible
                    setTabViewsVisible(false);
                } else {
                    int userInput = javax.swing.JOptionPane.showConfirmDialog(EDACCPlotTabView.this, "Do you want to merge the opened plots with the main plot window?", "Plots", javax.swing.JOptionPane.YES_NO_CANCEL_OPTION);
                    if (userInput == 0) {
                        // merge plots
                        while (tabbedPanePlots.getTabCount() > 0) {
                            addPanelInMainTabView((TabComponent) tabbedPanePlots.getTabComponentAt(0), (PlotPanel) tabbedPanePlots.getComponentAt(0));
                        }

                    } else if (userInput == 2) {
                        // cancel
                        return;
                    }
                    removeTabView(EDACCPlotTabView.this);
                    EDACCPlotTabView.this.dispose();
                }
            }
        });

        if (downArrow == null) {
            downArrow = new JWindow() {

                @Override
                public void paint(Graphics g) {
                    // this will draw an arrow
                    AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
                            .8f);
                    Color old = g.getColor();
                    Graphics2D g2d = (Graphics2D) g;
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_ON);
                    Composite composite = g2d.getComposite();
                    g2d.setColor(new Color(0, 0, 0, 0));
                    g2d.fillRect(0, 0, getWidth(), getHeight());
                    g2d.setColor(Color.blue);
                    g2d.setComposite(ac);
                    g2d.fillPolygon(new int[]{3, 3, 7, 7}, new int[]{0, 8, 8, 0}, 4);
                    g2d.fillPolygon(new int[]{0, 10, 5}, new int[]{8, 8, 12}, 3);
                    g2d.setComposite(composite);
                    g2d.setColor(old);

                }
            };
            downArrow.setSize(10, 13);
        }

        tabbedPanePlots.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
        tabbedPanePlots.addMouseMotionListener(new TabbedPaneMouseMotionListener());
        tabbedPanePlots.addMouseListener(new TabbedPaneMouseListener());
        pnlAdditionalInformations.setVisible(false);
        splitPane.setDividerSize(0);
        splitPane.setDividerLocation(.9);

    }

    private void addPanel(TabComponent tabComp, PlotPanel pnl) {
        // title: null, icon: null, panel: pnl, tooltip: title
        tabbedPanePlots.addTab(null, null, pnl, tabComp.getTitle());
        pnl.setParent(this);
        // replace the tab component and select this new tab
        tabbedPanePlots.setTabComponentAt(tabbedPanePlots.getTabCount() - 1, tabComp);
        tabbedPanePlots.setSelectedIndex(tabbedPanePlots.getTabCount() - 1);
        requestFocus();
    }

    private void closeTab(TabComponent tabComponent, boolean closeDevice) {
        for (int i = 0; i < tabbedPanePlots.getTabCount(); i++) {
            if (tabbedPanePlots.getTabComponentAt(i) == tabComponent) {
                PlotPanel plotPanel = (PlotPanel) tabbedPanePlots.getComponentAt(i);
                if (closeDevice) {
                    AnalysisController.closeDevice(plotPanel.getDeviceNumber());
                    plotPanel.closeDisplay();
                }
                tabbedPanePlots.remove(i);
                break;
            }
        }
        if (tabbedPanePlots.getTabCount() == 0) {
            removeTabView(this);
        }
    }

    private void dragEvent(int screenX, int screenY) {
        int locX = screenX - tabbedPanePlots.getLocationOnScreen().x;
        int locY = screenY - tabbedPanePlots.getLocationOnScreen().y;
        dropIdx = -1;
        dropView = null;
        for (int i = 0; i < tabbedPanePlots.getTabCount(); i++) {
            //Rectangle rect_begin = jTabbedPane1.getTabComponentAt(i).getBounds();
            Rectangle rect_begin = tabbedPanePlots.getUI().getTabBounds(tabbedPanePlots, i);
            rect_begin.width /= 2;
            Rectangle rect_end = new Rectangle(rect_begin);
            rect_end.x += rect_end.width;
            rect_end.width++;
            Point p = new Point(locX, locY);
            if (rect_begin.contains(p)) {
                dropIdx = i;
                dropBegin = true;
            } else if (rect_end.contains(p)) {
                dropIdx = i;
                dropBegin = false;
            }
        }

        if (dropIdx == -1
                && locX >= 0 && locY >= 0
                && locX <= tabbedPanePlots.getWidth() && locY <= tabbedPanePlots.getTabComponentAt(tabbedPanePlots.getSelectedIndex()).getHeight()) {
            dropIdx = tabbedPanePlots.getTabCount() - 1;
            dropBegin = false;
        }

        if (dropIdx != -1) {
            dropView = this;
            // Rectangle rect = jTabbedPane1.getTabComponentAt(dropIdx).getBounds();
            Rectangle rect = tabbedPanePlots.getUI().getTabBounds(tabbedPanePlots, dropIdx);
            int x, y;
            if (dropIdx == 0 && dropBegin) {
                x = tabbedPanePlots.getLocationOnScreen().x - downArrow.getWidth() / 2;
                y = tabbedPanePlots.getLocationOnScreen().y - downArrow.getHeight();
            } else {
                if (dropBegin) {
                    rect = tabbedPanePlots.getUI().getTabBounds(tabbedPanePlots, dropIdx - 1);
                }
                x = (int) rect.getX() + tabbedPanePlots.getLocationOnScreen().x + rect.width - downArrow.getWidth() / 2;
                y = tabbedPanePlots.getLocationOnScreen().y - downArrow.getHeight();
            }
            if (x < 0) {
                x = 0;
            }
            if (y < 0) {
                y = 0;
            }
            if (x > tabbedPanePlots.getLocationOnScreen().x + tabbedPanePlots.getWidth()) {
                dropIdx = -1;
                dropView = null;
                downArrow.setVisible(false);
                return;
            } else {
                if (!downArrow.getLocation().equals(new Point(x, y))) {

                    downArrow.setLocation(x, y);
                    downArrow.setVisible(false);
                }
            }
            downArrow.setVisible(true);
        } else {
            downArrow.setVisible(false);
        }
    }

    public void updateTitle(String _title) {
        String tmp = "";
        if (tabViews.size() == 0 || tabViews.get(0) == this) {
            tmp = mainWindowTitle;
        } else {
            tmp = title;
        }
        if ("".equals(_title)) {
            setTitle(tmp);
        } else {
            setTitle(tmp + " - " + _title);
        }
    }

    public void updateAdditionalInformations(PlotPanel plotPanel) {
        if (plotPanel.getPlot().getAdditionalInformations() != null) {
            txtAdditionalInformations.setText(plotPanel.getPlot().getAdditionalInformations());
            txtAdditionalInformations.setCaretPosition(0);
            pnlAdditionalInformations.setVisible(true);
            splitPane.setDividerSize(5);
        } else {
            pnlAdditionalInformations.setVisible(false);
            splitPane.setDividerSize(0);
        }
    }

    /**
     * Adds a PlotPanel to the tab view.
     * @param pnl the panel to be added
     */
    public void addPanel(PlotPanel pnl) {
        addPanel(new TabComponent(this, null), pnl);
    }

    /**
     * Adds a PlotPanel to the tab view with a title.
     * @param title
     * @param pnl
     */
    public void addPanel(String title, PlotPanel pnl) {
        addPanel(new TabComponent(this, title), pnl);
        updateTitle(title);
        updateAdditionalInformations(pnl);
    }

    public static void addListener(PlotTabEvents listener) {
        listeners.add(listener);
    }

    public static void removeListener(PlotTabEvents listener) {
        listeners.remove(listener);
    }

    private static void tabViewCountChanged() {
        for (PlotTabEvents pte : listeners) {
            pte.tabViewCountChanged(getTabViewCount());
        }
    }

    private static void tabViewVisiblityChanged(boolean visible) {
        for (PlotTabEvents pte : listeners) {
            pte.tabViewVisibilityChanged(visible);
        }
    }

    /**
     * Sets all tab views visible or invisible
     * @param visible
     */
    public static void setTabViewsVisible(boolean visible) {
        for (EDACCPlotTabView tabView : tabViews) {
            tabView.setVisible(visible);
        }
        tabViewVisiblityChanged(visible);
    }

    /**
     * Returns the main tab view.
     * @return the main tab view
     */
    public static EDACCPlotTabView getMainTabView() {
        if (tabViews.size() == 0) {
            EDACCPlotTabView tabView = new EDACCPlotTabView();
            tabView.updateTitle("");
            tabViews.add(tabView);
            tabViewCountChanged();
        }
        return tabViews.get(0);
    }

    /**
     * Returns the number of tab view windows
     * @return the number of tab view windows
     */
    public static int getTabViewCount() {
        return tabViews.size();
    }

    /**
     * Adds the panel to the main tab view using tc as its tab component.
     * @param tc
     * @param pnl
     */
    private static void addPanelInMainTabView(TabComponent tc, PlotPanel pnl) {
        EDACCPlotTabView tabView = getMainTabView();
        if (tc == null) {
            tabView.addPanel(pnl);
        } else {
            tabView.addPanel(tc, pnl);
        }
    }

    /**
     * Closes the tab view window and cleans up all plots in the rEngine.
     * @param tabView
     */
    public static void removeTabView(EDACCPlotTabView tabView) {
        for (int i = 0; i < tabView.tabbedPanePlots.getTabCount(); i++) {
            PlotPanel plotPanel = (PlotPanel) tabView.tabbedPanePlots.getComponentAt(i);
            AnalysisController.closeDevice(plotPanel.getDeviceNumber());
        }
        tabViews.remove(tabView);
        if (tabViews.size() > 0) {
            tabViews.get(0).updateTitle("");
        }
        tabView.dispose();
        tabViewCountChanged();
    }

    /**
     * Adds the panel to the main tab view.
     * @param pnl
     */
    public static void addPanelInMainTabView(String title, PlotPanel pnl) {
        if (title == null) {
            addPanelInMainTabView((TabComponent) null, pnl);
        } else {
            getMainTabView().addPanel(title, pnl);
        }
    }

    /**
     * creates a new tab view at the position of the mouse event and adds the panel.
     * @param evt
     * @param tabComp
     * @param pnl
     */
    private static void addPanel(MouseEvent evt, TabComponent tabComp, PlotPanel pnl) {
        EDACCPlotTabView tabView = new EDACCPlotTabView();
        tabView.setLocation(evt.getXOnScreen(), evt.getYOnScreen());
        if (tabViews.size() > 0) {
            tabView.setSize(tabViews.get(0).getSize());
        } else {
            tabView.updateTitle("");
        }
        tabViews.add(tabView);
        tabComp.setParent(tabView);
        tabView.addPanel(tabComp, pnl);
        tabView.updateAdditionalInformations(pnl);
        tabView.updateTitle(pnl.getPlot().getPlotTitle());
        tabViewCountChanged();
    }

    @Action
    public void btnSave() {
        if (!(tabbedPanePlots.getSelectedComponent() instanceof PlotPanel)) {
            return;
        }
        PlotPanel plotPanel = (PlotPanel) tabbedPanePlots.getSelectedComponent();
        FileChooser fc = new FileChooser(plotPanel.gdc.getWidth(), plotPanel.gdc.getHeight());
        // Set the platform dependent image types
       /* for (String suffix : ImageIO.getWriterFileSuffixes()) {
        fc.setFileFilter(new ExtensionFileFilter(suffix.toUpperCase() + " (*." + suffix + ")", suffix));
        }*/
        fc.setFileFilter(new ExtensionFileFilter("PDF (*.pdf)", "pdf", ExtensionFileFilter.OP_UNIT_INCH));
        fc.setFileFilter(new ExtensionFileFilter("EPS (*.eps)", "eps", ExtensionFileFilter.OP_UNIT_INCH));
        fc.setFileFilter(new ExtensionFileFilter("JPEG (*.jpg)", "jpg", 1 | 2 | 4 | 8 | 16 | 32));
        fc.setFileFilter(new ExtensionFileFilter("BMP (*.bmp)", "bmp", 1 | 2 | 4 | 8 | 32));
        fc.setFileFilter(new ExtensionFileFilter("TIFF (*.tiff)", "tiff", 1 | 2 | 4 | 8 | 32));
        fc.setFileFilter(new ExtensionFileFilter("PNG (*.png)", "png", 1 | 2 | 4 | 8 | 32));
        fc.setAcceptAllFileFilterUsed(false);
        fc.setMultiSelectionEnabled(false);
        if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            String fileName = fc.getSelectedFile().getAbsolutePath();
            String imgType;
            // find out the image type to be used
          /*  if (fc.getFileFilter() == null || !(fc.getFileFilter() instanceof ExtensionFileFilter)) {
            imgType = "";
            for (String suffix : ImageIO.getWriterFileSuffixes()) {
            if (fileName.endsWith("." + suffix)) {
            imgType = suffix;
            break;
            }
            }
            // add pdf & eps
            if (fileName.endsWith(".pdf")) {
            imgType = "pdf";
            } else if (fileName.endsWith(".eps")) {
            imgType = "eps";
            }
            } else {*/
            imgType = ((ExtensionFileFilter) fc.getFileFilter()).extensions[0];
            if (!fileName.endsWith("." + imgType)) {
                fileName += "." + imgType;
            }
            //}
           /* if ("".equals(imgType)) {
            javax.swing.JOptionPane.showMessageDialog(this, "Unknown file extension.", "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
            return;
            }*/
            File f = new File(fileName);
            if (f.exists()) {
                int userInput = javax.swing.JOptionPane.showConfirmDialog(this, "File exists. Overwrite?", "File exists", javax.swing.JOptionPane.YES_NO_OPTION);
                if (userInput == 1) {
                    return;
                }
            }
            try {
                if ("pdf".equals(imgType)) {
                    AnalysisController.saveToPdf(plotPanel, fileName, fc.getImageWidth(), fc.getImageHeight());
                } else if ("eps".equals(imgType)) {
                    AnalysisController.saveToEps(plotPanel, fileName, fc.getImageWidth(), fc.getImageHeight());
                } else if ("jpg".equals(imgType)) {
                    AnalysisController.saveToJpeg(plotPanel, fileName, fc.getImageWidth(), fc.getImageHeight(), fc.getQuality(), fc.getResolution(), fc.getUnit());
                } else if ("bmp".equals(imgType)) {
                    AnalysisController.saveToBmp(plotPanel, fileName, fc.getImageWidth(), fc.getImageHeight(), fc.getResolution(), fc.getUnit());
                } else if ("tiff".equals(imgType)) {
                    AnalysisController.saveToTiff(plotPanel, fileName, fc.getImageWidth(), fc.getImageHeight(), fc.getResolution(), fc.getUnit());
                } else if ("png".equals(imgType)) {
                    AnalysisController.saveToPng(plotPanel, fileName, fc.getImageWidth(), fc.getImageHeight(), fc.getResolution(), fc.getUnit());
                }
            } catch (NumberFormatException e) {
                javax.swing.JOptionPane.showMessageDialog(this, "Invalid data.", "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
            } catch (ImageSaveFailedException e) {
                javax.swing.JOptionPane.showMessageDialog(this, "Saving the image failed.", "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
            }

        }
    }

    public void setCoordinates(String coordinates) {
        lblCoordinates.setText(coordinates);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel2 = new javax.swing.JPanel();
        btnSave = new javax.swing.JButton();
        btnRefresh = new javax.swing.JButton();
        btnSettings = new javax.swing.JButton();
        lblCoordinates = new javax.swing.JLabel();
        splitPane = new javax.swing.JSplitPane();
        tabbedPanePlots = new javax.swing.JTabbedPane();
        pnlAdditionalInformations = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        txtAdditionalInformations = new javax.swing.JEditorPane();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setName("Form"); // NOI18N

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(edacc.EDACCApp.class).getContext().getResourceMap(EDACCPlotTabView.class);
        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(resourceMap.getString("jPanel2.border.title"))); // NOI18N
        jPanel2.setName("jPanel2"); // NOI18N

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance(edacc.EDACCApp.class).getContext().getActionMap(EDACCPlotTabView.class, this);
        btnSave.setAction(actionMap.get("btnSave")); // NOI18N
        btnSave.setText(resourceMap.getString("btnSave.text")); // NOI18N
        btnSave.setName("btnSave"); // NOI18N

        btnRefresh.setAction(actionMap.get("btnRefresh")); // NOI18N
        btnRefresh.setText(resourceMap.getString("btnRefresh.text")); // NOI18N
        btnRefresh.setName("btnRefresh"); // NOI18N

        btnSettings.setAction(actionMap.get("btnSettings")); // NOI18N
        btnSettings.setText(resourceMap.getString("btnSettings.text")); // NOI18N
        btnSettings.setName("btnSettings"); // NOI18N

        lblCoordinates.setText(resourceMap.getString("lblCoordinates.text")); // NOI18N
        lblCoordinates.setName("lblCoordinates"); // NOI18N

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnSave)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnRefresh)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnSettings)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 340, Short.MAX_VALUE)
                .addComponent(lblCoordinates)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(btnSave)
                .addComponent(btnRefresh)
                .addComponent(btnSettings)
                .addComponent(lblCoordinates))
        );

        splitPane.setDividerLocation(200);
        splitPane.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        splitPane.setResizeWeight(1.0);
        splitPane.setName("splitPane"); // NOI18N

        tabbedPanePlots.setName("tabbedPanePlots"); // NOI18N
        tabbedPanePlots.setPreferredSize(new java.awt.Dimension(100, 100));
        tabbedPanePlots.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                tabbedPanePlotsStateChanged(evt);
            }
        });
        tabbedPanePlots.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseMoved(java.awt.event.MouseEvent evt) {
                tabbedPanePlotsMouseMoved(evt);
            }
        });
        splitPane.setTopComponent(tabbedPanePlots);

        pnlAdditionalInformations.setBorder(javax.swing.BorderFactory.createTitledBorder(resourceMap.getString("pnlAdditionalInformations.border.title"))); // NOI18N
        pnlAdditionalInformations.setName("pnlAdditionalInformations"); // NOI18N

        jScrollPane2.setName("jScrollPane2"); // NOI18N

        txtAdditionalInformations.setContentType(resourceMap.getString("txtAdditionalInformations.contentType")); // NOI18N
        txtAdditionalInformations.setName("txtAdditionalInformations"); // NOI18N
        jScrollPane2.setViewportView(txtAdditionalInformations);

        javax.swing.GroupLayout pnlAdditionalInformationsLayout = new javax.swing.GroupLayout(pnlAdditionalInformations);
        pnlAdditionalInformations.setLayout(pnlAdditionalInformationsLayout);
        pnlAdditionalInformationsLayout.setHorizontalGroup(
            pnlAdditionalInformationsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 617, Short.MAX_VALUE)
        );
        pnlAdditionalInformationsLayout.setVerticalGroup(
            pnlAdditionalInformationsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 208, Short.MAX_VALUE)
        );

        splitPane.setRightComponent(pnlAdditionalInformations);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(splitPane, javax.swing.GroupLayout.DEFAULT_SIZE, 631, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(splitPane, javax.swing.GroupLayout.DEFAULT_SIZE, 441, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void tabbedPanePlotsStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_tabbedPanePlotsStateChanged
        lblCoordinates.setText("");
        if (tabbedPanePlots.getSelectedIndex() >= 0) {
            TabComponent tabComponent = (TabComponent) tabbedPanePlots.getTabComponentAt(tabbedPanePlots.getSelectedIndex());
            if (tabComponent == null) {
                return;
            }
            PlotPanel plotPanel = (PlotPanel) tabbedPanePlots.getComponentAt(tabbedPanePlots.getSelectedIndex());
            updateAdditionalInformations(plotPanel);
            this.updateTitle(tabComponent.getTitle());
        }
    }//GEN-LAST:event_tabbedPanePlotsStateChanged

    private void tabbedPanePlotsMouseMoved(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tabbedPanePlotsMouseMoved
    }//GEN-LAST:event_tabbedPanePlotsMouseMoved
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnRefresh;
    private javax.swing.JButton btnSave;
    private javax.swing.JButton btnSettings;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lblCoordinates;
    private javax.swing.JPanel pnlAdditionalInformations;
    private javax.swing.JSplitPane splitPane;
    private javax.swing.JTabbedPane tabbedPanePlots;
    private javax.swing.JEditorPane txtAdditionalInformations;
    // End of variables declaration//GEN-END:variables

    class TabComponent extends JPanel {

        private JLabel lblTitle;
        private JButton closeButton;
        private EDACCPlotTabView parent;

        TabComponent(final EDACCPlotTabView parent, String title) {
            this.parent = parent;
            setOpaque(false);
            if (title == null) {
                title = "untitled";
                if (count != 0) {
                    title += " (" + count + ")";
                }
                count++;
            }
            this.setPreferredSize(new Dimension(120, 24));
            lblTitle = new JLabel(title);
            closeButton = new TabButton();
            closeButton.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    TabComponent.this.parent.closeTab(TabComponent.this, true);
                }
            });
            add(lblTitle);
            add(closeButton);
            lblTitle.setPreferredSize(new Dimension(93, 17));
            repaint();
        }

        public void setParent(EDACCPlotTabView parent) {
            this.parent = parent;
        }

        public void setTitle(String title) {
            lblTitle.setText(title);
        }

        public String getTitle() {
            return lblTitle.getText();
        }
    }

    private class TabButton extends JButton {

        public TabButton() {
            int size = 17;
            setPreferredSize(new Dimension(size, size));
            setToolTipText("close this tab");
            setUI(new BasicButtonUI());
            setContentAreaFilled(false);
            setFocusable(false);
            setBorder(BorderFactory.createEtchedBorder());
            setBorderPainted(false);
            addMouseListener(buttonMouseListener);
            setRolloverEnabled(true);
        }

        @Override
        public void updateUI() {
        }

        //paint the cross
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            //shift the image for pressed buttons
            if (getModel().isPressed()) {
                g2.translate(1, 1);
            }
            g2.setStroke(new BasicStroke(2));
            g2.setColor(Color.BLACK);
            if (getModel().isRollover()) {
                //   g2.setColor(Color.MAGENTA);
            }
            int delta = 6;
            g2.drawLine(delta, delta, getWidth() - delta - 1, getHeight() - delta - 1);
            g2.drawLine(getWidth() - delta - 1, delta, delta, getHeight() - delta - 1);
            g2.dispose();
        }
    }
    private final static MouseListener buttonMouseListener = new MouseAdapter() {

        @Override
        public void mouseEntered(MouseEvent e) {
            Component component = e.getComponent();
            if (component instanceof AbstractButton) {
                AbstractButton button = (AbstractButton) component;
                button.setBorderPainted(true);
            }
        }

        @Override
        public void mouseExited(MouseEvent e) {
            Component component = e.getComponent();
            if (component instanceof AbstractButton) {
                AbstractButton button = (AbstractButton) component;
                button.setBorderPainted(false);
            }
        }
    };

    /**
     * This class implements basic drag events for the tabbed pane.
     */
    class TabbedPaneMouseMotionListener extends MouseAdapter {

        @Override
        public void mouseDragged(MouseEvent e) {
            if (dragWindow == null && tabbedPanePlots.getTabCount() > 0 && tabbedPanePlots.getTabComponentAt(tabbedPanePlots.getSelectedIndex()).getBounds().contains(e.getPoint())) {
                // a tab is being dragged
                // create a new dragWindow ("screenshot" of the current selected tab)

                final PlotPanel comp = (PlotPanel) tabbedPanePlots.getSelectedComponent();
                final BufferedImage bufferedImage = new BufferedImage(comp.gdc.getWidth(), comp.gdc.getHeight(), BufferedImage.TYPE_INT_RGB);

                Graphics g = bufferedImage.createGraphics();
                g.setColor(Color.WHITE);
                g.fillRect(0, 0, bufferedImage.getWidth(), bufferedImage.getHeight());
                g.setClip(0, 0, comp.gdc.getWidth(), comp.gdc.getHeight());
                comp.gdc.invalidate();
                comp.gdc.paintAll(g);
                comp.gdc.paint(g);
                int height = 150;
                int width = bufferedImage.getWidth() / bufferedImage.getHeight() * height;
                if (width == 0) {
                    width = 10;
                }
                final Image img = bufferedImage.getScaledInstance(width, height, Image.SCALE_SMOOTH);
                dragWindow = new JWindow() {

                    @Override
                    public void paint(Graphics g) {
                        if (getSize().width <= 0 || getSize().height <= 0) {
                            return;
                        }
                        g.drawImage(img, 0, 0, this);

                    }
                };
                dragWindow.setSize(new Dimension(width, height));
                try {
                    try {
                        com.sun.awt.AWTUtilities.setWindowOpacity(dragWindow, 0.8f);
                    } catch (NoClassDefFoundError er) {
                    }
                } catch (Exception ex) {
                }
                dragWindow.setVisible(true);
            }
            if (dragWindow != null) {
                // set the new position of the drag window
                dragWindow.setLocation(e.getXOnScreen() + 1, e.getYOnScreen());
                // find the tab window which is visible under the mouse pointer if such window exists and call the drag event.
                boolean found = false;
                for (EDACCPlotTabView tv : tabViews) {
                    if (tv.getMousePosition() != null) {
                        /* if the mouse pointer is over this tab view then call
                        the drag event */
                        found = true;
                        tv.dragEvent(e.getXOnScreen(), e.getYOnScreen());
                        break;
                    }
                }
                if (!found) {
                    downArrow.setVisible(false);
                    dropView = null;
                }
            }
        }
    }

    class TabbedPaneMouseListener extends MouseAdapter {

        @Override
        public void mouseReleased(MouseEvent e) {
            if (dragWindow != null) {
                // we have to drop the tab dragged tab
                dragWindow.dispose();
                dragWindow = null;
                downArrow.setVisible(false);
                if (dropView == null) {
                    if (tabbedPanePlots.getTabCount() > 1) {
                        // create a new tab window
                        PlotPanel pnl = (PlotPanel) tabbedPanePlots.getSelectedComponent();
                        TabComponent tabComp = (TabComponent) tabbedPanePlots.getTabComponentAt(tabbedPanePlots.getSelectedIndex());
                        EDACCPlotTabView.this.closeTab(tabComp, false);
                        EDACCPlotTabView.addPanel(e, tabComp, pnl);
                        EDACCPlotTabView.setTabViewsVisible(true);
                    }
                } else {
                    // drop the tab into the drop view
                    int sel = tabbedPanePlots.getSelectedIndex();
                    if (!dropView.dropBegin) {
                        dropView.dropIdx++;
                    }
                    PlotPanel pnl = (PlotPanel) tabbedPanePlots.getSelectedComponent();
                    TabComponent tabComponent = (TabComponent) tabbedPanePlots.getTabComponentAt(tabbedPanePlots.getSelectedIndex());
                    dropView.tabbedPanePlots.add(tabbedPanePlots.getSelectedComponent(), dropView.dropIdx);
                    if (dropView == EDACCPlotTabView.this && sel < dropView.dropIdx) {
                        dropView.dropIdx--;
                    }
                    tabComponent.setParent(dropView);

                    dropView.tabbedPanePlots.setTabComponentAt(dropView.dropIdx, tabComponent);
                    dropView.tabbedPanePlots.setSelectedIndex(dropView.dropIdx);
                    if (dropView != EDACCPlotTabView.this) {
                        EDACCPlotTabView.this.closeTab(tabComponent, false);
                    }
                    dropView.updateTitle(pnl.getPlot().getPlotTitle());
                }
            }
        }
    }

    class ExtensionFileFilter extends FileFilter {

        public static final int OP_UNIT_INCH = 1;
        public static final int OP_UNIT_CM = 2;
        public static final int OP_UNIT_MM = 4;
        public static final int OP_UNIT_PX = 8;
        public static final int OP_QUALITY = 16;
        public static final int OP_RESOLUTION = 32;
        String description;
        String extensions[];
        int options;

        public ExtensionFileFilter(String description, String extension, int options) {
            this(description, new String[]{extension}, options);
        }

        public ExtensionFileFilter(String description, String extensions[], int options) {
            if (description == null) {
                this.description = extensions[0];
            } else {
                this.description = description;
            }
            this.extensions = (String[]) extensions.clone();
            this.options = options;
            toLower(this.extensions);
        }

        private void toLower(String array[]) {
            for (int i = 0, n = array.length; i < n; i++) {
                array[i] = array[i].toLowerCase();
            }
        }

        @Override
        public String getDescription() {
            return description;
        }

        @Override
        public boolean accept(File file) {
            if (file.isDirectory()) {
                return true;
            } else {
                String path = file.getAbsolutePath().toLowerCase();
                for (int i = 0, n = extensions.length; i < n; i++) {
                    String extension = extensions[i];
                    if ((path.endsWith(extension) && (path.charAt(path.length() - extension.length() - 1)) == '.')) {
                        return true;
                    }
                }
            }
            return false;
        }
    }

    @Action
    public void btnSettings() {
        if (!(tabbedPanePlots.getSelectedComponent() instanceof PlotPanel)) {
            return;
        }
        PlotPanel panel = (PlotPanel) tabbedPanePlots.getSelectedComponent();

        if (AnalysisController.setSelectedPlotType(panel.getPlot())) {
            // set the dependencies
            panel.getPlot().updateDependencies();
            // repaint the analysis panel and set the main form to the foreground.
            AnalysisController.analysisPanel.revalidate();
            AnalysisController.analysisPanel.repaint();
            AnalysisController.analysisPanel.requestFocus();
        } else {
            // TODO: message: failed!
        }
    }

    @Action
    public void btnRefresh() {
        // TODO: selbes experiment?!!
        if (!(tabbedPanePlots.getSelectedComponent() instanceof PlotPanel)) {
            return;
        }
        Tasks.startTask(new TaskRunnable() {

            @Override
            public void run(Tasks task) {
                synchronized (AnalysisController.syncR) {
                    final PlotPanel panel = (PlotPanel) tabbedPanePlots.getSelectedComponent();
                    if (!AnalysisController.setCurrentDeviceNumber(panel.getDeviceNumber())) {
                        return;
                    }
                    try {
                        panel.getPlot().plot(AnalysisController.getRengine(), panel.pointInformations);
                    } catch (REngineInitializationException ex) {
                        // TODO: error
                    } catch (Exception ex) {
                        // TODO: error
                    }
                }
            }
        }, true, this);


    }

    class FileChooser extends JFileChooser {

        private JComboBox comboUnits;
        private JTextField txtWidth, txtHeight, txtQuality, txtResolution;
        private JPanel pnlOptions;
        private Integer width, height;
        private String cur;
        public static final int DPI = 72;

        public FileChooser(int width, int height) {
            super();
            this.width = width;
            this.height = height;
            this.addPropertyChangeListener("fileFilterChanged", new PropertyChangeListener() {

                @Override
                public void propertyChange(PropertyChangeEvent evt) {
                    if (evt.getOldValue() != evt.getNewValue()) {
                        if (!(evt.getNewValue() instanceof ExtensionFileFilter)) {
                            setAccessory(null);
                        } else {
                            updateOptionsPanel((ExtensionFileFilter) evt.getNewValue());
                            setAccessory(pnlOptions);
                        }
                        FileChooser.this.revalidate();
                    }
                }
            });
        }

        private void updateOptionsPanel(ExtensionFileFilter filter) {
            txtHeight = new JTextField();
            txtWidth = new JTextField();
            txtQuality = new JTextField("75");
            txtResolution = new JTextField(String.valueOf(DPI));
            txtHeight.setPreferredSize(new Dimension(100, txtHeight.getPreferredSize().height));
            txtWidth.setPreferredSize(new Dimension(100, txtWidth.getPreferredSize().height));
            txtQuality.setPreferredSize(new Dimension(100, txtQuality.getPreferredSize().height));
            txtResolution.setPreferredSize(new Dimension(100, txtResolution.getPreferredSize().height));
            comboUnits = new JComboBox();
            comboUnits.setPreferredSize(new Dimension(100, comboUnits.getPreferredSize().height));
            cur = null;
            comboUnits.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    int w, h;
                    int dpi;
                    try {
                        dpi = getResolution();
                    } catch (NumberFormatException ex) {
                        dpi = DPI;
                    }
                    if ("inch".equals(cur)) {
                        try {
                            double tmp = Double.parseDouble(txtWidth.getText());
                            tmp *= dpi;
                            w = (int) Math.round(tmp);
                        } catch (NumberFormatException ex) {
                            w = width;
                        }
                        try {
                            double tmp = Double.parseDouble(txtHeight.getText());
                            tmp *= dpi;
                            h = (int) Math.round(tmp);
                        } catch (NumberFormatException ex) {
                            h = height;
                        }
                    } else if ("cm".equals(cur)) {
                        try {
                            double tmp = Double.parseDouble(txtWidth.getText());
                            tmp *= dpi;
                            tmp /= 2.54;
                            w = (int) Math.round(tmp);
                        } catch (NumberFormatException ex) {
                            w = width;
                        }
                        try {
                            double tmp = Double.parseDouble(txtHeight.getText());
                            tmp *= dpi;
                            tmp /= 2.54;
                            h = (int) Math.round(tmp);
                        } catch (NumberFormatException ex) {
                            h = height;
                        }
                    } else if ("mm".equals(cur)) {
                        try {
                            double tmp = Double.parseDouble(txtWidth.getText());
                            tmp *= dpi;
                            tmp /= 2.54;
                            tmp /= 10.;
                            w = (int) Math.round(tmp);
                        } catch (NumberFormatException ex) {
                            w = width;
                        }
                        try {
                            double tmp = Double.parseDouble(txtHeight.getText());
                            tmp *= dpi;
                            tmp /= 2.54;
                            tmp /= 10.;
                            h = (int) Math.round(tmp);
                        } catch (NumberFormatException ex) {
                            h = height;
                        }
                    } else if ("pixel".equals(cur)) {
                        try {
                            w = Integer.parseInt(txtWidth.getText());
                        } catch (NumberFormatException ex) {
                            w = width;
                        }
                        try {
                            h = Integer.parseInt(txtHeight.getText());
                        } catch (NumberFormatException ex) {
                            h = height;
                        }
                    } else {
                        w = width;
                        h = height;
                    }
                    // w, h in pixels
                    if ("inch".equals(comboUnits.getSelectedItem())) {
                        Double i_width = (double) w / (double) dpi;
                        Double i_height = (double) h / (double) dpi;
                        i_width = (double) Math.round(i_width * 100) / (double) 100;
                        i_height = (double) Math.round(i_height * 100) / (double) 100;
                        txtHeight.setText(i_height.toString());
                        txtWidth.setText(i_width.toString());
                    } else if ("cm".equals(comboUnits.getSelectedItem())) {
                        Double i_width = (double) w / (double) dpi * 2.54;
                        Double i_height = (double) h / (double) dpi * 2.54;
                        i_width = (double) Math.round(i_width * 100) / (double) 100;
                        i_height = (double) Math.round(i_height * 100) / (double) 100;
                        txtHeight.setText(i_height.toString());
                        txtWidth.setText(i_width.toString());
                    } else if ("mm".equals(comboUnits.getSelectedItem())) {
                        Double i_width = (double) w / (double) dpi * 2.54 * 10.;
                        Double i_height = (double) h / (double) dpi * 2.54 * 10.;
                        i_width = (double) Math.round(i_width * 100) / (double) 100;
                        i_height = (double) Math.round(i_height * 100) / (double) 100;
                        txtHeight.setText(i_height.toString());
                        txtWidth.setText(i_width.toString());
                    } else if ("pixel".equals(comboUnits.getSelectedItem())) {
                        txtHeight.setText(String.valueOf(h));
                        txtWidth.setText(String.valueOf(w));
                    } else {
                    }
                    if (comboUnits.getSelectedItem() instanceof String) {
                        cur = (String) comboUnits.getSelectedItem();
                    } else {
                        cur = null;
                    }
                }
            });
            if ((filter.options & ExtensionFileFilter.OP_UNIT_INCH) > 0) {
                comboUnits.addItem("inch");
            }
            if ((filter.options & ExtensionFileFilter.OP_UNIT_CM) > 0) {
                comboUnits.addItem("cm");
            }
            if ((filter.options & ExtensionFileFilter.OP_UNIT_MM) > 0) {
                comboUnits.addItem("mm");
            }
            if ((filter.options & ExtensionFileFilter.OP_UNIT_PX) > 0) {
                comboUnits.addItem("pixel");
            }
            pnlOptions = new JPanel(new GridBagLayout());
            GridBagConstraints c = new GridBagConstraints();
            c.anchor = GridBagConstraints.PAGE_START;
            c.fill = GridBagConstraints.HORIZONTAL;
            c.weighty = 1;
            c.gridx = 0;
            c.gridy = 0;
            c.insets = new Insets(3, 3, 3, 3);
            pnlOptions.add(new JLabel("Units:"), c);
            c.gridx = 1;
            pnlOptions.add(comboUnits, c);
            c.gridx = 0;
            c.gridy = 1;
            c.weighty *= 1000;
            pnlOptions.add(new JLabel("Width:"), c);
            c.gridx = 1;
            pnlOptions.add(txtWidth, c);
            c.gridx = 0;
            c.gridy = 2;
            c.weighty *= 1000;
            pnlOptions.add(new JLabel("Height"), c);
            c.gridx = 1;
            pnlOptions.add(txtHeight, c);

            if ((filter.options & ExtensionFileFilter.OP_RESOLUTION) > 0) {
                c.gridx = 0;
                c.gridy++;
                c.weighty *= 1000;
                pnlOptions.add(new JLabel("Resolution (DPI):"), c);
                c.gridx = 1;
                pnlOptions.add(txtResolution, c);
            }
            if ((filter.options & ExtensionFileFilter.OP_QUALITY) > 0) {
                c.gridx = 0;
                c.gridy++;
                c.weighty *= 1000;
                pnlOptions.add(new JLabel("Quality:"), c);
                c.gridx = 1;
                pnlOptions.add(txtQuality, c);
            }
            pnlOptions.setBorder(new TitledBorder("Options"));
        }

        public double getImageWidth() throws NumberFormatException {
            return Double.parseDouble(txtWidth.getText());
        }

        public double getImageHeight() throws NumberFormatException {
            return Double.parseDouble(txtHeight.getText());
        }

        public int getResolution() throws NumberFormatException {
            return Integer.parseInt(txtResolution.getText());
        }

        public int getQuality() throws NumberFormatException {
            return Integer.parseInt(txtQuality.getText());
        }

        /**
         * Returns the selected unit as a string which R understands.
         * @return
         */
        public String getUnit() {
            String unit = (String) comboUnits.getSelectedItem();
            if ("inch".equals(unit)) {
                return "in";
            } else if ("cm".equals(unit)) {
                return "cm";
            } else if ("mm".equals(unit)) {
                return "mm";
            } else if ("pixel".equals(unit)) {
                return "px";
            }
            return "";
        }
    }
}
