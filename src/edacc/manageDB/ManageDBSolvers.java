/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edacc.manageDB;

import edacc.model.SolverIsInExperimentException;
import edacc.EDACCManageDBMode;
import edacc.model.NoConnectionToDBException;
import edacc.model.Solver;
import edacc.model.SolverDAO;
import edacc.model.SolverNotInDBException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author dgall
 */
public class ManageDBSolvers {

    private EDACCManageDBMode gui;
    private SolverTableModel solverTableModel;
    private Solver currentSolver;

    public ManageDBSolvers(EDACCManageDBMode gui, SolverTableModel solverTableModel) {
        this.gui = gui;
        this.solverTableModel = solverTableModel;
    }

    /**
     * Loads all solvers from the DB and adds it to the Solver table.
     * @throws NoConnectionToDBException
     * @throws SQLException
     */
    public void loadSolvers() throws NoConnectionToDBException, SQLException {
        solverTableModel.clear();
        for (Solver s: SolverDAO.getAll()) {
            solverTableModel.addSolver(s);
        }
    }

    /**
     * Applies the name and the description of a solver.
     * @param name
     * @param description
     */
    public void applySolver(String name, String description) {
        if (currentSolver != null) {
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
    public void saveSolvers() throws SQLException, FileNotFoundException {
        for (Solver s : solverTableModel.getSolvers()) {
            SolverDAO.save(s);
        }

        // TODO save parameters of solver!
    }

    public void newSolver() {
        solverTableModel.addSolver(new Solver());
    }

    public void showSolver(int index) {
        currentSolver = solverTableModel.getSolver(index); // will be null if no solver selected!
        gui.showSolverDetails(currentSolver);
    }

    public void addSolverBinary(File binary) throws FileNotFoundException, IOException, NoSuchAlgorithmException, NoConnectionToDBException, SQLException, SolverAlreadyInDBException {
        if (!binary.exists())
           throw new FileNotFoundException("Couldn't find file \"" + binary.getName() + "\".");
        currentSolver.setBinaryFile(binary);
        currentSolver.setBinaryName(binary.getName());
        currentSolver.setMd5(Util.calculateMD5(binary));
        if (SolverDAO.solverAlreadyInDB(currentSolver) != null) {
            currentSolver.setBinaryFile(null);
            currentSolver.setBinaryName(null);
            currentSolver.setMd5(null);
            throw new SolverAlreadyInDBException();
        }
    }

    public void addSolverCode(File code) throws FileNotFoundException {
        if (!code.exists())
           throw new FileNotFoundException("Couldn't find file \"" + code.getName() + "\".");
        currentSolver.setCodeFile(code);
    }

    /**
     * Removes the current solver from the solver table model.
     * If it is persisted in the db, it will also remove it from the db.
     * @throws SolverIsInExperimentException if the solver is used in an experiment.
     * @throws SQLException if an SQL error occurs while deleting the solver.
     */
    public void removeSolver() throws SolverIsInExperimentException, SQLException {
        try {
            SolverDAO.removeSolver(currentSolver);
        } catch (SolverNotInDBException ex) {
            // if the solver isn't in the db, just remove it from the table model
        } 
        solverTableModel.removeSolver(currentSolver);
    }
}
