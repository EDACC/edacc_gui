/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edacc.model;

import edacc.properties.SolverPropertyType;
import edacc.properties.SolverPropertyTypeNotExistException;
import edacc.satinstances.PropertyValueType;
import edacc.satinstances.PropertyValueTypeManager;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;


/**
 * data access object of the SolverProperty class
 * @author rretz
 */
public class SolverPropertyDAO {
    protected static final String table = "SolverProperty";
    private static final ObjectCache<SolverProperty> cache = new ObjectCache<SolverProperty>();
    private static String deleteQuery = "DELETE FROM " + table + " WHERE idSolverProperty=?;";
    private static String updateQuery = "UPDATE " + table + " SET name=?, prefix=?, description=?, PropertyValueType_name=?, propertyType=?, multiple=? WHERE idSolverProperty=?;";
    private static String insertQuery = "INSERT INTO " + table + " (name, prefix, description, PropertyValueType_name, propertyType, multiple) VALUES (?, ?, ?, ?, ?, ?);";

    /**
     * Creates a new  SolverProperty object, saves it into the database and cache, and returns it.
     * @param name <String> of the SolverProperty object
     * @param prefix <String> prefix of the SolverProperty object
     * @param description <String> description of the SolverProperty object
     * @param valueType related PropertyValueType object
     * @return new SolverProperty which is also deposited in the database.
     * @throws NoConnectionToDBException
     * @throws SQLException
     */
    public static SolverProperty createResultProperty(String name, String prefix, String description, PropertyValueType valueType, SolverPropertyType type, boolean multiple) throws NoConnectionToDBException, SQLException{
        SolverProperty r = new SolverProperty();
        r.setName(name);
        r.setPrefix(prefix);
        r.setDescription(description);
        r.setValueType(valueType);
        r.setSolverPropertyType(type);
        r.setMultiple(multiple);
        r.setNew();
        save(r);
        return r;
    }

    /**
     * Returns and caches (if necessary) the requested SolverProperty object
     * @param id of the requested SolverProperty object
     * @return the requested SolverProperty object
     * @throws NoConnectionToDBException
     * @throws SQLException
     * @throws SolverPropertyNotInDBException
     */
    public static SolverProperty getById(int id) throws NoConnectionToDBException, SQLException, SolverPropertyNotInDBException, SolverPropertyTypeNotExistException, IOException {
        SolverProperty res = cache.getCached(id);
        if(res != null){
            return res;
        }else{
            res = new SolverProperty();
            PreparedStatement ps = DatabaseConnector.getInstance().getConn().prepareStatement(
                    "SELECT PropertyValueType_name, name, description, prefix, propertyType, multiple "
                    + "FROM " + table + " WHERE idSolverProperty=?");
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if(!rs.next())
                throw new SolverPropertyNotInDBException();
            res.setId(id);
            res.setValueType(PropertyValueTypeManager.getInstance().getPropertyValueTypeByName(rs.getString(1)));
            res.setName(rs.getString(2));
            res.setDescription(rs.getString(3));
            res.setPrefix(rs.getString(4));
            res.setSolverPropertyType(rs.getInt(5));
            res.setMultiple(rs.getBoolean(6));
            res.setSaved();
            cache.cache(res);
            return res;
        }
    }

    /**
     * Saves the given SolverProperty into the database. Dependend on the PersistanteState of
     * the given object a new entry is created, deleted or updated in the database.
     * @param r the SolverProperty object to save into the database
     * @throws NoConnectionToDBException
     * @throws SQLException
     */
    private static void save(SolverProperty r) throws NoConnectionToDBException, SQLException {
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
            ps.setInt(5, r.getSolverPropertyTypeDBRepresentation());
            ps.setBoolean(6, r.isMultiple());
            ps.setInt(7, r.getId());
            ps.executeUpdate();
            ps.close();
            r.setSaved();
        }else if(r.isNew()){
            PreparedStatement ps = DatabaseConnector.getInstance().getConn().prepareStatement(insertQuery);
            ps.setString(1, r.getName());
            ps.setString(2, r.getPrefix());
            ps.setString(3, r.getDescription());
            ps.setString(4, r.getPropertyValueType().getName());
            ps.setInt(5, r.getSolverPropertyTypeDBRepresentation());
            ps.setBoolean(6, r.isMultiple());
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

    /**
     *
     * @return a Vector of all SolverProperty Objects which are in the database.
     * @throws NoConnectionToDBException
     * @throws SQLException
     * @throws SolverPropertyNotInDBException
     * @throws SolverPropertyTypeNotExistException
     * @throws IOException
     */
    public static Vector<SolverProperty> getAll() throws NoConnectionToDBException, SQLException, SolverPropertyNotInDBException, SolverPropertyTypeNotExistException, IOException{
        Vector<SolverProperty> res = new Vector<SolverProperty>();
        PreparedStatement ps = DatabaseConnector.getInstance().getConn().prepareStatement(
            "SELECT idSolverProperty "
            + "FROM " + table);
        ResultSet rs = ps.executeQuery();
        while(rs.next()){
            res.add(SolverPropertyDAO.getById(rs.getInt(1)));
        }
        return res;
    }

}
