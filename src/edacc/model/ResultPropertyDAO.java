/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edacc.model;

import edacc.satinstances.PropertyValueTypeManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


/**
 * data access object of the ResultProperty class
 * @author rretz
 */
public class ResultPropertyDAO {
    protected static final String table = "ResultProperty";
    private static final ObjectCache<ResultProperty> cache = new ObjectCache<ResultProperty>();
    private static String deleteQuery = "DELETE FROM " + table + " WHERE idResultProperty=?;";
    private static String updateQuery = "UPDATE " + table + " SET name=?, prefix=?, description=?, PropertyValueType_name=? WHERE idResultProperty=?;";
    private static String insertQuery = "INSERT INTO " + table + " (name, prefix, description, PropertyValueType_name) VALUES (?, ?, ?, ?);";

    /**
     * Creates a new  ResultProperty object, saves it into the database and cache, and returns it.
     * @param name <String> of the ResultProperty object
     * @param prefix <String> prefix of the ResultProperty object
     * @param description <String> description of the ResultProperty object
     * @param valueType related PropertyValueType object
     * @return new ResultProperty which is also deposited in the database.
     * @throws NoConnectionToDBException
     * @throws SQLException
     */
    public static ResultProperty createResultProperty(String name, String prefix, String description, PropertyValueType valueType) throws NoConnectionToDBException, SQLException{
        ResultProperty r = new ResultProperty();
        r.setName(name);
        r.setPrefix(prefix);
        r.setDescription(description);
        r.setValueType(valueType);
        r.setNew();
        save(r);
        return r;
    }

    /**
     * Returns and caches (if necessary) the requested ResultProperty object
     * @param id of the requested ResultProperty object
     * @return the requested ResultProperty object 
     * @throws NoConnectionToDBException
     * @throws SQLException
     * @throws ResultPropertyNotInDBException
     */
    public static ResultProperty getById(int id) throws NoConnectionToDBException, SQLException, ResultPropertyNotInDBException {
        ResultProperty res = cache.getCached(id);
        if(res != null){
            return res;
        }else{
            PreparedStatement ps = DatabaseConnector.getInstance().getConn().prepareStatement(
                    "SELECT PropertyValueType_name, name, description, prefix "
                    + "FROM " + table + " WHERE idResultProperty=?");
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if(!rs.next())
                throw new ResultPropertyNotInDBException();
            res.setId(id);
            res.setValueType(PropertyValueTypeManager.getInstance().getPropertyValueTypeByName(rs.getString(1)));
            res.setName(rs.getString(2));
            res.setDescription(rs.getString(3));
            res.setPrefix(rs.getString(4));
            res.setSaved();
            cache.cache(res);
            return res;
        }
    }

    /**
     * Saves the given ResultProperty into the database. Dependend on the PersistanteState of
     * the given object a new entry is created, deleted or updated in the database.
     * @param r the ResultProperty object to save into the database
     * @throws NoConnectionToDBException
     * @throws SQLException
     */
    private static void save(ResultProperty r) throws NoConnectionToDBException, SQLException {
        if(r.isDeleted()){
            PreparedStatement ps = DatabaseConnector.getInstance().getConn().prepareStatement(deleteQuery);
            ps.setInt(1, r.getId());
            ps.executeUpdate();
            ps.close();
            cache.remove(r);
        }else if( r.isModified()){
            PreparedStatement ps = DatabaseConnector.getInstance().getConn().prepareStatement(updateQuery);
            ps.setString(1, r.getName());
            ps.setString(2, r.getPrefix());
            ps.setString(3, r.getDescription());
            ps.setString(4, r.getPropertyValueType().getName());
            ps.setInt(5, r.getId());
            ps.executeUpdate();
            ps.close();
            r.setSaved();
        }else if(r.isNew()){
            PreparedStatement ps = DatabaseConnector.getInstance().getConn().prepareStatement(insertQuery);
            ps.setString(1, r.getName());
            ps.setString(2, r.getPrefix());
            ps.setString(3, r.getDescription());
            ps.setString(4, r.getPropertyValueType().getName());
            ps.executeUpdate();
            ResultSet generatedKeys = ps.getGeneratedKeys();
            if (generatedKeys.next()) {
                r.setId(generatedKeys.getInt(1));
            }
            generatedKeys.close();
            ps.close();
            r.setSaved();
            cache.cache(r);
        }
    }

}
