package edacc.experiment.plots;

import java.sql.SQLException;
import org.rosuda.JRI.Rengine;

/**
 *
 * @author simon
 */
public interface PlotInterface {
    public void loadDefaultValues() throws Exception;
    public Dependency[] getDependencies();
    public void plot(Rengine engine) throws SQLException, DependencyException;
}
