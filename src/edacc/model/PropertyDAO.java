/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edacc.model;

import edacc.properties.PropertySource;
import edacc.properties.SolverPropertyTypeNotExistException;
import edacc.satinstances.PropertyValueType;
import edacc.satinstances.PropertyValueTypeManager;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;


/**
 * data access object of the Property class
 * @author rretz
 */
public class PropertyDAO {
    protected static final String table = "Property";
    private static final ObjectCache<Property> cache = new ObjectCache<Property>();
    private static String deleteQuery = "DELETE FROM " + table + " WHERE idProperty=?;";
    private static String updateQuery = "UPDATE " + table + " SET name=?, regExp=?, description=?, propertyType=?, propertySource=? ," +
            "propertyValueType=?, multiple=?, idComputationMethod=?, computationMethodParameters=?  WHERE idProperty=?;";
    private static String insertQuery = "INSERT INTO " + table + " (name, regExp, description, propertyType, propertySource ," +
            "propertyValueType, multiple, idComputationMethod, computationMethodParameters, isDefault) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";

    /**
     * Creates a new  Property object, saves it into the database and cache, and returns it.
     * @param name <String> of the Property object
     * @param prefix <String> prefix of the Property object
     * @param description <String> description of the Property object
     * @param valueType related PropertyValueType object
     * @return new Property which is also deposited in the database.
     * @throws NoConnectionToDBException
     * @throws SQLException
     */
    public static Property createProperty(String name, String regularExpression, String description, PropertyType type, PropertyValueType valueType,
            PropertySource source, boolean multiple, ComputationMethod computationMethod, String computationMethodParameters, String parameter, Boolean isDefault)
            throws NoConnectionToDBException, SQLException, PropertyIsUsedException, PropertyTypeDoesNotExistException, IOException,
            PropertyNotInDBException, SolverPropertyTypeNotExistException{
        Property r = new Property();
        r.setName(name);
        r.setDescription(description);
        r.setType(type);
        r.setValueType(valueType);
        r.setPropertySource(source);
        r.setMultiple(multiple);
        r.setIsDefault(isDefault);
        if(!source.equals(PropertySource.Parameter)){
            if(regularExpression.equals("")){
                r.setComputationMethod(computationMethod);
                r.setComputationMethodParameters(computationMethodParameters);
            }else
                r.setRegularExpression(regularExpression);
        }
        r.setNew();
        save(r);
        return r;
    }

 

    /**
     * Returns and caches (if necessary) the requested Property object
     * @param id of the requested Property object
     * @return the requested Property object
     * @throws NoConnectionToDBException
     * @throws SQLException
     * @throws PropertyNotInDBException
     */
    public static Property getById(int id) throws NoConnectionToDBException, SQLException, PropertyNotInDBException, SolverPropertyTypeNotExistException, IOException, ComputationMethodDoesNotExistException {
        Property res = cache.getCached(id);
        if(res != null){
            return res;
        }else{
            res = new Property();
            PreparedStatement ps = DatabaseConnector.getInstance().getConn().prepareStatement(
                    "SELECT name, description, regExp, propertyType, propertySource ,propertyValueType, multiple, idComputationMethod, " +
                    "computationMethodParameters FROM " + table + " WHERE idProperty=?");
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if(!rs.next())
                throw new PropertyNotInDBException();
            res.setId(id);
            res.setName(rs.getString(1));
            res.setDescription(rs.getString(2));            
            res.setType(rs.getInt(4));
            res.setPropertySource(rs.getInt(5));
            if(!res.getPropertySource().equals(PropertySource.Parameter)){
                res.setRegularExpression(rs.getString(3));
                res.setValueType(PropertyValueTypeManager.getInstance().getPropertyValueTypeByName(rs.getString(6)));
                res.setMultiple(rs.getBoolean(7));
                res.setComputationMethod(ComputationMethodDAO.getById(rs.getInt(8)));
                res.setComputationMethodParameters(rs.getString(9));
            }else{
                res.setRegularExpression("");
                res.setValueType(null);
                res.setMultiple(false);
                res.setComputationMethod(null);
                res.setComputationMethodParameters("");
            }
            res.setSaved();
            cache.cache(res);
            return res;
        }
    }

    /**
     * Saves the given Property into the database. Dependend on the PersistanteState of
     * the given object a new entry is created, deleted or updated in the database.
     * @param r the Property object to save into the database
     * @throws NoConnectionToDBException
     * @throws SQLException
     */
    public static void save(Property r) throws NoConnectionToDBException, SQLException, PropertyIsUsedException, PropertyTypeDoesNotExistException, 
            IOException, PropertyNotInDBException, SolverPropertyTypeNotExistException {
        if(r.isDeleted()){
            if(r.getType().equals(PropertyType.InstanceProperty))
                InstanceHasPropertyDAO.removeAllOfProperty(r);
            else{
                ExperimentResultHasPropertyDAO.removeAllOfProperty(r);
            }
            PreparedStatement ps = DatabaseConnector.getInstance().getConn().prepareStatement(deleteQuery);
            ps.setInt(1, r.getId());
            ps.executeUpdate();
            ps.close();
            cache.remove(r);
        }else if( r.isModified()){            
            PreparedStatement ps = DatabaseConnector.getInstance().getConn().prepareStatement(updateQuery);
            ps.setString(1, r.getName());
            ps.setString(2, r.getRegularExpression());
            ps.setString(3, r.getDescription());
            ps.setInt(4, r.getPropertyTypeDBRepresentation());
            ps.setInt(5, r.getPropertySourceDBRepresentation());
            if(r.getPropertySource().equals(PropertySource.Parameter)){
               ps.setNull(6, java.sql.Types.NULL);
               ps.setNull(7, java.sql.Types.NULL);
            }else {
                ps.setString(6, r.getPropertyValueType().getName());
                ps.setBoolean(7, r.isMultiple());
            }
            ps.setInt(8, r.getComputationMethod().getId());
            ps.setString(9, r.getComputationMethodParameters());
            ps.setInt(10, r.getId());
            ps.executeUpdate();
            ps.close();
            r.setSaved();
        }else if(r.isNew()){
            // TODO: an die neue DB anpassen
            PreparedStatement ps = DatabaseConnector.getInstance().getConn().prepareStatement(insertQuery, PreparedStatement.RETURN_GENERATED_KEYS);
            ps.setString(1, r.getName());
            ps.setString(2, r.getRegularExpression());
            ps.setString(3, r.getDescription());
            ps.setInt(4, r.getPropertyTypeDBRepresentation());
            ps.setInt(5, r.getPropertySourceDBRepresentation());
            if(r.getPropertySource().equals(PropertySource.Parameter)){
               ps.setNull(6, java.sql.Types.NULL);
               ps.setNull(7, java.sql.Types.NULL);
            }else {
                ps.setString(5, r.getPropertyValueType().getName());
                ps.setBoolean(6, r.isMultiple());
            }
            ps.setInt(8, r.getComputationMethod().getId());
            ps.setString(9, r.getComputationMethodParameters());
            ps.setBoolean(10, r.IsDefault());
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
     * @return a Vector of all Property Objects which are in the database.
     * @throws NoConnectionToDBException
     * @throws SQLException
     * @throws PropertyNotInDBException
     * @throws SolverPropertyTypeNotExistException
     * @throws IOException
     */
    public static Vector<Property> getAll() throws NoConnectionToDBException, SQLException, PropertyNotInDBException, SolverPropertyTypeNotExistException, IOException, ComputationMethodDoesNotExistException{
        Vector<Property> res = new Vector<Property>();
        PreparedStatement ps = DatabaseConnector.getInstance().getConn().prepareStatement(
            "SELECT idSolverProperty "
            + "FROM " + table);
        ResultSet rs = ps.executeQuery();
        while(rs.next()){
            res.add(PropertyDAO.getById(rs.getInt(1)));
        }
        return res;
    }

    /**
     * Removes the given property from the cache and database.
     * @param solverProperty to remove
     * @throws NoConnectionToDBException
     * @throws SQLException
     * @throws PropertyIsUsedException
     * @throws PropertyTypeDoesNotExistException
     */
    public static void remove(Property toRemove) throws NoConnectionToDBException, SQLException, PropertyIsUsedException, PropertyTypeDoesNotExistException, 
            IOException, PropertyNotInDBException, SolverPropertyTypeNotExistException {
        toRemove.setDeleted();
        save(toRemove);
    }

    /**
     * 
     * @return all Property objects with the PropertyType InstanceProperty
     * @throws NoConnectionToDBException
     * @throws SQLException
     * @throws PropertyNotInDBException
     * @throws SolverPropertyTypeNotExistException
     * @throws IOException
     */
    public static Vector<Property> getAllInstanceProperties() throws NoConnectionToDBException, SQLException, PropertyNotInDBException,
            SolverPropertyTypeNotExistException, IOException, ComputationMethodDoesNotExistException{
        Vector<Property> res = new Vector<Property>();
        PreparedStatement ps = DatabaseConnector.getInstance().getConn().prepareStatement(
            "SELECT idProperty FROM " + table + " WHERE PropertyType=0;");
        ResultSet rs = ps.executeQuery();
        while(rs.next()){
            res.add(PropertyDAO.getById(rs.getInt("idProperty")));
        }
        return res;
    }

    /**
     *
     * @return all Property objects with the PropertyType ResultProperty
     * @throws NoConnectionToDBException
     * @throws SQLException
     * @throws PropertyNotInDBException
     * @throws SolverPropertyTypeNotExistException
     * @throws SolverPropertyTypeNotExistException
     * @throws IOException
     */
    public static Vector<Property> getAllResultProperties() throws NoConnectionToDBException, SQLException, PropertyNotInDBException,
            SolverPropertyTypeNotExistException, SolverPropertyTypeNotExistException, IOException, ComputationMethodDoesNotExistException{
        Vector<Property> res = new Vector<Property>();
        PreparedStatement ps = DatabaseConnector.getInstance().getConn().prepareStatement(
            "SELECT idProperty FROM " + table + " WHERE PropertyType=1;");
        ResultSet rs = ps.executeQuery();
        while(rs.next()){
            res.add(PropertyDAO.getById(rs.getInt("idProperty")));
        }
        return res;
    }

    /**
     *
     * @param name of the requested Property
     * @return The property with the given name from the database
     * @throws NoConnectionToDBException
     * @throws SQLException
     * @throws PropertyNotInDBException
     * @throws PropertyNotInDBException
     * @throws SolverPropertyTypeNotExistException
     * @throws IOException
     */
    public static Property getByName(String name) throws NoConnectionToDBException, SQLException, PropertyNotInDBException,
            PropertyNotInDBException, SolverPropertyTypeNotExistException, IOException, ComputationMethodDoesNotExistException {
         PreparedStatement ps = DatabaseConnector.getInstance().getConn().prepareStatement(
            "SELECT idSolverProperty FROM " + table + " WHERE name=?;");
         ps.setString(1, name);
         ResultSet rs = ps.executeQuery();
         rs.next();
         return getById(rs.getInt("idSolverProperty"));
    }



}
