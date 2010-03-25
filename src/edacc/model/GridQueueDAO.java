/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edacc.model;

import com.mysql.jdbc.NotImplemented;
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
import java.util.Hashtable;
import java.util.Vector;

/**
 *
 * @author dgall
 */
public class GridQueueDAO {

    protected static final String table = "gridQueue";
    private static final Hashtable<GridQueue, GridQueue> cache = new Hashtable<GridQueue, GridQueue>();

//    /**
//     * Grid Queue factory method, ensures that the created instance is persisted and assigned an ID
//     * so it can be referenced by related objects. Checks if the instance is already in the Datebase.
//     * @param md5
//     * @return new Instance object
//     * @throws SQLException, FileNotFoundException, InstanceAlreadyInDBException
//     */
//     public static GridQueue createQueue(File file, String name, int numAtoms, int numClauses ,
//             float ratio, int maxClauseLength, String md5, InstanceClass instanceClass) throws SQLException, FileNotFoundException,
//             InstanceAlreadyInDBException {
//         PreparedStatement ps;
//         final String Query = "SELECT * FROM " + table +" WHERE md5 = ?";
//         ps = DatabaseConnector.getInstance().getConn().prepareStatement(Query);
//         ps.setString(1, md5);
//         ResultSet rs = ps.executeQuery();
//         if(rs.next()){
//            throw new InstanceAlreadyInDBException();
//         }
//         Instance i = new Instance();
//        i.setFile(file);
//        i.setName(name);
//        i.setNumAtoms(numAtoms);
//        i.setNumClauses(numClauses);
//        i.setRatio(ratio);
//        i.setMaxClauseLength(maxClauseLength);
//        i.setMd5(md5);
//        i.setInstanceClass(instanceClass);
//        save(i);
//        cacheInstance(i);
//        return i;
//     }
    public static void delete(GridQueue q) throws NoConnectionToDBException, SQLException, InstanceIsInExperimentException {
        if (!isInAnyExperiment(q)) {
            PreparedStatement ps = DatabaseConnector.getInstance().getConn().prepareStatement("DELETE FROM " + table + " WHERE idgridQueue=?");
            ps.setInt(1, q.getId());
            ps.executeUpdate();
            cache.remove(q);
            q.setDeleted();
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
            // TODO insert instance blob
            // insert instance into db
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

        q.setSaved();
    }

    private static GridQueue getCached(GridQueue i) {
        if (cache.containsKey(i)) {
            return cache.get(i);
        } else {
            return null;
        }
    }

    private static void cacheQueue(GridQueue i) {
        if (cache.containsKey(i)) {
            return;
        } else {
            cache.put(i, i);
        }
    }

    /**
     * retrieves a grid queue from the database
     * @param id the id of the grid queue to be retrieved
     * @return the grid queue specified by its id
     * @throws SQLException
     */
    public static GridQueue getById(int id) throws SQLException {
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

            GridQueue c = getCached(q);
            if (c != null) {
                return c;
            } else {
                q.setSaved();
                cacheQueue(q);
                return q;
            }
        }
        return null;
    }

    public static Vector<GridQueue> getAll() throws NoConnectionToDBException, SQLException {
        PreparedStatement st = DatabaseConnector.getInstance().getConn().prepareStatement(
                "SELECT * FROM " + table);
        ResultSet rs = st.executeQuery();
        Vector<GridQueue> res = new Vector<GridQueue>();
        while (rs.next()) {
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

            GridQueue c = getCached(q);
            if (c != null) {
                res.add(c);
            } else {
                q.setSaved();
                cacheQueue(q);
                res.add(q);
            }
        }
        rs.close();
        return res;
    }

    public static Vector<GridQueue> getAllByExperiment(Experiment e) throws SQLException {
        PreparedStatement st = DatabaseConnector.getInstance().getConn().prepareStatement(
                "SELECT * FROM " + table + " as q JOIN Experiment_has_gridQueue as eq ON "
                + "q.idgridQueue = eq.gridQueue_idgridQueue WHERE eq.Experiment_idExperiment = ?");
        st.setInt(1, e.getId());
        ResultSet rs = st.executeQuery();
        Vector<GridQueue> res = new Vector<GridQueue>();
        while (rs.next()) {
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

            GridQueue c = getCached(q);
            if (c != null) {
                res.add(c);
            } else {
                q.setSaved();
                cacheQueue(q);
                res.add(q);
            }
        }
        rs.close();
        return res;
    }

    /**
     * @author dgall
     * TODO implement
     * Checks if Queue is used in an experiment.
     * @return Hashtable with all instances which belong to a experiment.
     * @throws NoConnectionToDBException if no connection to database exists.
     * @throws SQLException if an SQL error occurs while reading the instances from the database.
     */
    public static boolean isInAnyExperiment(GridQueue q) throws NoConnectionToDBException, SQLException {
        /*Statement st = DatabaseConnector.getInstance().getConn().createStatement();

        ResultSet rs = st.executeQuery("SELECT i.idInstance FROM " + table + " AS i JOIN Experiment_has_Instances as ei ON " +
        "i.idInstance = ei.Instances_idInstance WHERE idInstance = "+ id);
        return rs.next();*/
        throw new NotImplemented();
    }

    /**
     * Copies the PBS Script of the specified queue to a temporary location.
     * @param q
     * @return
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
            FileOutputStream out = new FileOutputStream(f);
            InputStream in = rs.getBinaryStream("genericPBSScript");
            int data;
            while ((data = in.read()) > -1) {
                out.write(data);
            }
            out.close();
            in.close();
        }
    }
}
