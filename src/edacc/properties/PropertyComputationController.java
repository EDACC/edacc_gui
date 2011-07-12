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
import java.util.ArrayList;
import java.util.Vector;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author rretz
 */
public class PropertyComputationController implements Runnable {

    private int availableProcessors;
    LinkedBlockingQueue<InstanceHasProperty> instancePropertyQueue;
    LinkedBlockingQueue<ExperimentResultHasProperty> resultPropertyQueue;
    boolean recompute;
    private Integer jobs = new Integer(0);
    private Tasks task;
    private int allJobs;
    private Condition condition;
    private Lock lock;

    public Vector<Exception> getExceptionCollector() {
        return exceptionCollector;
    }
    private Vector<Exception> exceptionCollector;

    public PropertyComputationController(ArrayList<ExperimentResult> results, Vector<Property> givenProperties, boolean recompute, Tasks task, Lock lock) throws NoConnectionToDBException, SQLException, PropertyTypeNotExistException, IOException, PropertyNotInDBException, ComputationMethodDoesNotExistException {
        this.condition = lock.newCondition();
        this.task = task;
        this.lock = lock;
        // availableProcessors = Runtime.getRuntime().availableProcessors();
        availableProcessors = DatabaseConnector.getInstance().getMaxconnections();
        this.task.setOperationName("compute properties");
        this.task.setStatus("initialize the computation");
        this.recompute = recompute;
        try {
            createJobQueue(results, givenProperties);
        } catch (ExpResultHasSolvPropertyNotInDBException ex) {
            Logger.getLogger(PropertyComputationController.class.getName()).log(Level.SEVERE, null, ex);
        }
        allJobs = resultPropertyQueue.size();
        exceptionCollector = new Vector<Exception>();
        task.setStatus("computed " + (allJobs - resultPropertyQueue.size()) + " of " + allJobs + " properties");
        task.setTaskProgress(((float) (allJobs - resultPropertyQueue.size())) / ((float) allJobs));
    }

    public PropertyComputationController(Vector<Instance> instances, Vector<Property> givenProperties, Tasks task, Lock lock) {
        this.condition = lock.newCondition();
        this.task = task;
        this.lock = lock;
        //availableProcessors = Runtime.getRuntime().availableProcessors();
        availableProcessors = DatabaseConnector.getInstance().getMaxconnections();
        this.task.setOperationName("compute properties");
        this.task.setStatus("initialize the computation");
        createJobQueue(instances, givenProperties);
        allJobs = instancePropertyQueue.size();
        exceptionCollector = new Vector<Exception>();
        task.setStatus("computed " + (allJobs - instancePropertyQueue.size()) + " of " + allJobs + " properties");
        task.setTaskProgress(((float) (allJobs - instancePropertyQueue.size())) / ((float) allJobs));
    }

    @Override
    public void run() {
        task.setOperationName("Property computation");
        for (int i = 0; i < availableProcessors; i++) {
            if (instancePropertyQueue != null) {
                if (i == 0 && instancePropertyQueue.isEmpty()) {
                    task.cancel(true);
                    lock.lock();
                    try {
                        condition.signal();
                    } finally {
                        lock.unlock();
                    }
                    return;
                }
                if (instancePropertyQueue.isEmpty()) {
                    jobs = i;
                    return;
                }
                try {
                    
                    new Thread(new PropertyComputationUnit(instancePropertyQueue.take(), this)).start();
                } catch (InterruptedException ex) {
                    Logger.getLogger(PropertyComputationController.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else if (resultPropertyQueue != null) {
                if (resultPropertyQueue.isEmpty() && i == 0) {
                    lock.lock();
                    try {
                        condition.signal();
                    } finally {
                        lock.unlock();
                    }
                    return;
                }
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
        PropertyComputationUnit unit = null;
        if (instancePropertyQueue != null) {
            if (!instancePropertyQueue.isEmpty()) {
                try {
                    unit = new PropertyComputationUnit(instancePropertyQueue.take(), this);
                    synchronized (PropertyComputationUnit.sync) {
                        jobs++;
                    }
                    task.setStatus("computed " + (allJobs - instancePropertyQueue.size()) + " of " + allJobs + " properties");
                    task.setTaskProgress(((float) (allJobs - instancePropertyQueue.size())) / ((float) allJobs));
                } catch (InterruptedException ex) {
                    Logger.getLogger(PropertyComputationController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        } else if (resultPropertyQueue != null) {
            if (!resultPropertyQueue.isEmpty()) {
                try {
                    unit = new PropertyComputationUnit(resultPropertyQueue.take(), this);
                    synchronized (PropertyComputationUnit.sync) {
                        jobs++;
                    }
                    task.setStatus("computed " + (allJobs - resultPropertyQueue.size()) + " of " + allJobs + " properties");
                    task.setTaskProgress(((float) (allJobs - resultPropertyQueue.size())) / ((float) allJobs));
                } catch (InterruptedException ex) {
                    Logger.getLogger(PropertyComputationController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

        if (unit != null) {
            unit.run();
        }
        synchronized (PropertyComputationUnit.sync) {
            jobs--;
        }
        if (instancePropertyQueue != null) {
            if (jobs == 0 && instancePropertyQueue.isEmpty()) {              
                lock.lock();
                try {
                    condition.signal();
                } finally {
                    lock.unlock();                   
                }
                task.cancel(true);
            }

        } else if (resultPropertyQueue != null) {
            if (jobs == 0 && resultPropertyQueue.isEmpty()) {
                task.cancel(true);
                lock.lock();
                try {
                    condition.signal();
                } finally {
                    lock.unlock();
                }
                return;
            }
        }



    }

    private void createJobQueue(ArrayList<ExperimentResult> results, Vector<Property> givenProperties) throws NoConnectionToDBException, SQLException, ExpResultHasSolvPropertyNotInDBException {
        resultPropertyQueue = new LinkedBlockingQueue<ExperimentResultHasProperty>();
        // create all experimentResultHasProperty objects and adds them into the resultPropertyQueue
        for (ExperimentResult result : results) {
            for (int i = 0; i < givenProperties.size(); i++) {
                try {
                    try {
                        ExperimentResultHasProperty tmp = ExperimentResultHasPropertyDAO.getByExperimentResultAndResultProperty(result.getId(), givenProperties.get(i).getId());
                        resultPropertyQueue.add(tmp);
                        if (recompute) {
                            tmp.setValue(new Vector<String>());
                            ExperimentResultHasPropertyDAO.save(tmp);
                        }
                    } catch (ExpResultHasSolvPropertyNotInDBException ex) {
                        resultPropertyQueue.add(ExperimentResultHasPropertyDAO.createExperimentResultHasPropertyDAO(result.getId(), givenProperties.get(i)));
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
        for (int i = 0; i < instances.size(); i++) {
            for (int j = 0; j < givenProperties.size(); j++) {
                try {
                    InstanceHasProperty tmp = InstanceHasPropertyDAO.getByInstanceAndProperty(instances.get(i), givenProperties.get(j));
                    instancePropertyQueue.add(tmp);
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

    public synchronized boolean completed() {
        synchronized (this.jobs) {
            if (instancePropertyQueue != null) {
                if (jobs <= 0 && instancePropertyQueue.isEmpty()) {
                    return true;
                } else {
                    return false;
                }
            } else if (resultPropertyQueue != null) {
                if (jobs <= 0 && resultPropertyQueue.isEmpty()) {
                    return true;
                } else {
                    return false;
                }
            }
            return true;
        }
    }

    public void exceptionCausedByThread(Exception ex) {
        ex.printStackTrace();
        exceptionCollector.add(ex);
    }
}
