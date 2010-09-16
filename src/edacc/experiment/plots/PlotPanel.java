package edacc.experiment.plots;

import edacc.experiment.AnalyseController;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.util.Vector;
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
    public Vector<PointInformation> pointInformations;
    private int deviceNumber;

    public PlotPanel() {
        super();
        pointInformations = new Vector<PointInformation>();
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
    

}
class PointScanner extends MouseInputAdapter {

    PlotPanel graphicComponent;
    JWindow toolTip;
    JLabel label;

    public PointScanner(PlotPanel gtt) {
        graphicComponent = gtt;
        initToolTip();
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
        Point p = e.getPoint();
        if (graphicComponent.pointInformations == null) {
            return;
        }
        String str = null;
        double dist = 65535.; // infinity
        for (PointInformation info : graphicComponent.pointInformations) {
            double[] point = AnalyseController.convertPoint(graphicComponent.getDeviceNumber(), info.getPoint());
            Point p2 = new Point((int)point[0], (int)point[1]);
            double tmpdist = p.distance(p2);
            if (p.distance(p2) < 5.) {
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