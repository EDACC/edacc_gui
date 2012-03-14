package edacc.model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author simon
 */
public class VerifierDAO {

    private static final String table = "Verifier";
    private static final String selectQuery = "SELECT idVerifier, name FROM " + table;

    private static Verifier getVerifierFromResultSet(ResultSet rs, Map<Integer, List<VerifierParameter>> verifierParameterMap) throws SQLException {
        Verifier verifier = new Verifier();
        verifier.setId(rs.getInt("idVerifier"));
        verifier.setName(rs.getString("name"));
        verifier.setDescription(rs.getString("description"));
        verifier.setMd5(rs.getString("md5"));
        verifier.setRunCommand(rs.getString("runCommand"));
        verifier.setRunPath(rs.getString("runPath"));
        return verifier;
    }

    public static List<Verifier> getAllVerifiers() throws SQLException {
        List<Verifier> res = new ArrayList<Verifier>();
        Statement st = DatabaseConnector.getInstance().getConn().createStatement();
        Map<Integer, List<VerifierParameter>> verifierParameterMap = VerifierParameterDAO.getAll();
        ResultSet rs = st.executeQuery(selectQuery);
        while (rs.next()) {
            Verifier v = getVerifierFromResultSet(rs, verifierParameterMap);
            res.add(v);
        }
        return res;
    }
}
