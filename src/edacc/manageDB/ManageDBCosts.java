package edacc.manageDB;

import edacc.EDACCApp;
import edacc.EDACCCostBinaryDialog;
import edacc.EDACCManageDBMode;
import edacc.model.Cost;
import edacc.model.CostBinary;
import edacc.model.CostDAO;
import edacc.model.DatabaseConnector;
import edacc.model.NoConnectionToDBException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.SequenceInputStream;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import javax.swing.JOptionPane;

/**
 *
 * @author simon
 */
public class ManageDBCosts {

    private EDACCManageDBMode gui;
    private ManageDBSolvers solverController;

    public ManageDBCosts(EDACCManageDBMode gui, ManageDBSolvers solverController) {
        this.gui = gui;
        this.solverController = solverController;
    }

    public void addCostBinary(File[] binary) throws FileNotFoundException, IOException, NoSuchAlgorithmException, NoConnectionToDBException, SQLException, SolverAlreadyInDBException {
        if (binary.length == 0) {
            return;
        }
        Arrays.sort(binary);
        CostBinary b = new CostBinary(solverController.getCurrentSolver());
        b.setBinaryArchive(binary);
        b.setBinaryName(binary[0].getName());
        try {
            FileInputStreamList is = new FileInputStreamList(binary);
            SequenceInputStream seq = new SequenceInputStream(is);
            String md5 = Util.calculateMD5(seq);
            b.setMd5(md5);
            Util.removeCommonPrefix(b);
            EDACCCostBinaryDialog dialog = new EDACCCostBinaryDialog(EDACCApp.getApplication().getMainFrame(), b, this, EDACCCostBinaryDialog.DialogMode.CREATE_MODE);
            EDACCApp.getApplication().show(dialog);
            gui.showCostBinaryDetails(solverController.getCurrentSolver().getCostBinaries());
        } catch (NoSuchElementException e) {
            JOptionPane.showMessageDialog(gui, "You have to choose some files!", "No files chosen!", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void selectCostBinary(boolean selected) {
        gui.enableCostBinaryButtons(selected);
    }

    public void addCostBinary(CostBinary costBinary) {
        solverController.getCurrentSolver().addCostBinary(costBinary);
    }

    public List<Cost> getCosts() throws SQLException {
        return CostDAO.getAllCosts();
    }
    
    public void reload() {
        CostDAO.clearCache();
    }

    public void saveCacheAndCosts(List<Cost> costs) throws SQLException {
        boolean autocommit = DatabaseConnector.getInstance().getConn().getAutoCommit();
        try {
            DatabaseConnector.getInstance().getConn().setAutoCommit(false);
            CostDAO.saveCachedCosts();
            for (Cost c : costs) {
                CostDAO.saveCost(c);
            }
        } catch (Throwable t) {
            if (autocommit) {
                DatabaseConnector.getInstance().getConn().rollback();
            }
            if (t instanceof SQLException)
                throw (SQLException) t;
            if (t instanceof Error) 
                throw (Error) t;
        } finally {
            if (autocommit) {
                DatabaseConnector.getInstance().getConn().commit();
                DatabaseConnector.getInstance().getConn().setAutoCommit(autocommit);
            }
        }
    }

    public void removeCostBinary(CostBinary costBinary) throws SQLException, IOException, NoSuchAlgorithmException {
        costBinary.setDeleted();
        CostDAO.saveBinary(costBinary);
    }
    
}
