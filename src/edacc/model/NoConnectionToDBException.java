/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edacc.model;

import java.sql.SQLException;

/**
 *
 * @author dgall
 */
class NoConnectionToDBException extends SQLException {

    public NoConnectionToDBException() {
        this("No connection to database.");
    }

    public NoConnectionToDBException(String msg) {
        super(msg);
    }
}
