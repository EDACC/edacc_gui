/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edacc.statuscodes;

import edacc.model.StatusCode;
import edacc.model.StatusCodeDAO;
import java.sql.SQLException;
import java.util.LinkedList;
import javax.swing.AbstractListModel;

/**
 *
 * @author dgall
 */
public class StatusCodeListModel extends AbstractListModel {

    private LinkedList<StatusCode> statusCodes;

    public StatusCodeListModel() throws SQLException {
        statusCodes = StatusCodeDAO.getAll();
    }

    @Override
    public int getSize() {
        return statusCodes.size();
    }

    @Override
    public StatusCode getElementAt(int index) {
        return statusCodes.get(index);
    }

    public void addStatusCode(StatusCode statusCode) {
        statusCodes.add(statusCode);
    }

    public void removeStatusCode(StatusCode statusCode) {
        statusCodes.remove(statusCode);
    }

}
