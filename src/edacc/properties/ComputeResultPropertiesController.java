/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edacc.properties;

import edacc.EDACCComputeResultProperties;
import edacc.model.NoConnectionToDBException;
import edacc.model.SolverProperty;
import edacc.model.SolverPropertyDAO;
import edacc.model.SolverPropertyNotInDBException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JTable;

/**
 *
 * @author rretz
 */
public class ComputeResultPropertiesController {
    EDACCComputeResultProperties main;
    JTable tableResultProperty;

    public ComputeResultPropertiesController(EDACCComputeResultProperties main, JTable tableResultProperty){
        this.main = main;
        this.tableResultProperty = tableResultProperty;
    }

    public void loadResultProperties() {
        try {
            ((ResultPropertySelectionTableModel) tableResultProperty.getModel()).addResultProperties(SolverPropertyDAO.getAll());
        } catch (NoConnectionToDBException ex) {
            Logger.getLogger(ComputeResultPropertiesController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(ComputeResultPropertiesController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SolverPropertyNotInDBException ex) {
            Logger.getLogger(ComputeResultPropertiesController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SolverPropertyTypeNotExistException ex) {
            Logger.getLogger(ComputeResultPropertiesController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ComputeResultPropertiesController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void computeResultProperties(Vector<SolverProperty> toCalculate, boolean recompute) {
        try {
            Thread compute = new Thread(new PropertyComputationController(null, null, true));
            compute.start();
        } catch (NoConnectionToDBException ex) {
            Logger.getLogger(ComputeResultPropertiesController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(ComputeResultPropertiesController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
