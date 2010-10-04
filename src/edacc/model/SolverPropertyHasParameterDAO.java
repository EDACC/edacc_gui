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
public class SolverPropertyHasParameterDAO {
    private static final String table = "SolverProperty_has_Parameter";
    private static final ObjectCache<SolverPropertyHasParameter> cache = new ObjectCache<SolverPropertyHasParameter>();
    private static String deleteQuery = "DELETE FROM " + table + " WHERE idSP=?;";
    private static String updateQuery = "UPDATE " + table + " SET SolverProperty_idSolverProperty=?, Parameter=? WHERE idSP=?;";
    private static String insertQuery = "INSERT INTO " + table + " (SolverProperty_idSolverProperty, Parameter) VALUES (?, ?);";

    /**
     * Creates a new  SolverPropertyHasParameter object, saves it into the database and cache, and returns it.
     * @param solvProperty SolverProperty which is related to the object.
     * @param parameter Parameter object which is related to the object.
     * @return the created SolverPropertyHasParameter object.
     * @throws NoConnectionToDBException
     * @throws SQLException
     */
    public static SolverPropertyHasParameter createSolverPropertyHasParamter(SolverProperty solvProperty, String parameter) throws NoConnectionToDBException, SQLException{
        SolverPropertyHasParameter s = new SolverPropertyHasParameter();
        s.setSolvProperty(solvProperty);
        s.setParameter(parameter);
        s.setNew();
        save(s);
        return s;
    }

    /**
     * Saves the given SolverPropertyHasParameter into the database. Dependend on the PersistanteState of
     * the given object a new entry is created, deleted or updated in the database.
     * @param s the SolverPropertyHasParameter object to save.
     * @throws NoConnectionToDBException
     * @throws SQLException
     */
    public static void save(SolverPropertyHasParameter s) throws NoConnectionToDBException, SQLException {
        if(s.isDeleted()){
            PreparedStatement ps = DatabaseConnector.getInstance().getConn().prepareStatement(deleteQuery);
            ps.setInt(1, s.getId());
            ps.executeUpdate();
            ps.close();
            cache.remove(s);
        }else if(s.isModified()){
            PreparedStatement ps = DatabaseConnector.getInstance().getConn().prepareStatement(updateQuery);            
            ps.setInt(1, s.getSolvProperty().getId());
            ps.setString(2, s.getParameter());
            ps.setInt(3, s.getId());
            ps.executeUpdate();
            ps.close();
            s.setSaved();
        }else if(s.isNew()){
            PreparedStatement ps = DatabaseConnector.getInstance().getConn().prepareStatement(insertQuery, PreparedStatement.RETURN_GENERATED_KEYS);
            ps.setInt(1, s.getSolvProperty().getId());
            ps.setString(2, s.getParameter());
            ps.executeUpdate();
            ResultSet generatedKeys = ps.getGeneratedKeys();
            if (generatedKeys.next()) {
                s.setId(generatedKeys.getInt(1));
            }
            generatedKeys.close();
            ps.close();
            s.setSaved();
            cache.cache(s);
        }
    }

    /**
     * Returns and caches the SolverPropertyHasParameter object which is related to the given SolverProperty object.
     * @param solvProperty
     * @return SolverPropertyHasParameter object which is related to the given SolverProperty object.
     * @throws NoConnectionToDBException
     * @throws SQLException
     * @throws SolverPropertyHasParameterNotInDBException
     * @throws SolverPropertyNotInDBException
     * @throws SolverPropertyTypeNotExistException
     * @throws IOException
     */
    public static SolverPropertyHasParameter getBySolverProperty(SolverProperty solvProperty) throws NoConnectionToDBException, SQLException, SolverPropertyHasParameterNotInDBException,
            SolverPropertyNotInDBException, SolverPropertyTypeNotExistException, IOException{
        return getBySolverProperty(solvProperty.getId());

    }

    /**
     * Returns and caches the SolverPropertyHasParameter object which is related to the given SolverProperty id.
     * @param id of the SolverProperty
     * @return SolverPropertyHasParameter object which is related to the given SolverProperty id.
     * @throws NoConnectionToDBException
     * @throws SQLException
     * @throws SolverPropertyHasParameterNotInDBException
     * @throws SolverPropertyNotInDBException
     * @throws SolverPropertyTypeNotExistException
     * @throws IOException
     */
    public static SolverPropertyHasParameter getBySolverProperty(int id) throws NoConnectionToDBException, SQLException, SolverPropertyHasParameterNotInDBException,
            SolverPropertyNotInDBException, SolverPropertyTypeNotExistException, IOException{

        PreparedStatement ps = DatabaseConnector.getInstance().getConn().prepareStatement(
            "SELECT idSP FROM " + table + " WHERE SolverProperty_idSolverProperty=?");
        ps.setInt(1, id);
        ResultSet rs = ps.executeQuery();
        if(!rs.next())
            throw new SolverPropertyHasParameterNotInDBException();
        return getById(rs.getInt("idSP"));


    }

    /**
     * Returns and caches (if necessary) the requested SolverPropertyHasParameter object
     * @param id the id of the requsted SolverPropertyHasParameter object.
     * @return requested SolverPropertyHasParameter object
     * @throws NoConnectionToDBException
     * @throws SQLException
     * @throws SolverPropertyHasParameterNotInDBException
     * @throws SolverPropertyNotInDBException
     * @throws SolverPropertyTypeNotExistException
     * @throws IOException
     */
    public static SolverPropertyHasParameter getById(int id) throws NoConnectionToDBException, SQLException, SolverPropertyHasParameterNotInDBException,
            SolverPropertyNotInDBException, SolverPropertyTypeNotExistException, IOException {
        SolverPropertyHasParameter res = cache.getCached(id);
        if(res != null){
            return res;
        }else{
            res = new SolverPropertyHasParameter();
            PreparedStatement ps = DatabaseConnector.getInstance().getConn().prepareStatement(
                    "SELECT SolverProperty_idSolverProperty, Parameter "
                    + "FROM " + table + " WHERE idSP=?");
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if(!rs.next())
                throw new SolverPropertyHasParameterNotInDBException();
            res.setId(id);
            res.setSolvProperty(SolverPropertyDAO.getById(rs.getInt("SolverProperty_idSolverProperty")));
            res.setParameter(rs.getString("Parameter"));
            res.setSaved();
            cache.cache(res);
            return res;
        }
    }

}
