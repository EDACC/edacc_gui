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
public class AddInstanceToInstanceSourceClassTreeSelectionListener  implements TreeSelectionListener{
    EDACCAddInstanceToInstanceClass main;

    public AddInstanceToInstanceSourceClassTreeSelectionListener(EDACCAddInstanceToInstanceClass main) {
        this.main = main;
    }

    @Override
    public void valueChanged(TreeSelectionEvent e) {
        main.selectSourceClass();
    }

}
