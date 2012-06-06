/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edacc.model;

import edacc.properties.PropertyTypeNotExistException;
import edacc.satinstances.ConvertException;
import edacc.satinstances.InvalidVariableException;
import edacc.satinstances.PropertyValueType;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

/**
 *
 * @author dgall
 */
public class InstanceHasPropertyDAO {

    protected static final String table = "Instance_has_Property";
    protected static final String insertQuery = "INSERT INTO " + table + " (idInstance, idProperty, value) VALUES (?, ?, ?)";
    protected static final String deleteQuery = "DELETE FROM " + table + " WHERE idInstance=? AND idProperty=?";
    private static String updateQuery = "UPDATE " + table + " SET value=? WHERE idInstance=? AND idProperty=?";
    // idCache is a Hashtable <InstanceId, Hashtable<PropertyId, id>
    private static HashMap<Integer, HashMap<Integer, Integer>> idCache = new HashMap<Integer, HashMap<Integer, Integer>>();
    private static final ObjectCache<InstanceHasProperty> cache = new ObjectCache<InstanceHasProperty>();

    /**
     * Calculates the value of an InstanceProperty for an instance and saves it
     * in a new InstanceHasInstancePropertyObject which is automatically
     * persisted in the db.
     * @return new InstanceHasProperty object with the calculated value.
     */
    public static InstanceHasProperty createInstanceHasInstanceProperty(Instance instance, Property instanceProperty) throws SQLException, ConvertException, IOException, InvalidVariableException, InstanceNotInDBException {
        PropertyValueType type = instanceProperty.getPropertyValueType();
        // TODO: fix!
        String value = null; //type.getStringRepresentation(instanceProperty.computeProperty(InstanceDAO.getSATFormulaOfInstance(instance)));
        InstanceHasProperty i = new InstanceHasProperty(instance, instanceProperty, value);
        i.setNew();
        save(i);
        return i;
    }

    /**
     * Persists an InstanceHasInstanceClass object in the DB.
     * This means: If it's new, it will be inserted into the db and if it's deleted, it will be removed from the db.
     * @param i
     * @throws SQLException
     */
    public static void save(InstanceHasProperty i) throws SQLException {
        PreparedStatement st;
        if (i.isDeleted()) {
            st = DatabaseConnector.getInstance().getConn().prepareStatement(deleteQuery);
            cache.remove(i);
            idCache.remove(i.getInstance().getId());
        } else if (i.isNew()) {
            st = DatabaseConnector.getInstance().getConn().prepareStatement(insertQuery, PreparedStatement.RETURN_GENERATED_KEYS);
            cache.cache(i);
            i.setSaved();
            if (idCache.get(i.getInstance().getId()) == null) {
                HashMap tmp = new HashMap<Integer, Integer>();
                tmp.put(i.getProperty().getId(), i.getId());
                idCache.put(i.getInstance().getId(), tmp);
            } else {
                idCache.get(i.getInstance().getId()).put(i.getProperty().getId(), i.getId());
            }
            st.setString(3, i.getValue());
        } else if (i.isModified()) {
            st = DatabaseConnector.getInstance().getConn().prepareStatement(updateQuery);
            st.setString(1, i.getValue());
            st.setInt(2, i.getInstance().getId());
            st.setInt(3, i.getProperty().getId());
            st.executeUpdate();
            return;
        } else {
            st = null;
            return;
        }
        st.setInt(1, i.getInstance().getId());
        st.setInt(2, i.getProperty().getId());
        st.executeUpdate();

        // set id if necessary
        if (i.isSaved()) {
            ResultSet rs = st.getGeneratedKeys();
            if (rs.next()) {
                i.setId(rs.getInt(1));
            }
        }
    }

    /**
     * Removes an InstanceHasProperty object from the DB and the cache.
     * @param i
     * @throws SQLException
     */
    public static void removeInstanceHasInstanceProperty(InstanceHasProperty i) throws SQLException {
        i.setDeleted();
        save(i);
    }

    private static InstanceHasProperty getInstanceHasInstancePropertyFromResultSet(ResultSet rs) throws SQLException, IOException, NoConnectionToDBException, PropertyNotInDBException, PropertyTypeNotExistException, ComputationMethodDoesNotExistException {
        rs.next();
        Instance i = InstanceDAO.getById(rs.getInt("idInstance"));
        Property p = PropertyDAO.getById(rs.getInt("idProperty"));
        String value = rs.getString("value");
        return new InstanceHasProperty(i, p, value);
    }

    public static void assign(List<Instance> inst) throws SQLException {
        HashMap<Integer, Instance> instances = new HashMap<Integer, Instance>();
        List<Integer> instanceIds = new LinkedList<Integer>();
        
        for (Instance i : inst) {
            i.setPropertyValues(new HashMap<Integer, InstanceHasProperty>());
            instances.put(i.getId(), i);
            instanceIds.add(i.getId());
        }

        HashMap<Integer, Property> instanceProperties = new HashMap<Integer, Property>();
        try {
            for (Property p : PropertyDAO.getAllInstanceProperties()) {
                instanceProperties.put(p.getId(), p);
            }
        } catch (Exception e) {
            throw new SQLException(e.getMessage() + e.getClass());
        }
        
        

        PreparedStatement ps = DatabaseConnector.getInstance().getConn().prepareStatement(
                "SELECT ihp.id, ihp.idInstance, ihp.idProperty, ihp.value "
                + "FROM Instance_has_Property AS ihp "
                + "LEFT JOIN Instances AS i ON (ihp.idInstance = i.idInstance) "
                + "WHERE i.idInstance IN (" + edacc.experiment.Util.getIdArray(instanceIds) + ")");
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            int id = rs.getInt(1);
            int idInstance = rs.getInt(2);
            int idInstanceProperty = rs.getInt(3);
            String value = rs.getString(4);
            Instance instance = instances.get(idInstance);
            if (instance != null) {
                InstanceHasProperty ihip = cache.getCached(id);
                if (ihip == null) {
                    Property ip = instanceProperties.get(idInstanceProperty);
                    if (ip == null) {
                        continue;
                    }
                    ihip = new InstanceHasProperty(instance, ip, value);
                    cache.cache(ihip);
                    if (idCache.get(ihip.getInstance().getId()) == null) {
                        HashMap tmp = new HashMap();
                        tmp.put(ihip.getProperty().getId(), ihip.getId());
                        idCache.put(ihip.getInstance().getId(), tmp);
                    } else {
                        idCache.get(ihip.getInstance().getId()).put(ihip.getProperty().getId(), ihip.getId());
                    }
                }
                instance.getPropertyValues().put(idInstanceProperty, ihip);
            }
        }
        rs.close();
        ps.close();
    }

    public static void removeAllOfProperty(Property r) throws NoConnectionToDBException, SQLException, IOException, PropertyNotInDBException, PropertyTypeNotExistException, ComputationMethodDoesNotExistException {
        PreparedStatement ps = DatabaseConnector.getInstance().getConn().prepareStatement(
                "SELECT id FROM " + table + " WHERE idProperty=?;");
        ps.setInt(1, r.getId());
        ResultSet rs = ps.executeQuery();
        PreparedStatement psDelete = DatabaseConnector.getInstance().getConn().prepareStatement("DELETE FROM " + table + " WHERE idProperty=?");
        psDelete.setInt(1, r.getId());
        psDelete.executeUpdate();
        while (rs.next()) {
            removeInstanceHasInstancePropertyFromCache(rs.getInt(1));
        }
        rs.close();
        ps.close();
    }

    public static InstanceHasProperty getById(int id) throws NoConnectionToDBException, SQLException, IOException, PropertyNotInDBException, PropertyTypeNotExistException, ComputationMethodDoesNotExistException {
        InstanceHasProperty res = cache.getCached(id);
        if (res != null) {
            return res;
        }
        PreparedStatement ps = DatabaseConnector.getInstance().getConn().prepareStatement(
                "SELECT idInstance, idProperty, value FROM " + table + " WHERE id=?;");
        ps.setInt(1, id);
        ResultSet rs = ps.executeQuery();
        res = getInstanceHasInstancePropertyFromResultSet(rs);
        res.setSaved();
        cache.cache(res);
        idCache.get(res.getInstance().getId()).put(res.getProperty().getId(), res.getId());
        rs.close();
        ps.close();
        return res;
    }

    public static InstanceHasProperty getByInstanceAndProperty(Instance instance, Property property) throws NoConnectionToDBException, SQLException, IOException, PropertyNotInDBException, PropertyTypeNotExistException, ComputationMethodDoesNotExistException, InstanceHasPropertyNotInDBException {
        if (idCache.containsKey(instance.getId())) {
            if (idCache.get(instance.getId()).containsKey(property.getId())) {
                return getById(idCache.get(instance.getId()).get(property.getId()));
            }
        }
        throw new InstanceHasPropertyNotInDBException();

        /*PreparedStatement ps = DatabaseConnector.getInstance().getConn().prepareStatement(
        "SELECT id, value FROM " + table + " WHERE idInstance=? AND idProperty=?;");
        ps.setInt(1, instance.getId());
        ps.setInt(2, property.getId());
        ResultSet rs = ps.executeQuery();
        if (!rs.next()) {
        
        }
        InstanceHasProperty res = new InstanceHasProperty(instance, property, rs.getString(2));
        res.setId(rs.getInt(1));
        res.setSaved();
        cache.cache(res);
        idCache.get(res.getInstance().getId()).put(res.getProperty().getId(), res.getId());
        return res;*/
    }

    private static void removeInstanceHasInstancePropertyFromCache(int id) {
        cache.removeById(id);
    }

    public static void init() throws SQLException, NoConnectionToDBException, PropertyNotInDBException, PropertyTypeNotExistException, IOException, ComputationMethodDoesNotExistException {
        PreparedStatement ps = DatabaseConnector.getInstance().getConn().prepareStatement(
                "SELECT id, idInstance, idProperty, value FROM " + table);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            Instance in = InstanceDAO.getById(rs.getInt("idInstance"));
            Property p = PropertyDAO.getById(rs.getInt("idProperty"));
            String value = rs.getString("value");
            InstanceHasProperty i = new InstanceHasProperty();
            i.setValue(value);
            i.setInstance(in);
            i.setInstanceProperty(p);
            i.setId(rs.getInt("id"));
            i.setSaved();
            cache.cache(i);
            if (idCache.get(i.getInstance().getId()) == null) {
                HashMap<Integer, Integer> tmp = new HashMap<Integer, Integer>();
                tmp.put(i.getProperty().getId(), i.getId());
                idCache.put(i.getInstance().getId(), tmp);
            } else {
                idCache.get(i.getInstance().getId()).put(i.getProperty().getId(), i.getId());
            }

        }
        rs.close();
        ps.close();
    }

    public static void clearCache() {
        cache.clear();
        idCache.clear();
    }

    /**
     * Creates an InstanceHasPropety object with the given data.
     * @param i Instance 
     * @param prop Property
     * @param value Value of the InstanceHasProperty object
     * @param overwrite Overwrite existing value
     * @param psNew PreparedStatement to insert an new InstanceHasProperty object into the database.
     * @param psMod PreparedStatement to modify anInstanceHasProperty object in database.
     * @throws SQLException
     * @throws NoConnectionToDBException
     * @throws IOException
     * @throws PropertyTypeNotExistException
     * @throws ComputationMethodDoesNotExistException
     * @throws InstanceHasPropertyNotInDBException 
     */
    public static void createInstanceHasInstanceProperty(Instance i, Property prop, String value, Boolean overwrite, PreparedStatement psNew, PreparedStatement psMod) throws SQLException, NoConnectionToDBException, IOException, PropertyTypeNotExistException, ComputationMethodDoesNotExistException, InstanceHasPropertyNotInDBException {
        // Convert value, if an conversionErrorOccures, the value is set to null
        PropertyValueType type = prop.getPropertyValueType();
        try {
            Object check = type.getJavaTypeRepresentation(value);
        } catch (ConvertException ex) {
            //Logger.getLogger(InstanceHasPropertyDAO.class.getName()).log(Level.SEVERE, null, ex);
            value = null;
        }

        // Check if the InstanceHasProperty already exist, modify or create a new one. The result depends on
        // the Boolean value of overwrite.
        try {
            InstanceHasProperty alreadyExist = getByInstanceAndProperty(i, prop);
            if (overwrite) {

                alreadyExist.setValue(value);
                save(alreadyExist, psMod);
                alreadyExist.isSaved();
            }
        } catch (InstanceHasPropertyNotInDBException ex) {
            InstanceHasProperty ihp = new InstanceHasProperty(i, prop, value);
            save(ihp, psNew);
            ihp.isSaved();
        }

    }

    public static String getInsertQuery() {
        return insertQuery;
    }

    public static String getUpdateQuery() {
        return updateQuery;
    }

    /**
     * Persists an InstanceHasInstanceClass object in the DB.
     * This means: If it's new, it will be inserted into the db and if it's deleted, it will be removed from the db.
     * Extends the method save(InstanceHasProperty i) with a prepared Statement, to reduces getConn() requests from the DatabaseConnector to
     * improve the performance. 
     * @param i
     * @param st PreparedStatemant have to match with the persistantState of the given InstanceHasPropert-Object. Like PersistanteState = new, 
     * the PreparedStatement have to provide the InsertQuery.
     * @throws SQLException 
     */
    private static void save(InstanceHasProperty i, PreparedStatement st) throws SQLException {
        if (i.isDeleted()) {
            cache.remove(i);
            idCache.remove(i.getInstance().getId());
        } else if (i.isNew()) {
            cache.cache(i);
            i.setSaved();
            if (idCache.get(i.getInstance().getId()) == null) {
                HashMap tmp = new HashMap<Integer, Integer>();
                tmp.put(i.getProperty().getId(), i.getId());
                idCache.put(i.getInstance().getId(), tmp);
            } else {
                idCache.get(i.getInstance().getId()).put(i.getProperty().getId(), i.getId());
            }
            st.setString(3, i.getValue());
        } else if (i.isModified()) {
            st.setString(1, i.getValue());
            st.setInt(2, i.getInstance().getId());
            st.setInt(3, i.getProperty().getId());
            st.executeUpdate();
            i.setSaved();
            return;
        } else {
            st = null;
            return;
        }
        st.setInt(1, i.getInstance().getId());
        st.setInt(2, i.getProperty().getId());
        st.executeUpdate();

        // set id if necessary
        if (i.isSaved()) {
            ResultSet rs = st.getGeneratedKeys();
            if (rs.next()) {
                i.setId(rs.getInt(1));
            }
        }
    }
}
