/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edacc.model;

import edacc.instanceGenerator.unifRandomKSAT;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 *
 * @author balint
 */
public class instanceGeneratorUnifKCNFController {

    /**
     *
     * @param k the length of the clause
     * @param r the ratio between the number of valuses and the number of variables
     * @param n the number of variables
     * @param series specifies if a series of instances is desired
     * @param step if a series of instances is desired this specifies the steps between two elements of the series
     * @param stop where the series should stop
     * @param num number of instances per class
     * @param genClass generate instance classes
     */
    public void generate(int k, double r, int n, boolean series, int step, int stop, int num, boolean genClass, InstanceClass parent, Tasks task) throws SQLException, InstanceClassAlreadyInDBException {
       
        unifRandomKSAT instance;
        InstanceDAO instanceController = new InstanceDAO();
        InstanceClass ic;
        //parent classe braucht man noch
        String className = "";
        if (!series) {
            stop = n; //the next for-iteration will be executed only once
            step = 1;
        }
        int tmp = stop - n;
        //int steps = 0;
        int steps = ((stop-n)/step+1)*num;
        //if(tmp > 0){
          //   steps = Math.round((float)tmp / (float)step);
        //}
        int count =1;
        for (int i = n; i <= stop; i = i + step) {
             //task.setStatus("Generated " + count + " of " + steps + " instances" );
             task.setTaskProgress((float)count / (float) steps);
            if (genClass) {
                className = "v" + i;
                task.setStatus("Generating class: "+ className);
                ic = InstanceClassDAO.createInstanceClass(className, "Automated generated Class by unif-KSAT Generator", parent);
            } else{
                ic=parent;
                className="";
            }
            //the input to the generator has to be correct 
            for (int j = 0; j < num; j++) {
                task.setOperationName("Generating uniform random k-SAT instances"+"(" + count + "/" + steps + ")");
                
                task.setTaskProgress((float)count / (float) steps);
                instance = new unifRandomKSAT(k, r, i);
                task.setStatus(className+" ("+(j+1)+"/"+num+"):"+ instance.suggestedFN() );
                //task.setStatus("Generated fdkg jdfkl  df sdfg sdf fdg instance:"+ instance.suggestedFN() );
                //System.out.println(className + " : " + instance.suggestedFN());

                count++;
                //add instance to the specified class
                try {
                    instanceController.createInstance(instance.suggestedFN(), instance.toDIMACS(), ic);
                } catch (Exception ex) {
                    Logger.getLogger(InstanceDAO.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        task.setStatus("Generated " + steps + " of " + steps + " instances" );
        task.setTaskProgress(1);

    }
}
