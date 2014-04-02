/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edacc.manageDB;

import java.io.SequenceInputStream;
import edacc.model.SolverIsInExperimentException;
import edacc.model.SolverNotInDBException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Vector;
import edacc.model.DatabaseConnector;
import edacc.model.Parameter;
import java.io.File;
import edacc.model.Solver;
import edacc.model.SolverBinaries;
import edacc.model.SolverDAO;
import edacc.model.Tasks;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
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
public class ManageDBSolversTest {
    
    SolverTableModel solverTableModel;
    ManageDBParameters manageDBParameters;
    edacc.model.Solver newSolver = new Solver();
    edacc.model.Solver newSolver2 = new Solver();
    
    ParameterTableModel parameterTableModel = new ParameterTableModel();
    
    File [] binaryFiles;
    File [] binaryFiles2;
    edacc.model.Parameter para0 = new Parameter();
    edacc.model.Parameter para1 = new Parameter();
    edacc.model.Parameter para2 = new Parameter();
    edacc.model.Parameter para3 = new Parameter();
    edacc.model.Parameter para4 = new Parameter();
    edacc.model.Parameter para5 = new Parameter();
    edacc.model.Parameter para6 = new Parameter();
    edacc.model.Parameter para7 = new Parameter();
    edacc.model.Parameter para8 = new Parameter();
    edacc.model.Parameter para9 = new Parameter();
    edacc.model.Parameter para10 = new Parameter();
    edacc.model.Parameter para11 = new Parameter();
    edacc.model.Parameter para12 = new Parameter();
    edacc.model.Parameter para13 = new Parameter();
    edacc.model.Parameter para14 = new Parameter();
    edacc.model.Parameter para15 = new Parameter();
    edacc.model.Parameter para16 = new Parameter();
    edacc.model.Parameter para17 = new Parameter();

    
    
    

    public ManageDBSolversTest() {
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
    public void setUp() throws IOException {
        //create a SolverTableModell and add some TestSolvers.
        solverTableModel = new SolverTableModel();
        
        SolverBinaries sb = new SolverBinaries(newSolver);
        String path = System.getProperty("user.dir") + File.separatorChar + "test" + File.separatorChar + "edacc" + File.separatorChar + "manageDB" + File.separatorChar;
        binaryFiles = new File[2];
        binaryFiles[0] = new File(path + "binary1.txt");
        binaryFiles[1] = new File(path + "binary2.txt");
        
        sb.setBinaryArchive(binaryFiles);
        FileInputStreamList is = new FileInputStreamList(binaryFiles);
        SequenceInputStream seq = new SequenceInputStream(is);
        String md5;
        try {
            md5 = Util.calculateMD5(seq);
            sb.setMd5(md5);
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(ManageDBSolversTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        sb.setRootDir("");
        sb.setBinaryName("testbinary");
        sb.setRunPath("blaa");
        
        newSolver.setAuthor("Tester");
        newSolver.setName("neuer Solver");
        newSolver.setVersion("v1.0");
        newSolver.addSolverBinary(sb);
        newSolver.setNew();

        
        
        SolverBinaries sb2 = new SolverBinaries(newSolver2);
        
        binaryFiles2 = new File[2];
        binaryFiles2[0] = new File(path + "binary3.txt");
        binaryFiles2[1] = new File(path + "binary4.txt");
        
        sb2.setBinaryArchive(binaryFiles2);
        FileInputStreamList is2 = new FileInputStreamList(binaryFiles2);
        SequenceInputStream seq2 = new SequenceInputStream(is2);
        String md52;
        try {
            md52 = Util.calculateMD5(seq2);
            sb2.setMd5(md52);
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(ManageDBSolversTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        sb2.setRootDir("");
        sb2.setBinaryName("testbinary2");
        sb2.setRunPath("runpath2");
        
        newSolver2.setAuthor("Tester");
        newSolver2.setName("neuer Solver2");
        newSolver2.setVersion("v2.0");
        newSolver2.addSolverBinary(sb2);
        newSolver2.setNew();
        
        
        
        solverTableModel.addSolver(newSolver);
        solverTableModel.addSolver(newSolver2);
        
        
        //Create parameter 
        para0.setName("Testpara 0");
        para0.setPrefix("-000");
        para0.setOrder(0);
        para0.setMandatory(false);
        para0.setHasValue(false);
        para0.setAttachToPrevious(false);
        para0.setSpace(false);
        
        para1.setName("Testpara 1");
        para1.setPrefix("-001");
        para1.setOrder(1);
        para1.setMandatory(false);
        para1.setHasValue(false);
        para1.setAttachToPrevious(false);
        para1.setSpace(true);
        
        para2.setName("Testpara 2");
        para2.setPrefix("-para2");
        para2.setOrder(2);
        para2.setMandatory(false);
        para2.setHasValue(false);
        para2.setAttachToPrevious(true);
        para2.setSpace(false);
        
        para3.setName("Testpara 3");
        para3.setPrefix("-3para");
        para3.setOrder(3);
        para3.setMandatory(false);
        para3.setHasValue(false);
        para3.setAttachToPrevious(true);
        para3.setSpace(true);
        
        para4.setName("Testpara 4");
        para4.setPrefix("-vier");
        para4.setOrder(4);
        para4.setMandatory(false);
        para4.setHasValue(true);
        para4.setAttachToPrevious(false);
        para4.setSpace(false);
        
        para5.setName("Testpara 5");
        para5.setPrefix("-p5");
        para5.setOrder(5);
        para5.setMandatory(false);
        para5.setHasValue(true);
        para5.setAttachToPrevious(false);
        para5.setSpace(true);
        
        para6.setName("Testpara 6");
        para6.setPrefix("-6p");
        para6.setOrder(6);
        para6.setMandatory(false);
        para6.setHasValue(true);
        para6.setAttachToPrevious(true);
        para6.setSpace(false);
        
        para7.setName("Testpara 7");
        para7.setPrefix("-07");
        para7.setOrder(7);
        para7.setMandatory(false);
        para7.setHasValue(true);
        para7.setAttachToPrevious(true);
        para7.setSpace(true);
        
        para8.setName("Testpara 8");
        para8.setPrefix("-p8");
        para8.setOrder(8);
        para8.setMandatory(true);
        para8.setHasValue(false);
        para8.setAttachToPrevious(false);
        para8.setSpace(false);
        
        para9.setName("Testpara 9");
        para9.setPrefix("-p9");
        para9.setOrder(9);
        para9.setMandatory(true);
        para9.setHasValue(false);
        para9.setAttachToPrevious(false);
        para9.setSpace(true);
        
        para10.setName("Testpara 10");
        para10.setPrefix("-0010");
        para10.setOrder(10);
        para10.setMandatory(true);
        para10.setHasValue(false);
        para10.setAttachToPrevious(true);
        para10.setSpace(false);
        
        para11.setName("Testpara 11");
        para11.setPrefix("-0011");
        para11.setOrder(11);
        para11.setMandatory(true);
        para11.setHasValue(false);
        para11.setAttachToPrevious(true);
        para11.setSpace(true);
        
        para12.setName("Testpara 12");
        para12.setPrefix("-0012");
        para12.setOrder(12);
        para12.setMandatory(true);
        para12.setHasValue(true);
        para12.setAttachToPrevious(false);
        para12.setSpace(false);
        
        para13.setName("Testpara 13");
        para13.setPrefix("-0013");
        para13.setOrder(13);
        para13.setMandatory(true);
        para13.setHasValue(true);
        para13.setAttachToPrevious(false);
        para13.setSpace(true);
        
        para14.setName("Testpara 14");
        para14.setPrefix("-0014");
        para14.setOrder(14);
        para14.setMandatory(true);
        para14.setHasValue(true);
        para14.setAttachToPrevious(true);
        para14.setSpace(false);
        
        para15.setName("Testpara 15");
        para15.setPrefix("-0015");
        para15.setOrder(15);
        para15.setMandatory(true);
        para15.setHasValue(true);
        para15.setAttachToPrevious(true);
        para15.setSpace(true);
        
        // Parameters to solver2
        para16.setName("Testpara 1");
        para16.setPrefix("-eins");
        para16.setOrder(1);
        para16.setMandatory(true);
        para16.setHasValue(true);
        para16.setAttachToPrevious(true);
        para16.setSpace(true);
        
        para17.setName("Testpara 2");
        para17.setPrefix("-zwei");
        para17.setOrder(2);
        para17.setMandatory(true);
        para17.setHasValue(true);
        para17.setAttachToPrevious(true);
        para17.setSpace(true);
        
        //add Parameters to the corresponding Solvers
        parameterTableModel.addParameter(newSolver, para0);
        parameterTableModel.addParameter(newSolver, para1);
        parameterTableModel.addParameter(newSolver, para2);
        parameterTableModel.addParameter(newSolver, para3);
        parameterTableModel.addParameter(newSolver, para4);
        parameterTableModel.addParameter(newSolver, para5);
        parameterTableModel.addParameter(newSolver, para6);
        parameterTableModel.addParameter(newSolver, para7);
        parameterTableModel.addParameter(newSolver, para8);
        parameterTableModel.addParameter(newSolver, para9);
        parameterTableModel.addParameter(newSolver, para10);
        parameterTableModel.addParameter(newSolver, para11);
        parameterTableModel.addParameter(newSolver, para12);
        parameterTableModel.addParameter(newSolver, para13);
        parameterTableModel.addParameter(newSolver, para14);
        parameterTableModel.addParameter(newSolver, para15);
        parameterTableModel.addParameter(newSolver2, para16);
        parameterTableModel.addParameter(newSolver2, para17);
        
        manageDBParameters = new ManageDBParameters(null, parameterTableModel);
    }

    @After
    public void tearDown() {
        try {
            SolverDAO.removeSolver(newSolver);
            SolverDAO.removeSolver(newSolver2);
        } catch (SolverIsInExperimentException ex) {
            Logger.getLogger(ManageDBSolversTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(ManageDBSolversTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SolverNotInDBException ex) {
            Logger.getLogger(ManageDBSolversTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSolverBinarySpecifiedException ex) {
            Logger.getLogger(ManageDBSolversTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ManageDBSolversTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ManageDBSolversTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Test of saveSolvers method, of class ManageDBSolvers.
     */
    @Test
    public void testSaveSolvers() throws Exception {
        System.out.println("saveSolvers");
        
        ManageDBSolvers writeInstance = new ManageDBSolvers(null, solverTableModel, manageDBParameters, null);
        Tasks ts = new Tasks();
        //save all solvers to the DB
        writeInstance.saveSolvers(ts);
        
        //select the current Solver and delete some Parameters.
        manageDBParameters.setCurrentSolver(newSolver);
        manageDBParameters.removeParameter(para0);
        manageDBParameters.removeParameter(para3);
        manageDBParameters.removeParameter(para7);
        
        //save the delete changes to the DB
        writeInstance.saveSolvers(ts);
        
        SolverTableModel solverTableModelRead = new SolverTableModel();
        ManageDBSolvers readInstance = new ManageDBSolvers(null, solverTableModelRead, manageDBParameters, null);
        //load all solvers and check if they were the saved solvers
        readInstance.loadSolvers();
        Vector<Solver> solvers = solverTableModelRead.getSolvers();
        Solver s1 = solvers.get(0);
        Solver s2 = solvers.get(1);
        assertEquals(newSolver, s1);
        assertEquals(newSolver2, s2);
    }
}
