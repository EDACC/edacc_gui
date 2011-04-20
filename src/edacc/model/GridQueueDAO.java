/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edacc.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;

/**
 *
 * @author dgall
 */
public class GridQueueDAO {

    protected static final String table = "gridQueue";
    private static final ObjectCache<GridQueue> cache = new ObjectCache<GridQueue>();

    public static void delete(GridQueue q) throws NoConnectionToDBException, SQLException, InstanceIsInExperimentException {
        if (!isInAnyExperiment(q)) {
            PreparedStatement ps = DatabaseConnector.getInstance().getConn().prepareStatement("DELETE FROM " + table + " WHERE idgridQueue=?");
            ps.setInt(1, q.getId());
            ps.executeUpdate();
            cache.remove(q);
            q.setDeleted();
            ps.close();
        } else {
            throw new InstanceIsInExperimentException();
        }

    }

    /**
     * persists a grid queue object in the database
     * @param q The grid queue object to persist
     * @throws SQLException if an SQL error occurs while saving the grid queue.
     * @throws FileNotFoundException if the generic PBS script couldn't be found.
     */
    public static void save(GridQueue q) throws SQLException, FileNotFoundException {
        PreparedStatement ps;
        if (q.isNew()) {
            // insert query, set ID!
            final String insertQuery = "INSERT INTO " + table + " (name, location, numNodes, numCPUs, wallTime, availNodes, maxJobsQueue, description, genericPBSScript) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            ps = DatabaseConnector.getInstance().getConn().prepareStatement(insertQuery, PreparedStatement.RETURN_GENERATED_KEYS);
            if (q.getGenericPBSScript() != null) {
                ps.setBinaryStream(9, new FileInputStream(q.getGenericPBSScript()));
            } else {
                ps.setNull(9, Types.BLOB);
            }
        } else if (q.isModified()) {
            // update query
            final String updateQuery = "UPDATE " + table + " SET name=?, location=?, numNodes=?, numCPUs=?, wallTime=?, availNodes=?, maxJobsQueue=?, description=? "
                    + "WHERE idgridQueue=?";
            ps = DatabaseConnector.getInstance().getConn().prepareStatement(updateQuery);

            ps.setInt(9, q.getId());

        } else {
            return;
        }

        ps.setString(1, q.getName());
        ps.setString(2, q.getLocation());
        ps.setInt(3, q.getNumNodes());
        ps.setInt(4, q.getNumCPUs());
        ps.setInt(5, q.getWalltime());
        ps.setInt(6, q.getAvailNodes());
        ps.setInt(7, q.getMaxJobsQueue());
        ps.setString(8, q.getDescription());

        ps.executeUpdate();

        // set id
        if (q.isNew()) {
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                q.setId(rs.getInt(1));
            }
        }

        // update PBS script if necessary
        if (q.isModified()) {
            if (q.getGenericPBSScript() != null) {
                final String query = "UPDATE " + table + " SET genericPBSScript=? WHERE idgridQueue=?";
                ps = DatabaseConnector.getInstance().getConn().prepareStatement(query);
                ps.setBinaryStream(1, new FileInputStream(q.getGenericPBSScript()));
                ps.setInt(2, q.getId());
                ps.executeUpdate();
            }
        }

        ps.close();
        q.setSaved();
    }

    public static void remove(GridQueue q) throws NoConnectionToDBException, SQLException {
        if (q.isNew()) {
            return;
        }
        final String deleteQuery = "DELETE FROM " + table
                + " WHERE idgridQueue=?";
        PreparedStatement ps = DatabaseConnector.getInstance().getConn().prepareStatement(deleteQuery);
        ps.setInt(1, q.getId());
        ps.executeUpdate();
        cache.remove(q);
        q.setDeleted();
    }

    /**
     * retrieves a grid queue from the database
     * @param id the id of the grid queue to be retrieved
     * @return the grid queue specified by its id
     * @throws SQLException
     */
    public static GridQueue getById(int id) throws SQLException {
        GridQueue c = cache.getCached(id);
        if (c != null) {
            return c;
        }

        PreparedStatement st = DatabaseConnector.getInstance().getConn().prepareStatement("SELECT * FROM " + table + " WHERE idgridQueue=?");
        st.setInt(1, id);
        ResultSet rs = st.executeQuery();
        GridQueue q = new GridQueue();
        if (rs.next()) {
            q.setId(rs.getInt("idgridQueue"));
            q.setLocation(rs.getString("location"));
            q.setNumNodes(rs.getInt("numNodes"));
            q.setNumCPUs(rs.getInt("numCPUs"));
            q.setWalltime(rs.getInt("walltime"));
            q.setAvailNodes(rs.getInt("availNodes"));
            q.setMaxJobsQueue(rs.getInt("maxJobsQueue"));
            q.setDescription(rs.getString("description"));
            q.setName(rs.getString("name"));

            q.setSaved();
            cache.cache(q);
            rs.close();
            st.close();
            return q;

        }
        rs.close();
        st.close();
        return null;
    }

    public static ArrayList<GridQueue> getAll() throws NoConnectionToDBException, SQLException {
        PreparedStatement st = DatabaseConnector.getInstance().getConn().prepareStatement(
                "SELECT * FROM " + table);
        ResultSet rs = st.executeQuery();
        ArrayList<GridQueue> res = new ArrayList<GridQueue>();
        while (rs.next()) {
            GridQueue c = cache.getCached(rs.getInt("idgridQueue"));
            if (c != null) {
                res.add(c);
            } else {
                GridQueue q = new GridQueue();
                q.setId(rs.getInt("idgridQueue"));
                q.setName(rs.getString("name"));
                q.setLocation(rs.getString("location"));
                q.setNumNodes(rs.getInt("numNodes"));
                q.setNumCPUs(rs.getInt("numCPUs"));
                q.setWalltime(rs.getInt("walltime"));
                q.setAvailNodes(rs.getInt("availNodes"));
                q.setMaxJobsQueue(rs.getInt("maxJobsQueue"));
                q.setDescription(rs.getString("description"));
                q.setSaved();
                cache.cache(q);
                res.add(q);
            }
        }
        rs.close();
        st.close();
        return res;
    }

    public static ArrayList<GridQueue> getAllByExperiment(Experiment e) throws SQLException {
        PreparedStatement st = DatabaseConnector.getInstance().getConn().prepareStatement(
                "SELECT * FROM " + table + " as q JOIN Experiment_has_gridQueue as eq ON "
                + "q.idgridQueue = eq.gridQueue_idgridQueue WHERE eq.Experiment_idExperiment = ?");
        st.setInt(1, e.getId());
        ResultSet rs = st.executeQuery();
        ArrayList<GridQueue> res = new ArrayList<GridQueue>();
        while (rs.next()) {
            GridQueue c = cache.getCached(rs.getInt("idgridQueue"));
            if (c != null) {
                res.add(c);
            } else {
                GridQueue q = new GridQueue();
                q.setId(rs.getInt("idgridQueue"));
                q.setName(rs.getString("name"));
                q.setLocation(rs.getString("location"));
                q.setNumNodes(rs.getInt("numNodes"));
                q.setNumCPUs(rs.getInt("numCPUs"));
                q.setWalltime(rs.getInt("walltime"));
                q.setAvailNodes(rs.getInt("availNodes"));
                q.setMaxJobsQueue(rs.getInt("maxJobsQueue"));
                q.setDescription(rs.getString("description"));

                q.setSaved();
                cache.cache(q);
                res.add(q);
            }
        }
        rs.close();
        st.close();
        return res;
    }

    /**
     * @author dgall
     * Checks if Queue is used in an experiment.
     * @return if the Queue is used in an experiment
     * @throws NoConnectionToDBException if no connection to database exists.
     * @throws SQLException if an SQL error occurs while reading the instances from the database.
     */
    public static boolean isInAnyExperiment(GridQueue q) throws NoConnectionToDBException, SQLException {
        PreparedStatement st = DatabaseConnector.getInstance().getConn().prepareStatement("SELECT gridQueue_idgridQueue FROM Experiment_has_gridQueue WHERE gridQueue_idgridQueue=?");
        st.setInt(1, q.getId());
        ResultSet rs = st.executeQuery();
        return rs.next();
    }

    /**
     * Copies the PBS Script of the specified queue to a temporary location.
     * @param q
     * @return the resulting file
     * @throws NoConnectionToDBException
     * @throws SQLException
     * @throws FileNotFoundException
     * @throws IOException
     */
    public static File getPBS(GridQueue q) throws NoConnectionToDBException, SQLException, FileNotFoundException, IOException {
        File f = new File("tmp/start_client.pbs");
        getPBS(q, f);
        return f;
    }

    /**
     * Checks if another queue with the same name exists in the cache (not the DB!)
     * @param name
     * @return the first found queue with the same name or @{code null} if no
     * queue with the same name exists.
     */
    public static GridQueue queueWithSameNameExistsInCache(String name) {
        for (GridQueue q : cache.values()) {
            if (q.getName().equals(name)) {
                return q;
            }
        }
        return null;
    }

    /**
     * Copies the PBS Script of the specified queue to a specified place on the filesystem.
     * @param q
     * @param f
     * @throws NoConnectionToDBException
     * @throws SQLException
     * @throws FileNotFoundException
     * @throws IOException
     */
    public static void getPBS(GridQueue q, File f) throws NoConnectionToDBException, SQLException, FileNotFoundException, IOException {
        PreparedStatement ps = DatabaseConnector.getInstance().getConn().prepareStatement("SELECT `genericPBSScript` FROM " + table + " WHERE idgridQueue=?");
        ps.setInt(1, q.getId());
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            f.delete();
            InputStream in = rs.getBinaryStream("genericPBSScript");
            if (in != null) {
                FileOutputStream out = new FileOutputStream(f);
                int data;
                while ((data = in.read()) > -1) {
                    out.write(data);
                }
                out.close();
                in.close();
            }

        }
        rs.close();
        ps.close();
    }

    public static void clearCache() {
        cache.clear();
    }
}
