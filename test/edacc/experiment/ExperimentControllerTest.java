/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edacc.experiment;

import edacc.EDACCExperimentMode;
import java.sql.SQLException;
import edacc.manageDB.ManageDBSolversTest;
import java.util.logging.Logger;
import java.util.logging.Level;
import edacc.model.DatabaseConnector;
import edacc.model.Experiment;
import edacc.model.Experiment.Cost;
import edacc.model.VerifierConfiguration;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Gregor
 */
public class ExperimentControllerTest {
    
    public ExperimentControllerTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
        String hostname = "edacc3.informatik.uni-ulm.de";
        int port = 3306;
        String username = "edacc";
        String database = "test";
        String password = "edaccteam";
        int maxconnections = 1;
        try {
            DatabaseConnector.getInstance().connect(hostname, port, username, database, password, false, false, maxconnections);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ManageDBSolversTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(ManageDBSolversTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
        DatabaseConnector.getInstance().disconnect();
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of createExperiment method, of class ExperimentController.
     */
    @Test
    public void testCreateExperiment() throws Exception {
        System.out.println("createExperiment");
        String name = "TestExperiment";
        String description = "Only for Testing";
        boolean configurationExp = false;
        Cost defaultCost = Cost.resultTime;
        Integer solverOutputPreserveFirst = null;
        Integer solverOutputPreserveLast = null;
        Integer watcherOutputPreserveFirst = null;
        Integer watcherOutputPreserveLast = null;
        Integer verifierOutputPreserveFirst = null;
        Integer verifierOutputPreserveLast = null;
        VerifierConfiguration verifierConfig = null;
        edacc.model.Cost cost = null;
        boolean minimize = false;
        Double costPenalty = null;
        EDACCExperimentMode main = null;
        try{
            main = new EDACCExperimentMode();
        }catch(ClassCastException e){
            
        }
        ExperimentController instance = new ExperimentController(main);
        
        Experiment result = instance.createExperiment(name, description, configurationExp, defaultCost, solverOutputPreserveFirst, solverOutputPreserveLast, watcherOutputPreserveFirst, watcherOutputPreserveLast, verifierOutputPreserveFirst, verifierOutputPreserveLast, verifierConfig, cost, minimize, costPenalty);
        
        Experiment expResult = instance.getExperiment(name);
        assertEquals(expResult, result);
        
        //clean up
        instance.removeExperiment(expResult.getId());
    }
}
