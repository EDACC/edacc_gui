package edacc.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.Hashtable;
import java.sql.*;
import java.util.Vector;

/**
 * data access object for the Instance class
 * @author daniel
 */
public class InstanceDAO {

    protected static final String table = "Instances";
    private static final ObjectCache<Instance> cache = new ObjectCache<Instance>();

    /**
     * Instance factory method. Checks if the instance is already in the Datebase and if so,
     * throws an InstanceAlreadyInDBException
     * @param md5
     * @return new Instance object
     * @throws SQLException, FileNotFoundException, InstanceAlreadyInDBException
     */
    public static Instance createInstance(File file, String name, int numAtoms, int numClauses,
            float ratio, int maxClauseLength, String md5, InstanceClass instanceClass) throws SQLException, FileNotFoundException,
            InstanceAlreadyInDBException {
        PreparedStatement ps;
        final String Query = "SELECT idInstance FROM " + table + " WHERE md5 = ?";
        ps = DatabaseConnector.getInstance().getConn().prepareStatement(Query);
        ps.setString(1, md5);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            throw new InstanceAlreadyInDBException();
        }
        Instance i = new Instance();
        i.setFile(file);
        i.setName(name);
        i.setNumAtoms(numAtoms);
        i.setNumClauses(numClauses);
        i.setRatio(ratio);
        i.setMaxClauseLength(maxClauseLength);
        i.setMd5(md5);
        i.setInstanceClass(instanceClass);
        rs.close();
        ps.close();
        return i;
    }

    public static void delete(Instance i) throws NoConnectionToDBException, SQLException, InstanceIsInExperimentException {
        if (!IsInAnyExperiment(i.getId())) {
            PreparedStatement ps = DatabaseConnector.getInstance().getConn().prepareStatement("DELETE FROM Instances WHERE idInstance=?");
            ps.setInt(1, i.getId());
            ps.executeUpdate();
            cache.remove(i);
            i.setDeleted();
            ps.close();
        } else {
            throw new InstanceIsInExperimentException();
        }

    }

    /**
     * persists an instance object in the database
     * @param instance The instance object to persist
     * @throws SQLException if an SQL error occurs while saving the instance.
     * @throws FileNotFoundException if the file of the instance couldn't be found.
     */
    public static void save(Instance instance) throws SQLException, FileNotFoundException {
        PreparedStatement ps;
        if (instance.isNew()) {
            // insert query, set ID!
            // TODO insert instance blob
            // insert instance into db
            final String insertQuery = "INSERT INTO " + table + " (name, md5, numAtoms, numClauses, ratio, maxClauseLength, instanceClass_idinstanceClass, instance) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            ps = DatabaseConnector.getInstance().getConn().prepareStatement(insertQuery, PreparedStatement.RETURN_GENERATED_KEYS);
            if (instance.getFile() != null) {
                ps.setBinaryStream(8, new FileInputStream(instance.getFile()));
            } else {
                ps.setNull(8, Types.BLOB);
            }
        } else if (instance.isModified()) {
            // update query
            final String updateQuery = "UPDATE " + table + " SET name=?, md5=?, numAtoms=?, numClauses=?, ratio=?, maxClauseLength=?, instanceClass_idinstanceClass=? "
                    + "WHERE idInstance=?";
            ps = DatabaseConnector.getInstance().getConn().prepareStatement(updateQuery);

            ps.setInt(8, instance.getId());

        } else {
            return;
        }

        ps.setString(1, instance.getName());
        ps.setString(2, instance.getMd5());
        ps.setInt(3, instance.getNumAtoms());
        ps.setInt(4, instance.getNumClauses());
        ps.setFloat(5, instance.getRatio());
        ps.setInt(6, instance.getMaxClauseLength());
        if (instance.getInstanceClass() != null) {
            ps.setInt(7, instance.getInstanceClass().getInstanceClassID());
        } else {
            ps.setNull(7, Types.INTEGER);
        }

        ps.executeUpdate();

        // set id
        if (instance.isNew()) {
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                instance.setId(rs.getInt(1));
            }
            cache.cache(instance);
        }
        ps.close();
        instance.setSaved();
    }

    /**
     * retrieves an instance from the database
     * @param id the id of the instance to be retrieved
     * @return the instance specified by its id
     * @throws SQLException
     */
    public static Instance getById(int id) throws SQLException, InstanceClassMustBeSourceException {
        Instance c = cache.getCached(id);
        if (c != null) return c;

        PreparedStatement st = DatabaseConnector.getInstance().getConn().prepareStatement("SELECT idInstance, maxClauseLength, md5, name, numAtoms, numClauses, ratio, instanceClass_idinstanceClass FROM " + table + " WHERE idInstance=?");
        st.setInt(1, id);
        ResultSet rs = st.executeQuery();
        Instance i = new Instance();
        if (rs.next()) {
            i.setId(rs.getInt("idInstance"));
            i.setMaxClauseLength(rs.getInt("maxClauseLength"));
            i.setMd5(rs.getString("md5"));
            i.setName(rs.getString("name"));
            i.setNumAtoms(rs.getInt("numAtoms"));
            i.setNumClauses(rs.getInt("numClauses"));
            i.setRatio(rs.getFloat("ratio"));
            Integer idInstanceClass = rs.getInt("instanceClass_idinstanceClass");
            i.setInstanceClass(InstanceClassDAO.getById(idInstanceClass));

            i.setSaved();
            cache.cache(i);
            return i;
        }
        rs.close();
        return null;
    }

    /**
     * retrieves all instances from the database
     * @return all instances in a List
     * @throws SQLException
     */
    public static LinkedList<Instance> getAll() throws SQLException, InstanceClassMustBeSourceException {
        // return linked list with all instances
        Statement st = DatabaseConnector.getInstance().getConn().createStatement();
        ResultSet rs = st.executeQuery("SELECT idInstance, maxClauseLength, md5, name, numAtoms, numClauses, ratio, instanceClass_idinstanceClass FROM " + table);
        LinkedList<Instance> res = new LinkedList<Instance>();
        while (rs.next()) {
            Instance i = new Instance();
            i.setId(rs.getInt("idInstance"));
            i.setMaxClauseLength(rs.getInt("maxClauseLength"));
            i.setMd5(rs.getString("md5"));
            i.setName(rs.getString("name"));
            i.setNumAtoms(rs.getInt("numAtoms"));
            i.setNumClauses(rs.getInt("numClauses"));
            i.setRatio(rs.getFloat("ratio"));
            Integer idInstanceClass = rs.getInt("instanceClass_idinstanceClass");
            i.setInstanceClass(InstanceClassDAO.getById(idInstanceClass));

            Instance c = cache.getCached(i.getId());
            if (c != null) {
                res.add(c);
            } else {
                i.setSaved();
                cache.cache(i);
                res.add(i);
            }
        }
        rs.close();
        return res;
    }

    public static LinkedList<Instance> getAllByExperimentId(int id) throws SQLException, InstanceClassMustBeSourceException {
        PreparedStatement st = DatabaseConnector.getInstance().getConn().prepareStatement(
                "SELECT DISTINCT i.idInstance, i.maxClauseLength, i.md5, i.name, i.numAtoms, i.numClauses, i.ratio, i.instanceClass_idinstanceClass FROM " + table + " as i JOIN Experiment_has_Instances as ei ON "
                + "i.idInstance = ei.Instances_idInstance WHERE ei.Experiment_idExperiment = ?");
        st.setInt(1, id);
        ResultSet rs = st.executeQuery();
        LinkedList<Instance> res = new LinkedList<Instance>();
        while (rs.next()) {
            Instance c = cache.getCached(rs.getInt("i.idInstance"));
            if (c != null) {
                res.add(c);
            }
            else {
                Instance i = new Instance();
                i.setId(rs.getInt("i.idInstance"));
                i.setMaxClauseLength(rs.getInt("i.maxClauseLength"));
                i.setMd5(rs.getString("i.md5"));
                i.setName(rs.getString("i.name"));
                i.setNumAtoms(rs.getInt("i.numAtoms"));
                i.setNumClauses(rs.getInt("i.numClauses"));
                i.setRatio(rs.getFloat("i.ratio"));
                Integer idInstanceClass = rs.getInt("i.instanceClass_idinstanceClass");
                i.setInstanceClass(InstanceClassDAO.getById(idInstanceClass));

                i.setSaved();
                cache.cache(i);
                res.add(i);
            }
        }
        rs.close();
        return res;
    }

    /**
     * @author rretz
     * retrieves instances from the database.
     * @return Hashtable with all instances which belong to a experiment.
     * @throws NoConnectionToDBException if no connection to database exists.
     * @throws SQLException if an SQL error occurs while reading the instances from the database.
     */
    public static boolean IsInAnyExperiment(int id) throws NoConnectionToDBException, SQLException {
        Statement st = DatabaseConnector.getInstance().getConn().createStatement();

        ResultSet rs = st.executeQuery("SELECT idEI FROM Experiment_has_Instances WHERE Instances_idInstance = " + id + " LIMIT 1;");
        return rs.next();
    }

    /**
     * @author rretz
     * Get the binary of a instance with the given id as a Blob from the database.
     * @param id
     * @return Blob of the instance binary.
     * @throws NoConnectionToDBException
     * @throws SQLException
     * @throws InstaceNotInDBException
     */
    public static Blob getBinary(int id) throws NoConnectionToDBException, SQLException, InstaceNotInDBException {
        Statement st = DatabaseConnector.getInstance().getConn().createStatement();

        ResultSet rs = st.executeQuery("SELECT i.instance FROM instances AS i WHERE i.idInstance = " + id);
        if (rs.next()) {
            return rs.getBlob("instance");
        } else {
            throw new InstaceNotInDBException();
        }
    }

    /**
     * 
     * @param allChoosen
     * @return all instances from the database which have one of the given instance classes and returns them.
     * @throws NoConnectionToDBException
     * @throws SQLException
     */
    public static LinkedList<Instance> getAllByInstanceClasses(Vector<InstanceClass> allChoosen) throws NoConnectionToDBException, SQLException {
        if (!allChoosen.isEmpty()) {
            String query = "SELECT i.idInstance, i.maxClauseLength, i.md5, i.name, i.numAtoms, i.numClauses,"
                    + " i.ratio, i.instanceClass_idinstanceClass FROM " + table + " as i "
                    + " LEFT JOIN Instances_has_instanceClass as ii ON i.idInstance = ii.Instances_idInstance "
                    + " WHERE i.instanceClass_idinstanceClass = " + allChoosen.get(0).getInstanceClassID()
                    + " OR ii.instanceClass_idinstanceClass = " + allChoosen.get(0).getInstanceClassID();
            for (int i = 1; i < allChoosen.size(); i++) {
                query += " OR i.instanceClass_idinstanceClass = " + allChoosen.get(i).getInstanceClassID()
                        + " OR ii.instanceClass_idinstanceClass = " + allChoosen.get(i).getInstanceClassID();
            }
            Statement st = DatabaseConnector.getInstance().getConn().createStatement();
            ResultSet rs = st.executeQuery(query);
            LinkedList<Instance> res = new LinkedList<Instance>();

            while (rs.next()) {
                Instance i = new Instance();
                i.setId(rs.getInt("i.idInstance"));
                i.setMaxClauseLength(rs.getInt("i.maxClauseLength"));
                i.setMd5(rs.getString("i.md5"));
                i.setName(rs.getString("i.name"));
                i.setNumAtoms(rs.getInt("i.numAtoms"));
                i.setNumClauses(rs.getInt("i.numClauses"));
                i.setRatio(rs.getFloat("i.ratio"));
                Integer idInstanceClass = rs.getInt("i.instanceClass_idinstanceClass");
                i.setInstanceClass(InstanceClassDAO.getById(idInstanceClass));

                Instance c = cache.getCached(i.getId());
                if (c != null) {
                    res.add(c);
                } else {
                    i.setSaved();
                    cache.cache(i);
                    res.add(i);
                }
            }
            rs.close();
            return res;
        }

        return null;
    }

    /**
     * Copies the binary file of an instance to a temporary location on the file system
     * and returns a File reference on it.
     * @param i
     * @return
     */
    public static File getBinaryFileOfInstance(Instance i) throws NoConnectionToDBException, SQLException, FileNotFoundException, IOException {
        File f = new File("tmp" + System.getProperty("file.separator") + i.getId() + "_" + i.getName());
        // create missing directories
        f.getParentFile().mkdirs();
        getBinaryFileOfInstance(i, f);
        return f;
    }

    /**
     * Copies the binary file of an instance to a specified location on the filesystem.
     * @param i
     * @param f
     * @throws NoConnectionToDBException
     * @throws SQLException
     * @throws FileNotFoundException
     * @throws IOException
     */
    public static void getBinaryFileOfInstance(Instance i, File f) throws NoConnectionToDBException, SQLException, FileNotFoundException, IOException {
        PreparedStatement ps = DatabaseConnector.getInstance().getConn().prepareStatement("SELECT `instance` FROM " + table + " WHERE idInstance=?");
        ps.setInt(1, i.getId());
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            FileOutputStream out = new FileOutputStream(f);
            InputStream in = rs.getBinaryStream("instance");
            int data;
            while ((data = in.read()) > -1) {
                out.write(data);
            }
            out.close();
            in.close();
        }
    }
}
