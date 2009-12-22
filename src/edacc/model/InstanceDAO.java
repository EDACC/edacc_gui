package edacc.model;

import java.util.List;
import java.util.LinkedList;
import java.sql.*;

/**
 * data access object for the Instance class
 * @author daniel
 */
public class InstanceDAO {
    private static final String table = "instances";
    
    /*
     * So there are still some problems with this. Suppose we call getById() from two different
     * places with the same id. getById() will return 2 different objects in memory currently. Changing one
     * of them won't affect the other. This might lead to inconsistencies or memory hogs (although this is java after all...)
     * possible solution: local cache for Instance objects (e.g. in a hash table). All get methods will
     * have to check if the retrieved object already exists in memory and if so, return a reference to the
     * cached object instead.
     * problem: newly created objects will have to be added to the cache, so InstanceDAO could act as factory responsible
     * for the creation of all new Instance objects.
     */

    /**
     * persists an instance object in the database
     * @param instance The instance object to persist
     */
    public static void save(Instance instance) {
        if (instance.isNew()) {
            // insert query
            instance.setSaved();
        }
        else if (instance.isModified()) {
            // update query
            instance.setSaved();
        }
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
        if (rs.next()) {
            Instance i = new Instance();
            i.setId(rs.getInt("idInstance"));
            i.setMaxClauseLength(rs.getInt("maxClauseLength"));
            // ...
            i.setSaved();
            rs.close();
            return i;
        }
        else {
            rs.close();
            return null;
        }
    }

    /**
     * retrieves all instances from the database
     * @return all instances in a List
     * @throws SQLException
     */
    public static List<Instance> getAll() throws SQLException {
        // return linked list with all instances
        Statement st = DatabaseConnector.getInstance().conn.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM " + table);
        LinkedList<Instance> res = new LinkedList<Instance>();
        while (rs.next()) {
            Instance i = new Instance();
            i.setId(rs.getInt("idInstance"));
            i.setMaxClauseLength(rs.getInt("maxClauseLength"));
            // ...
            i.setSaved();
            res.add(i);
        }
        rs.close();
        return res;
    }
}
