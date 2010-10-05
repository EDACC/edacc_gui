/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edacc.model;

import edacc.satinstances.ConvertException;
import edacc.satinstances.InstancePropertyManager;
import edacc.satinstances.InvalidVariableException;
import edacc.satinstances.PropertyValueType;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Hashtable;
import java.util.Vector;

/**
 *
 * @author dgall
 */
public class InstanceHasInstancePropertyDAO {

    protected static final String table = "Instance_has_InstanceProperty";
    protected static final String insertQuery = "INSERT INTO " + table + " (idInstance, idInstanceProperty, value) VALUES (?, ?, ?)";
    protected static final String deleteQuery = "DELETE FROM " + table + " WHERE idInstance=? AND idInstanceProperty=?";
    private static final ObjectCache<InstanceHasInstanceProperty> cache = new ObjectCache<InstanceHasInstanceProperty>();

    /**
     * Calculates the value of an InstanceProperty for an instance and saves it
     * in a new InstanceHasInstancePropertyObject which is automatically
     * persisted in the db.
     * @return new InstanceHasInstanceProperty object with the calculated value.
     */
    public static InstanceHasInstanceProperty createInstanceHasInstanceProperty(Instance instance, InstanceProperty instanceProperty) throws SQLException, ConvertException, IOException, InvalidVariableException, InstanceNotInDBException {
        PropertyValueType type = instanceProperty.getPropertyValueType();
        String value = type.getStringRepresentation(instanceProperty.computeProperty(InstanceDAO.getSATFormulaOfInstance(instance)));
        InstanceHasInstanceProperty i = new InstanceHasInstanceProperty(instance, instanceProperty, value);
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
    private static void save(InstanceHasInstanceProperty i) throws SQLException {
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

    }

    /**
     * Removes an InstanceHasInstanceProperty object from the DB and the cache.
     * @param i
     * @throws SQLException
     */
    public static void removeInstanceHasInstanceProperty(InstanceHasInstanceProperty i) throws SQLException {
        i.setDeleted();
        save(i);
    }

    private static Vector<InstanceHasInstanceProperty> getInstanceHasInstanceClassByInstancePropertyName(String propertyName) throws SQLException, InstanceClassMustBeSourceException, IOException {
        Vector<InstanceHasInstanceProperty> res = new Vector<InstanceHasInstanceProperty>();
        PreparedStatement st = DatabaseConnector.getInstance().getConn().prepareStatement("SELECT * FROM " + table + " WHERE idInstanceProperty=?");
        st.setString(1, propertyName);
        ResultSet rs = st.executeQuery();
        while (rs.next()) {
            InstanceHasInstanceProperty i = getInstanceHasInstancePropertyFromResultSet(rs);
            InstanceHasInstanceProperty c = cache.getCached(i.getId());
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

    private static InstanceHasInstanceProperty getInstanceHasInstancePropertyFromResultSet(ResultSet rs) throws SQLException, IOException {
        Instance i = InstanceDAO.getById(rs.getInt("idInstance"));
        InstanceProperty p = InstancePropertyManager.getInstance().getByName(rs.getString("idInstanceProperty"));
        String value = rs.getString("value");
        return new InstanceHasInstanceProperty(i, p, value);
    }

     private static Vector<InstanceHasInstanceProperty> getInstanceHasInstancePropertyByInstanceId(int id) throws SQLException, InstanceClassMustBeSourceException, IOException {
        Vector<InstanceHasInstanceProperty> res = new Vector<InstanceHasInstanceProperty>();
        PreparedStatement st = DatabaseConnector.getInstance().getConn().prepareStatement("SELECT * FROM " + table + " WHERE idInstance=?");
        st.setInt(1, id);
        ResultSet rs = st.executeQuery();
        while (rs.next()) {
            InstanceHasInstanceProperty i = getInstanceHasInstancePropertyFromResultSet(rs);
            InstanceHasInstanceProperty c = cache.getCached(i.getId());
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
}
