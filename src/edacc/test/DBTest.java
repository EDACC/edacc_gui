package edacc.test;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import edacc.model.*;
import java.util.LinkedList;
/**
 *
 * @author daniel
 */
public class DBTest {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {
        DatabaseConnector.getInstance().connect("localhost", 3306, "root", "EDACC", "affe42" );
        LinkedList<Instance> l = InstanceDAO.getAll();
        Instance i1 = l.getFirst();
        for (Instance i : l) {
            System.out.println(i.getId());
        }

        LinkedList<Instance> l2 = InstanceDAO.getAll();
        Instance i2 = l2.getFirst();
        assert(i1 == i2);
    }

}
