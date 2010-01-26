/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edacc.manageDB;

import edacc.EDACCManageDBMode;
import edacc.model.Solver;
import edacc.model.SolverDAO;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.Vector;
import sun.awt.geom.AreaOp.CAGOp;

/**
 *
 * @author dgall
 */
public class ManageDBSolvers {

    private EDACCManageDBMode gui;
    private SolverTableModel solverTableModel;
    private Solver currentSolver;
    private File lastBinaryLocation;

    public ManageDBSolvers(EDACCManageDBMode gui, SolverTableModel solverTableModel) {
        this.gui = gui;
        this.solverTableModel = solverTableModel;
    }

    public void applySolver(String name, String description) throws NoSolverBinarySpecifiedException {
        //TODO add the remaining fields of a solver like binary...; calculate md5 hash etc.
        if (currentSolver != null) {
            if (currentSolver.getBinaryFile() == null)
                throw new NoSolverBinarySpecifiedException();
            currentSolver.setName(name);
            currentSolver.setDescription(description);
        }
    }

    /**
     * Tries to save all solvers in the solver table to DB.
     * If a solver is already saved in the DB, it will update its data in the DB.
     * @throws SQLException
     * @throws FileNotFoundException
     */
    public void saveSolvers() throws SQLException, FileNotFoundException, IOException, NoSuchAlgorithmException {
        for (Solver s : solverTableModel.getSolvers()) {
            s.setMd5(Util.calculateMD5(s.getBinaryFile()));
            SolverDAO.save(s);
        }

        // TODO save parameters of solver!
    }

    public void newSolver() {
        solverTableModel.addSolver(new Solver());
    }

    public void showSolver(int index) {
        currentSolver = solverTableModel.getSolver(index);
        if (currentSolver != null) {
            gui.showSolverDetails(currentSolver);
        }
    }

    /**
     *
     * @return the last location from where the user added a solver binary or null
     * if no last location is known.
     */
    public File getLastBinaryLocation() {
        return lastBinaryLocation;
    }

    public void addSolverBinary(File binary) {
        currentSolver.setBinaryFile(binary);
        currentSolver.setBinaryName(binary.getName());
        lastBinaryLocation = binary.getParentFile();
    }
}
