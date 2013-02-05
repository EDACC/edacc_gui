package edacc.model;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.*;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;

/**
 *
 * @author simon
 */
public class ResultCodeDAO {
    protected static final String table = "ResultCodes";
    protected static final String insertQuery = "INSERT INTO " + table + " (resultCode, description) VALUES (?, ?)";
    protected static final String selectQuery = "SELECT * FROM " + table + " WHERE resultCode = ?";
    protected static final HashMap<Integer, ResultCode> cache = new HashMap<Integer, ResultCode>();

    public static void save(ResultCode status) throws SQLException {
        if (status.isNew()) {
            PreparedStatement st = DatabaseConnector.getInstance().getConn().prepareStatement(insertQuery);
            st.setInt(1, status.getResultCode());
            st.setString(2, status.getDescription());
            st.executeUpdate();
            status.setSaved();
        } else if (status.isModified()) {
            throw new IllegalArgumentException("Modified status codes are not allowed.");
        }
    }

    public static ResultCode getByResultCode(int resultCode) throws SQLException, ResultCodeNotInDBException {
        if (cache.containsKey(resultCode)) {
            return cache.get(resultCode);
        } else {
            PreparedStatement st = DatabaseConnector.getInstance().getConn().prepareStatement(selectQuery);
            st.setInt(1, resultCode);
            ResultSet rs = st.executeQuery();
            if (rs.next()) {
                ResultCode result = new ResultCode(resultCode, rs.getString("description"));
                cache.put(resultCode, result);
                rs.close();
                st.close();
                result.setSaved();
                return result;
            } else {
                rs.close();
                st.close();
                throw new ResultCodeNotInDBException();
            }
        }
    }

    public static LinkedList<ResultCode> getAll() throws SQLException {
        Statement st = DatabaseConnector.getInstance().getConn().createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM " + table);
        LinkedList<ResultCode> res = new LinkedList<ResultCode>();
        while (rs.next()) {
            int resultCode = rs.getInt("resultCode");
            ResultCode c = cache.get(resultCode);
            if (c != null) {
                res.add(c);
            } else {
                ResultCode i = new ResultCode(resultCode, rs.getString("description"));
                i.setSaved();
                cache.put(resultCode, i);
                res.add(i);
            }

        }
        rs.close();
        return res;
    }

    public static void remove(Collection<ResultCode> codes) throws SQLException {
        String list = "(";
        StringBuilder b = new StringBuilder(list);
        for (ResultCode r : codes) {
            b.append(r.getResultCode());
            b.append(",");
        }
        b.setCharAt(b.length() - 1, ')');
        final String query = "DELETE FROM " + table + " WHERE resultCode in " + b.toString();
        Statement st = DatabaseConnector.getInstance().getConn().createStatement();
        st.executeUpdate(query);
    }

    public static void initialize() throws SQLException {
        cache.clear();
        for (ResultCode result : ResultCode.CONST) {
            try {
                getByResultCode(result.getResultCode());
            } catch (ResultCodeNotInDBException ex) {
                result.setNew();
                save(result);
            }
        }
        cache.clear();
        for (ResultCode result : ResultCode.CONST) {
            result.setSaved();
            cache.put(result.getResultCode(), result);
        }
    }
    
}
