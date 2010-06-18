/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edacc.model;

import java.io.FileNotFoundException;

/**
 *
 * @author dgall
 */
class SQLQueryFileNotFoundException extends FileNotFoundException {

    public SQLQueryFileNotFoundException() {
        super("The file containing the SQL queries for generating the EDACC tables couldn't be found!");
    }

}
