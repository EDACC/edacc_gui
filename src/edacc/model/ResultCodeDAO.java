package edacc.model;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

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
                return result;
            } else {
                rs.close();
                st.close();
                throw new ResultCodeNotInDBException();
            }
        }
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
