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
public class VerifierParameterDAO {

    private static final String table = "VerifierParameter";
    private static final String selectQuery = "SELECT * FROM " + table;

    private static String getInsertQuery(int count) {
        StringBuilder query = new StringBuilder("INSERT INTO " + table + " "
                + "(Verifier_idVerifier,name,prefix,hasValue,defaultValue,`order`,mandatory,space,attachToPrevious) "
                + "VALUES (?,?,?,?,?,?,?,?,?)");
        count--;
        for (int i = 0; i < count; i++) {
            query.append(",(?,?,?,?,?,?,?,?,?)");
        }
        return query.toString();
    }

    private static String getUpdateQuery(int count) {
        // create a dummy header (there is no dataset with idVerifier == -1)
        StringBuilder constTable = new StringBuilder("(SELECT -1 AS idVerifierParameter, 0 AS Verifier_idVerifier, \"\" AS name, \"\" AS prefix, 0 AS hasValue, \"\" AS defaultValue, 0 AS `order`, 0 AS mandatory, 0 AS space, 0 AS attachToPrevious");
        // append rows to constTable
        for (int i = 0; i < count; i++) {
            constTable.append(" UNION SELECT ?,?,?,?,?,?,?,?,?,?");
        }
        constTable.append(")");

        StringBuilder query = new StringBuilder("UPDATE " + table + " AS t1, ");
        query.append(constTable);
        query.append(" AS t2 ");
        query.append("SET t1.Verifier_idVerifier=t2.Verifier_idVerifier,t1.name=t2.name,t1.prefix=t2.prefix,t1.hasValue=t2.hasValue,t1.defaultValue=t2.defaultValue,t1.`order`=t2.`order`,t1.mandatory=t2.mandatory,t1.space=t2.space,t1.attachToPrevious=t2.attachToPrevious ");
        query.append("WHERE t1.idVerifierParameter=t2.idVerifierParameter");
        return query.toString();
    }

    private static String getDeleteQuery(int count) {
        StringBuilder query = new StringBuilder("DELETE FROM " + table + " WHERE idVerifierParameter IN (?");
        count--;
        for (int i = 0; i < count; i++) {
            query.append(",?");
        }
        query.append(')');
        return query.toString();
    }

    public static void saveAll(List<Verifier> verifiers) throws SQLException {
        boolean autoCommit = DatabaseConnector.getInstance().getConn().getAutoCommit();
        DatabaseConnector.getInstance().getConn().setAutoCommit(false);
        try {
            List<VerifierParameter> newVerifierParams = new LinkedList<VerifierParameter>();
            List<VerifierParameter> modifiedVerifierParams = new LinkedList<VerifierParameter>();
            List<VerifierParameter> deletedVerifierParams = new LinkedList<VerifierParameter>();
            for (Verifier v : verifiers) {
                for (VerifierParameter vp : v.getParameters()) {
                    if (vp.isNew()) {
                        newVerifierParams.add(vp);
                    } else if (vp.isModified()) {
                        modifiedVerifierParams.add(vp);
                    } else if (vp.isDeleted()) {
                        deletedVerifierParams.add(vp);
                    }
                }
            }
            if (!newVerifierParams.isEmpty()) {
                String query = getInsertQuery(newVerifierParams.size());
                PreparedStatement st = DatabaseConnector.getInstance().getConn().prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS);
                int curCount = 1;
                for (VerifierParameter vp : newVerifierParams) {
                    st.setInt(curCount++, vp.getIdVerifier());
                    st.setString(curCount++, vp.getName());
                    st.setString(curCount++, vp.getPrefix());
                    st.setBoolean(curCount++, vp.getHasValue());
                    st.setString(curCount++, vp.getDefaultValue());
                    st.setInt(curCount++, vp.getOrder());
                    st.setBoolean(curCount++, vp.isMandatory());
                    st.setBoolean(curCount++, vp.getSpace());
                    st.setBoolean(curCount++, vp.isAttachToPrevious());
                }
                st.executeUpdate();
                ResultSet rs = st.getGeneratedKeys();
                int i = 0;
                while (rs.next()) {
                    newVerifierParams.get(i).setId(rs.getInt(1));
                    newVerifierParams.get(i).setSaved();
                    final VerifierParameter vp = newVerifierParams.get(i);
                    DatabaseConnector.getInstance().addRollbackOperation(new Runnable() {

                        @Override
                        public void run() {
                            vp.setNew();
                        }
                    });
                    i++;
                }
                rs.close();
                st.close();
            }
            if (!modifiedVerifierParams.isEmpty()) {
                String query = getUpdateQuery(modifiedVerifierParams.size());
                PreparedStatement st = DatabaseConnector.getInstance().getConn().prepareStatement(query);
                int curCount = 1;
                for (VerifierParameter vp : modifiedVerifierParams) {
                    st.setInt(curCount++, vp.getId());
                    st.setInt(curCount++, vp.getIdVerifier());
                    st.setString(curCount++, vp.getName());
                    st.setString(curCount++, vp.getPrefix());
                    st.setBoolean(curCount++, vp.getHasValue());
                    st.setString(curCount++, vp.getDefaultValue());
                    st.setInt(curCount++, vp.getOrder());
                    st.setBoolean(curCount++, vp.isMandatory());
                    st.setBoolean(curCount++, vp.getSpace());
                    st.setBoolean(curCount++, vp.isAttachToPrevious());

                    vp.setSaved();
                    final VerifierParameter ve = vp;
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
                PreparedStatement st = DatabaseConnector.getInstance().getConn().prepareStatement(query);
                int curCount = 1;
                for (VerifierParameter vp : deletedVerifierParams) {
                    st.setInt(curCount++, vp.getId());
                }
                st.executeUpdate();
                st.close();
            }
        } catch (Throwable t) {
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

    private static VerifierParameter getVerifierParameterFromResultSet(ResultSet rs) throws SQLException {
        VerifierParameter vp = new VerifierParameter();
        vp.setId(rs.getInt("idVerifierParameter"));
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

    public static Map<Integer, List<VerifierParameter>> getAll() throws SQLException {
        HashMap<Integer, List<VerifierParameter>> res = new HashMap<Integer, List<VerifierParameter>>();
        Statement st = DatabaseConnector.getInstance().getConn().createStatement();
        ResultSet rs = st.executeQuery(selectQuery);

        while (rs.next()) {
            VerifierParameter vp = getVerifierParameterFromResultSet(rs);
            List<VerifierParameter> params = res.get(vp.getIdVerifier());
            if (params == null) {
                params = new LinkedList<VerifierParameter>();
                res.put(vp.getIdVerifier(), params);
            }
            params.add(vp);
        }
        rs.close();
        st.close();
        return res;
    }
}
