package edacc.model;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedList;

/**
 *
 * @author simon
 */
public class StatusCodeDAO {
    protected static final String table = "StatusCodes";
    protected static final String insertQuery = "INSERT INTO " + table + " (statusCode, description) VALUES (?, ?)";
    protected static final String selectQuery = "SELECT * FROM " + table + " WHERE statusCode = ?";
    protected static final String selectAllQuery = "SELECT * FROM " + table;
    protected static final HashMap<Integer, StatusCode> cache = new HashMap<Integer, StatusCode>();

    public static void save(StatusCode status) throws SQLException {
        if (status.isNew()) {
            PreparedStatement st = DatabaseConnector.getInstance().getConn().prepareStatement(insertQuery);
            st.setInt(1, status.getStatusCode());
            st.setString(2, status.getDescription());
            st.executeUpdate();
            status.setSaved();
        } else if (status.isModified()) {
            throw new IllegalArgumentException("Modified status codes are not allowed.");
        }
    }

    public static StatusCode getByStatusCode(int statusCode) throws SQLException, StatusCodeNotInDBException {
        if (cache.containsKey(statusCode)) {
            return cache.get(statusCode);
        } else {
            PreparedStatement st = DatabaseConnector.getInstance().getConn().prepareStatement(selectQuery);
            st.setInt(1, statusCode);
            ResultSet rs = st.executeQuery();
            if (rs.next()) {
                StatusCode status = new StatusCode(statusCode, rs.getString("description"));
                cache.put(statusCode, status);
                rs.close();
                st.close();
                return status;
            } else {
                rs.close();
                st.close();
                throw new StatusCodeNotInDBException();
            }
        }
    }

    public static LinkedList<StatusCode> getAll() throws SQLException {
        PreparedStatement st = DatabaseConnector.getInstance().getConn().prepareStatement(selectAllQuery);
        ResultSet rs = st.executeQuery();
        LinkedList<StatusCode> codes = new LinkedList<StatusCode>();
        if (rs.next()) {
            int statusCode = rs.getInt("statusCode");
            StatusCode status = new StatusCode(statusCode, rs.getString("description"));
            cache.put(statusCode, status);
            rs.close();
            st.close();
            codes.add(status);
        } else {
            rs.close();
            st.close();
        }
        return codes;
    }

    public static void initialize() throws SQLException {
        cache.clear();
        for (StatusCode status : StatusCode.CONST) {
            try {
                getByStatusCode(status.getStatusCode());
            } catch (StatusCodeNotInDBException ex) {
                status.setNew();
                save(status);
            }
        }
        cache.clear();
        for (StatusCode status : StatusCode.CONST) {
            status.setSaved();
            cache.put(status.getStatusCode(), status);
        }
    }
}
