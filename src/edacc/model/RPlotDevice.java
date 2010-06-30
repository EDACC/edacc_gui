package edacc.model;

import edacc.EDACCPlotView;
import org.rosuda.javaGD.GDInterface;

/**
 *
 * @author simon
 */
public class RPlotDevice extends GDInterface {
    @Override
    public void gdOpen(double w, double h) {
        c = EDACCPlotView.gdc;
    }
}
