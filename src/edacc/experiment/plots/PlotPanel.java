package edacc.experiment.plots;

import edacc.experiment.AnalysisController;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Point;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.LinkedList;
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
        gdc = new GDCanvas(0, 0);

        gdc.setPreferredSize(new Dimension(0, 0));
        gdc.setMinimumSize(new Dimension(0, 0));
        gdc.setMaximumSize(new Dimension(65535, 65535));
        gdc.addMouseMotionListener(new PointScanner(this));

        setLayout(new BorderLayout());
        add(gdc, BorderLayout.CENTER);
    }

    public void setDeviceNumber(int deviceNumber) {
        this.deviceNumber = deviceNumber;
    }

    public int getDeviceNumber() {
        return deviceNumber;
    }

    public Plot getPlot() {
        return plot;
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
