/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edacc.manageDB;

import java.util.logging.Logger;
import java.sql.SQLException;
import java.util.logging.Level;
import edacc.model.DatabaseConnector;
import edacc.model.ResultCode;
import edacc.model.ResultCodeDAO;
import java.util.LinkedList;
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
public class ManageDBResultCodesTest {
    
    public ManageDBResultCodesTest() {
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
     * Test of saveNewCode method, of class ManageDBResultCodes.
     */
    @Test
    public void testSaveNewCode() throws Exception {
        System.out.println("saveNewCode");
        int code = 42;
        String description = "The Answer to the Ultimate Question of Life, the Universe, and Everything";
        ManageDBResultCodes writeInstance = ManageDBResultCodes.getInstance();
        
        writeInstance.saveNewCode(code, description);
        
        LinkedList<ResultCode> all = ResultCodeDAO.getAll();
        assertTrue(all.contains(new ResultCode(code, description)));
        
        LinkedList<ResultCode> remove = new LinkedList<ResultCode>();
        remove.add(ResultCodeDAO.getByResultCode(code));
        ResultCodeDAO.remove(remove);
        
    }
}
