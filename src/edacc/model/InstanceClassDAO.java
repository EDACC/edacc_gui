/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edacc.model;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Vector;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 *
 * @author dgall
 */
public class InstanceClassDAO {

    protected static final String table = "instanceClass";
    private static final ObjectCache<InstanceClass> cache = new ObjectCache<InstanceClass>();

    /**
     * InstanceClass factory method, ensures that the created instance class is persisted and assigned an ID
     * so it can be referenced by related objects. Checks if the instance class is already in the Datebase.
     * @param name
     * @param description
     * @param source
     * @return
     * @throws SQLException
     */
    public static InstanceClass createInstanceClass(String name, String description, boolean source) throws SQLException, InstanceClassAlreadyInDBException {
        PreparedStatement ps;
        final String Query = "SELECT * FROM " + table + " WHERE name = ?";
        ps = DatabaseConnector.getInstance().getConn().prepareStatement(Query);
        ps.setString(1, name);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            throw new InstanceClassAlreadyInDBException();
        }

        InstanceClass i = new InstanceClass();
        i.setName(name);
        i.setDescription(description);
        i.setSource(source);
        save(i);
        rs.close();
        ps.close();
        return i;
    }

    public static void delete(InstanceClass i) throws NoConnectionToDBException, SQLException, InstanceSourceClassHasInstance {
        PreparedStatement ps;
        if(i.isSource()){
            ps = DatabaseConnector.getInstance().getConn().prepareStatement("SELECT name FROM Instances WHERE instanceClass_idinstanceClass = ?");
            ps.setInt(1, i.getInstanceClassID());
            ResultSet rs = ps.executeQuery();
            if(rs.next()) throw new InstanceSourceClassHasInstance();
        }
        ps = DatabaseConnector.getInstance().getConn().prepareStatement("DELETE FROM " + table + " WHERE idinstanceClass=?");
        ps.setInt(1, i.getInstanceClassID());
        ps.executeUpdate();
        cache.remove(i);
        i.setDeleted();
        ps.close();
    }

    /**
     * persists an instance class object in the database
     * @param instance The instance object to persist
     * @throws SQLException if an SQL error occurs while saving the instance.
     */
    public static void save(InstanceClass instanceClass) throws SQLException {
        PreparedStatement ps;
        if (instanceClass.isNew()) {
            // insert query, set ID!
            // insert instance into db
            final String insertQuery = "INSERT INTO " + table + " (name, description, source) "
                    + "VALUES (?, ?, ?)";
            ps = DatabaseConnector.getInstance().getConn().prepareStatement(insertQuery, PreparedStatement.RETURN_GENERATED_KEYS);
        } else if (instanceClass.isModified()) {
            // update query
            final String updateQuery = "UPDATE " + table + " SET name=?, description=?, source=? "
                    + "WHERE idinstanceClass=?";
            ps = DatabaseConnector.getInstance().getConn().prepareStatement(updateQuery);

            ps.setInt(4, instanceClass.getInstanceClassID());

        } else {
            return;
        }

        ps.setString(1, instanceClass.getName());
        ps.setString(2, instanceClass.getDescription());
        ps.setBoolean(3, instanceClass.isSource());
        ps.executeUpdate();

        // set id
        if (instanceClass.isNew()) {
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                instanceClass.setInstanceClassID(rs.getInt(1));
            }
            rs.close();
        }

        instanceClass.setSaved();
        cache.cache(instanceClass);
        ps.close();
    }

    /**
     * retrieves an instance class from the database
     * @param id the id of the instance class to be retrieved
     * @return the instance class specified by its id
     * @throws SQLException
     */
    public static InstanceClass getById(int id) throws SQLException {
        InstanceClass c = cache.getCached(id);
        if (c != null) {
            return c;
        }


        PreparedStatement st = DatabaseConnector.getInstance().getConn().prepareStatement("SELECT idinstanceClass, name, description, source FROM " + table + " WHERE idinstanceClass=?");
        st.setInt(1, id);
        ResultSet rs = st.executeQuery();
        InstanceClass i = new InstanceClass();
        if (rs.next()) {
            i.setInstanceClassID(rs.getInt("idinstanceClass"));
            i.setName(rs.getString("name"));
            i.setDescription(rs.getString("description"));
            i.setSource(rs.getBoolean("source"));

            i.setSaved();
            cache.cache(i);
            rs.close();
            st.close();
            return i;
        }
        rs.close();
        st.close();
        return null;
    }

    public static InstanceClass getByName(String name) throws NoConnectionToDBException, SQLException{
        PreparedStatement st = DatabaseConnector.getInstance().getConn().prepareStatement("SELECT idinstanceClass, name, description, source FROM " + table + " WHERE name=?");
        st.setString(1, name);
        ResultSet rs = st.executeQuery();
        InstanceClass i = new InstanceClass();
        if (rs.next()) {
            i.setInstanceClassID(rs.getInt("idinstanceClass"));
            i.setName(rs.getString("name"));
            i.setDescription(rs.getString("description"));
            i.setSource(rs.getBoolean("source"));

            InstanceClass c = cache.getCached(i.getId());
            if (c != null) {
                return c;
            } else {
                i.setSaved();
                cache.cache(i);
                return i;
            }
        }
        return null;
    }

    /**
     * 
     * @author rretz
     * 
     * @return
     * @throws NoConnectionToDBException
     * @throws SQLException
     */
    public static LinkedList<InstanceClass> getAllSourceClass() throws NoConnectionToDBException, SQLException{
        Statement st = DatabaseConnector.getInstance().getConn().createStatement();
        ResultSet rs = st.executeQuery("SELECT idInstanceClass, name, description, source FROM " + table +
                " WHERE source = 1");
        LinkedList<InstanceClass> res = new LinkedList<InstanceClass>();
        while (rs.next()) {
            InstanceClass i = new InstanceClass();
            i.setInstanceClassID(rs.getInt("idinstanceClass"));
            i.setName(rs.getString("name"));
            i.setDescription(rs.getString("description"));
            i.setSource(rs.getBoolean("source"));

            InstanceClass c = cache.getCached(i.getId());

            if (c != null) {
                res.add(c);
            } else {
                i.setSaved();
                cache.cache(i);
                res.add(i);
            }
        }
        rs.close();
        return res;
    }

    /**
     * retrieves all instance classes from the database
     * @return all instance classes in a List
     * @throws SQLException
     */
    public static LinkedList<InstanceClass> getAll() throws SQLException {
        // return linked list with all instances
        Statement st = DatabaseConnector.getInstance().getConn().createStatement();
        ResultSet rs = st.executeQuery("SELECT idInstanceClass, name, description, source FROM " + table);
        LinkedList<InstanceClass> res = new LinkedList<InstanceClass>();
        while (rs.next()) {
            InstanceClass i = new InstanceClass();
            i.setInstanceClassID(rs.getInt("idinstanceClass"));
            i.setName(rs.getString("name"));
            i.setDescription(rs.getString("description"));
            i.setSource(rs.getBoolean("source"));

            InstanceClass c = cache.getCached(i.getId());

            if (c != null) {
                res.add(c);
            } else {
                i.setSaved();
                cache.cache(i);
                res.add(i);
            }
        }
        rs.close();
        return res;
    }

    public static LinkedList<InstanceClass> getAllUserClass() throws NoConnectionToDBException, SQLException {
        Statement st = DatabaseConnector.getInstance().getConn().createStatement();
        ResultSet rs = st.executeQuery("SELECT idInstanceClass, name, description, source FROM " + table +
                " WHERE source = 0");
        LinkedList<InstanceClass> res = new LinkedList<InstanceClass>();
        while (rs.next()) {
            InstanceClass i = new InstanceClass();
            i.setInstanceClassID(rs.getInt("idinstanceClass"));
            i.setName(rs.getString("name"));
            i.setDescription(rs.getString("description"));
            i.setSource(rs.getBoolean("source"));

            InstanceClass c = cache.getCached(i.getId());

            if (c != null) {
                res.add(c);
            } else {
                i.setSaved();
                cache.cache(i);
                res.add(i);
            }
        }
        rs.close();
        return res;
    }

    public static void clearCache() {
        cache.clear();
    }

    public static DefaultMutableTreeNode getAllAsTree(){
        PreparedStatement st = DatabaseConnector.getInstance().getConn().prepareStatement("SELECT * FROM " + table + " WHERE name=?");
        st.setString(1, name);
        ResultSet rs = st.executeQuery();
        InstanceClass i = new InstanceClass();
    }

}
