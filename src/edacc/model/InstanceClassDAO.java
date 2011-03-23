/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edacc.model;

import java.io.File;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Vector;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

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
     * @param parent 
     * @param source
     * @return
     * @throws SQLException
     */
    public static InstanceClass createInstanceClass(String name, String description, InstanceClass parent, boolean source) throws SQLException, InstanceClassAlreadyInDBException {
        PreparedStatement ps;
        if (parent == null) {
            ps = DatabaseConnector.getInstance().getConn().prepareStatement("SELECT idinstanceClass FROM " + table + " WHERE name=?  AND parent IS NULL;");
        } else {
            ps = DatabaseConnector.getInstance().getConn().prepareStatement("SELECT idinstanceClass FROM " + table + " WHERE name=? AND parent=?;");
            ps.setInt(2, parent.getId());
        }

        ps.setString(1, name);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            throw new InstanceClassAlreadyInDBException();
        }
        InstanceClass i = new InstanceClass();
        i.setName(name);
        i.setDescription(description);
        i.setSource(source);
        if (parent == null) {
            i.setParentId(0);
        } else {
            i.setParentId(parent.getInstanceClassID());
        }
        save(i, parent);
        return i;
    }

    public static void delete(InstanceClass i) throws NoConnectionToDBException, SQLException, InstanceSourceClassHasInstance {
        PreparedStatement ps;
        if (i.isSource()) {
            ps = DatabaseConnector.getInstance().getConn().prepareStatement("SELECT name FROM Instances WHERE instanceClass_idinstanceClass = ?");
            ps.setInt(1, i.getInstanceClassID());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                throw new InstanceSourceClassHasInstance();
            }
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
    public static void save(InstanceClass instanceClass, InstanceClass parent) throws SQLException {
        PreparedStatement ps;
        if (instanceClass.isNew()) {
            // insert query, set ID!
            // insert instance into db
            final String insertQuery = "INSERT INTO " + table + " (name, description, source, parent) "
                    + "VALUES (?, ?, ?, ?)";
            ps = DatabaseConnector.getInstance().getConn().prepareStatement(insertQuery, PreparedStatement.RETURN_GENERATED_KEYS);
            ps.setString(1, instanceClass.getName());
            ps.setString(2, instanceClass.getDescription());
            ps.setBoolean(3, instanceClass.isSource());
            if (parent != null) {
                ps.setInt(4, parent.getId());
            } else {
                ps.setNull(4, java.sql.Types.NULL);
            }

            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                instanceClass.setInstanceClassID(rs.getInt(1));
            }
            rs.close();
            instanceClass.setSaved();
            cache.cache(instanceClass);
            ps.close();

        } else if (instanceClass.isModified()) {
            // update query
            final String updateQuery = "UPDATE " + table + " SET name=?, description=?, source=?,  parent=? "
                    + "WHERE idinstanceClass=?";
            ps = DatabaseConnector.getInstance().getConn().prepareStatement(updateQuery);

            ps.setInt(5, instanceClass.getInstanceClassID());
            ps.setString(1, instanceClass.getName());
            ps.setString(2, instanceClass.getDescription());
            ps.setBoolean(3, instanceClass.isSource());
            if (parent != null) {
                ps.setInt(4, parent.getId());
            } else {
                ps.setNull(4, java.sql.Types.NULL);
            }

            ps.executeUpdate();
            instanceClass.setSaved();
            cache.cache(instanceClass);
            ps.close();

        } else {
            return;
        }




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


        PreparedStatement st = DatabaseConnector.getInstance().getConn().prepareStatement("SELECT idinstanceClass, name, description, source, parent FROM " + table + " WHERE idinstanceClass=?");
        st.setInt(1, id);
        ResultSet rs = st.executeQuery();
        InstanceClass i = new InstanceClass();
        if (rs.next()) {
            i.setInstanceClassID(rs.getInt("idinstanceClass"));
            i.setName(rs.getString("name"));
            i.setDescription(rs.getString("description"));
            i.setSource(rs.getBoolean("source"));
            i.setParentId(rs.getInt("parent"));

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

    /**
     * 
     * @author rretz
     * 
     * @return
     * @throws NoConnectionToDBException
     * @throws SQLException
     */
    public static LinkedList<InstanceClass> getAllSourceClass() throws NoConnectionToDBException, SQLException {
        Statement st = DatabaseConnector.getInstance().getConn().createStatement();
        ResultSet rs = st.executeQuery("SELECT idInstanceClass, name, description, source, parent FROM " + table
                + " WHERE source = 1");
        LinkedList<InstanceClass> res = new LinkedList<InstanceClass>();
        while (rs.next()) {
            InstanceClass i = new InstanceClass();
            i.setInstanceClassID(rs.getInt("idinstanceClass"));
            i.setName(rs.getString("name"));
            i.setDescription(rs.getString("description"));
            i.setSource(rs.getBoolean("source"));
            i.setParentId(rs.getInt("parent"));

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
        ResultSet rs = st.executeQuery("SELECT idInstanceClass, name, description, source, parent FROM " + table);
        LinkedList<InstanceClass> res = new LinkedList<InstanceClass>();
        while (rs.next()) {
            InstanceClass i = new InstanceClass();
            i.setInstanceClassID(rs.getInt("idinstanceClass"));
            i.setName(rs.getString("name"));
            i.setDescription(rs.getString("description"));
            i.setSource(rs.getBoolean("source"));
            i.setParentId(rs.getInt("parent"));

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
        ResultSet rs = st.executeQuery("SELECT idInstanceClass, name, description, source, parent FROM " + table
                + " WHERE source = 0");
        LinkedList<InstanceClass> res = new LinkedList<InstanceClass>();
        while (rs.next()) {
            InstanceClass i = new InstanceClass();
            i.setInstanceClassID(rs.getInt("idinstanceClass"));
            i.setName(rs.getString("name"));
            i.setDescription(rs.getString("description"));
            i.setSource(rs.getBoolean("source"));
            i.setParentId(rs.getInt("parent"));

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

    public static DefaultMutableTreeNode getAllAsTreeFast() throws NoConnectionToDBException, SQLException {
        loadAllInstanceClasses();
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("root");
        Statement st = DatabaseConnector.getInstance().getConn().createStatement();
        ResultSet rs = st.executeQuery("SELECT idInstanceClass, parent FROM " + table);
        HashMap<Integer, LinkedList<Integer>> mapIds = new HashMap<Integer, LinkedList<Integer>>();
        LinkedList<DefaultMutableTreeNode> toIterate = new LinkedList<DefaultMutableTreeNode>();
        while (rs.next()) {
            int id = rs.getInt(1);
            Integer parent = rs.getInt(2);
            if (rs.wasNull()) {
                DefaultMutableTreeNode tmp = new DefaultMutableTreeNode(getById(id));
                root.add(tmp);
                toIterate.add(tmp);
            } else {
                LinkedList idList = mapIds.get(parent);
                if (idList == null) {
                    idList = new LinkedList<Integer>();
                    mapIds.put(parent, idList);
                }
                idList.add(id);
            }
        }
        while (toIterate.size() > 0) {
            DefaultMutableTreeNode node = toIterate.pop();
            InstanceClass i = (InstanceClass) node.getUserObject();
            LinkedList<Integer> idList = mapIds.get(i.getId());
            if (idList != null) {
                for (Integer id : idList) {
                    DefaultMutableTreeNode tmp = new DefaultMutableTreeNode(getById(id));
                    node.add(tmp);
                    toIterate.add(tmp);
                }
            }
        }
        return root;
    }

    public static DefaultMutableTreeNode getAllAsTree() throws NoConnectionToDBException, SQLException {
        loadAllInstanceClasses();
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("root");
        // First get all root InstanceClasses
        PreparedStatement st = DatabaseConnector.getInstance().getConn().prepareStatement("SELECT idinstanceClass FROM " + table + " WHERE parent IS NULL");
        ResultSet rs = st.executeQuery();
        Boolean first = true;
        PreparedStatement ps = null;
        while (rs.next()) {
            if (first) {
                ps = DatabaseConnector.getInstance().getConn().prepareStatement("SELECT idinstanceClass FROM " + table + " WHERE parent=?");
                first = false;
            }
            root.add(getNodeWithChildren(rs.getInt(1), ps));
        }
        return root;
    }

    private static DefaultMutableTreeNode getNodeWithChildren(int id, PreparedStatement ps) throws SQLException, SQLException {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode(getById(id));
        ps.setInt(1, id);
        ResultSet rs = ps.executeQuery();
        Boolean first = true;
        PreparedStatement st = null;
        while (rs.next()) {
            if (first) {
                st = DatabaseConnector.getInstance().getConn().prepareStatement("SELECT idinstanceClass FROM " + table + " WHERE parent=?");
                first = false;
            }
            root.add(getNodeWithChildren(rs.getInt(1), st));
        }
        return root;
    }

    public static boolean checkIfEmpty(Vector<InstanceClass> toRemove) throws SQLException {
        PreparedStatement ps;
        for (int i = 0; i < toRemove.size(); i++) {
            if (toRemove.get(i).isSource()) {
                ps = DatabaseConnector.getInstance().getConn().prepareStatement("SELECT name FROM Instances WHERE instanceClass_idinstanceClass = ?");
                ps.setInt(1, toRemove.get(i).getInstanceClassID());
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    return false;
                }
            }

        }
        return true;
    }

    public static DefaultTreeModel getSourceAsTree() throws NoConnectionToDBException, SQLException {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("root");
        // First get all root InstanceClasses
        PreparedStatement st = DatabaseConnector.getInstance().getConn().prepareStatement("SELECT idinstanceClass FROM " + table + " WHERE parent IS NULL AND source;");
        ResultSet rs = st.executeQuery();
        PreparedStatement ps = null;
        Boolean first = true;
        while (rs.next()) {
            if (first) {
                ps = DatabaseConnector.getInstance().getConn().prepareStatement("SELECT idinstanceClass FROM " + table + " WHERE parent=?");
                first = false;
            }
            root.add(getNodeWithChildren(rs.getInt(1), ps));
        }
        return new DefaultTreeModel(root);
    }

    public static DefaultTreeModel getUserClassAsTree() throws NoConnectionToDBException, SQLException {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("root");
        // First get all root InstanceClasses
        PreparedStatement st = DatabaseConnector.getInstance().getConn().prepareStatement("SELECT idinstanceClass FROM " + table + " WHERE parent IS NULL AND NOT source;");
        ResultSet rs = st.executeQuery();
        PreparedStatement ps = null;
        Boolean first = true;
        while (rs.next()) {
            if (first) {
                ps = DatabaseConnector.getInstance().getConn().prepareStatement("SELECT idinstanceClass FROM " + table + " WHERE parent=?");
                first = false;
            }
            root.add(getNodeWithChildren(rs.getInt(1), ps));
        }
        return new DefaultTreeModel(root);
    }

    public static DefaultTreeModel getSourceAsTreeWithoutNode(InstanceClass without) throws NoConnectionToDBException, SQLException {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("root");
        // First get all root InstanceClasses
        PreparedStatement st = DatabaseConnector.getInstance().getConn().prepareStatement("SELECT idinstanceClass FROM " + table + " WHERE parent IS NULL AND source;");
        ResultSet rs = st.executeQuery();
        while (rs.next()) {
            if (rs.getInt(1) != without.getId()) {
                root.add(getNodeWithChildrenWithoutNode(rs.getInt(1), without));
            }

        }
        return new DefaultTreeModel(root);
    }

    public static DefaultTreeModel getUserClassAsTreeWithoutNode(InstanceClass without) throws NoConnectionToDBException, SQLException {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("root");
        // First get all root InstanceClasses
        PreparedStatement st = DatabaseConnector.getInstance().getConn().prepareStatement("SELECT idinstanceClass FROM " + table + " WHERE parent IS NULL AND NOT source;");
        ResultSet rs = st.executeQuery();
        while (rs.next()) {
            if (rs.getInt(1) != without.getId()) {
                root.add(getNodeWithChildrenWithoutNode(rs.getInt(1), without));
            }

        }
        return new DefaultTreeModel(root);
    }

    private static DefaultMutableTreeNode getNodeWithChildrenWithoutNode(int id, InstanceClass without) throws SQLException, SQLException {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode(getById(id));
        PreparedStatement st = DatabaseConnector.getInstance().getConn().prepareStatement("SELECT idinstanceClass FROM " + table + " WHERE parent=?");
        st.setInt(1, id);
        ResultSet rs = st.executeQuery();
        while (rs.next()) {
            if (rs.getInt(1) != without.getId()) {
                root.add(getNodeWithChildrenWithoutNode(rs.getInt(1), without));
            }
        }
        return root;
    }

    private static void loadAllInstanceClasses() throws SQLException {
        Statement st = DatabaseConnector.getInstance().getConn().createStatement();
        ResultSet rs = st.executeQuery("SELECT idInstanceClass, name, description, source, parent FROM " + table);
        while (rs.next()) {
            InstanceClass i = new InstanceClass();
            i.setInstanceClassID(rs.getInt("idinstanceClass"));
            i.setName(rs.getString("name"));
            i.setDescription(rs.getString("description"));
            i.setSource(rs.getBoolean("source"));
            i.setParentId(rs.getInt("parent"));

            InstanceClass c = cache.getCached(i.getId());
            if (c != null) {
            } else {
                i.setSaved();
                cache.cache(i);
            }
        }
        rs.close();
        st.close();
    }


    /**
     *
     * @param root The root directory/file
     * @return All directories in the root File as InstanceClasses, structred as a tree in a DefaultMutableTreeNode. If root isn't a directory
     *         null is returned.
     */
    public static DefaultMutableTreeNode createInstanceClassFromDirectory(File root) throws SQLException, InstanceClassAlreadyInDBException{
        if(root.isDirectory()){
            InstanceClass rootClass = InstanceClassDAO.createInstanceClass(root.getName(), "Autogenerated instance source class", null, true);
            DefaultMutableTreeNode node = new DefaultMutableTreeNode(rootClass);
            File[] children = root.listFiles();
            //Add all child InstanceClasses to the root InstanceClass
            for(int i = 0; i < children.length; i++){
                DefaultMutableTreeNode tmp = createInstanceClassFromDirectory(children[i], rootClass);
                if(tmp != null)
                    node.add(tmp);
            }
            return node;
        }else
            return null;
    }

    /**
     *
     * @param root The root directory/file
     * @param parentClass The parent InstanceClass
     * @return All directories in the root File as InstanceClasses, structred as a tree in a DefaultMutableTreeNode. If root isn't a directory
     *         null is returned.
     * @throws SQLException
     * @throws InstanceClassAlreadyInDBException
     */
    private static DefaultMutableTreeNode createInstanceClassFromDirectory(File root, InstanceClass parentClass) throws SQLException, InstanceClassAlreadyInDBException {
        if(root.isDirectory()){
            
        }
        if(root.isDirectory()){
            InstanceClass rootClass = InstanceClassDAO.createInstanceClass(root.getName(), "Autogenerated instance source class", parentClass, true);
            DefaultMutableTreeNode node = new DefaultMutableTreeNode(rootClass);
            File[] children = root.listFiles();
            //Add all child InstanceClasses to the root InstanceClass
            for(int i = 0; i < children.length; i++){
                DefaultMutableTreeNode tmp = createInstanceClassFromDirectory(children[i], rootClass);
                if(tmp != null)
                    node.add(tmp);
            }
            return node;
        }else
            return null;
    }
}
