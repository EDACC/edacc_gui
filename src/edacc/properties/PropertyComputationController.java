/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edacc.properties;

import edacc.model.DatabaseConnector;
import edacc.model.ExpResultHasSolvPropertyNotInDBException;
import edacc.model.Experiment;
import edacc.model.ExperimentResult;
import edacc.model.ExperimentResultDAO;
import edacc.model.ExperimentResultHasSolverProperty;
import edacc.model.ExperimentResultHasPropertyDAO;
import edacc.model.ExperimentResultNotInDBException;
import edacc.model.NoConnectionToDBException;
import edacc.model.Property;
import edacc.model.PropertyNotInDBException;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author rretz
 */
public class PropertyComputationController implements Runnable{
    final Lock lock = new ReentrantLock(); 
    private int availableProcessors = 10 ;
    Experiment exp;
    Vector<Property> toParse;
    Vector<Property> parameterResProp;
    Vector<ExperimentResultHasSolverProperty> queue;
    boolean recompute;

    public PropertyComputationController(Experiment exp, Vector<Property> givenProperties, boolean recompute) throws NoConnectionToDBException, SQLException{
        //availableProcessors = Runtime.getRuntime().availableProcessors();
        /*this.exp = exp;
        this.toParse = givenProperties;
        this.recompute = recompute;    
        createParserJobs();*/
    }

    @Override
    public void run() {
        for(int i = 0; i < availableProcessors; i++){
            /*if(queue.isEmpty())
                break;*/
            new Thread(new PropertyComputationUnit(null, this)).start();
        }
    }

    private void createParserJobs() throws NoConnectionToDBException, SQLException, SolverPropertyTypeNotExistException, IOException, PropertyNotInDBException {
        queue = new Vector<ExperimentResultHasSolverProperty>();
        PreparedStatement ps = DatabaseConnector.getInstance().getConn().prepareStatement("SELECT idJob FROM " +
                "ExperimentResults WHERE Experiment_idExperiment=?;");
        ps.setInt(exp.getId(), 1);
        ResultSet rs = ps.executeQuery();

        // create all experimentResultHasSolverProperty object
        while(rs.next()){
            for(int i = 0; i < toParse.size(); i++){
                try {
                    ExperimentResult res = ExperimentResultDAO.getById( rs.getInt(1));
                    try {
                        ExperimentResultHasSolverProperty tmp = ExperimentResultHasPropertyDAO.getByExperimentResultAndResultProperty(res, toParse.get(i));
                        queue.add(tmp);
                        if(recompute){
                            tmp.setValue(new Vector<String>());
                            ExperimentResultHasPropertyDAO.save(tmp);
                        }
                    } catch (ExpResultHasSolvPropertyNotInDBException ex) {
                        queue.add(ExperimentResultHasPropertyDAO.createExperimentResultHasResultPropertyDAO(res, toParse.get(i)));
                    } catch (PropertyNotInDBException ex) {
                        Logger.getLogger(PropertyComputationController.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (SolverPropertyTypeNotExistException ex) {
                        Logger.getLogger(PropertyComputationController.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (IOException ex) {
                        Logger.getLogger(PropertyComputationController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } catch (ExperimentResultNotInDBException ex) {
                    Logger.getLogger(PropertyComputationController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

        }
    }

    public void callback() {
       // if(!queue.isEmpty())
            new Thread(new PropertyComputationUnit(null, this)).start();
        
    }

}
