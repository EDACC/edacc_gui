/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edacc.model;

import edacc.model.InstanceProperty;
import edacc.satinstances.ConvertException;
import edacc.satinstances.InstancePropertyManager;
import edacc.satinstances.InvalidVariableException;
import edacc.satinstances.PropertyValueType;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Vector;

/**
 *
 * @author dgall
 */
public class InstanceHasPropertyDAO {

    protected static final String table = "Instance_has_Property";
    protected static final String insertQuery = "INSERT INTO " + table + " (idInstance, idInstanceProperty, value) VALUES (?, ?, ?)";
    protected static final String deleteQuery = "DELETE FROM " + table + " WHERE idInstance=? AND idInstanceProperty=?";
    private static final ObjectCache<InstanceHasProperty> cache = new ObjectCache<InstanceHasProperty>();

    /**
     * Calculates the value of an InstanceProperty for an instance and saves it
     * in a new InstanceHasInstancePropertyObject which is automatically
     * persisted in the db.
     * @return new InstanceHasProperty object with the calculated value.
     */
    public static InstanceHasProperty createInstanceHasInstanceProperty(Instance instance, InstanceProperty instanceProperty) throws SQLException, ConvertException, IOException, InvalidVariableException, InstanceNotInDBException {
        PropertyValueType type = instanceProperty.getPropertyValueType();
        String value = type.getStringRepresentation(instanceProperty.computeProperty(InstanceDAO.getSATFormulaOfInstance(instance)));
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
    private static void save(InstanceHasProperty i) throws SQLException {
        PreparedStatement st;
        if (i.isDeleted()) {
            st = DatabaseConnector.getInstance().getConn().prepareStatement(deleteQuery);
            cache.remove(i);
        } else if (i.isNew()) {
            st = DatabaseConnector.getInstance().getConn().prepareStatement(insertQuery, PreparedStatement.RETURN_GENERATED_KEYS);
            cache.cache(i);
            i.setSaved();
        } else {
            st = null;
            return;
        }
        st.setInt(1, i.getInstance().getId());
        st.setString(2, i.getInstanceProperty().getName());
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

    private static Vector<InstanceHasProperty> getInstanceHasInstanceClassByInstancePropertyName(String propertyName) throws SQLException, InstanceClassMustBeSourceException, IOException {
        Vector<InstanceHasProperty> res = new Vector<InstanceHasProperty>();
        PreparedStatement st = DatabaseConnector.getInstance().getConn().prepareStatement("SELECT * FROM " + table + " WHERE idInstanceProperty=?");
        st.setString(1, propertyName);
        ResultSet rs = st.executeQuery();
        while (rs.next()) {
            InstanceHasProperty i = getInstanceHasInstancePropertyFromResultSet(rs);
            InstanceHasProperty c = cache.getCached(i.getId());
            if (c != null) {
                res.add(c);
            } else {
                i.setSaved();
                cache.cache(i);
                res.add(i);
            }
        }
        return res;
    }

    private static InstanceHasProperty getInstanceHasInstancePropertyFromResultSet(ResultSet rs) throws SQLException, IOException {
        Instance i = InstanceDAO.getById(rs.getInt("idInstance"));
        InstanceProperty p = InstancePropertyManager.getInstance().getByName(rs.getString("idInstanceProperty"));
        String value = rs.getString("value");
        return new InstanceHasProperty(i, p, value);
    }

    private static Vector<InstanceHasProperty> getInstanceHasInstancePropertyByInstanceId(int id) throws SQLException, InstanceClassMustBeSourceException, IOException {
        Vector<InstanceHasProperty> res = new Vector<InstanceHasProperty>();
        PreparedStatement st = DatabaseConnector.getInstance().getConn().prepareStatement("SELECT * FROM " + table + " WHERE idInstance=?");
        st.setInt(1, id);
        ResultSet rs = st.executeQuery();
        while (rs.next()) {
            InstanceHasProperty i = getInstanceHasInstancePropertyFromResultSet(rs);
            InstanceHasProperty c = cache.getCached(i.getId());
            if (c != null) {
                res.add(c);
            } else {
                i.setSaved();
                cache.cache(i);
                res.add(i);
            }
        }
        return res;
    }

    public static void assign(ArrayList<Instance> inst) throws SQLException {
        HashMap<Integer, Instance> instances = new HashMap<Integer, Instance>();
        for (Instance i : inst) {
            i.setPropertyValues(new HashMap<String, InstanceHasProperty>());
            instances.put(i.getId(), i);
        }

        HashMap<String, InstanceProperty> instanceProperties = new HashMap<String, InstanceProperty>();
        try {
            for (InstanceProperty ip : InstancePropertyManager.getInstance().getAll()) {
                instanceProperties.put(ip.getName(), ip);
            }
        } catch (Exception e) {
            throw new SQLException(e.getMessage());
        }

        PreparedStatement ps = DatabaseConnector.getInstance().getConn().prepareStatement(
                "SELECT ihip.id, ihip.idInstance, ihip.idInstanceProperty, ihip.value "
                + "FROM Instance_has_InstanceProperty AS ihip "
                + "LEFT JOIN Instances AS i ON (ihip.idInstance = i.idInstance)");
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            int id = rs.getInt(1);
            int idInstance = rs.getInt(2);
            String idInstanceProperty = rs.getString(3);
            String value = rs.getString(4);
            Instance instance = instances.get(idInstance);
            if (instance != null) {
                InstanceHasProperty ihip = cache.getCached(id);
                if (ihip == null) {
                    InstanceProperty ip = instanceProperties.get(idInstanceProperty);
                    if (ip == null) {
                        continue;
                    }
                    ihip = new InstanceHasProperty(instance, ip, value);
                    cache.cache(ihip);
                }
                instance.getPropertyValues().put(idInstanceProperty, ihip);
            }
        }
    }

    public static void removeAllOfProperty(Property r) throws NoConnectionToDBException, SQLException, IOException {
         PreparedStatement ps = DatabaseConnector.getInstance().getConn().prepareStatement(
                "SELECT id FROM " + table + "WHERE idProperty=?;");
         ps.setInt(1, r.getId());
         ResultSet rs = ps.executeQuery();
         while(rs.next()){
             removeInstanceHasInstanceProperty(getById(rs.getInt(1)));
         }
    }

    public static InstanceHasProperty getById(int id) throws NoConnectionToDBException, SQLException, IOException{
        PreparedStatement ps = DatabaseConnector.getInstance().getConn().prepareStatement(
                "SELECT idInstance, idProperty, value FROM " + table + "WHERE idProperty=?;");
        ps.setInt(1, id);
        return getInstanceHasInstancePropertyFromResultSet(ps.executeQuery());
    }
}
