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
}
