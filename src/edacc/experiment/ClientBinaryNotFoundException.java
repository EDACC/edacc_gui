/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edacc.experiment;

/**
 *
 * @author dgall
 */
public class ClientBinaryNotFoundException extends Exception {

    public ClientBinaryNotFoundException() {
        this ("Client binary couldn't be found!");
    }

    public ClientBinaryNotFoundException(String msg) {
        super (msg);
    }
}
