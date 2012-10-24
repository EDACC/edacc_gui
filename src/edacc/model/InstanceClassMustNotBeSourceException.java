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
public class InstanceClassMustNotBeSourceException extends SQLException {

    public InstanceClassMustNotBeSourceException() {
        super("The source instance class must have the source flag set on false!");
    }

}
