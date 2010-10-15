package edacc.model;

import SevenZip.Compression.LZMA.Decoder;
import SevenZip.Compression.LZMA.Encoder;
import edacc.manageDB.InstanceParser.InstanceParser;
import edacc.satinstances.InstancePropertyManager;
import edacc.satinstances.InvalidVariableException;
import edacc.satinstances.SATInstance;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * data access object for the Instance class
 * @author daniel
 */
public class InstanceDAO {

    protected static final String table = "Instances";
    private static final ObjectCache<Instance> cache = new ObjectCache<Instance>();

    private static String getPropertySelect(Vector<InstanceProperty> props) {
        String select = " ";
        int tbl = 0;
        for (InstanceProperty p : props) {
            select += ", tbl_" + tbl++ + ".value";
        }
        return select + " ";
    }

    private static String getPropertyFrom(Vector<InstanceProperty> props) throws IOException, NoConnectionToDBException, SQLException {
        String from = " ";
        int tbl = 0;
        for (InstanceProperty p : props) {
            from += "JOIN (SELECT idInstance, value FROM Instance_has_InstanceProperty WHERE idInstanceProperty = \"" + p.getName() + "\") AS tbl_" + tbl++ + " USING (idInstance) ";
        }
        return from;
    }

    private static Instance getInstance(ResultSet rs, Vector<Property> props) throws IOException, NoConnectionToDBException, SQLException {
        Instance i = new Instance();
        i.setId(rs.getInt("idInstance"));
        i.setMd5(rs.getString("md5"));
        i.setName(rs.getString("name"));
        Integer idInstanceClass = rs.getInt("instanceClass_idinstanceClass");
        i.setInstanceClass(InstanceClassDAO.getById(idInstanceClass));
        i.setPropertyValues(new HashMap<String, InstanceHasProperty>());
        for (int prop = 0; prop < props.size(); prop++) {
            i.getPropertyValues().put(props.get(prop).getName(), new InstanceHasProperty(i, props.get(prop), rs.getString("tbl_" + prop + ".value")));
        }
        return i;
    }

    /**
     * Instance factory method. Checks if the instance is already in the Datebase and if so,
     * throws an InstanceAlreadyInDBException
     * @param file
     * @param name
     * @param md5
     * @param instanceClass
     * @return new Instance object
     * @throws SQLException
     * @throws FileNotFoundException
     * @throws InstanceAlreadyInDBException
     */
    public static Instance createInstance(File file, String name, String md5, InstanceClass instanceClass) throws SQLException, FileNotFoundException,
            InstanceAlreadyInDBException {
        PreparedStatement ps;
        final String Query = "SELECT idInstance FROM " + table + " WHERE md5 = ? OR name = ?";
        ps = DatabaseConnector.getInstance().getConn().prepareStatement(Query);
        ps.setString(1, md5);
        ps.setString(2, name);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            throw new InstanceAlreadyInDBException();
        }
        Instance i = new Instance();
        i.setFile(file);
        i.setName(name);
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
    public static void save(Instance instance) throws SQLException, FileNotFoundException, IOException {
        PreparedStatement ps;
        if (instance.isNew()) {
            try {
                // insert query, set ID!
                // TODO insert instance blob
                // insert instance into db
                final String insertQuery = "INSERT INTO " + table + " (name, md5, instanceClass_idinstanceClass, instance) "
                        + "VALUES (?, ?, ?, ?)";
                ps = DatabaseConnector.getInstance().getConn().prepareStatement(insertQuery, PreparedStatement.RETURN_GENERATED_KEYS);
                ps.setString(1, instance.getName());
                ps.setString(2, instance.getMd5());
                if (instance.getInstanceClass() != null) {
                    ps.setInt(3, instance.getInstanceClass().getInstanceClassID());
                } else {
                    ps.setNull(3, Types.INTEGER);
                }

                File input = null;
                //      File output = null;
                FileInputStream fInStream = null;

                if (instance.getFile() != null) {

                    input = instance.getFile();
                    //output = new File(instance.getFile().getName());
                    //Util.sevenZipEncode(input, output);
                    fInStream = new FileInputStream(input);

                    ps.setBinaryStream(4, fInStream);

                } else {
                    ps.setNull(4, Types.BLOB);
                }

                ps.executeUpdate();


                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    instance.setId(rs.getInt(1));
                }
                cache.cache(instance);

                ps.close();
                instance.setSaved();

                fInStream.close();
                //                output.delete();
                //input.delete();
            } catch (Exception ex) {
                Logger.getLogger(InstanceDAO.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else if (instance.isModified()) {
            // update query
            final String updateQuery = "UPDATE " + table + " SET name=?, md5=?, instanceClass_idinstanceClass=? "
                    + "WHERE idInstance=?";
            ps = DatabaseConnector.getInstance().getConn().prepareStatement(updateQuery);
            ps.setString(1, instance.getName());
            ps.setString(2, instance.getMd5());
            if (instance.getInstanceClass() != null) {
                ps.setInt(3, instance.getInstanceClass().getInstanceClassID());
            } else {
                ps.setNull(3, Types.INTEGER);
            }
            ps.setInt(4, instance.getId());
            ps.executeUpdate();

        } else {
            return;
        }
    }

    /**
     * retrieves an instance from the database
     * @param id the id of the instance to be retrieved
     * @return the instance specified by its id
     * @throws SQLException
     */
    public static Instance getById(int id) throws SQLException, InstanceClassMustBeSourceException {
        Instance c = cache.getCached(id);
        if (c != null) {
            return c;
        }

        PreparedStatement st = DatabaseConnector.getInstance().getConn().prepareStatement(
                "SELECT idInstance,  md5, name, instanceClass_idinstanceClass FROM " + table + " WHERE idInstance=?");
        st.setInt(1, id);
        ResultSet rs = st.executeQuery();
        Instance i = new Instance();
        if (rs.next()) {
            i.setId(rs.getInt("idInstance"));
            i.setMd5(rs.getString("md5"));
            i.setName(rs.getString("name"));
            Integer idInstanceClass = rs.getInt("instanceClass_idinstanceClass");
            i.setInstanceClass(InstanceClassDAO.getById(idInstanceClass));

            ArrayList<Instance> tmp = new ArrayList<Instance>();
            tmp.add(c);
            InstanceHasPropertyDAO.assign(tmp);

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
    public static LinkedList<Instance> getAll() throws SQLException, InstanceClassMustBeSourceException, IOException {
        // return linked list with all instances
        Vector<InstanceProperty> props = InstancePropertyManager.getInstance().getAll();
        Statement st = DatabaseConnector.getInstance().getConn().createStatement();
        ResultSet rs = st.executeQuery("SELECT i.idInstance, i.md5, i.name, i.instanceClass_idinstanceClass" + getPropertySelect(props)
                + "FROM " + table + " AS i " + getPropertyFrom(props));
        LinkedList<Instance> res = new LinkedList<Instance>();
        while (rs.next()) {
            Instance c = cache.getCached(rs.getInt("i.idInstance"));
            if (c != null) {
                res.add(c);
                continue;
            }
            Instance i = getInstance(rs, props);
            i.setSaved();
            cache.cache(i);
            res.add(i);
        }
        rs.close();
        return res;
    }

    public static LinkedList<Instance> getAllByExperimentId(int id) throws SQLException, InstanceClassMustBeSourceException, IOException {
        Vector<InstanceProperty> props = InstancePropertyManager.getInstance().getAll();
        PreparedStatement st = DatabaseConnector.getInstance().getConn().prepareStatement(
                "SELECT DISTINCT i.idInstance, i.md5, i.name, i.instanceClass_idinstanceClass" + getPropertySelect(props)
                + "FROM " + table + " as i JOIN Experiment_has_Instances as ei ON "
                + "i.idInstance = ei.Instances_idInstance " + getPropertyFrom(props) + " WHERE ei.Experiment_idExperiment = ?");
        st.setInt(1, id);
        ResultSet rs = st.executeQuery();
        LinkedList<Instance> res = new LinkedList<Instance>();
        while (rs.next()) {
            Instance c = cache.getCached(rs.getInt("i.idInstance"));
            if (c != null) {
                res.add(c);
            } else {
                Instance i = getInstance(rs, props);
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
     * @throws InstanceNotInDBException
     */
    public static Blob getBinary(int id) throws NoConnectionToDBException, SQLException, InstanceNotInDBException {
        Statement st = DatabaseConnector.getInstance().getConn().createStatement();

        ResultSet rs = st.executeQuery("SELECT i.instance FROM instances AS i WHERE i.idInstance = " + id);
        if (rs.next()) {
            return rs.getBlob("instance");
        } else {
            throw new InstanceNotInDBException();
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
            String query = "SELECT i.idInstance, i.md5, i.name,"
                    + " i.instanceClass_idinstanceClass FROM " + table + " as i "
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
            ArrayList<Instance> instanceHasPropertyAssignList = new ArrayList<Instance>();
            while (rs.next()) {

                Instance c = cache.getCached(rs.getInt("i.idInstance"));
                if (c != null) {
                    res.add(c);
                    continue;
                }
                Instance i = new Instance();
                i.setId(rs.getInt("i.idInstance"));
                i.setMd5(rs.getString("i.md5"));
                i.setName(rs.getString("i.name"));
                Integer idInstanceClass = rs.getInt("i.instanceClass_idinstanceClass");
                i.setInstanceClass(InstanceClassDAO.getById(idInstanceClass));
                i.setSaved();
                cache.cache(i);
                res.add(i);
                instanceHasPropertyAssignList.add(i);
            }
            rs.close();
            InstanceHasPropertyDAO.assign(instanceHasPropertyAssignList);
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
    public static void getBinaryFileOfInstance(Instance i, File f) throws NoConnectionToDBException, FileNotFoundException, IOException {
        try {
            PreparedStatement ps = DatabaseConnector.getInstance().getConn().prepareStatement("SELECT `instance` FROM " + table + " WHERE idInstance=?");

            ps.setInt(1, i.getId());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                FileOutputStream out = new FileOutputStream(f);
                InputStream in = rs.getBinaryStream(1);
                int len = 0;
                byte[] buffer = new byte[256 * 1024];
                while ((len = in.read(buffer)) > -1) {
                    out.write(buffer, 0, len);
                }
                out.close();
                in.close();
            }
            /*
            File input = new File(f.getAbsolutePath() + "test");
            input.getParentFile().mkdirs();
            if (rs.next()) {
            FileOutputStream out = new FileOutputStream(input);
            InputStream in = rs.getBinaryStream("instance");
            int len;
            byte[] buf = new byte[256 * 1024];
            while ((len = in.read(buf)) > -1) {
            out.write(buf, 0, len);
            }
            out.flush();
            out.close();
            in.close();
            try{
            Util.sevenZipDecode(input, f);
            } catch (Exception ex) {
            Logger.getLogger(InstanceDAO.class.getName()).log(Level.SEVERE, null, ex);
            }
            input.delete();
            }*/
        } catch (SQLException ex) {
            Logger.getLogger(InstanceDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        /*
        File input = new File(f.getAbsolutePath() + "test");
        input.getParentFile().mkdirs();
        if (rs.next()) {
        FileOutputStream out = new FileOutputStream(input);
        InputStream in = rs.getBinaryStream("instance");
        int len;
        byte[] buf = new byte[256 * 1024];
        while ((len = in.read(buf)) > -1) {
        out.write(buf, 0, len);
        }
        out.flush();
        out.close();
        in.close();

        try{
        Util.sevenZipDecode(input, f);
        } catch (Exception ex) {
        Logger.getLogger(InstanceDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        input.delete();
        }*/
    }

    public static void clearCache() {
        cache.clear();
    }

    public static SATInstance getSATFormulaOfInstance(Instance i) throws IOException, InvalidVariableException, InstanceNotInDBException, SQLException {
        return edacc.satinstances.InstanceParser.getInstance().parseInstance(getBinary(i.getId()).getBinaryStream());
    }
}
