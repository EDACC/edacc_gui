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
import java.util.Vector;

/**
 *
 * @author rretz
 */
public class ComputationMethodDAO {
    private static ObjectCache<ComputationMethod> cache = new ObjectCache<ComputationMethod>();
    private static String table = "ComputationMethod";
    private static String deleteQuery = "DELETE FROM " + table + "WHERE idComputationMethod=?;";
    private static String updateQuery = "UPDATE " + table + " SET name=?, description=? WHERE idComputationMethod=?;";
    private static String insertQuery = "INSERT INTO " + table + "(name, description, md5, binaryName, binary) " +
            "VALUES (?, ?, ?, ?, ?);";

    /**
     * Creates a new ComputationMethod object, saves it into the database, put it into the cache and returns it.
     * @param name
     * @param description
     * @param md5
     * @param binary
     * @return a new created and saved ComputationMethod object with the given parameters.
     * @throws NoConnectionToDBException
     * @throws SQLException
     * @throws ComputationMethodAlreadyExistsException
     * @throws NoComputationMethodBinarySpecifiedException
     * @throws FileNotFoundException
     */
    public static ComputationMethod createComputationMethod(String name, String description, String md5, File binary) throws NoConnectionToDBException, SQLException, ComputationMethodAlreadyExistsException, NoComputationMethodBinarySpecifiedException, FileNotFoundException{
        ComputationMethod cm = new ComputationMethod();
        cm.setName(name);
        cm.setDescription(description);
        cm.setMd5(md5);
        cm.setBinary(binary);
        cm.setBinaryName(binary.getName());
        cm.setNew();
        save(cm);
        return cm;
    }

    /**
     * Saves the given Property into the database. Dependend on the PersistanteState of
     * the given object a new entry is created, deleted or updated in the database.
     * @param cm the ComputitionMethod toSave.
     * @throws NoConnectionToDBException
     * @throws NoConnectionToDBException
     * @throws NoConnectionToDBException
     * @throws SQLException
     * @throws ComputationMethodAlreadyExistsException
     * @throws NoComputationMethodBinarySpecifiedException
     * @throws FileNotFoundException
     */
    public static void save(ComputationMethod cm) throws NoConnectionToDBException, NoConnectionToDBException, NoConnectionToDBException, SQLException, ComputationMethodAlreadyExistsException, NoComputationMethodBinarySpecifiedException, FileNotFoundException {
        if(cm.isDeleted()){
            PreparedStatement ps = DatabaseConnector.getInstance().getConn().prepareStatement(deleteQuery);
            ps.setInt(1, cm.getId());
            ps.executeUpdate();
            ps.close();
            cache.remove(cm);
        }else if(cm.isModified()){
            PreparedStatement ps = DatabaseConnector.getInstance().getConn().prepareStatement(updateQuery);
            ps.setString(1, cm.getName());
            ps.setString(2, cm.getDescription());
            ps.setInt(3, cm.getId());
            ps.executeUpdate();;
            ps.close();
        }else if(cm.isNew()){
            // A new ComputationMethod without binary are not allowed
            if(cm.getBinary() == null)
                throw new NoComputationMethodBinarySpecifiedException();

            PreparedStatement ps = DatabaseConnector.getInstance().getConn().prepareStatement(
                    "SELECT idComputationMethod FROM " + table + "WHERE name=? OR md5=?;");
            ResultSet rs = ps.executeQuery();
            if(rs.next())
                throw new ComputationMethodAlreadyExistsException();
            ps = DatabaseConnector.getInstance().getConn().prepareStatement(insertQuery, PreparedStatement.RETURN_GENERATED_KEYS);
            ps.setString(1, cm.getName());
            ps.setString(2, cm.getDescription());
            ps.setString(3, cm.getMd5());
            ps.setString(4, cm.getBinaryName());
            ps.setBinaryStream(5, new FileInputStream(cm.getBinary()));
            ps.executeUpdate();
            ps.close();
            cm.setSaved();
            cache.cache(cm);
        }
    }

    /**
     * Returns the requested ComputationMethod object from cache or from the database if the object is not in the cache.
     * @param id of the requested ComputationMethod object
     * @return the requested ComputationMethod object 
     * @throws NoConnectionToDBException
     * @throws SQLException
     * @throws ComputationMethodDoesNotExistException
     */
    public static ComputationMethod getById(int id) throws NoConnectionToDBException, SQLException, ComputationMethodDoesNotExistException{
        ComputationMethod res = cache.getCached(id);
        if(res != null)
            return res;
        res = new ComputationMethod();
        PreparedStatement ps = DatabaseConnector.getInstance().getConn().prepareStatement(
                    "SELECT name, description, md5, binaryName FROM " + table + "WHERE idComputationMethod=?;");
         ps.setInt(1, id);
         ResultSet rs = ps.executeQuery();
         if(!rs.next())
            throw new ComputationMethodDoesNotExistException();
         res.setId(id);
         res.setName(rs.getString("name"));
         res.setDescription("description");
         res.setMd5("md5");
         res.setBinaryName("binaryName");
         res.setSaved();
         cache.cache(res);
         return res;
    }

    /**
      * Copies the binary file of a ComputationMethod to a temporary location on the file system
     * and returns a File reference on it.
     * @param cm
     * @return
     * @throws NoConnectionToDBException
     * @throws SQLException
     * @throws SQLException
     * @throws ComputationMethodDoesNotExistException
     * @throws FileNotFoundException
     * @throws IOException
     */
     public static File getBinaryOfComputationMethod(ComputationMethod cm) throws NoConnectionToDBException, SQLException, SQLException,
             ComputationMethodDoesNotExistException, FileNotFoundException, IOException {
        File f = new File("tmp" + System.getProperty("file.separator") + cm.getBinaryName());
        // create missing direcotries
        f.getParentFile().mkdirs();
        getBinaryOfComputationMethod(f, cm);
        return f;
    }

     /**
      * Copies the binary file of a Computationmethod to a specified location on the file system.
      * @param f
      * @param cm
      * @throws NoConnectionToDBException
      * @throws SQLException
      * @throws ComputationMethodDoesNotExistException
      * @throws FileNotFoundException
      * @throws IOException
      */
    public static void getBinaryOfComputationMethod(File f, ComputationMethod cm) throws NoConnectionToDBException, SQLException,
            ComputationMethodDoesNotExistException, FileNotFoundException, IOException{
        PreparedStatement ps = DatabaseConnector.getInstance().getConn().prepareStatement(
                    "SELECT binary FROM " + table + "WHERE idComputationMethod=?;");
        ps.setInt(1, cm.getId());
        ResultSet rs = ps.executeQuery();
        if(!rs.next())
            throw new ComputationMethodDoesNotExistException();
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

    /**
     * Returns and caches all ComputationMethod objects form the database
     * @return all ComputationMethod objects from the database.
     * @throws NoConnectionToDBException
     * @throws SQLException
     * @throws ComputationMethodDoesNotExistException
     */
    public static Vector<ComputationMethod> getAll() throws NoConnectionToDBException, SQLException, ComputationMethodDoesNotExistException {
        PreparedStatement ps = DatabaseConnector.getInstance().getConn().prepareStatement(
            "SELECT idComputationMethod FROM " + table + ";");
        ResultSet rs = ps.executeQuery();
        Vector<ComputationMethod> all = new Vector<ComputationMethod>();
        while(rs.next()){
            all.add(getById(rs.getInt("idComputationMethod")));
        }
        return all;
     }

}
