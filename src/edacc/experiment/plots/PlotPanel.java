package edacc.experiment.plots;

import edacc.experiment.AnalysisController;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Point;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseEvent;
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
    public LinkedList<Point2D> points;

    public PointScanner(PlotPanel gtt) {
        graphicComponent = gtt;
        initToolTip();
        gtt.addComponentListener(new ComponentAdapter() {

            @Override
            public void componentResized(ComponentEvent e) {
                points = null;
            }
        });
    }

    private void initToolTip() {
        label = new JLabel(" ");
        label.setOpaque(true);
        label.setBackground(UIManager.getColor("ToolTip.background"));
        toolTip = new JWindow(new Frame());

        label.setBorder(BorderFactory.createTitledBorder(""));
        toolTip.getContentPane().add(label);
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        if (points == null) {
            points = new LinkedList<Point2D>();
            for (int i = 0; i < graphicComponent.pointInformations.size(); i++) {
                points.add(AnalysisController.convertPoint(graphicComponent.getDeviceNumber(), graphicComponent.pointInformations.get(i).getPoint()));
            }
        }
        Point p = e.getPoint();
        if (graphicComponent.pointInformations == null) {
            return;
        }
        String str = null;
        int index = 0;
        double dist = 65535.; // infinity
        for (PointInformation info : graphicComponent.pointInformations) {
            Point2D point = points.get(index++);
            double tmpdist = points.get(index++).distance(p.x, p.y);
            if (tmpdist < 5.) {
                if (tmpdist < dist) {
                    dist = tmpdist;
                    str = info.getDescription();
                }
            }
        }
        if (str != null) {
            SwingUtilities.convertPointToScreen(p, graphicComponent.gdc);
            label.setText(str);
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
