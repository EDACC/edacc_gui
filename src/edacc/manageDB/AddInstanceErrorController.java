/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edacc.manageDB;

import edacc.model.Instance;
import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.table.TableModel;

/**
 *
 * @author rretz
 */
public class AddInstanceErrorController {
    public TableModel getDuplicateModel;

    public AddInstanceErrorController(ArrayList<Instance> toAdd, HashMap<Instance, ArrayList<Instance>> duplicateMd5, HashMap<Instance, ArrayList<Instance>> duplicateName) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public TableModel getToAddModel() {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public TableModel getDuplicateModel() {
        throw new UnsupportedOperationException("Not yet implemented");
    }
    
}
