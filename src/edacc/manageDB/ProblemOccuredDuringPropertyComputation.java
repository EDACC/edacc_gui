/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edacc.manageDB;

import java.util.Vector;

/**
 *
 * @author rretz
 */
public class ProblemOccuredDuringPropertyComputation extends Exception {
    private Vector<Exception> exceptionCollector;
    
    public ProblemOccuredDuringPropertyComputation(Vector<Exception> exceptionCollector) {
        super();
        this.exceptionCollector = exceptionCollector;
    }
    
    public  Vector<Exception> getExceptionsCollector(){
        return  exceptionCollector;
    }
}
