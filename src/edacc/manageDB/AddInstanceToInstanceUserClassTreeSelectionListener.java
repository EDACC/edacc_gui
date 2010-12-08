/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edacc.manageDB;

import edacc.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;

/**
 *
 * @author rretz
 */
public class AddInstanceToInstanceUserClassTreeSelectionListener implements TreeSelectionListener{
    EDACCAddInstanceToInstanceClass main;

    public AddInstanceToInstanceUserClassTreeSelectionListener(EDACCAddInstanceToInstanceClass main) {
        this.main = main;
    }

    @Override
    public void valueChanged(TreeSelectionEvent e) {
        main.selectUserClass();
    }

}
