package edacc.model;

import edacc.experiment.AnalysisController;
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
        view = AnalysisController.lastPlotPanel;
        c = view.gdc;
    }
}
