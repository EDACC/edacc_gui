/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edacc.model;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Vector;

/**
 *
 * @author dgall
 */
public class InstanceHasInstanceClassDAO {

    protected static final String table = "Instances_has_instanceClass";
    protected static final String insertQuery = "INSERT INTO " + table + " (Instances_idInstance, instanceClass_idinstanceClass) VALUES (?, ?)";
    protected static final String deleteQuery = "DELETE FROM " + table + " WHERE Instances_idInstance=? AND instanceClass_idinstanceClass=?";
    private static final Hashtable<InstanceHasInstanceClass, InstanceHasInstanceClass> cache = new Hashtable<InstanceHasInstanceClass, InstanceHasInstanceClass>();

    /**
     * InstanceHasInstanceClass factory method, ensures that the created experiment is persisted.
     * @return new InstanceHasInstanceClass object
     */
    public static InstanceHasInstanceClass createInstanceHasInstance(Instance instance, InstanceClass instanceClass) throws SQLException {
        InstanceHasInstanceClass i = new InstanceHasInstanceClass(instance, instanceClass);
        i.setNew();
        save(i);
        return i;
    }

    /**
     * Returns a new InstanceHasInstanceClass object from a complete result set.
     * @param rs a complete result set containing all fields of the Instances_has_instanceClass table!
     * @return a new InstanceHasInstanceClass object. It won't check the cache!
     * @throws SQLException
     */
    private static InstanceHasInstanceClass getInstanceHasInstanceClassFromResultset(ResultSet rs) throws SQLException, InstanceClassMustBeSourceException {
        int idInstance = rs.getInt("Instances_idInstance");
        int idInstanceClass = rs.getInt("instanceClass_idinstanceClass");
        // get the Instance and InstanceClass objects from the DB
        Instance instance = InstanceDAO.getById(idInstance);
        InstanceClass instanceClass = InstanceClassDAO.getById(idInstanceClass);
        InstanceHasInstanceClass i = new InstanceHasInstanceClass(instance, instanceClass);
        return i;
    }

    /**
     * Persists an InstanceHasInstanceClass object in the DB.
     * This means: If it's new, it will be inserted into the db and if it's deleted, it will be removed from the db.
     * @param i
     * @throws SQLException
     */
    private static void save(InstanceHasInstanceClass i) throws SQLException {
        if (i.isDeleted()) {
            PreparedStatement st = DatabaseConnector.getInstance().getConn().prepareStatement(deleteQuery);
            st.setInt(1, i.getInstance().getId());
            st.setInt(2, i.getInstanceClass().getInstanceClassID());
            st.executeUpdate();
            cache.remove(i);
        } else if (i.isNew()) {
            PreparedStatement st = DatabaseConnector.getInstance().getConn().prepareStatement(insertQuery, PreparedStatement.RETURN_GENERATED_KEYS);
            st.setInt(1, i.getInstance().getId());
            st.setInt(2, i.getInstanceClass().getInstanceClassID());
            st.executeUpdate();
            i.setSaved();
            cacheInstanceHasInstanceClass(i);
        }

    }

    private static InstanceHasInstanceClass getCached(InstanceHasInstanceClass i) {
        if (cache.containsKey(i)) {
            return cache.get(i);
        } else {
            return null;
        }
    }

    private static void cacheInstanceHasInstanceClass(InstanceHasInstanceClass i) {
        if (cache.containsKey(i)) {
            return;
        } else {
            cache.put(i, i);
        }
    }

    /**
     * Removes an InstanceHasInstanceClass object from the DB and the cache.
     * @param i
     * @throws SQLException
     */
    public static void removeInstanceHasInstanceClass(InstanceHasInstanceClass i) throws SQLException {
        i.setDeleted();
        save(i);
    }

    private static Vector<InstanceHasInstanceClass> getInstanceHasInstanceClassByInstanceClassId(int id) throws SQLException, InstanceClassMustBeSourceException {
        Vector<InstanceHasInstanceClass> res = new Vector<InstanceHasInstanceClass>();
        PreparedStatement st = DatabaseConnector.getInstance().getConn().prepareStatement("SELECT * FROM " + table + " WHERE instanceClass_idinstanceClass=?");
        st.setInt(1, id);
        ResultSet rs = st.executeQuery();
        while (rs.next()) {
            InstanceHasInstanceClass i = getInstanceHasInstanceClassFromResultset(rs);

            InstanceHasInstanceClass c = getCached(i);
            if (c != null) {
                res.add(c);
            } else {
                i.setSaved();
                cacheInstanceHasInstanceClass(i);
                res.add(i);
            }
        }
        return res;
    }

     private static Vector<InstanceHasInstanceClass> getInstanceHasInstanceClassByInstanceId(int id) throws SQLException, InstanceClassMustBeSourceException {
        Vector<InstanceHasInstanceClass> res = new Vector<InstanceHasInstanceClass>();
        PreparedStatement st = DatabaseConnector.getInstance().getConn().prepareStatement("SELECT * FROM " + table + " WHERE Instances_idInstance=?");
        st.setInt(1, id);
        ResultSet rs = st.executeQuery();
        while (rs.next()) {
            InstanceHasInstanceClass i = getInstanceHasInstanceClassFromResultset(rs);

            InstanceHasInstanceClass c = getCached(i);
            if (c != null) {
                res.add(c);
            } else {
                i.setSaved();
                cacheInstanceHasInstanceClass(i);
                res.add(i);
            }
        }
        return res;
    }

    /**
     * Returns all persisted instances of an already persisted user defined instance class.
     * @param instanceClass
     * @return
     */
    public static Vector<Instance> getInstanceClassElements(InstanceClass instanceClass) throws SQLException {
        Vector<Instance> elements = new Vector<Instance>();
        Vector<InstanceHasInstanceClass> relation = getInstanceHasInstanceClassByInstanceClassId(instanceClass.getInstanceClassID());
        for (InstanceHasInstanceClass el : relation) {
            elements.add(el.getInstance());
        }
        return elements;
    }

    /**
     * Returns all persisted instance classes of an already persisted instance.
     * @param instanceClass
     * @return
     */
    public static Vector<InstanceClass> getInstanceClassElements(Instance instance) throws SQLException {
        Vector<InstanceClass> elements = new Vector<InstanceClass>();
        Vector<InstanceHasInstanceClass> relation = getInstanceHasInstanceClassByInstanceId(instance.getId());
        for (InstanceHasInstanceClass el : relation) {
            elements.add(el.getInstanceClass());
        }
        return elements;
    }

    public static InstanceHasInstanceClass getInstanceHasInstanceClass(InstanceClass tempInstanceClass,
            Instance tempInstance) throws NoConnectionToDBException, SQLException {
        String query = "SELECT * FROM " + table + " WHERE instanceClass_idinstanceClass=? " +
                "AND Instances_idInstance = ?";
        PreparedStatement st = DatabaseConnector.getInstance().getConn().prepareStatement(query);
        st.setInt(1, tempInstanceClass.getInstanceClassID());
        st.setInt(2, tempInstance.getId());
        ResultSet rs = st.executeQuery();
        if(rs.next()) {
            InstanceHasInstanceClass i = getInstanceHasInstanceClassFromResultset(rs);
            return i;
        }
        return null;
    }

    /**
     *
     * @param instances 
     * @return the intersection of all instanc classes of the given instances
     */
     public static Vector<InstanceClass> getIntersectionOfInstances(Vector<Instance> instances) throws NoConnectionToDBException, SQLException {

        String queryUser =  "SELECT instanceClass_idinstanceClass, COUNT(instanceClass_idinstanceClass) " +
                "FROM " + table + " WHERE Instances_idInstance = " + instances.firstElement().getId();
        for(int i = 1; i < instances.size(); i++){
            queryUser += " OR Instances_idInstance = " + instances.get(i).getId();
        }
        queryUser += " GROUP BY instanceClass_idinstanceClass";
        Statement st = DatabaseConnector.getInstance().getConn().createStatement();
        ResultSet rsUser = st.executeQuery(queryUser);

        Vector<InstanceClass> instanceClasses = new Vector<InstanceClass>();

        while(rsUser.next()){
            if(rsUser.getInt("COUNT(instanceClass_idinstanceClass)") == instances.size())
                instanceClasses.add(InstanceClassDAO.getById(rsUser.getInt("instanceClass_idinstanceClass")));
        }
        return instanceClasses;
    }
     
     
    public static void fillInstanceClassIds(HashMap<Instance, LinkedList<Integer>> instanceClassIds) throws SQLException {
        Statement st = DatabaseConnector.getInstance().getConn().createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM " + table);
        while (rs.next()) {
            InstanceHasInstanceClass i = getInstanceHasInstanceClassFromResultset(rs);
            LinkedList<Integer> classes = instanceClassIds.get(i.getInstance());
            if (classes != null) {
                classes.add(i.getInstanceClass().getId());
            }
        }
        rs.close();
        st.close();
    }

    public static LinkedList<Integer> getRelatedInstanceClasses(int id) throws SQLException {
        Statement st = DatabaseConnector.getInstance().getConn().createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM " + table + " WHERE Instances_idInstance=" + id);
        LinkedList<Integer> classes = new LinkedList<Integer>();
        while(rs.next()){
            InstanceHasInstanceClass i = getInstanceHasInstanceClassFromResultset(rs);
            classes.add(i.getInstanceClass().getId());
        }
        return classes;
    }
}
