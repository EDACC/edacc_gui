package edacc.experiment.plots;

import java.sql.SQLException;
import org.rosuda.JRI.Rengine;

/**
 * The plot interface. Plot classes have to implement it.
 * @author simon
 */
public interface PlotInterface {
    /**
     * Will be called to reinitialize the dependency gui values.
     * @throws Exception can throw an exception
     */
    public void loadDefaultValues() throws Exception;
    /**
     * Returns the dependencies for that plot.
     * @return the dependencies
     */
    public Dependency[] getDependencies();
    /**
     * Plots the plot to the R-engine
     * @param engine
     * @throws SQLException
     * @throws DependencyException
     */
    public void plot(Rengine engine) throws SQLException, DependencyException;
}
