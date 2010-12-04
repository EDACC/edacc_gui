/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edacc.properties;

import edacc.EDACCComputeResultProperties;
import edacc.model.ComputationMethodDoesNotExistException;
import edacc.model.Experiment;
import edacc.model.NoConnectionToDBException;
import edacc.model.Property;
import edacc.model.PropertyDAO;
import edacc.model.PropertyNotInDBException;
import edacc.model.Tasks;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Vector;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JTable;

/**
 *
 * @author rretz
 */
public class ComputePropertiesController {
    EDACCComputeResultProperties main;
    JTable tableResultProperty;
    Lock lock =  new ReentrantLock();

    public ComputePropertiesController(EDACCComputeResultProperties main, JTable tableResultProperty){
        this.main = main;
        this.tableResultProperty = tableResultProperty;
    }

    public void loadResultProperties() {
        try {
            try {
                ((PropertySelectionTableModel) tableResultProperty.getModel()).addProperties(PropertyDAO.getAll());
            } catch (ComputationMethodDoesNotExistException ex) {
                Logger.getLogger(ComputePropertiesController.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (NoConnectionToDBException ex) {
            Logger.getLogger(ComputePropertiesController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(ComputePropertiesController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (PropertyNotInDBException ex) {
            Logger.getLogger(ComputePropertiesController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (PropertyTypeNotExistException ex) {
            Logger.getLogger(ComputePropertiesController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ComputePropertiesController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void computProperties(Vector<Property> toCalculate, boolean recompute, Experiment exp, Tasks task) {
        try {
            lock.lock();
            Condition condition = lock.newCondition();
            Thread compute = new Thread(new PropertyComputationController(exp, toCalculate, recompute, task, condition, lock));
            compute.start();
            try {
                condition.await();
            } catch ( InterruptedException e ) {

            } finally {
                lock.unlock();
            }
        } catch (PropertyTypeNotExistException ex) {
            Logger.getLogger(ComputePropertiesController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ComputePropertiesController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (PropertyNotInDBException ex) {
            Logger.getLogger(ComputePropertiesController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ComputationMethodDoesNotExistException ex) {
            Logger.getLogger(ComputePropertiesController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoConnectionToDBException ex) {
            Logger.getLogger(ComputePropertiesController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(ComputePropertiesController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}
