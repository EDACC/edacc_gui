package edacc.experiment.plots;

import edacc.experiment.AnalysisController;
import java.awt.BorderLayout;
import java.awt.BufferCapabilities;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.text.AttributedCharacterIterator;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JWindow;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.MouseInputAdapter;
import org.rosuda.javaGD.GDCanvas;

/**
 *
 * @author simon
 */
public class PlotPanel extends JPanel {

    public GDCanvas gdc;
    private Plot plot;
    public ArrayList<PointInformation> pointInformations;
    private int deviceNumber;

    public PlotPanel(Plot plot) {
        super();
        this.plot = plot;
        pointInformations = new ArrayList<PointInformation>();
        gdc = new GDCanvas(0, 0) {

            public void paint(final Graphics g) {
                // this fixes a bug with the plot
                Rectangle rect = g.getClipBounds();
                BufferedImage img = new BufferedImage(gdc.getWidth(), gdc.getHeight(), BufferedImage.TYPE_INT_RGB);
                Graphics tmp = img.createGraphics();
                tmp.clipRect(0, 0, gdc.getWidth(), gdc.getHeight());
                tmp.setColor(Color.WHITE);
                tmp.fillRect(0, 0, gdc.getWidth(), gdc.getHeight());
                super.paint(tmp);
                g.drawImage(img, rect.x, rect.y, rect.x + rect.width, rect.y + rect.height, rect.x, rect.y, rect.x + rect.width, rect.y + rect.height, null);
            }
        };
        gdc.setPreferredSize(new Dimension(0, 0));
        gdc.setMinimumSize(new Dimension(0, 0));
        gdc.setMaximumSize(new Dimension(65535, 65535));
        gdc.addMouseMotionListener(new PointScanner(this));
        setLayout(new BorderLayout());
        add(gdc, BorderLayout.CENTER);
    }

    public void setDeviceNumber(int deviceNumber) {
        gdc.setDeviceNumber(deviceNumber);
        this.deviceNumber = deviceNumber;
    }

    public int getDeviceNumber() {
        return deviceNumber;
    }

    public Plot getPlot() {
        return plot;
    }

    public void closeDisplay() {
        gdc.closeDisplay();
    }
}

class PointScanner extends MouseInputAdapter {

    PlotPanel graphicComponent;
    JWindow toolTip;
    JLabel label;
    private QuadTree<PointInformation> quadTree;
    private Thread updateQuadTree;
    private Runnable updateQuadTreeRunnable;
    private final Object sync = new Object();

    public PointScanner(PlotPanel gtt) {
        graphicComponent = gtt;
        initToolTip();
        gtt.addComponentListener(new ComponentAdapter() {

            @Override
            public void componentResized(ComponentEvent e) {
                synchronized (sync) {
                    quadTree = null;
                }
            }
        });

        updateQuadTreeRunnable = new Runnable() {

            @Override
            public void run() {
                synchronized (sync) {
                    quadTree = new QuadTree<PointInformation>(
                            new Point(0, 0), new Point(graphicComponent.gdc.getWidth(), graphicComponent.gdc.getHeight()));
                }
                for (PointInformation info : graphicComponent.pointInformations) {
                    Point2D point;
                    synchronized (edacc.experiment.AnalysisController.syncR) {
                        if (!AnalysisController.setCurrentDeviceNumber(graphicComponent.getDeviceNumber())) {
                            break;
                        }
                        point = AnalysisController.convertPoint(graphicComponent.getDeviceNumber(), info.getPoint());
                    }
                    synchronized (sync) {
                        // TODO: check if tab is visible!
                        if (quadTree == null) {
                            break;
                        }
                        quadTree.insert(point, info);
                    }
                }
            }
        };
    }

    private void initToolTip() {
        label = new JLabel(" ");
        label.setOpaque(true);
        label.setBackground(UIManager.getColor("ToolTip.background"));
        toolTip = new JWindow(new Frame());
        toolTip.addMouseMotionListener(new MouseMotionAdapter() {

            @Override
            public void mouseMoved(MouseEvent e) {
                toolTip.setVisible(false);
            }
        });
        label.setBorder(BorderFactory.createTitledBorder(""));
        toolTip.getContentPane().add(label);
    }

    public void buildTree() {
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        boolean createQuadTree;
        synchronized (sync) {
            createQuadTree = quadTree == null && (updateQuadTree == null || !updateQuadTree.isAlive());
        }
        if (createQuadTree) {
            updateQuadTree = new Thread(updateQuadTreeRunnable);
            updateQuadTree.start();
            return;
        }
        PointInformation res;
        synchronized (sync) {
            if (quadTree == null) {
                return;
            }
            res = quadTree.query(new Point2D.Double(e.getX(), e.getY()), 5.);
        }
        if (res != null) {
            Point p = e.getPoint();
            SwingUtilities.convertPointToScreen(p, graphicComponent.gdc);
            label.setText(res.getDescription());
            toolTip.pack();
            toolTip.setLocation(p.x + 5, p.y - toolTip.getHeight() - 5);
            if (!toolTip.isVisible()) {
                toolTip.setVisible(true);
            }
        } else {
            toolTip.setVisible(false);
        }
    }
}
