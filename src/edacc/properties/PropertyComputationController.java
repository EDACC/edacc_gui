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
import edacc.satinstances.ConvertException;
import edacc.satinstances.InvalidVariableException;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author rretz
 */
public class PropertyComputationController implements Runnable{
    private int availableProcessors;
    Vector<InstanceHasProperty> instancePropertyQueue;
    Vector<ExperimentResultHasProperty> resultPropertyQueue;
    boolean recompute;

    public PropertyComputationController(Experiment exp, Vector<Property> givenProperties, boolean recompute) throws NoConnectionToDBException, SQLException, PropertyTypeNotExistException, IOException, PropertyNotInDBException, ComputationMethodDoesNotExistException{
        availableProcessors = Runtime.getRuntime().availableProcessors();
        this.recompute = recompute;    
        createJobQueue(exp, givenProperties);
    }

    public PropertyComputationController(Vector<Instance> instances, Vector<Property> givenProperties){
        availableProcessors = Runtime.getRuntime().availableProcessors();
        createJobQueue(instances, givenProperties);
    }

    @Override
    public void run() {
        for(int i = 0; i < availableProcessors; i++){
            if(instancePropertyQueue != null){
                if(instancePropertyQueue.isEmpty())
                    break;
                 new Thread(new PropertyComputationUnit(instancePropertyQueue.get(i), this)).start();
            }else if(resultPropertyQueue != null){
                if(resultPropertyQueue.isEmpty())
                    break;
                new Thread(new PropertyComputationUnit(resultPropertyQueue.get(i), this)).start();
            }           
        }
    }

    public void callback() {
        if(instancePropertyQueue != null){
            if(!instancePropertyQueue.isEmpty())
                new Thread(new PropertyComputationUnit(instancePropertyQueue.get(0), this)).start();
        }else if(resultPropertyQueue != null){
            if(!resultPropertyQueue.isEmpty())
                new Thread(new PropertyComputationUnit(resultPropertyQueue.get(0), this)).start();
        }
    }

    private void createJobQueue(Experiment exp, Vector<Property> givenProperties) throws NoConnectionToDBException, SQLException {
        resultPropertyQueue = new Vector<ExperimentResultHasProperty>();
        PreparedStatement ps = DatabaseConnector.getInstance().getConn().prepareStatement("SELECT idJob FROM " +
                "ExperimentResults WHERE Experiment_idExperiment=?;");
        ps.setInt(exp.getId(), 1);
        ResultSet rs = ps.executeQuery();
        // create all experimentResultHasProperty objects and adds them into the resultPropertyQueue
        while(rs.next()){
            for(int i = 0; i < givenProperties.size(); i++){
                try {
                    ExperimentResult expRes = ExperimentResultDAO.getById(rs.getInt(1));
                        try {
                            ExperimentResultHasProperty tmp = ExperimentResultHasPropertyDAO.getByExperimentResultAndResultProperty(expRes, givenProperties.get(i));
                            resultPropertyQueue.add(tmp);
                            if(recompute){
                                tmp.setValue(new Vector<String>());
                                ExperimentResultHasPropertyDAO.save(tmp);
                            }
                        } catch (ExpResultHasSolvPropertyNotInDBException ex) {
                             resultPropertyQueue.add(ExperimentResultHasPropertyDAO.createExperimentResultHasPropertyDAO(expRes, givenProperties.get(i)));
                        } catch (PropertyTypeNotExistException ex) {
                            Logger.getLogger(PropertyComputationController.class.getName()).log(Level.SEVERE, null, ex);
                        }

                } catch (ExperimentResultNotInDBException ex) {
                    Logger.getLogger(PropertyComputationController.class.getName()).log(Level.SEVERE, null, ex);
                } catch (PropertyTypeNotExistException ex) {
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
        instancePropertyQueue = new Vector<InstanceHasProperty>();
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
