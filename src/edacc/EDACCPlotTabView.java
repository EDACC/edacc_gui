/*
 * EDACCPlotTabView.java
 *
 * Created on 24.07.2010, 17:12:03
 */
package edacc;

import edacc.events.PlotTabEvents;
import edacc.experiment.AnalyseController;
import edacc.experiment.plots.PlotPanel;
import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Composite;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
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
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import javax.imageio.ImageIO;
import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JWindow;
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
        this.setSize(new Dimension(800, 600));
        this.setTitle(title);
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
            //org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(edacc.EDACCApp.class).getContext().getResourceMap(EDACCPlotTabView.class);
            // final ImageIcon downArrowIcon = resourceMap.getImageIcon("image.downArrow");
            // final Image bi = downArrowIcon.getImage();

            downArrow = new JWindow() {

                @Override
                public void paint(Graphics g) {
                    // this will draw an arrow

                    AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
                            .0f);
                    AlphaComposite ac2 = AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
                            .8f);
                    Color old = g.getColor();
                    Graphics2D g2d = (Graphics2D) g;
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_ON);
                    Composite composite = g2d.getComposite();
                    g2d.setComposite(ac);
                    g2d.fillRect(0, 0, 11, 15);
                    g2d.setComposite(composite);
                    g2d.setColor(Color.blue);
                    g2d.setComposite(ac2);
                    g2d.fillPolygon(new int[]{3, 3, 7, 7}, new int[]{0, 8, 8, 0}, 4);
                    g2d.fillPolygon(new int[]{0, 10, 5}, new int[]{8, 8, 12}, 3);
                    g2d.setColor(old);
                }
            };
            downArrow.setSize(10, 13);
            //downArrow.setSize(downArrowIcon.getIconWidth(), downArrowIcon.getIconHeight());
        }

        tabbedPanePlots.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
        tabbedPanePlots.addMouseMotionListener(new TabbedPaneMouseMotionListener());
        tabbedPanePlots.addMouseListener(new TabbedPaneMouseListener());
    }

    private void addPanel(TabComponent tabComp, PlotPanel pnl) {
        tabbedPanePlots.addTab(null, pnl);
        tabbedPanePlots.setTabComponentAt(tabbedPanePlots.getTabCount() - 1, tabComp);
        tabbedPanePlots.setSelectedIndex(tabbedPanePlots.getTabCount() - 1);
        requestFocus();
    }

    private void closeTab(TabComponent tabComponent, boolean closeDevice) {
        for (int i = 0; i < tabbedPanePlots.getTabCount(); i++) {
            if (tabbedPanePlots.getTabComponentAt(i) == tabComponent) {
                PlotPanel plotPanel = (PlotPanel) tabbedPanePlots.getComponentAt(i);
                if (closeDevice) {
                    AnalyseController.closeDevice(plotPanel.getDeviceNumber());
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

        if (dropIdx == -1 &&
                locX >= 0 && locY >= 0 &&
                locX <= tabbedPanePlots.getWidth() && locY <= tabbedPanePlots.getTabComponentAt(tabbedPanePlots.getSelectedIndex()).getHeight()) {
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
    }

    public static void addListener(PlotTabEvents listener) {
        listeners.add(listener);
    }

    public static void removeListener(PlotTabEvents listener) {
        listeners.remove(listener);
    }

    private static void tabViewCountChanged() {
        for (PlotTabEvents pte: listeners) {
            pte.tabViewCountChanged(getTabViewCount());
        }
    }

    private static void tabViewVisiblityChanged(boolean visible) {
        for (PlotTabEvents pte: listeners) {
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
     * @return
     */
    public static EDACCPlotTabView getMainTabView() {
        if (tabViews.size() == 0) {
            EDACCPlotTabView tabView = new EDACCPlotTabView();
            tabView.setTitle(mainWindowTitle);
            tabViews.add(tabView);
            tabViewCountChanged();
        }
        return tabViews.get(0);
    }

    /**
     * Returns the number of tab view windows
     * @return
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
            AnalyseController.closeDevice(plotPanel.getDeviceNumber());
        }
        tabViews.remove(tabView);
        if (tabViews.size() > 0) {
            tabViews.get(0).setTitle(mainWindowTitle);
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
            tabView.setTitle(mainWindowTitle);
        }
        tabViews.add(tabView);
        tabComp.setParent(tabView);
        tabView.addPanel(tabComp, pnl);
        tabViewCountChanged();
    }

    @Action
    public void btnSave() {
        JFileChooser fc = new JFileChooser();

        // Set the platform dependent image types
        for (String suffix : ImageIO.getWriterFileSuffixes()) {
            fc.setFileFilter(new ExtensionFileFilter(suffix.toUpperCase() + " (*." + suffix + ")", suffix));
        }
        fc.setFileFilter(new ExtensionFileFilter("PDF (*.pdf)", "pdf"));
        fc.setAcceptAllFileFilterUsed(true);
        fc.setMultiSelectionEnabled(false);
        if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            String fileName = fc.getSelectedFile().getAbsolutePath();
            String name = fc.getSelectedFile().getName();
            String imgType;
            // find out the image type to be used
            if (fc.getFileFilter() == null || !(fc.getFileFilter() instanceof ExtensionFileFilter)) {
                imgType = "";
                for (String suffix : ImageIO.getWriterFileSuffixes()) {
                    if (fileName.endsWith("." + suffix)) {
                        imgType = suffix;
                        break;
                    }
                }
                // add pdf
                if (fileName.endsWith(".pdf")) {
                    imgType = "pdf";
                }
            } else {
                imgType = ((ExtensionFileFilter) fc.getFileFilter()).extensions[0];
                if (!fileName.endsWith("." + imgType)) {
                    fileName += "." + imgType;
                }
            }
            if ("".equals(imgType)) {
                javax.swing.JOptionPane.showMessageDialog(this, "Unknown file extension.", "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
                return;
            }
            File f = new File(fileName);
            if (f.exists()) {
                int userInput = javax.swing.JOptionPane.showConfirmDialog(this, "File exists. Overwrite?", "File exists", javax.swing.JOptionPane.YES_NO_OPTION);
                if (userInput == 1) {
                    return;
                }
            }
            PlotPanel plotPanel = (PlotPanel) tabbedPanePlots.getSelectedComponent();
            TabComponent tc = (TabComponent) tabbedPanePlots.getTabComponentAt(tabbedPanePlots.getSelectedIndex());
            if ("pdf".equals(imgType)) {
                AnalyseController.saveToPdf(plotPanel, fileName);
                tc.setTitle(name);
            } else {
                try {
                    // Get a buffered Image with the right dimension
                    BufferedImage bufferedImage = new BufferedImage(plotPanel.gdc.getWidth(), plotPanel.gdc.getHeight(), BufferedImage.TYPE_INT_RGB);
                    // paint the image on the new buffered image
                    plotPanel.gdc.paintAll(bufferedImage.createGraphics());
                    // save the image to disk
                    ImageIO.write(bufferedImage, imgType, f);
                    tc.setTitle(name);

                } catch (IOException ex) {
                    javax.swing.JOptionPane.showMessageDialog(this, "Error while writing file: " + ex.getMessage(), "Error while writing file", javax.swing.JOptionPane.ERROR_MESSAGE);
                }
            }

        }
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
        tabbedPanePlots = new javax.swing.JTabbedPane();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setName("Form"); // NOI18N

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(edacc.EDACCApp.class).getContext().getResourceMap(EDACCPlotTabView.class);
        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(resourceMap.getString("jPanel2.border.title"))); // NOI18N
        jPanel2.setName("jPanel2"); // NOI18N

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance(edacc.EDACCApp.class).getContext().getActionMap(EDACCPlotTabView.class, this);
        btnSave.setAction(actionMap.get("btnSave")); // NOI18N
        btnSave.setText(resourceMap.getString("btnSave.text")); // NOI18N
        btnSave.setName("btnSave"); // NOI18N

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnSave)
                .addContainerGap(484, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(btnSave)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        tabbedPanePlots.setName("tabbedPanePlots"); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(tabbedPanePlots, javax.swing.GroupLayout.DEFAULT_SIZE, 577, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(tabbedPanePlots, javax.swing.GroupLayout.DEFAULT_SIZE, 206, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnSave;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JTabbedPane tabbedPanePlots;
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
            repaint();
        }

        public void setParent(EDACCPlotTabView parent) {
            this.parent = parent;
        }

        public void setTitle(String title) {
            lblTitle.setText(title);
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
                // create a new dragWindow ("screenshot" of tab)
                dragWindow = new JWindow(new Frame());
                Component comp = tabbedPanePlots.getSelectedComponent();
                BufferedImage bufferedImage = new BufferedImage(comp.getWidth(), comp.getHeight(), BufferedImage.TYPE_INT_RGB);
                comp.paintAll(bufferedImage.createGraphics());
                int height = 150;
                int width = bufferedImage.getWidth() / bufferedImage.getHeight() * height;
                if (width == 0) {
                    width = 10;
                }
                final Image img = bufferedImage.getScaledInstance(width, height, Image.SCALE_SMOOTH);
                Component comp2 = new JComponent() {

                    @Override
                    public void paint(Graphics g) {
                        g.drawImage(img, 0, 0, this);
                    }
                };
                comp2.setPreferredSize(new Dimension(width, height));
                dragWindow.add(comp2);
                try {
                    com.sun.awt.AWTUtilities.setWindowOpacity(dragWindow, 0.8f);
                } catch (Exception ex) {
                }
                dragWindow.pack();
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
                }
            }
        }
    }

    class ExtensionFileFilter extends FileFilter {

        String description;
        String extensions[];

        public ExtensionFileFilter(String description, String extension) {
            this(description, new String[]{extension});
        }

        public ExtensionFileFilter(String description, String extensions[]) {
            if (description == null) {
                this.description = extensions[0];
            } else {
                this.description = description;
            }
            this.extensions = (String[]) extensions.clone();
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
}
