/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edacc.properties;

import edacc.model.ComputationMethodDoesNotExistException;
import edacc.model.DatabaseConnector;
import edacc.model.ExpResultHasSolvPropertyNotInDBException;
import edacc.model.Experiment;
import edacc.model.ExperimentResult;
import edacc.model.ExperimentResultDAO;
import edacc.model.ExperimentResultHasProperty;
import edacc.model.ExperimentResultHasPropertyDAO;
import edacc.model.ExperimentResultNotInDBException;
import edacc.model.Instance;
import edacc.model.InstanceHasProperty;
import edacc.model.InstanceHasPropertyDAO;
import edacc.model.InstanceHasPropertyNotInDBException;
import edacc.model.InstanceNotInDBException;
import edacc.model.NoConnectionToDBException;
import edacc.model.Property;
import edacc.model.PropertyNotInDBException;
import edacc.model.Tasks;
import edacc.satinstances.ConvertException;
import edacc.satinstances.InvalidVariableException;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author rretz
 */
public class PropertyComputationController implements Runnable{
    private int availableProcessors;
    LinkedBlockingQueue<InstanceHasProperty> instancePropertyQueue;
     LinkedBlockingQueue<ExperimentResultHasProperty> resultPropertyQueue;
    boolean recompute;
    private int jobs;
    private Tasks task;
    private int allJobs;

    public PropertyComputationController(Experiment exp, Vector<Property> givenProperties, boolean recompute, Tasks task) throws NoConnectionToDBException, SQLException, PropertyTypeNotExistException, IOException, PropertyNotInDBException, ComputationMethodDoesNotExistException{
        this.task = task;
        availableProcessors = Runtime.getRuntime().availableProcessors();
        this.task.setOperationName("compute properties");
        this.task.setStatus("initialize the computation");
        this.recompute = recompute;    
        try {
            createJobQueue(exp, givenProperties);
        } catch (ExpResultHasSolvPropertyNotInDBException ex) {
            Logger.getLogger(PropertyComputationController.class.getName()).log(Level.SEVERE, null, ex);
        }
        allJobs = resultPropertyQueue.size();
        this.task.setStatus("compute");
    }

    public PropertyComputationController(Vector<Instance> instances, Vector<Property> givenProperties){
        availableProcessors = Runtime.getRuntime().availableProcessors();
        createJobQueue(instances, givenProperties);
    }

    @Override
    public void run() {

        for(int i = 0; i < availableProcessors; i++){
            if(instancePropertyQueue != null){
                if(instancePropertyQueue.isEmpty()){
                    jobs = i;
                    return;
                }
                try {
                    new Thread(new PropertyComputationUnit(instancePropertyQueue.take(), this)).start();
                } catch (InterruptedException ex) {
                    Logger.getLogger(PropertyComputationController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }else if(resultPropertyQueue != null){
                try {
                    if (resultPropertyQueue.isEmpty()) {
                        jobs = i;
                        return;
                    }
                    new Thread(new PropertyComputationUnit(resultPropertyQueue.take(), this)).start();
                } catch (InterruptedException ex) {
                    Logger.getLogger(PropertyComputationController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }           
        }
        jobs = availableProcessors;
    }

    public void callback() {
        
        if(instancePropertyQueue != null){
            if(!instancePropertyQueue.isEmpty())
                try {
                new Thread(new PropertyComputationUnit(instancePropertyQueue.take(), this)).start();
                jobs++;
            } catch (InterruptedException ex) {
                Logger.getLogger(PropertyComputationController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }else if(resultPropertyQueue != null){
            if(!resultPropertyQueue.isEmpty())
                try {
                new Thread(new PropertyComputationUnit(resultPropertyQueue.take(), this)).start();
                jobs++;
            } catch (InterruptedException ex) {
                Logger.getLogger(PropertyComputationController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        jobs--;
        if(jobs == 0){
          task.cancel(true);
          return;
        }

    }

    private void createJobQueue(Experiment exp, Vector<Property> givenProperties) throws NoConnectionToDBException, SQLException, ExpResultHasSolvPropertyNotInDBException {
        resultPropertyQueue = new  LinkedBlockingQueue<ExperimentResultHasProperty>();
        PreparedStatement ps = DatabaseConnector.getInstance().getConn().prepareStatement("SELECT idJob FROM " +
                "ExperimentResults WHERE Experiment_idExperiment=?;");
        ps.setInt(1, exp.getId());
        ResultSet rs = ps.executeQuery();
        // create all experimentResultHasProperty objects and adds them into the resultPropertyQueue
        while(rs.next()){
            for(int i = 0; i < givenProperties.size(); i++){
                try {
                        try {
                            ExperimentResultHasProperty tmp = ExperimentResultHasPropertyDAO.getByExperimentResultAndResultProperty(rs.getInt(1), givenProperties.get(i).getId());
                            resultPropertyQueue.add(tmp);
                            if(recompute){
                                tmp.setValue(new Vector<String>());
                                ExperimentResultHasPropertyDAO.save(tmp);
                            }
                        } catch (ExpResultHasSolvPropertyNotInDBException ex) {
                             resultPropertyQueue.add(ExperimentResultHasPropertyDAO.createExperimentResultHasPropertyDAO(rs.getInt(1), givenProperties.get(i)));
                        } catch (PropertyTypeNotExistException ex) {
                            Logger.getLogger(PropertyComputationController.class.getName()).log(Level.SEVERE, null, ex);
                        }

                } catch (ExperimentResultNotInDBException ex) {
                    Logger.getLogger(PropertyComputationController.class.getName()).log(Level.SEVERE, null, ex);           
                } catch (IOException ex) {
                    Logger.getLogger(PropertyComputationController.class.getName()).log(Level.SEVERE, null, ex);
                } catch (PropertyNotInDBException ex) {
                    Logger.getLogger(PropertyComputationController.class.getName()).log(Level.SEVERE, null, ex);
                } catch (ComputationMethodDoesNotExistException ex) {
                    Logger.getLogger(PropertyComputationController.class.getName()).log(Level.SEVERE, null, ex);
                }                             
            }
        }
    }

    private void createJobQueue(Vector<Instance> instances, Vector<Property> givenProperties) {
        instancePropertyQueue = new LinkedBlockingQueue<InstanceHasProperty>();
        // create all InstanceHasProperty objects and adds them to the instancePropertyQueue
        for(int i = 0; i < instances.size(); i++){
            for(int j = 0; j < givenProperties.size(); j++){
                try {
                    InstanceHasProperty tmp = InstanceHasPropertyDAO.getByInstanceAndProperty(instances.get(i), givenProperties.get(j));  
                } catch (NoConnectionToDBException ex) {
                    Logger.getLogger(PropertyComputationController.class.getName()).log(Level.SEVERE, null, ex);
                } catch (SQLException ex) {
                    Logger.getLogger(PropertyComputationController.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(PropertyComputationController.class.getName()).log(Level.SEVERE, null, ex);
                } catch (PropertyNotInDBException ex) {
                    Logger.getLogger(PropertyComputationController.class.getName()).log(Level.SEVERE, null, ex);
                } catch (PropertyTypeNotExistException ex) {
                    Logger.getLogger(PropertyComputationController.class.getName()).log(Level.SEVERE, null, ex);
                } catch (ComputationMethodDoesNotExistException ex) {
                    Logger.getLogger(PropertyComputationController.class.getName()).log(Level.SEVERE, null, ex);
                } catch (InstanceHasPropertyNotInDBException ex) {
                    try {
                        instancePropertyQueue.add(InstanceHasPropertyDAO.createInstanceHasInstanceProperty(instances.get(i), givenProperties.get(j)));
                    } catch (SQLException ex1) {
                        Logger.getLogger(PropertyComputationController.class.getName()).log(Level.SEVERE, null, ex1);
                    } catch (ConvertException ex1) {
                        Logger.getLogger(PropertyComputationController.class.getName()).log(Level.SEVERE, null, ex1);
                    } catch (IOException ex1) {
                        Logger.getLogger(PropertyComputationController.class.getName()).log(Level.SEVERE, null, ex1);
                    } catch (InvalidVariableException ex1) {
                        Logger.getLogger(PropertyComputationController.class.getName()).log(Level.SEVERE, null, ex1);
                    } catch (InstanceNotInDBException ex1) {
                        Logger.getLogger(PropertyComputationController.class.getName()).log(Level.SEVERE, null, ex1);
                    }
                }
                

            }
        }
    }

}
