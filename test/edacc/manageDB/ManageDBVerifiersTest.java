/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edacc.manageDB;

import java.util.List;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.io.SequenceInputStream;
import java.io.File;
import java.sql.SQLException;
import java.util.logging.Logger;
import java.util.logging.Level;
import edacc.model.DatabaseConnector;
import edacc.model.Verifier;
import edacc.model.VerifierDAO;
import edacc.model.VerifierParameter;
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
public class ManageDBVerifiersTest {
    
    public ManageDBVerifiersTest() {
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
     * Test of addVerifier method, of class ManageDBVerifiers.
     */
    @Test
    public void testAddVerifier() {
        System.out.println("addVerifier");
        //Select verifier files
        String path = System.getProperty("user.dir") + File.separatorChar + "test" + File.separatorChar + "edacc" + File.separatorChar + "manageDB" + File.separatorChar;
        File[] files = new File[2];
        files[0] = new File(path + "verifier1.txt");
        files[1] = new File(path + "verifier2.txt");
        
        //create verifier and set atributes 
        Verifier verifier = new Verifier();
        verifier.setName("TestVeri");
        verifier.setDescription("only for testing");
        verifier.setRunCommand("start");
        verifier.setRunPath("von/hier");
        verifier.setFiles(files);
        
        List <VerifierParameter> parameters = new LinkedList<VerifierParameter>();
        
        VerifierParameter para0 = new VerifierParameter();
        para0.setName("Testpara 0");
        para0.setPrefix("-000");
        para0.setOrder(0);
        para0.setMandatory(false);
        para0.setHasValue(false);
        para0.setAttachToPrevious(false);
        para0.setSpace(false);
        
        VerifierParameter para1 = new VerifierParameter();
        para1.setName("Testpara 1");
        para1.setPrefix("-001");
        para1.setOrder(1);
        para1.setMandatory(false);
        para1.setHasValue(false);
        para1.setAttachToPrevious(false);
        para1.setSpace(true);
        
        VerifierParameter para2 = new VerifierParameter();
        para2.setName("Testpara 2");
        para2.setPrefix("-para2");
        para2.setOrder(2);
        para2.setMandatory(false);
        para2.setHasValue(false);
        para2.setAttachToPrevious(true);
        para2.setSpace(false);
        
        VerifierParameter para3 = new VerifierParameter();
        para3.setName("Testpara 3");
        para3.setPrefix("-3para");
        para3.setOrder(3);
        para3.setMandatory(false);
        para3.setHasValue(false);
        para3.setAttachToPrevious(true);
        para3.setSpace(true);
        
        VerifierParameter para4 = new VerifierParameter();
        para4.setName("Testpara 4");
        para4.setPrefix("-vier");
        para4.setOrder(4);
        para4.setMandatory(false);
        para4.setHasValue(true);
        para4.setAttachToPrevious(false);
        para4.setSpace(false);
        
        VerifierParameter para5 = new VerifierParameter();
        para5.setName("Testpara 5");
        para5.setPrefix("-p5");
        para5.setOrder(5);
        para5.setMandatory(false);
        para5.setHasValue(true);
        para5.setAttachToPrevious(false);
        para5.setSpace(true);
        
        VerifierParameter para6 = new VerifierParameter();
        para6.setName("Testpara 6");
        para6.setPrefix("-6p");
        para6.setOrder(6);
        para6.setMandatory(false);
        para6.setHasValue(true);
        para6.setAttachToPrevious(true);
        para6.setSpace(false);
        
        VerifierParameter para7 = new VerifierParameter();
        para7.setName("Testpara 7");
        para7.setPrefix("-07");
        para7.setOrder(7);
        para7.setMandatory(false);
        para7.setHasValue(true);
        para7.setAttachToPrevious(true);
        para7.setSpace(true);
        
        VerifierParameter para8 = new VerifierParameter();
        para8.setName("Testpara 8");
        para8.setPrefix("-p8");
        para8.setOrder(8);
        para8.setMandatory(true);
        para8.setHasValue(false);
        para8.setAttachToPrevious(false);
        para8.setSpace(false);
        
        VerifierParameter para9 = new VerifierParameter();
        para9.setName("Testpara 9");
        para9.setPrefix("-p9");
        para9.setOrder(9);
        para9.setMandatory(true);
        para9.setHasValue(false);
        para9.setAttachToPrevious(false);
        para9.setSpace(true);
        
        VerifierParameter para10 = new VerifierParameter();
        para10.setName("Testpara 10");
        para10.setPrefix("-0010");
        para10.setOrder(10);
        para10.setMandatory(true);
        para10.setHasValue(false);
        para10.setAttachToPrevious(true);
        para10.setSpace(false);
        
        VerifierParameter para11 = new VerifierParameter();
        para11.setName("Testpara 11");
        para11.setPrefix("-0011");
        para11.setOrder(11);
        para11.setMandatory(true);
        para11.setHasValue(false);
        para11.setAttachToPrevious(true);
        para11.setSpace(true);
        
        VerifierParameter para12 = new VerifierParameter();
        para12.setName("Testpara 12");
        para12.setPrefix("-0012");
        para12.setOrder(12);
        para12.setMandatory(true);
        para12.setHasValue(true);
        para12.setAttachToPrevious(false);
        para12.setSpace(false);
        
        VerifierParameter para13 = new VerifierParameter();
        para13.setName("Testpara 13");
        para13.setPrefix("-0013");
        para13.setOrder(13);
        para13.setMandatory(true);
        para13.setHasValue(true);
        para13.setAttachToPrevious(false);
        para13.setSpace(true);
        
        VerifierParameter para14 = new VerifierParameter();
        para14.setName("Testpara 14");
        para14.setPrefix("-0014");
        para14.setOrder(14);
        para14.setMandatory(true);
        para14.setHasValue(true);
        para14.setAttachToPrevious(true);
        para14.setSpace(false);
        
        VerifierParameter para15 = new VerifierParameter();
        para15.setName("Testpara 15");
        para15.setPrefix("-0015");
        para15.setOrder(15);
        para15.setMandatory(true);
        para15.setHasValue(true);
        para15.setAttachToPrevious(true);
        para15.setSpace(true);
        
        
        parameters.add(para0);
        parameters.add(para1);
        parameters.add(para2);
        parameters.add(para3);
        parameters.add(para4);
        parameters.add(para5);
        parameters.add(para6);
        parameters.add(para7);
        parameters.add(para8);
        parameters.add(para9);
        parameters.add(para10);
        parameters.add(para11);
        parameters.add(para12);
        parameters.add(para13);
        parameters.add(para14);
        parameters.add(para15);
        
        verifier.setParameters(parameters);
        
        //set md5
        FileInputStreamList is = new FileInputStreamList(files);
        SequenceInputStream seq = new SequenceInputStream(is);
        String md5;
        try {
            md5 = Util.calculateMD5(seq);
            verifier.setMd5(md5);
        } catch (IOException ex) {
            Logger.getLogger(ManageDBVerifiersTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(ManageDBSolversTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        //save the verifier to the DB
        VerifierTableModel vtm = new VerifierTableModel();
        ManageDBVerifiers writeInstance = new ManageDBVerifiers(vtm);
        writeInstance.addVerifier(verifier);
        try {
            writeInstance.save();
        } catch (SQLException ex) {
            Logger.getLogger(ManageDBVerifiersTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        //load all Verifiers from the DB
        VerifierTableModel readVtm = new VerifierTableModel();
        readVtm.setRowCount(0);
        ManageDBVerifiers readInstance = new ManageDBVerifiers(readVtm);
        List <Verifier> veri = null;
        try {
            veri = VerifierDAO.getAllVerifiers();
        } catch (SQLException ex) {
            Logger.getLogger(ManageDBVerifiersTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        Verifier v1 = veri.get(0);
        assertEquals(verifier, v1);
        
        //clean up
        readInstance.markAsDeleted(verifier);
        try {
            readInstance.save();
        } catch (SQLException ex) {
            Logger.getLogger(ManageDBVerifiersTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
