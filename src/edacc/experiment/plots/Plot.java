package edacc.experiment.plots;

import edacc.experiment.ExperimentController;
import java.sql.SQLException;
import org.rosuda.JRI.Rengine;

/**
 * The abstract plot class. Plot classes have to extend it.
 * @author simon
 */
public abstract class Plot {
    protected ExperimentController expController;

    protected Plot(ExperimentController expController) {
        this.expController = expController;
    };
    /**
     * Will be called to reinitialize the dependency gui values.
     * @throws Exception can throw an exception
     */
    public abstract void loadDefaultValues() throws Exception;
    /**
     * Returns the dependencies for that plot.
     * @return the dependencies
     */
    public abstract Dependency[] getDependencies();
    /**
     * Plots the plot to the R-engine
     * @param engine
     * @throws SQLException
     * @throws DependencyException
     */
    public abstract void plot(Rengine engine) throws SQLException, DependencyException;
}
