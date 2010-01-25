package edacc.model;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Hashtable;
import java.util.LinkedList;

/**
 *
 * @author simon
 */
public class SolverDAO {

    protected static final String table = "Solver";
    protected static final String insertQuery = "INSERT INTO " + table + " (`name`, `binaryName`, `binary`, `description`, `md5`, `code`) VALUES (?, ?, ?, ?, ?, ?)";
    protected static final String updateQuery = "UPDATE " + table + " SET `name`=?, `binaryName`=?, `binary`=?, `description`=?, `md5`=?, `code`=? WHERE `md5`=?";
    protected static final String removeQuery = "DELETE FROM " + table + " WHERE idSolver=?";
    private static final Hashtable<Solver, Solver> cache = new Hashtable<Solver, Solver>();

    /**
     * persists a solver to database and assigns an id. it also ensures that
     * the solver is cached.
     * @param solver The Solver object to persist.
     */
    public static void save(Solver solver) throws SQLException, FileNotFoundException {
        PreparedStatement ps;

        boolean alreadyInDB = false;

        // insert  into db
        if (solver.isNew() && !(alreadyInDB = solverAlreadyInDB(solver) != null)) {
            ps = DatabaseConnector.getInstance().getConn().prepareStatement(insertQuery, PreparedStatement.RETURN_GENERATED_KEYS);
        } else {
            ps = DatabaseConnector.getInstance().getConn().prepareStatement(updateQuery);
            ps.setString(7, solver.getMd5());
        }

        ps.setString(1, solver.getName());
        ps.setString(2, solver.getBinaryName());
        ps.setBinaryStream(3, new FileInputStream(solver.getBinaryFile()));
        ps.setString(4, solver.getDescription());
        ps.setString(5, solver.getMd5());
        if (solver.getCodeFile() != null)
            ps.setBinaryStream(6, new FileInputStream(solver.getCodeFile()));
        else
            ps.setBinaryStream(6, null);
        ps.executeUpdate();

        if (solver.isNew() && !alreadyInDB) {
            // set id
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                solver.setId(rs.getInt(1));
            }
        }

        cacheSolver(solver);
        solver.setSaved();
    }

    /**
     * Removes a solver from DB and cache. It also ensures that all parameters of a solver are deleted.
     * TODO delete SolverCOnfigs??
     * @param solver the solver to remove.
     * @throws SQLException if an error occurs while executing the SQL query.
     */
    public static void removeSolver(Solver solver) throws SQLException {
        ParameterDAO.removeParametersOfSolver(solver);
        PreparedStatement ps = DatabaseConnector.getInstance().getConn().prepareStatement(removeQuery);
        ps.setInt(1, solver.getId());
        ps.executeUpdate();

        if (cache.containsKey(solver)) {
            cache.remove(solver);
        }
        solver.setDeleted();
    }

    private static Solver getSolverFromResultset(ResultSet rs) throws SQLException {
        Solver i = new Solver();
        i.setId(rs.getInt("idSolver"));
        i.setName(rs.getString("name"));
        i.setBinaryName(rs.getString("binaryName"));
        i.setDescription(rs.getString("description"));
        i.setMd5(rs.getString("md5"));
        return i;
    }

    private static Solver getCached(Solver i) {
        if (cache.containsKey(i)) {
            return cache.get(i);
        } else {
            return null;
        }
    }

    private static void cacheSolver(Solver i) {
        if (cache.containsKey(i)) {
            return;
        } else {
            cache.put(i, i);
        }
    }

    /**
     * retrieves an solver from the database
     * @param id the id of the solver to be retrieved
     * @return the solver specified by its id
     * @throws SQLException
     */
    public static Solver getById(int id) throws SQLException {
        PreparedStatement st = DatabaseConnector.getInstance().getConn().prepareStatement("SELECT * FROM " + table + " WHERE idSolver=?");
        st.setInt(1, id);
        ResultSet rs = st.executeQuery();
        if (rs.next()) {
            Solver i = getSolverFromResultset(rs);

            Solver c = getCached(i);
            if (c != null) {
                return c;
            } else {
                i.setSaved();
                cacheSolver(i);
                return i;
            }
        }
        return null;
    }

    /**
     * retrieves all solvers from the database
     * @return all solvers in a List
     * @throws SQLException
     */
    public static LinkedList<Solver> getAll() throws SQLException {
        Statement st = DatabaseConnector.getInstance().getConn().createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM " + table);
        LinkedList<Solver> res = new LinkedList<Solver>();
        while (rs.next()) {
            Solver i = getSolverFromResultset(rs);
            Solver c = getCached(i);
            if (c != null) {
                res.add(c);
            } else {
                i.setSaved();
                cacheSolver(i);
                res.add(i);
            }
        }
        rs.close();
        return res;
    }

    /**
     * Tests if a solver is already in DB (by MD5) and returns the cached object or
     * null if solver isn't in DB.
     * @param s
     * @return
     * @throws NoConnectionToDBException
     * @throws SQLException
     */
    public static Solver solverAlreadyInDB(Solver s) throws NoConnectionToDBException, SQLException {
        PreparedStatement ps;
        final String Query = "SELECT * FROM " + table + " WHERE md5 = ?";
        ps = DatabaseConnector.getInstance().getConn().prepareStatement(Query);
        ps.setString(1, s.getMd5());
        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            Solver i = getSolverFromResultset(rs);

            Solver c = getCached(i);
            if (c != null) {
                return c;
            } else {
                i.setSaved();
                cacheSolver(i);
                return i;
            }
        }
        return null;
    }
}
