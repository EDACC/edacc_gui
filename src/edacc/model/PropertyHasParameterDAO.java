/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edacc.model;

import edacc.properties.SolverPropertyTypeNotExistException;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author rretz
 */
public class PropertyHasParameterDAO {
    private static final String table = "Property_has_Parameter";
    private static final ObjectCache<PropertyHasParameter> cache = new ObjectCache<PropertyHasParameter>();
    private static String deleteQuery = "DELETE FROM " + table + " WHERE idProperty=?;";
    private static String updateQuery = "UPDATE " + table + " SET  parameterName=? WHERE idProperty=?;";
    private static String insertQuery = "INSERT INTO " + table + " (idProperty, parameterName) VALUES (?, ?);";

    /**
     * Creates a new  PropertyHasParameter object, saves it into the database and cache, and returns it.
     * @param solvProperty Property which is related to the object.
     * @param parameter Parameter object which is related to the object.
     * @return the created PropertyHasParameter object.
     * @throws NoConnectionToDBException
     * @throws SQLException
     */
    public static PropertyHasParameter createPropertyHasParamter(Property property, String parameter) throws NoConnectionToDBException,
            SQLException{
        PropertyHasParameter s = new PropertyHasParameter();
        s.setId(property.getId());
        s.setProperty(property);
        s.setParameter(parameter);
        s.setNew();
        save(s);
        return s;
    }

    /**
     * Saves the given PropertyHasParameter into the database. Dependend on the PersistanteState of
     * the given object a new entry is created, deleted or updated in the database.
     * @param s the PropertyHasParameter object to save.
     * @throws NoConnectionToDBException
     * @throws SQLException
     */
    public static void save(PropertyHasParameter s) throws NoConnectionToDBException, SQLException {
        if(s.isDeleted()){
            PreparedStatement ps = DatabaseConnector.getInstance().getConn().prepareStatement(deleteQuery);
            ps.setInt(1, s.getId());
            ps.executeUpdate();
            ps.close();
            cache.remove(s);
        }else if(s.isModified()){
            PreparedStatement ps = DatabaseConnector.getInstance().getConn().prepareStatement(updateQuery);            
            ps.setString(1, s.getParameter());
            ps.setInt(2, s.getId());
            ps.executeUpdate();
            ps.close();
            s.setSaved();
        }else if(s.isNew()){
            PreparedStatement ps = DatabaseConnector.getInstance().getConn().prepareStatement(insertQuery);
            ps.setInt(1, s.getId());
            ps.setString(2, s.getParameter());
            ps.executeUpdate();          
            ps.close();
            s.setSaved();
            cache.cache(s);
        }
    }

    /**
     * Returns and caches the PropertyHasParameter object which is related to the given Property object.
     * @param solvProperty
     * @return PropertyHasParameter object which is related to the given Property object.
     * @throws NoConnectionToDBException
     * @throws SQLException
     * @throws PropertyHasParameterNotInDBException
     * @throws PropertyNotInDBException
     * @throws SolverPropertyTypeNotExistException
     * @throws IOException
     */
    public static PropertyHasParameter getByProperty(Property solvProperty) throws NoConnectionToDBException, SQLException, PropertyHasParameterNotInDBException,
            PropertyNotInDBException, SolverPropertyTypeNotExistException, IOException{
        return getByProperty(solvProperty.getId());

    }

    /**
     * Returns and caches the PropertyHasParameter object which is related to the given Property id.
     * @param id of the Property
     * @return PropertyHasParameter object which is related to the given Property id.
     * @throws NoConnectionToDBException
     * @throws SQLException
     * @throws PropertyHasParameterNotInDBException
     * @throws PropertyNotInDBException
     * @throws SolverPropertyTypeNotExistException
     * @throws IOException
     */
    public static PropertyHasParameter getByProperty(int id) throws NoConnectionToDBException, SQLException, PropertyHasParameterNotInDBException,
            PropertyNotInDBException, SolverPropertyTypeNotExistException, IOException{
        return getById(id);
    }

    /**
     * Returns and caches (if necessary) the requested PropertyHasParameter object
     * @param id the id of the requsted PropertyHasParameter object.
     * @return requested PropertyHasParameter object
     * @throws NoConnectionToDBException
     * @throws SQLException
     * @throws PropertyHasParameterNotInDBException
     * @throws PropertyNotInDBException
     * @throws SolverPropertyTypeNotExistException
     * @throws IOException
     */
    public static PropertyHasParameter getById(int id) throws NoConnectionToDBException, SQLException, PropertyHasParameterNotInDBException,
            PropertyNotInDBException, SolverPropertyTypeNotExistException, IOException {
        PropertyHasParameter res = cache.getCached(id);
        if(res != null){
            return res;
        }else{
            res = new PropertyHasParameter();
            PreparedStatement ps = DatabaseConnector.getInstance().getConn().prepareStatement(
                    "SELECT parameter FROM " + table + " WHERE idProperty=?");
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if(!rs.next())
                throw new PropertyHasParameterNotInDBException();
            res.setId(id);
            res.setProperty(PropertyDAO.getById(id));
            res.setParameter(rs.getString("parameter"));
            res.setSaved();
            cache.cache(res);
            return res;
        }
    }

    public static void removeAllOfProperty(Property r) throws NoConnectionToDBException, SQLException, PropertyHasParameterNotInDBException,
            PropertyNotInDBException, SolverPropertyTypeNotExistException, IOException {
        remove(r.getId());
    }

    public static void remove(int id) throws NoConnectionToDBException, SQLException, PropertyHasParameterNotInDBException,
            PropertyNotInDBException, SolverPropertyTypeNotExistException, IOException {
        PropertyHasParameter toRemove = getById(id);
    }

    public static void remove(PropertyHasParameter r) throws NoConnectionToDBException, SQLException{
        r.setDeleted();
        save(r);
    }


}
