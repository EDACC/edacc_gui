package edacc.model;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.util.Vector;

/**
 *
 * @author daniel
 */
public class ParameterDAO {
    private static final String table = "Parameters";
    private static final ObjectCache<Parameter> cache = new ObjectCache<Parameter>();

    /**
     * TODO Add caching for parameters??
     * Saves a parameter for a solver in the DB.
     * A parameter can't exist without a solver.
     * @param solver
     * @param parameter
     */
    public static void saveParameterForSolver(Solver solver, Parameter parameter) throws NoConnectionToDBException, SQLException {
        if (!solver.isSaved())
            return; // TODO do something if solver isn't in db
        if (parameter.isSaved()) return;
        if (parameter.isNew()) {
            final String insertQuery = "INSERT INTO Parameters (name, prefix, value, hasValue, Parameters.order, Solver_idSolver) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement ps = DatabaseConnector.getInstance().getConn().prepareStatement(insertQuery, PreparedStatement.RETURN_GENERATED_KEYS);
            ps.setString(1, parameter.getName());
            ps.setString(2, parameter.getPrefix());
            ps.setString(3, parameter.getValue());
            ps.setBoolean(4, parameter.getHasValue());
            ps.setInt(5, parameter.getOrder());
            ps.setInt(6, solver.getId());
            ps.executeUpdate();
            // set id
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next())
                parameter.setId(rs.getInt(1));
            parameter.setSaved();
        }
        else if (parameter.isModified()) {
            final String updateQuery = "UPDATE Parameters SET name=?, prefix=?, value=?, hasValue=?, Parameters.order=?, Solver_idSolver=? WHERE idParameter=?";
            PreparedStatement ps = DatabaseConnector.getInstance().getConn().prepareStatement(updateQuery);
            ps.setString(1, parameter.getName());
            ps.setString(2, parameter.getPrefix());
            ps.setString(3, parameter.getValue());
            ps.setBoolean(4, parameter.getHasValue());
            ps.setInt(5, parameter.getOrder());
            ps.setInt(6, solver.getId());
            ps.setInt(7, parameter.getId());
            ps.executeUpdate();
            parameter.setSaved();
        }
        cache.cache(parameter);
    }

    private static Parameter getParameterFromResultset(ResultSet rs) throws SQLException {
        Parameter i = new Parameter();
        i.setId(rs.getInt("idParameter"));
        i.setName(rs.getString("name"));
        i.setOrder(rs.getInt("order"));
        i.setPrefix(rs.getString("prefix"));
        i.setValue(rs.getString("value"));
        i.setHasValue(rs.getBoolean("hasValue"));
        return i;
    }
    
    /**
     * Gets all parameters from a solver.
     * @param id The id of the solver
     * @return Vector<Parameter> with the parameters of the solver
     * @throws SQLException
     */
    public static Vector<Parameter> getParameterFromSolverId(int id) throws SQLException {
        Vector<Parameter> res = new Vector<Parameter>();
        PreparedStatement st = DatabaseConnector.getInstance().getConn().prepareStatement("SELECT * FROM " + table + " WHERE Solver_idSolver=?");
        st.setInt(1, id);
        ResultSet rs = st.executeQuery();
        while (rs.next()) {
            Parameter i = cache.getCached(rs.getInt("idParameter"));
            if (i == null) {
                i = getParameterFromResultset(rs);
                cache.cache(i);
            }
            res.add(i);
            i.setSaved();
        }
        return res;
    }

    public static Parameter getById(int id) throws SQLException {
        Parameter i = cache.getCached(id);
        if (i != null) {
            return i;
        }
        PreparedStatement st = DatabaseConnector.getInstance().getConn().prepareStatement("SELECT * FROM " + table + " WHERE idParameter=?");
        st.setInt(1, id);
        ResultSet rs = st.executeQuery();
        if (rs.next()) {
            i = getParameterFromResultset(rs);
            cache.cache(i);
            return i;
        }
        return null;
    }

    /**
     * Deletes all parameters of a solver.
     * @param solver
     * @throws NoConnectionToDBException
     * @throws SQLException
     */
    public static void removeParametersOfSolver(Solver solver) throws NoConnectionToDBException, SQLException {
       // final String query = "DELETE FROM Parameters WHERE Solver_idSolver=?";
      //  PreparedStatement ps = DatabaseConnector.getInstance().getConn().prepareStatement(query);
       // ps.setInt(1, solver.getId());
      //  ps.executeUpdate();
        Vector<Parameter> params = getParameterFromSolverId(solver.getId());
        for (Parameter param: params) {
            delete(param);
        }
    }

    public static void delete(Parameter p) throws NoConnectionToDBException, SQLException {
        cache.remove(p);
        if (p.isNew()) return;
        final String query = "DELETE FROM Parameters WHERE idParameter=?";
        PreparedStatement ps = DatabaseConnector.getInstance().getConn().prepareStatement(query);
        ps.setInt(1, p.getId());
        ps.executeUpdate();
    }

    public static void clearCache() {
        cache.clear();
    }
}
