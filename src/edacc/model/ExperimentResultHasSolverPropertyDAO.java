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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

/**
 * Implements the data access object of the ExperimentResultHasSolverProperty class
 * @author rretz
 */
public class ExperimentResultHasSolverPropertyDAO {

    protected static final String table = "ExperimentResult_has_SolverProperty";
    protected static final String valueTable = "SolverPropertyValue";
    private static final ObjectCache<ExperimentResultHasSolverProperty> cache = new ObjectCache<ExperimentResultHasSolverProperty>();
    private static final String deleteQuery = "DELETE FROM " + table + " WHERE idER_h_SP=?";
    private static String updateQuery = "UPDATE " + table + " SET ExperimentResults_idJob=?, SolverProperty_idSolverProperty=? WHERE idER_h_SP=?";
    private static String insertQuery = "INSERT INTO " + table + " (ExperimentResults_idJob, SolverProperty_idSolverProperty) VALUES (?, ?)";
    private static final String deleteValueQuery = "DELETE FROM " + valueTable + " WHERE ExperimentResult_has_SolverProperty_idER_h_SP=?";
    private static String insertValueQuery = "INSERT INTO " + valueTable + " (ExperimentResult_has_SolverProperty_idER_h_SP, value, order) VALUES (?, ?, ?)";

    /**
     * Creates a new  ExperimentResultHasSolverProperty object, saves it into the database and cache, and returns it.
     * @param expResult related ExperimentResult object
     * @param solvProperty related SolverProperty object
     * @return new ExperimentResultHasSolverProperty which is also deposited in the database.
     * @throws NoConnectionToDBException
     * @throws SQLException
     */
    public static ExperimentResultHasSolverProperty createExperimentResultHasResultPropertyDAO(ExperimentResult expResult, SolverProperty solvProperty)
            throws NoConnectionToDBException, SQLException {
        ExperimentResultHasSolverProperty e = new ExperimentResultHasSolverProperty();
        e.setExpResult(expResult);
        e.setSolvProperty(solvProperty);
        e.setValue(null);
        e.setNew();
        save(e);
        return e;
    }

    /**
     * Saves the given ExperimentResultHasSolverProperty into the database. Dependend on the PersistanteState of
     * the given object a new entry is created , deleted  or updated in the database.
     * @param e the ExperimentResultHasResultPorperty object which has to be saved into the db
     * @throws NoConnectionToDBException
     * @throws SQLException
     */
    private static void save(ExperimentResultHasSolverProperty e) throws NoConnectionToDBException, SQLException {
        if (e.isDeleted()) {
            PreparedStatement ps = DatabaseConnector.getInstance().getConn().prepareStatement(deleteValueQuery);
            ps.setInt(1, e.getId());
            ps.executeUpdate();
            ps = DatabaseConnector.getInstance().getConn().prepareStatement(deleteQuery);
            ps.setInt(1, e.getId());
            ps.executeUpdate();
            ps.close();
            cache.remove(e);
        } else if (e.isModified()) {
            PreparedStatement ps = DatabaseConnector.getInstance().getConn().prepareStatement(updateQuery);
            ps.setInt(1, e.getExpResult().getId());
            ps.setInt(2, e.getSolvProperty().getId());
            ps.setInt(3, e.getId());
            ps.executeUpdate();

            // Replace the value Vector in the SolverPropertyValue table of the database with the modified one
            ps = DatabaseConnector.getInstance().getConn().prepareStatement(deleteValueQuery);
            ps.setInt(1, e.getId());
            ps.executeUpdate();
            for (int i = 0; i < e.getValue().size(); i++) {
                ps = DatabaseConnector.getInstance().getConn().prepareStatement(insertValueQuery);
                ps.setInt(1, e.getId());
                ps.setString(2, e.getValue().get(i));
                ps.setInt(3, i);
                ps.execute();
            }

            ps.close();
            e.setSaved();
        } else if (e.isNew()) {
            PreparedStatement ps = DatabaseConnector.getInstance().getConn().prepareStatement(insertQuery);
            ps.setInt(1, e.getExpResult().getId());
            ps.setInt(2, e.getSolvProperty().getId());
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
     * Deletes the given ExperimentResultHasSolverProperty from the database and cache.
     * @param e ExperimentResultHasSolverProperty to delete
     * @throws NoConnectionToDBException
     * @throws SQLException
     */
    public static void delete(ExperimentResultHasSolverProperty e) throws NoConnectionToDBException, SQLException {
        e.setDeleted();
        save(e);
    }

    //
    /**
     * Returns an caches (if necessary) all ExperimentResultHasSolverProperty objects which are related to the given
     * ExperimentResult object.
     * @param expResult ExperimentResult object to search for
     * @return a Vector of all ExperimentResultHasSolverProperty objects related to the given ExperimentResult object
     * @throws NoConnectionToDBException
     * @throws SQLException
     * @throws ExpResultHassolvPropertyNotInDBException
     * @throws ExperimentResultNotInDBException
     * @throws SolverPropertyNotInDBException
     */
    public static Vector<ExperimentResultHasSolverProperty> getAllByExperimentResult(ExperimentResult expResult)
            throws NoConnectionToDBException, SQLException, ExpResultHasSolvPropertyNotInDBException, ExperimentResultNotInDBException, SolverPropertyNotInDBException, SolverPropertyTypeNotExistException, IOException {
        Vector<ExperimentResultHasSolverProperty> res = new Vector<ExperimentResultHasSolverProperty>();
        PreparedStatement ps = DatabaseConnector.getInstance().getConn().prepareStatement(
                "SELECT idER_h_RP "
                + "FROM " + table + " "
                + "WHERE ExperimentResults_idJob=?;");
        ps.setInt(1, expResult.getId());
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            res.add(getById(rs.getInt(1)));
        }
        rs.close();
        ps.close();
        return res;
    }

    /**
     * Returns and caches (if necessary) all ExperimentResultHasSolverProperty related to the given SolverProperty object
     * @param solvProperty SolverProperty object to search for
     * @return Vector of all ExperimentResultHasSolverProperty related to the given Result Property object
     * @throws NoConnectionToDBException
     * @throws SQLException
     * @throws ExpResultHassolvPropertyNotInDBException
     * @throws ExperimentResultNotInDBException
     * @throws SolverPropertyNotInDBException
     */
    public static Vector<ExperimentResultHasSolverProperty> getAllByResultProperty(SolverProperty solvProperty)
            throws NoConnectionToDBException, SQLException, ExpResultHasSolvPropertyNotInDBException, ExperimentResultNotInDBException, SolverPropertyNotInDBException, SolverPropertyTypeNotExistException, IOException {
        Vector<ExperimentResultHasSolverProperty> res = new Vector<ExperimentResultHasSolverProperty>();
        PreparedStatement ps = DatabaseConnector.getInstance().getConn().prepareStatement(
                "SELECT idER_h_RP "
                + "FROM " + table + " "
                + "WHERE SolverProperty_idSolverProperty=?;");
        ps.setInt(1, solvProperty.getId());
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            res.add(getById(rs.getInt(1)));
        }
        rs.close();
        ps.close();
        return res;
    }

    public static ExperimentResultHasSolverProperty getByExperimentResultAndResultProperty(ExperimentResult expResult, SolverProperty property) throws SQLException, NoConnectionToDBException, ExpResultHasSolvPropertyNotInDBException, ExperimentResultNotInDBException, SolverPropertyNotInDBException, SolverPropertyTypeNotExistException, IOException {
        ExperimentResultHasSolverProperty res = null;
        PreparedStatement ps = DatabaseConnector.getInstance().getConn().prepareStatement(
                "SELECT idER_h_SP "
                + "FROM " + table + " "
                + "WHERE SolverProperty_idSolverProperty=? AND ExperimentResults_idJob=?;");
        ps.setInt(1, property.getId());
        ps.setInt(2, expResult.getId());
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            res = getById(rs.getInt(1));
        }
        rs.close();
        ps.close();
        return res;
    }

    /**
     * Assigns the ExperimentResultHasSolverProperty objects to the experiment results.
     * @param expResults
     * @param experimentId
     * @throws SQLException
     * @throws Exception
     */
    public static void assign(ArrayList<ExperimentResult> expResults, int experimentId) throws SQLException, SolverPropertyNotInDBException, SolverPropertyTypeNotExistException, IOException {
        HashMap<Integer, ExperimentResult> experimentResults = new HashMap<Integer, ExperimentResult>();
        for (ExperimentResult er : expResults) {
            er.setPropertyValues(new HashMap<Integer, ExperimentResultHasSolverProperty>());
            experimentResults.put(er.getId(), er);
        }
        HashMap<Integer, SolverProperty> solverProperties = new HashMap<Integer, SolverProperty>();
        for (SolverProperty sp : SolverPropertyDAO.getAll()) {
            solverProperties.put(sp.getId(), sp);
        }
        PreparedStatement ps = DatabaseConnector.getInstance().getConn().prepareStatement(
                "SELECT erhsp.idER_h_SP, erhsp.ExperimentResults_idJob, erhsp.SolverProperty_idSolverProperty, " // ExperimentResult_has_SolverProperty
                + "spv.idSolverPropertyValue, spv.ExperimentResult_has_SolverProperty_idER_h_SP, spv.value, spv.order " // SolverPropertyValue
                + "FROM ExperimentResult_has_SolverProperty AS erhsp "
                + "RIGHT JOIN SolverPropertyValue AS spv ON (erhsp.idER_h_SP = spv.ExperimentResult_has_SolverProperty_idER_h_SP) "
                + "RIGHT JOIN SolverProperty AS sp ON (erhsp.SolverProperty_idSolverProperty = sp.idSolverProperty) "
                + "LEFT JOIN ExperimentResults AS er ON (erhsp.ExperimentResults_idJob = er.idJob) "
                + "WHERE er.Experiment_idExperiment = ? "
                + "ORDER BY `order`");
        ps.setInt(1, experimentId);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            int idJob = rs.getInt(2);
            int idSolverProperty = rs.getInt(3);
            ExperimentResult job = experimentResults.get(idJob);
            if (job != null) {
                ExperimentResultHasSolverProperty erhsp = job.getPropertyValues().get(rs.getInt(idSolverProperty));
                if (erhsp == null) {
                    erhsp = new ExperimentResultHasSolverProperty();
                    erhsp.setId(rs.getInt(1));
                    erhsp.setExpResult(experimentResults.get(idJob));
                    erhsp.setSolvProperty(solverProperties.get(idSolverProperty));
                    erhsp.setValue(new Vector<String>());
                    job.getPropertyValues().put(idSolverProperty, erhsp);
                }
                String value = rs.getString(6);
                erhsp.getValue().add(value);
            }
        }
    }

    /**
     * Returns and caches (if necessary) the ExperimentResultHasSolverProperty object with the given id. The  values are kept in their order.
     * @param id <Integer> of the requested ExperimentResultHasSolverProperty
     * @return the ExperimentResultHasSolverProperty object with the given id
     * @throws NoConnectionToDBException
     * @throws SQLException
     * @throws ExpResultHassolvPropertyNotInDBException
     * @throws ExperimentResultNotInDBException
     * @throws SolverPropertyNotInDBException
     */
    public static ExperimentResultHasSolverProperty getById(int id)
            throws NoConnectionToDBException, SQLException, ExpResultHasSolvPropertyNotInDBException, ExperimentResultNotInDBException, SolverPropertyNotInDBException, SolverPropertyTypeNotExistException, IOException {
        ExperimentResultHasSolverProperty res = cache.getCached(id);
        if (res != null) {
            return res;
        } else {
            PreparedStatement ps = DatabaseConnector.getInstance().getConn().prepareStatement(
                    "SELECT ExperimentResults_idJob, SolverProperty_idSolverProperty "
                    + "FROM " + table + " WHERE idER_h_SP=?");
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (!rs.next()) {
                throw new ExpResultHasSolvPropertyNotInDBException();
            }
            res = new ExperimentResultHasSolverProperty();
            res.setId(id);
            res.setExpResult(ExperimentResultDAO.getById(rs.getInt(1)));
            res.setSolvProperty(SolverPropertyDAO.getById(rs.getInt(2)));

            // Get the values for the value Vector of the ExperimentResultHasSolverProperty object from the SolverPropertyValue table from the database
            ps = DatabaseConnector.getInstance().getConn().prepareStatement(
                    "SELECT value "
                    + "FROM " + valueTable + " WHERE ExperimentResult_has_SolverProperty_idER_h_SP=? "
                    + "ORDER BY `order`");
            ps.setInt(1, id);
            rs = ps.executeQuery();
            Vector<String> value = new Vector<String>();
            while (rs.next()) {
                value.add(rs.getString(1));
            }
            res.setValue(value);

            res.setSaved();
            cache.cache(res);
            return res;
        }
    }
}
