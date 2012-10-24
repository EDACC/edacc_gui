/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edacc.model;

/**
 *
 * @author dgall
 */
public class MD5CheckFailedException extends Exception {

    public MD5CheckFailedException() {
        this ("A file seems to be corrupt because of a wrong md5 checksum.");
    }

    public MD5CheckFailedException(String msg) {
        super (msg);
    }

}
