/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edacc.manageDB;

import edacc.model.ResultCode;
import edacc.model.ResultCodeDAO;
import java.sql.SQLException;

/**
 *
 * @author dgall
 */
public class ManageDBResultCodes {

    private static ManageDBResultCodes instance;

    public static ManageDBResultCodes getInstance() throws SQLException {
        if (instance == null) {
            instance = new ManageDBResultCodes();
            instance.init();
        }
        return instance;
    }

    private ManageDBResultCodes() {

    }

    public void init() throws SQLException {
        ResultCodeTableModel.getInstance().addList(ResultCodeDAO.getAll());
    }

    public void refresh() throws SQLException {
        ResultCodeTableModel.getInstance().clear();
        init();
    }

    public void saveNewCode(int code, String description) throws SQLException {
        if (description.length() > 255)
            throw new IllegalArgumentException("Description too long! Maximal length: 255 characters.");
        ResultCode rc = new ResultCode(code, description);
        ResultCodeDAO.save(rc);
        ResultCodeTableModel.getInstance().add(rc);
    }
}
