package edacc.experiment;

import javax.swing.table.AbstractTableModel;
import java.util.Vector;
import edacc.model.Solver;

/**
 *
 * @author daniel
 */
public class SolverTableModel {
    private String[] columns = {"Name", "binary name", "md5", "description"};
    private Vector<Solver> solvers;
    

}
