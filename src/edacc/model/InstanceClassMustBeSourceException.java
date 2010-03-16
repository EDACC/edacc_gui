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
public class InstanceClassMustBeSourceException extends SQLException {

    public InstanceClassMustBeSourceException() {
        super("The source instance class must have the source flag set on true!");
    }

}
