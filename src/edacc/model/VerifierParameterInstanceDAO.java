package edacc.model;

import java.sql.PreparedStatement;
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
public class VerifierParameterInstanceDAO {

    private static final String table = "VerifierConfig_has_VerifierParameter";
    private static final String selectQuery = "SELECT * FROM " + table;

    private static String getInsertQuery(int count) {
        StringBuilder query = new StringBuilder("INSERT INTO " + table + " "
                + "(VerifierConfig_idVerifierConfig,VerifierParameter_idVerifierParameter,value) "
                + "VALUES (?,?,?)");
        count--;
        for (int i = 0; i < count; i++) {
            query.append(",(?,?,?)");
        }
        return query.toString();
    }

    private static String getUpdateQuery(int count) {
        // create a dummy header (there is no dataset with idVerifier == -1)
        StringBuilder constTable = new StringBuilder("(SELECT -1 AS VerifierConfig_idVerifierConfig, -1 AS VerifierParameter_idVerifierParameter, \"\" AS value");
        // append rows to constTable
        for (int i = 0; i < count; i++) {
            constTable.append(" UNION SELECT ?,?,?");
        }
        constTable.append(")");

        StringBuilder query = new StringBuilder("UPDATE " + table + " AS t1, ");
        query.append(constTable);
        query.append(" AS t2 ");
        query.append("SET t1.value=t2.value ");
        query.append("WHERE t1.VerifierConfig_idVerifierConfig=t2.VerifierConfig_idVerifierConfig AND t1.VerifierParameter_idVerifierParameter=t2.VerifierParameter_idVerifierParameter");
        return query.toString();
    }

    private static String getDeleteQuery(int count) {
        StringBuilder query = new StringBuilder("DELETE FROM " + table + " WHERE (VerifierConfig_idVerifierConfig=? AND VerifierParameter_idVerifierParameter=?)");
        count--;
        for (int i = 0; i < count; i++) {
            query.append(" OR (VerifierConfig_idVerifierConfig=? AND VerifierParameter_idVerifierParameter=?)");
        }
        return query.toString();
    }

    public static void saveAll(List<VerifierConfiguration> verifierConfigs) throws SQLException {
        boolean autoCommit = DatabaseConnector.getInstance().getConn().getAutoCommit();
        DatabaseConnector.getInstance().getConn().setAutoCommit(false);
        try {
            List<VerifierParameterInstance> newVerifierParams = new LinkedList<VerifierParameterInstance>();
            List<VerifierParameterInstance> modifiedVerifierParams = new LinkedList<VerifierParameterInstance>();
            List<VerifierParameterInstance> deletedVerifierParams = new LinkedList<VerifierParameterInstance>();
            for (VerifierConfiguration vc : verifierConfigs) {
                for (VerifierParameterInstance pi : vc.getParameterInstances()) {
                    if (pi.isNew()) {
                        newVerifierParams.add(pi);
                    } else if (pi.isModified()) {
                        modifiedVerifierParams.add(pi);
                    } else if (pi.isDeleted()) {
                        deletedVerifierParams.add(pi);
                    }
                }
            }
            if (!newVerifierParams.isEmpty()) {
                String query = getInsertQuery(newVerifierParams.size());
                PreparedStatement st = DatabaseConnector.getInstance().getConn().prepareStatement(query);
                int curCount = 1;
                for (VerifierParameterInstance pi : newVerifierParams) {
                    System.out.println("" + pi.getVerifierConfigId() + ":" + pi.getParameter_id() + ":" + pi.getValue());
                    st.setInt(curCount++, pi.getVerifierConfigId());
                    st.setInt(curCount++, pi.getParameter_id());
                    st.setString(curCount++, pi.getValue());
                    pi.setSaved();
                    final VerifierParameterInstance fpi = pi;
                    DatabaseConnector.getInstance().addRollbackOperation(new Runnable() {

                        @Override
                        public void run() {
                            fpi.setNew();
                        }
                    });
                }
                st.executeUpdate();
                st.close();
            }
            if (!modifiedVerifierParams.isEmpty()) {
                String query = getUpdateQuery(modifiedVerifierParams.size());
                PreparedStatement st = DatabaseConnector.getInstance().getConn().prepareStatement(query);
                int curCount = 1;
                for (VerifierParameterInstance pi : modifiedVerifierParams) {
                    st.setInt(curCount++, pi.getVerifierConfigId());
                    st.setInt(curCount++, pi.getParameter_id());
                    st.setString(curCount++, pi.getValue());

                    pi.setSaved();
                    final VerifierParameterInstance ve = pi;
                    DatabaseConnector.getInstance().addRollbackOperation(new Runnable() {

                        @Override
                        public void run() {
                            ve.setModified();
                        }
                    });
                }
                st.executeUpdate();
                st.close();
            }
            if (!deletedVerifierParams.isEmpty()) {
                String query = getDeleteQuery(deletedVerifierParams.size());
                System.out.println(query);
                PreparedStatement st = DatabaseConnector.getInstance().getConn().prepareStatement(query);
                int curCount = 1;
                for (VerifierParameterInstance pi : deletedVerifierParams) {
                    System.out.println(pi.getVerifierConfigId() + ":" + pi.getParameter_id());
                    st.setInt(curCount++, pi.getVerifierConfigId());
                    st.setInt(curCount++, pi.getParameter_id());
                }
                st.executeUpdate();
                st.close();
            }
        } catch (Throwable t) {
            t.printStackTrace();
            if (autoCommit) {
                DatabaseConnector.getInstance().rollback();
            }
            if (t instanceof Error) {
                throw (Error) t;
            } else if (t instanceof SQLException) {
                throw (SQLException) t;
            }
        } finally {
            if (autoCommit) {
                DatabaseConnector.getInstance().commit();
                DatabaseConnector.getInstance().getConn().setAutoCommit(autoCommit);
            }
        }
    }

    private static VerifierParameterInstance getVerifierParameterInstanceFromResultSet(ResultSet rs) throws SQLException {
        VerifierParameterInstance pi = new VerifierParameterInstance();
        pi.setVerifierConfigId(rs.getInt("VerifierConfig_idVerifierConfig"));
        pi.setParameter_id(rs.getInt("VerifierParameter_idVerifierParameter"));
        pi.setValue(rs.getString("value"));
        pi.setSaved();
        return pi;
    }

    protected static Map<Integer, List<VerifierParameterInstance>> getAll() throws SQLException {
        HashMap<Integer, List<VerifierParameterInstance>> res = new HashMap<Integer, List<VerifierParameterInstance>>();
        Statement st = DatabaseConnector.getInstance().getConn().createStatement();
        ResultSet rs = st.executeQuery(selectQuery);

        while (rs.next()) {
            VerifierParameterInstance pi = getVerifierParameterInstanceFromResultSet(rs);
            pi.setSaved();
            List<VerifierParameterInstance> params = res.get(pi.getVerifierConfigId());
            if (params == null) {
                params = new LinkedList<VerifierParameterInstance>();
                res.put(pi.getVerifierConfigId(), params);
            }
            params.add(pi);
        }
        rs.close();
        st.close();
        return res;
    }
}
