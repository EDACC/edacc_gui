/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edacc.satinstances;

/**
 *
 * @author dgall
 */
public class ConvertException extends Exception {

    public ConvertException() {
        this ("Error while trying to convert the given value.");
    }

    public ConvertException(String msg) {
        super (msg);
    }

    public ConvertException(String msg, Exception e) {
        super (msg, e);
    }

    public ConvertException(Exception e) {
        super (e);
    }
}
