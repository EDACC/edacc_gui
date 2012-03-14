package edacc.model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author simon
 */
public class VerifierConfigurationDAO {

    private static final String table = "VerifierConfig";
    private static final String selectQuery = "SELECT * FROM " + table;
    private static ObjectCache<VerifierConfiguration> cache = new ObjectCache<VerifierConfiguration>();

    private static VerifierConfiguration getVerifierConfigurationFromResultSet(ResultSet rs) throws SQLException {
        VerifierConfiguration vc = new VerifierConfiguration();
        vc.setId(rs.getInt("idVerifierConfig"));
        vc.setVerifier(VerifierDAO.getById(rs.getInt("Verifier_idVerifier")));
        return vc;
    }
    
    public static void save(VerifierConfiguration vc) {
        
    }

    public static List<VerifierConfiguration> getAll() throws SQLException {
        List<VerifierConfiguration> res = new ArrayList<VerifierConfiguration>();
        Statement st = DatabaseConnector.getInstance().getConn().createStatement();
        ResultSet rs = st.executeQuery(selectQuery);
        while (rs.next()) {
            VerifierConfiguration vc = cache.getCached(rs.getInt("idVerifierConfig"));
            if (vc == null) {
                vc = getVerifierConfigurationFromResultSet(rs);
                cache.cache(vc);
            }
            res.add(vc);
        }
        rs.close();
        st.close();
        return res;
    }
    
    public static VerifierConfiguration getById(int id) throws SQLException {
        VerifierConfiguration vc = cache.getCached(id);
        if (vc == null) {
            getAll();
            vc = cache.getCached(id);
        }
        return vc;
    }
    
    public static void clearCache() {
        cache.clear();
    }
}
