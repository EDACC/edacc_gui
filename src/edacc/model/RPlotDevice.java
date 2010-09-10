package edacc.model;

import edacc.experiment.AnalyseController;
import edacc.experiment.plots.PlotPanel;
import org.rosuda.javaGD.GDInterface;

/**
 *
 * @author simon
 */
public class RPlotDevice extends GDInterface {
    private PlotPanel view;

    @Override
    public void gdOpen(double w, double h) {
        view = AnalyseController.lastPlotPanel;
        c = view.gdc;
    }
}
