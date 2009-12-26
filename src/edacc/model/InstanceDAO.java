package edacc.model;

import java.util.List;
import java.util.LinkedList;
import java.util.Hashtable;
import java.sql.*;

/**
 * data access object for the Instance class
 * @author daniel
 */
public class InstanceDAO {
    private static final String table = "Instances";
    private static final Hashtable<Instance, Instance> cache = new Hashtable<Instance, Instance>();

    /**
     * Instance factory method, ensures that the created instance is persisted and assigned an ID
     * so it can be referenced by related objects
     * @return new Instance object
     */
     public static Instance createInstance() {
        Instance i = new Instance();
        save(i);
        cacheInstance(i);
        return i;
     }

    /**
     * persists an instance object in the database
     * @param instance The instance object to persist
     */
    private static void save(Instance instance) {
        if (instance.isNew()) {
            // insert query, set ID!
            instance.setSaved();
        }
        else if (instance.isModified()) {
            // update query
            instance.setSaved();
        }
    }

    private static Instance getCached(Instance i) {
        if (cache.containsKey(i)) {
            return cache.get(i);
        }
        else return null;
    }

    private static void cacheInstance(Instance i) {
        if (cache.containsKey(i)) return;
        else cache.put(i, i);
    }

    /**
     * retrieves an instance from the database
     * @param id the id of the instance to be retrieved
     * @return the instance specified by its id
     * @throws SQLException
     */
    public static Instance getById(int id) throws SQLException {
        PreparedStatement st = DatabaseConnector.getInstance().conn.prepareStatement("SELECT * FROM " + table + " WHERE idInstance=?");
        st.setInt(1, id);
        ResultSet rs = st.executeQuery();
        Instance i = new Instance();
        if (rs.next()) {
            i.setId(rs.getInt("idInstance"));
            i.setMaxClauseLength(rs.getInt("maxClauseLength"));
            i.setMd5(rs.getString("md5"));
            i.setName(rs.getString("name"));
            i.setNumAtoms(rs.getInt("numAtoms"));
            i.setNumClauses(rs.getInt("numClauses"));
            i.setRatio(rs.getInt("ratio"));

            Instance c = getCached(i);
            if (c != null) return c;
            else {
                i.setSaved();
                cacheInstance(i);
                return i;
            }
        }
        return null;
    }

    /**
     * retrieves all instances from the database
     * @return all instances in a List
     * @throws SQLException
     */
    public static LinkedList<Instance> getAll() throws SQLException {
        // return linked list with all instances
        Statement st = DatabaseConnector.getInstance().conn.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM " + table);
        LinkedList<Instance> res = new LinkedList<Instance>();
        while (rs.next()) {
            Instance i = new Instance();
            i.setId(rs.getInt("idInstance"));
            i.setMaxClauseLength(rs.getInt("maxClauseLength"));
            i.setMd5(rs.getString("md5"));
            i.setName(rs.getString("name"));
            i.setNumAtoms(rs.getInt("numAtoms"));
            i.setNumClauses(rs.getInt("numClauses"));
            i.setRatio(rs.getInt("ratio"));
            
            Instance c = getCached(i);
            if (c != null) res.add(c);
            else {
                i.setSaved();
                cacheInstance(i);
                res.add(i);
            }
        }
        rs.close();
        return res;
    }
}
