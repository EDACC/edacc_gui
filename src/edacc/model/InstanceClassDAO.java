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
import java.util.LinkedList;
import java.util.Vector;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 *
 * @author dgall
 */
public class InstanceClassDAO {

    public static DefaultMutableTreeNode getTreeCache() {
        return treeCache;
    }
    private static DefaultMutableTreeNode treeCache;
    private static DefaultMutableTreeNode tmpTreeBranch = new DefaultMutableTreeNode(null);
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
    public static InstanceClass createInstanceClass(String name, String description, InstanceClass parent) throws SQLException, InstanceClassAlreadyInDBException {
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
            return getById(rs.getInt(1));
        }
        InstanceClass i = new InstanceClass();
        i.setName(name);
        i.setDescription(description);
        if (parent == null) {
            i.setParentId(0);
        } else {
            i.setParentId(parent.getInstanceClassID());
        }
        addToTmpTreeBranch(i, parent);
        save(i, parent);
        return i;
    }

    /**
     * Deletes the given instance class. If the given instance class is the last related class of an instance, the instances are deleted too.
     * @param i The instanceClass object to delete.
     * @throws NoConnectionToDBException
     * @throws SQLException
     * @throws InstanceSourceClassHasInstance
     */
    public static void delete(InstanceClass i) throws NoConnectionToDBException, SQLException, InstanceSourceClassHasInstance, InstanceIsInExperimentException {
        PreparedStatement ps;
        // Check if the InstanceClass i is the last class of an instance, if true, delete all these instances.
        Vector<Instance> lastRelated = InstanceDAO.getLastRelatedInstances(i);
        if (!lastRelated.isEmpty()) {
            InstanceDAO.deleteAll(lastRelated);
        }
        ps = DatabaseConnector.getInstance().getConn().prepareStatement("DELETE FROM " + table + " WHERE idinstanceClass=?");
        ps.setInt(1, i.getInstanceClassID());
        ps.executeUpdate();
        removeFromTreeCache(i);
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
            final String insertQuery = "INSERT INTO " + table + " (name, description, parent) "
                    + "VALUES (?, ?, ?)";
            ps = DatabaseConnector.getInstance().getConn().prepareStatement(insertQuery, PreparedStatement.RETURN_GENERATED_KEYS);
            ps.setString(1, instanceClass.getName());
            ps.setString(2, instanceClass.getDescription());
            if (parent != null) {
                ps.setInt(3, parent.getId());
            } else {
                ps.setNull(3, java.sql.Types.NULL);
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
            final String updateQuery = "UPDATE " + table + " SET name=?, description=?, parent=? "
                    + " WHERE idinstanceClass=?";
            ps = DatabaseConnector.getInstance().getConn().prepareStatement(updateQuery);

            ps.setInt(4, instanceClass.getInstanceClassID());
            ps.setString(1, instanceClass.getName());
            ps.setString(2, instanceClass.getDescription());
            if (parent != null) {
                ps.setInt(3, parent.getId());
            } else {
                ps.setNull(3, java.sql.Types.NULL);
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


        PreparedStatement st = DatabaseConnector.getInstance().getConn().prepareStatement("SELECT idinstanceClass, name, description, parent FROM " + table + " WHERE idinstanceClass=?");
        st.setInt(1, id);
        ResultSet rs = st.executeQuery();
        InstanceClass i = new InstanceClass();
        if (rs.next()) {
            i.setInstanceClassID(rs.getInt("idinstanceClass"));
            i.setName(rs.getString("name"));
            i.setDescription(rs.getString("description"));
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
     * retrieves all instance classes from the database
     * @return all instance classes in a List
     * @throws SQLException
     */
    public static LinkedList<InstanceClass> getAll() throws SQLException {
        // return linked list with all instances
        Statement st = DatabaseConnector.getInstance().getConn().createStatement();
        ResultSet rs = st.executeQuery("SELECT idInstanceClass, name, description, parent FROM " + table);
        LinkedList<InstanceClass> res = new LinkedList<InstanceClass>();
        while (rs.next()) {
            InstanceClass i = new InstanceClass();
            i.setInstanceClassID(rs.getInt("idinstanceClass"));
            i.setName(rs.getString("name"));
            i.setDescription(rs.getString("description"));
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
        rs.close();
        st.close();
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
        treeCache = root;
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
        treeCache = root;
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

    /**
     * If the returned vector is empty, the instance classes can be removed.
     * @param toRemove The instance classes to remove
     * @return Vector<Instance> which one of the classes to remove are the last related class.
     * @throws SQLException
     */
    public static Vector<Instance> checkIfEmpty(Vector<InstanceClass> toRemove) throws SQLException {
        PreparedStatement ps;
        Vector<Instance> lastRelated = new Vector<Instance>();
        for (int i = 0; i < toRemove.size(); i++) {
            lastRelated.addAll(InstanceDAO.getLastRelatedInstances(toRemove.get(i)));
        }
        return lastRelated;
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
        ResultSet rs = st.executeQuery("SELECT idInstanceClass, name, description, parent FROM " + table);
        while (rs.next()) {
            InstanceClass i = new InstanceClass();
            i.setInstanceClassID(rs.getInt("idinstanceClass"));
            i.setName(rs.getString("name"));
            i.setDescription(rs.getString("description"));
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
    public static DefaultMutableTreeNode createInstanceClassFromDirectory(File root) throws SQLException, InstanceClassAlreadyInDBException {
        if (root.isDirectory()) {
            InstanceClass rootClass = InstanceClassDAO.createInstanceClass(root.getName(), "Autogenerated instance class", null);
            DefaultMutableTreeNode node = new DefaultMutableTreeNode(rootClass);
            File[] children = root.listFiles();
            //Add all child InstanceClasses to the root InstanceClass
            for (int i = 0; i < children.length; i++) {
                DefaultMutableTreeNode tmp = createInstanceClassFromDirectory(children[i], rootClass);
                if (tmp != null) {
                    node.add(tmp);
                }
            }
            return node;
        } else {
            return null;
        }
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
    public static DefaultMutableTreeNode createInstanceClassFromDirectory(File root, InstanceClass parentClass) throws SQLException, InstanceClassAlreadyInDBException {
        if (root.isDirectory()) {
            InstanceClass rootClass = InstanceClassDAO.createInstanceClass(root.getName(), "Autogenerated instance class", parentClass);
            DefaultMutableTreeNode node = new DefaultMutableTreeNode(rootClass);
            File[] children = root.listFiles();
            //Add all child InstanceClasses to the root InstanceClass
            for (int i = 0; i < children.length; i++) {
                DefaultMutableTreeNode tmp = createInstanceClassFromDirectory(children[i], rootClass);
                if (tmp != null) {
                    node.add(tmp);
                }
            }
            return node;
        } else {
            return null;
        }
    }

    /**
     * 
     * @param id The id of the InstanceClass, which ancestors path is requested.
     * @return The  ancestors path of the InstanceClass as a String. The path is represented like \grandparent\parent\child.
     * @throws SQLException 
     */
    public static String getCompletePathOf(int id) throws SQLException {
        PreparedStatement st = DatabaseConnector.getInstance().getConn().prepareStatement("SELECT parent FROM " + table + " WHERE idinstanceClass =?;");
        st.setInt(1, id);
        ResultSet rs = st.executeQuery();
        rs.next();
        Object tmp = rs.getObject(1);
        if (tmp != null) {
            String res = getCompletePathOf(rs.getInt(1));
            return res + "\\" + getById(id).getName();
        } else {
            return "\\" + getById(id).getName();
        }
    }

    /**
     * Adds the given InstanceClass-object to the InstanceClassTree to the given parent-object.
     * @param i
     * @param parent 
     */
    private static void addToTmpTreeBranch(InstanceClass i, InstanceClass parent) {
        if (parent == null) {
            tmpTreeBranch = new DefaultMutableTreeNode(null);
            tmpTreeBranch.add(new DefaultMutableTreeNode(i));
        } else if (tmpTreeBranch.isLeaf()) {
            tmpTreeBranch = new DefaultMutableTreeNode(parent);
            tmpTreeBranch.add(new DefaultMutableTreeNode(i));
        } else {
            searchNodeAddChild(parent, i, tmpTreeBranch);
        }
    }

    /**
     * Searchs the InstanceClass-object parent in the the given DefaultMutableTreeNode. If the parent is found, the the InstanceClass-object
     * i is added at a child of parent.
     * @param parent
     * @param i
     * @param node 
     */
    private static void searchNodeAddChild(InstanceClass parent, InstanceClass i, DefaultMutableTreeNode node) {
        if (node.getUserObject() == parent) {
            node.add(new DefaultMutableTreeNode(i));
        } else {
            for (int j = 0; j < node.getChildCount(); j++) {
                searchNodeAddChild(parent, i, (DefaultMutableTreeNode) node.getChildAt(j));
            }
        }
    }

    public static void addTmpTreeBranchToTreeCache() {
        if (((DefaultMutableTreeNode) tmpTreeBranch.getRoot()).getUserObject() == null) {
            for (int i = 0; i < tmpTreeBranch.getChildCount(); i++) {
                DefaultMutableTreeNode tmp = (DefaultMutableTreeNode) tmpTreeBranch.getChildAt(i);
                treeCache.add(tmp);
            }
        } else {
            InstanceClass parent = (InstanceClass) ((DefaultMutableTreeNode) tmpTreeBranch).getUserObject();
            for (int i = 0; i < tmpTreeBranch.getChildCount(); i++) {
                DefaultMutableTreeNode tmp = (DefaultMutableTreeNode) tmpTreeBranch.getChildAt(i);
                searchInstanceClassAddChildTree(parent, tmp, treeCache);
            }
        }
        tmpTreeBranch = new DefaultMutableTreeNode(null);
    }

    private static void searchInstanceClassAddChildTree(InstanceClass parent, DefaultMutableTreeNode tmpTreeBranch, DefaultMutableTreeNode treeCache) {
        if (treeCache.getUserObject() == parent) {
            treeCache.add(tmpTreeBranch);
        } else {
            for (int j = 0; j < treeCache.getChildCount(); j++) {
                searchInstanceClassAddChildTree(parent, tmpTreeBranch, (DefaultMutableTreeNode) treeCache.getChildAt(j));
            }
        }
    }

    /**
     * Removes the given InstanceClass-object i from the treeCache.
     * @param i 
     */
    private static void removeFromTreeCache(InstanceClass i) {
        searcheInstanceClassRemoveFromTree(i, treeCache);
    }

    private static void searcheInstanceClassRemoveFromTree(InstanceClass i, DefaultMutableTreeNode node) {
        for (int j = 0; j < node.getChildCount(); j++) {
            DefaultMutableTreeNode child = (DefaultMutableTreeNode) node.getChildAt(j);
            if (child.getUserObject() == i) {
                node.remove(j);
                return;
            }
        }
        for (int j = 0; j < node.getChildCount(); j++) {
            searcheInstanceClassRemoveFromTree(i, (DefaultMutableTreeNode) node.getChildAt(j));
        }

    }
}
