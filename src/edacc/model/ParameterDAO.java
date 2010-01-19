package edacc.model;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.Hashtable;
import java.sql.ResultSet;
import java.util.Vector;

/**
 *
 * @author daniel
 */
public class ParameterDAO {
    private static final String table = "Parameters";


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

        final String insertQuery = "INSERT INTO Parameters (name, prefix, value, order, Solver_idSolver) VALUES (?, ?, ?, ?, ?)";
        PreparedStatement ps = DatabaseConnector.getInstance().getConn().prepareStatement(insertQuery, PreparedStatement.RETURN_GENERATED_KEYS);
        ps.setString(1, parameter.getName());
        ps.setString(2, parameter.getPrefix());
        ps.setString(3, parameter.getValue());
        ps.setInt(4, parameter.getOrder());
        ps.setInt(5, solver.getId());
        ps.executeUpdate();

        // set id
        ResultSet rs = ps.getGeneratedKeys();
        if (rs.next())
            parameter.setId(rs.getInt(1));
    }

    private static Parameter getParameterFromResultset(ResultSet rs) throws SQLException {
        Parameter i = new Parameter();
        i.setId(rs.getInt("idParameter"));
        i.setName(rs.getString("name"));
        i.setOrder(rs.getInt("order"));
        i.setPrefix(rs.getString("prefix"));
        i.setValue(rs.getString("value"));
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
            Parameter i = getParameterFromResultset(rs);
            res.add(i);
        }
        return res;
    }

    public static void removeParametersOfSolver(Solver solver) throws NoConnectionToDBException, SQLException {
        final String query = "DELETE FROM Parameters WHERE Solver_idSolver=?";
        PreparedStatement ps = DatabaseConnector.getInstance().getConn().prepareStatement(query);
        ps.setInt(1, solver.getId());
        ps.executeUpdate();
    }
}
