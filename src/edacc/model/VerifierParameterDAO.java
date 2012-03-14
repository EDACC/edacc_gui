package edacc.model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author simon
 */
public class VerifierParameterDAO {
    private static final String table = "VerifierParameter";
    
    private static final String selectQuery = "SELECT * FROM " + table;
    
    private static VerifierParameter getVerifierParameterFromResultSet(ResultSet rs) throws SQLException {
        VerifierParameter vp = new VerifierParameter();
        vp.setIdVerifier(rs.getInt("Verifier_idVerifier"));
        vp.setName(rs.getString("name"));
        vp.setPrefix(rs.getString("prefix"));
        vp.setHasValue(rs.getBoolean("hasValue"));
        vp.setDefaultValue(rs.getString("defaultValue"));
        vp.setOrder(rs.getInt("order"));
        vp.setMandatory(rs.getBoolean("mandatory"));
        vp.setSpace(rs.getBoolean("space"));
        vp.setAttachToPrevious(rs.getBoolean("attachToPrevious"));
        vp.setSaved();
        return vp;
    }
    
    public static final Map<Integer, List<VerifierParameter>> getAll() throws SQLException {
        HashMap<Integer, List<VerifierParameter>> res = new HashMap<Integer, List<VerifierParameter>>();
        Statement st = DatabaseConnector.getInstance().getConn().createStatement();
        ResultSet rs = st.executeQuery(selectQuery);
        
        while (rs.next()) {
            VerifierParameter vp = getVerifierParameterFromResultSet(rs);
            List<VerifierParameter> params = res.get(vp.getIdVerifier());
            if (res == null) {
                params = new LinkedList<VerifierParameter>();
                res.put(vp.getIdVerifier(), params);
            }
            params.add(vp);
        }
        return res;
    }
}
