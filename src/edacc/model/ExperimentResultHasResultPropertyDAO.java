/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edacc.model;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;


/**
 * Implements the data access object of the ExperimentResultHasResultProperty class
 * @author rretz
 */
public class ExperimentResultHasResultPropertyDAO {

    protected static final String table = "ExperimentResult_has_ResultProperty";
    private static final ObjectCache<ExperimentResultHasResultProperty> cache = new ObjectCache<ExperimentResultHasResultProperty>();
    private static final String deleteQuery = "DELETE FROM " + table + " WHERE idER_h_RP=?";
    private static String updateQuery = "UPDATE " + table + " SET ExperimentResults_idJob=?, ResultProperty_idResultProperty=?, value=? WHERE idER_h_RP=?";
    private static String insertQuery = "INSERT INTO " + table + " (ExperimentResults_idJob, ResultProperty_idResultProperty, value) VALUES (?, ?, ?)";;

    /**
     * Creates a new  ExperimentResultHasResultProperty object, saves it into the database and cache, and returns it.
     * @param expResult related ExperimentResult object
     * @param resProperty related ResultProperty object
     * @return new ExperimentResultHasResultProperty which is also deposited in the database.
     * @throws NoConnectionToDBException
     * @throws SQLException
     */
    public static ExperimentResultHasResultProperty createExperimentResultHasResultPropertyDAO(ExperimentResult expResult, ResultProperty resProperty)
            throws NoConnectionToDBException, SQLException{
        ExperimentResultHasResultProperty e = new ExperimentResultHasResultProperty();
        e.setExpResult(expResult);
        e.setResProperty(resProperty);
        e.setValue(null);
        e.setNew();
        save(e);
        return e;
    }

    /**
     * Saves the given ExperimentResultHasResultProperty into the database. Dependend on the PersistanteState of
     * the given object a new entry is created , deleted  or updated in the database.
     * @param e the ExperimentResultHasResultPorperty object which has to be saved into the db
     * @throws NoConnectionToDBException
     * @throws SQLException
     */
    private static void save(ExperimentResultHasResultProperty e) throws NoConnectionToDBException, SQLException {
        if(e.isDeleted()){
            PreparedStatement ps = DatabaseConnector.getInstance().getConn().prepareStatement(deleteQuery);
            ps.setInt(1, e.getId());
            ps.executeUpdate();
            ps.close();
            cache.remove(e);
        }else if( e.isModified()){
            PreparedStatement ps = DatabaseConnector.getInstance().getConn().prepareStatement(updateQuery);
            ps.setInt(1, e.getExpResult().getId());
            ps.setInt(2, e.getResProperty().getId());
            ps.setString(3, e.getValue());
            ps.setInt(4, e.getId());
            ps.executeUpdate();
            ps.close();
            e.setSaved();
        }else if(e.isNew()){
            PreparedStatement ps = DatabaseConnector.getInstance().getConn().prepareStatement(insertQuery);
            ps.setInt(1, e.getId());
            ps.setInt(2, e.getExpResult().getId());
            ps.setInt(3, e.getResProperty().getId());
            ps.setString(4, e.getValue());
            ps.executeUpdate();
            ResultSet generatedKeys = ps.getGeneratedKeys();
            if (generatedKeys.next()) {
                e.setId(generatedKeys.getInt(1));
            }
            generatedKeys.close();           
            ps.close();
            e.setSaved();
            cache.cache(e);
        }
    }

    /**
     * Deletes the given ExperimentResultHasResultProperty from the database and cache.
     * @param e ExperimentResultHasResultProperty to delete
     * @throws NoConnectionToDBException
     * @throws SQLException
     */
    public static void delete(ExperimentResultHasResultProperty e) throws NoConnectionToDBException, SQLException{
        e.setDeleted();
        save(e);
    }

 //
    /**
     * Returns an caches (if necessary) all ExperimentResultHasResultProperty objects which are related to the given
     * ExperimentResult object.
     * @param expResult ExperimentResult object to search for
     * @return a Vector of all ExperimentResultHasResultProperty objects related to the given ExperimentResult object
     * @throws NoConnectionToDBException
     * @throws SQLException
     * @throws ExpResultHasResPropertyNotInDBException
     * @throws ExperimentResultNotInDBException
     * @throws ResultPropertyNotInDBException
     */
    public Vector<ExperimentResultHasResultProperty> getAllByExperimentResult(ExperimentResult expResult)
            throws NoConnectionToDBException, SQLException, ExpResultHasResPropertyNotInDBException, ExperimentResultNotInDBException, ResultPropertyNotInDBException{
        Vector<ExperimentResultHasResultProperty> res = new Vector<ExperimentResultHasResultProperty>();
        PreparedStatement ps = DatabaseConnector.getInstance().getConn().prepareStatement(
                "SELECT idER_h_RP " +
                "FROM " + table + " " +
                "WHERE ExperimentResults_idJob=?;");
        ps.setInt(1, expResult.getId());
        ResultSet rs = ps.executeQuery();
        while(rs.next()){
            res.add(getById(rs.getInt(1)));
        }
        rs.close();
        ps.close();
        return res;      
    }

    /**
     * Returns and caches (if necessary) all ExperimentResultHasResultProperty related to the given ResultProperty object
     * @param resProperty ResultProperty object to search for
     * @return Vector of all ExperimentResultHasResultProperty related to the given Result Property object
     * @throws NoConnectionToDBException
     * @throws SQLException
     * @throws ExpResultHasResPropertyNotInDBException
     * @throws ExperimentResultNotInDBException
     * @throws ResultPropertyNotInDBException
     */
    public Vector<ExperimentResultHasResultProperty> getAllByResultProperty(ResultProperty resProperty)
            throws NoConnectionToDBException, SQLException, ExpResultHasResPropertyNotInDBException, ExperimentResultNotInDBException, ResultPropertyNotInDBException{
        Vector<ExperimentResultHasResultProperty> res = new Vector<ExperimentResultHasResultProperty>();
        PreparedStatement ps = DatabaseConnector.getInstance().getConn().prepareStatement(
                "SELECT idER_h_RP " +
                "FROM " + table + " " +
                "WHERE ResultProperty_idResultProperty=?;");
        ps.setInt(1, resProperty.getId());
        ResultSet rs = ps.executeQuery();
        while(rs.next()){
            res.add(getById(rs.getInt(1)));
        }
        rs.close();
        ps.close();
        return res;
    }

    /**
     * Returns and caches (if necessary) the ExperimentResultHasResultProperty object with the given id.
     * @param id <Integer> of the requested ExperimentResultHasResultProperty
     * @return the ExperimentResultHasResultProperty object with the given id
     * @throws NoConnectionToDBException
     * @throws SQLException
     * @throws ExpResultHasResPropertyNotInDBException
     * @throws ExperimentResultNotInDBException
     * @throws ResultPropertyNotInDBException
     */
    public ExperimentResultHasResultProperty getById(int id)
            throws NoConnectionToDBException, SQLException, ExpResultHasResPropertyNotInDBException, ExperimentResultNotInDBException, ResultPropertyNotInDBException {
        ExperimentResultHasResultProperty res = cache.getCached(id);
        if(res != null){
            return res;
        }else{
            PreparedStatement ps = DatabaseConnector.getInstance().getConn().prepareStatement(
                    "SELECT ExperimentResults_idJob, ResultProperty_idResultProperty, value "
                    + "FROM " + table + " WHERE idER_h_RP=?");
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if(!rs.next())
                throw new ExpResultHasResPropertyNotInDBException();
            res.setId(id);
            res.setExpResult(ExperimentResultDAO.getById(rs.getInt(1)));
            res.setResProperty(ResultPropertyDAO.getById(rs.getInt(2)));
            res.setValue(rs.getString(3));
            res.setSaved();
            cache.cache(res);
            return res;
        }

    }

}
