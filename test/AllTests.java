/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import edacc.manageDB.ManageDBResultCodesTest;
import edacc.manageDB.ManageDBSolversTest;
import edacc.manageDB.ManageDBVerifiersTest;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 *
 * @author Gregor
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
    ManageDBResultCodesTest.class,
    ManageDBVerifiersTest.class,
    ManageDBSolversTest.class
})
public class AllTests {

    
    
}
