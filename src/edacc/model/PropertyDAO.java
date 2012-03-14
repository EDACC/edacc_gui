/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edacc.model;

import edacc.properties.PropertySource;
import edacc.properties.PropertyTypeNotExistException;
import edacc.satinstances.PropertyValueType;
import edacc.satinstances.PropertyValueTypeManager;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Vector;

/**
 * data access object of the Property class
 * @author rretz
 */
public class PropertyDAO {

    protected static final String table = "Property";
    private static final ObjectCache<Property> cache = new ObjectCache<Property>();
    private static String deleteQuery = "DELETE FROM " + table + " WHERE idProperty=?;";
    private static String updateQuery = "UPDATE " + table + " SET name=?,  description=?, propertyType=?, propertySource=? ,"
            + "propertyValueType=?, multipleOccourence=?, idComputationMethod=?, computationMethodParameters=?  WHERE idProperty=?;";
    private static String insertQuery = "INSERT INTO " + table + " (name, description, propertyType, propertySource ,"
            + "propertyValueType, multipleOccourence, idComputationMethod, computationMethodParameters, isDefault) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?);";

    /**
     * Creates a new  Property object, saves it into the database and cache, and returns it.
     * @param name <String> of the Property object
     * @param prefix <String> prefix of the Property object
     * @param description <String> description of the Property object
     * @param valueType related PropertyValueType object
     * @return new Property which is also deposited in the database.
     * @throws NoConnectionToDBException
     * @throws SQLException
     */
    public static Property createProperty(String name, Vector<String> regularExpression, String description, PropertyType type, PropertyValueType valueType,
            PropertySource source, boolean multiple, ComputationMethod computationMethod, String computationMethodParameters, String parameter, Boolean isDefault)
            throws NoConnectionToDBException, SQLException, PropertyIsUsedException, PropertyTypeDoesNotExistException, IOException,
            PropertyNotInDBException, PropertyTypeNotExistException, ComputationMethodDoesNotExistException, PropertyAlreadyInDBException {
        Property r = new Property();
        PreparedStatement ps = DatabaseConnector.getInstance().getConn().prepareStatement(
                "SELECT name  FROM " + table + " WHERE name=?");
        ps.setString(1, name);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            throw new PropertyAlreadyInDBException();
        }
        r.setName(name);
        r.setDescription(description);
        r.setType(type);
        r.setValueType(valueType);
        r.setPropertySource(source);
        r.setMultiple(multiple);
        r.setIsDefault(isDefault);
        if (!source.equals(PropertySource.Parameter)) {
            if (computationMethod != null) {
                r.setComputationMethod(computationMethod);
                r.setComputationMethodParameters(computationMethodParameters);
            } else {
                r.setRegularExpression(regularExpression);
            }
        }
        r.setNew();
        save(r);
        cache.cache(r);
        return r;
    }

    /**
     * Returns and caches (if necessary) the requested Property object
     * @param id of the requested Property object
     * @return the requested Property object
     * @throws NoConnectionToDBException
     * @throws SQLException
     * @throws PropertyNotInDBException
     */
    public static Property getById(int id) throws NoConnectionToDBException, SQLException, PropertyNotInDBException, PropertyTypeNotExistException, IOException, ComputationMethodDoesNotExistException {
        Property res = cache.getCached(id);
        if (res != null) {
            return res;
        } else {
            res = new Property();
            PreparedStatement ps = DatabaseConnector.getInstance().getConn().prepareStatement(
                    "SELECT name, description, propertyType, propertySource ,propertyValueType, multipleOccourence, idComputationMethod, "
                    + "computationMethodParameters, isDefault FROM " + table + " WHERE idProperty=?");
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (!rs.next()) {
                throw new PropertyNotInDBException();
            }
            res.setId(id);
            res.setName(rs.getString(1));
            res.setDescription(rs.getString(2));
            res.setType(rs.getInt(3));
            res.setPropertySource(rs.getInt(4));
            if (!res.getPropertySource().equals(PropertySource.Parameter)) {
                res.setRegularExpression(getRegularExpressions(id));
                res.setValueType(PropertyValueTypeManager.getInstance().getPropertyValueTypeByName(rs.getString(5)));
                res.setMultiple(rs.getBoolean(6));
                if (res.getRegularExpression().isEmpty()) {
                    res.setComputationMethod(ComputationMethodDAO.getById(rs.getInt(7)));
                } else {
                    res.setComputationMethod(null);
                }
                res.setComputationMethodParameters(rs.getString(8));
            } else {
                res.setRegularExpression(new Vector<String>());
                res.setValueType(null);
                res.setMultiple(false);
                res.setComputationMethod(null);
                res.setComputationMethodParameters("");
            }
            res.setIsDefault(rs.getBoolean(9));
            res.setSaved();
            cache.cache(res);
            return res;
        }
    }

    /**
     * Saves the given Property into the database. Dependend on the PersistanteState of
     * the given object a new entry is created, deleted or updated in the database.
     * @param r the Property object to save into the database
     * @throws NoConnectionToDBException
     * @throws SQLException
     */
    public static void save(Property r) throws NoConnectionToDBException, SQLException, PropertyIsUsedException, PropertyTypeDoesNotExistException,
            IOException, PropertyNotInDBException, PropertyTypeNotExistException, ComputationMethodDoesNotExistException {
        if (r.isDeleted()) {
            if (r.getType().equals(PropertyType.InstanceProperty)) {
                InstanceHasPropertyDAO.removeAllOfProperty(r);
            } else {
                ExperimentResultHasPropertyDAO.removeAllOfProperty(r);
            }
            PreparedStatement ps = DatabaseConnector.getInstance().getConn().prepareStatement(deleteQuery);
            ps.setInt(1, r.getId());
            ps.executeUpdate();
            ps.close();
            cache.remove(r);
        } else if (r.isModified()) {
            PreparedStatement ps = DatabaseConnector.getInstance().getConn().prepareStatement(updateQuery);
            ps.setString(1, r.getName());
            setRegularExpressions(r.getRegularExpression(), r.getId());
            ps.setString(2, r.getDescription());
            ps.setInt(3, r.getPropertyTypeDBRepresentation());
            ps.setInt(4, r.getPropertySourceDBRepresentation());
            if (r.getPropertySource().equals(PropertySource.Parameter)) {
                ps.setNull(5, java.sql.Types.NULL);
                ps.setNull(6, java.sql.Types.NULL);
            } else {
                ps.setString(5, r.getPropertyValueType().getName());
                ps.setBoolean(6, r.isMultiple());
            }
            if (r.getComputationMethod() != null) {
                ps.setInt(7, r.getComputationMethod().getId());
            } else {
                ps.setNull(7, java.sql.Types.NULL);
            }
            ps.setString(8, r.getComputationMethodParameters());
            ps.setInt(9, r.getId());
            ps.executeUpdate();
            ps.close();
            r.setSaved();
        } else if (r.isNew()) {
            PreparedStatement ps = DatabaseConnector.getInstance().getConn().prepareStatement(insertQuery, PreparedStatement.RETURN_GENERATED_KEYS);
            ps.setString(1, r.getName());
            ps.setString(2, r.getDescription());
            ps.setInt(3, r.getPropertyTypeDBRepresentation());
            ps.setInt(4, r.getPropertySourceDBRepresentation());
            if (r.getPropertySource().equals(PropertySource.Parameter)) {
                ps.setNull(5, java.sql.Types.NULL);
                ps.setNull(6, java.sql.Types.NULL);
            } else {
                ps.setString(5, r.getPropertyValueType().getName());
                ps.setBoolean(6, r.isMultiple());
            }
            if (r.getComputationMethod() != null) {
                ps.setInt(7, r.getComputationMethod().getId());
            } else {
                ps.setNull(7, java.sql.Types.NULL);
            }
            ps.setString(8, r.getComputationMethodParameters());
            ps.setBoolean(9, r.IsDefault());
            ps.executeUpdate();
            ResultSet generatedKeys = ps.getGeneratedKeys();
            if (generatedKeys.next()) {
                r.setId(generatedKeys.getInt(1));
            }
            generatedKeys.close();
            ps.close();
            r.setSaved();
            if (r.getRegularExpression() != null) {
                setRegularExpressions(r.getRegularExpression(), r.getId());
            }
        }
    }

    /**
     *
     * @return a Vector of all Property Objects which are in the database.
     * @throws NoConnectionToDBException
     * @throws SQLException
     * @throws PropertyNotInDBException
     * @throws PropertyTypeNotExistException
     * @throws IOException
     */
    public static Vector<Property> getAll() throws NoConnectionToDBException, SQLException, PropertyNotInDBException, PropertyTypeNotExistException, IOException, ComputationMethodDoesNotExistException {
        Vector<Property> res = new Vector<Property>();
        PreparedStatement ps = DatabaseConnector.getInstance().getConn().prepareStatement(
                "SELECT idProperty "
                + "FROM " + table);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            res.add(PropertyDAO.getById(rs.getInt(1)));
        }
        return res;
    }

    /**
     * Removes the given property from the cache and database.
     * @param solverProperty to remove
     * @throws NoConnectionToDBException
     * @throws SQLException
     * @throws PropertyIsUsedException
     * @throws PropertyTypeDoesNotExistException
     */
    public static void remove(Property toRemove) throws NoConnectionToDBException, SQLException, PropertyIsUsedException, PropertyTypeDoesNotExistException,
            IOException, PropertyNotInDBException, PropertyTypeNotExistException, ComputationMethodDoesNotExistException {
        toRemove.setDeleted();
        save(toRemove);
    }

    /**
     * 
     * @return all Property objects with the PropertyType InstanceProperty
     * @throws NoConnectionToDBException
     * @throws SQLException
     * @throws PropertyNotInDBException
     * @throws PropertyTypeNotExistException
     * @throws IOException
     */
    public static Vector<Property> getAllInstanceProperties() throws NoConnectionToDBException, SQLException, PropertyNotInDBException,
            PropertyTypeNotExistException, IOException, ComputationMethodDoesNotExistException {
        Vector<Property> res = new Vector<Property>();
        PreparedStatement ps = DatabaseConnector.getInstance().getConn().prepareStatement(
                "SELECT idProperty FROM " + table + " WHERE PropertyType=0;");
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            res.add(PropertyDAO.getById(rs.getInt("idProperty")));
        }
        rs.close();
        ps.close();
        return res;
    }

    /**
     *
     * @return all Property objects with the PropertyType ResultProperty
     * @throws NoConnectionToDBException
     * @throws SQLException
     * @throws PropertyNotInDBException
     * @throws PropertyTypeNotExistException
     * @throws PropertyTypeNotExistException
     * @throws IOException
     */
    public static Vector<Property> getAllResultProperties() throws NoConnectionToDBException, SQLException, PropertyNotInDBException,
            PropertyTypeNotExistException, PropertyTypeNotExistException, IOException, ComputationMethodDoesNotExistException {
        Vector<Property> res = new Vector<Property>();
        PreparedStatement ps = DatabaseConnector.getInstance().getConn().prepareStatement(
                "SELECT idProperty FROM " + table + " WHERE PropertyType=1;");
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            res.add(PropertyDAO.getById(rs.getInt("idProperty")));
        }
        rs.close();
        ps.close();
        return res;
    }

    /**
     *
     * @param name of the requested Property
     * @return The property with the given name from the database
     * @throws NoConnectionToDBException
     * @throws SQLException
     * @throws PropertyNotInDBException
     * @throws PropertyNotInDBException
     * @throws PropertyTypeNotExistException
     * @throws IOException
     */
    public static Property getByName(String name) throws NoConnectionToDBException, SQLException, PropertyNotInDBException,
            PropertyNotInDBException, PropertyTypeNotExistException, IOException, ComputationMethodDoesNotExistException {
        PreparedStatement ps = DatabaseConnector.getInstance().getConn().prepareStatement(
                "SELECT idProperty FROM " + table + " WHERE name=?;");
        ps.setString(1, name);
        ResultSet rs = ps.executeQuery();
        rs.next();
        return getById(rs.getInt("idProperty"));
    }

    private static Vector<String> getRegularExpressions(int id) throws NoConnectionToDBException, SQLException {
        PreparedStatement ps = DatabaseConnector.getInstance().getConn().prepareStatement(
                "SELECT regexpr FROM PropertyRegExp WHERE idProperty=?;");
        ps.setInt(1, id);
        ResultSet rs = ps.executeQuery();
        Vector<String> res = new Vector<String>();
        while (rs.next()) {
            res.add(rs.getString(1));
        }
        rs.close();
        ps.close();
        return res;
    }

    private static void setRegularExpressions(Vector<String> regularExpression, int id) throws NoConnectionToDBException, SQLException {
        PreparedStatement ps = DatabaseConnector.getInstance().getConn().prepareStatement(
                "DELETE FROM PropertyRegExp WHERE idProperty=?;");
        ps.setInt(1, id);
        ps.executeUpdate();
        ps = DatabaseConnector.getInstance().getConn().prepareStatement(
                "INSERT INTO PropertyRegExp (idProperty, regexpr) VALUES (?, ?);");
        for (int i = 0; i < regularExpression.size(); i++) {
            ps.setInt(1, id);
            ps.setString(2, regularExpression.get(i));
            ps.executeUpdate();
        }
    }

    /**
     * Exports the  Property object into a file, located in the given path. The name of the file
     * is the name of the property.
     * @param property Property Object to export
     * @param path Path in which the Property have to be exported.
     * @throws FileNotFoundException
     * @throws IOException 
     */
    public static void exportProperty(Property property, String path) throws FileNotFoundException, IOException {
        File f = new File(path + "/" + property.getName());
        OutputStream output = new FileOutputStream(f);
        ObjectOutputStream o = new ObjectOutputStream(output);
        o.writeObject(property);
    }

    /**
     * Import the properties included in the given files.
     * @param files
     * @throws FileNotFoundException
     * @throws IOException
     * @throws ClassNotFoundException
     * @throws NoConnectionToDBException
     * @throws SQLException
     * @throws ComputationMethodAlreadyExistsException
     * @throws NoComputationMethodBinarySpecifiedException
     * @throws PropertyIsUsedException
     * @throws PropertyTypeDoesNotExistException
     * @throws PropertyNotInDBException
     * @throws PropertyTypeNotExistException
     * @throws ComputationMethodDoesNotExistException 
     */
    public static void importProperty(File[] files) throws FileNotFoundException, IOException, ClassNotFoundException, NoConnectionToDBException, SQLException, NoComputationMethodBinarySpecifiedException, PropertyIsUsedException, PropertyTypeDoesNotExistException, PropertyNotInDBException, PropertyTypeNotExistException, ComputationMethodDoesNotExistException, ComputationMethodSameNameAlreadyExists {
        for (int i = 0; i < files.length; i++) {
            InputStream input = new FileInputStream(files[i]);
            ObjectInputStream in = new ObjectInputStream(input);
            Property prop = (Property) in.readObject();
            ComputationMethod compMeth = prop.getComputationMethod();
            if (compMeth != null) {

                compMeth.isNew();
                try {
                    ComputationMethodDAO.save(compMeth);
                } catch (ComputationMethodAlreadyExistsException ex) {
                    prop.setComputationMethod(ComputationMethodDAO.getByName(compMeth.getName()));
                } catch (ComputationMethodSameMD5AlreadyExists ex) {
                    prop.setComputationMethod(ComputationMethodDAO.getByMD5(compMeth.getMd5()));
                }

            }
            prop.isNew();
            PropertyDAO.save(prop);
        }
    }

    public static void clearCache() {
        cache.clear();
    }

    public static void init() throws SQLException, PropertyTypeNotExistException, IOException, NoConnectionToDBException, ComputationMethodDoesNotExistException, PropertyNotInDBException {
        InstanceHasPropertyDAO.init();
        PreparedStatement ps = DatabaseConnector.getInstance().getConn().prepareStatement(
                "SELECT idProperty, name, description, propertyType, propertySource ,propertyValueType, multipleOccourence, idComputationMethod, "
                + "computationMethodParameters, isDefault FROM " + table);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            Property res = new Property();
            res.setId(rs.getInt(1));
            res.setName(rs.getString(2));
            res.setDescription(rs.getString(3));
            res.setType(rs.getInt(4));
            res.setPropertySource(rs.getInt(5));
            if (!res.getPropertySource().equals(PropertySource.Parameter)) {
                res.setRegularExpression(getRegularExpressions(res.getId()));
                res.setValueType(PropertyValueTypeManager.getInstance().getPropertyValueTypeByName(rs.getString(6)));
                res.setMultiple(rs.getBoolean(7));
                if (res.getRegularExpression().isEmpty()) {
                    res.setComputationMethod(ComputationMethodDAO.getById(rs.getInt(8)));
                } else {
                    res.setComputationMethod(null);
                }
                res.setComputationMethodParameters(rs.getString(9));
            } else {
                res.setRegularExpression(new Vector<String>());
                res.setValueType(null);
                res.setMultiple(false);
                res.setComputationMethod(null);
                res.setComputationMethodParameters("");
            }
            res.setIsDefault(rs.getBoolean(10));
            res.setSaved();
            cache.cache(res);
            return;
        }

    }

    /**
     * Extract and adds the property data from the csvfile to the database.
     * @param selected Relation between found csvfile properties and existing Property objects.
     * @param overwrite Overwrite existing property data?
     * @param csvFile 
     * @param task
     * @throws IOException
     * @throws SQLException
     * @throws NoConnectionToDBException
     * @throws PropertyTypeNotExistException
     * @throws ComputationMethodDoesNotExistException
     * @throws InstanceHasPropertyNotInDBException
     * @throws InstancesNotFoundException 
     */
    public static void importCSV(Set<Entry<Property, String>> selected, Boolean overwrite, File csvFile, Tasks task) throws IOException, SQLException, NoConnectionToDBException, PropertyTypeNotExistException, ComputationMethodDoesNotExistException, InstanceHasPropertyNotInDBException, InstancesNotFoundException {
        task.setCancelable(true);
        task.setOperationName("Import properties from csv file");

        ArrayList<String[]> instanceError = new ArrayList<String[]>();

        BufferedReader br = new BufferedReader(new FileReader(csvFile));
        String firstLine = br.readLine();
        ArrayList<String> fstLine = new ArrayList<String>(Arrays.asList(firstLine.split(",")));
        ArrayList<Property> head = generateCSVImportHead(fstLine, selected);

        if (head == null) {
            task.cancel(true);
            return;
        }

        Boolean hasMD5 = false;;
        if (fstLine.get(1).equals("md5") || fstLine.get(1).equals("MD5") || fstLine.get(1).equals("Md5")) {
            hasMD5 = true;
        }

        //  To reduces the connection requests to the Databaseconnector during the creation an save Process of InstanceHasProperty Objects
        PreparedStatement psNew = DatabaseConnector.getInstance().getConn().prepareStatement(InstanceHasPropertyDAO.getInsertQuery(), PreparedStatement.RETURN_GENERATED_KEYS);
        PreparedStatement psMod = DatabaseConnector.getInstance().getConn().prepareStatement(InstanceHasPropertyDAO.getUpdateQuery());
       
        String line = br.readLine();

        while (line != null) {

            ArrayList<String> tmpLine = new ArrayList<String>(Arrays.asList(line.split(",")));

            Instance tmp;
            int count = 0;
            if (hasMD5) {
                tmp = InstanceDAO.getByMd5AndName(tmpLine.get(0), tmpLine.get(1));
                count = 2;
            } else {
                tmp = InstanceDAO.getByName(tmpLine.get(0));
                count = 1;
            }

            if (tmp == null) {
                if (hasMD5) {
                    instanceError.add(new String[]{tmpLine.get(0), tmpLine.get(1)});
                } else {
                    instanceError.add(new String[]{tmpLine.get(0), ""});
                }
            } else {
                // import property values
                for (int i = count; i < tmpLine.size(); i++) // Get the matching Instance
                {
                    if (head.get((i - count)) != null) {
                        InstanceHasPropertyDAO.createInstanceHasInstanceProperty(tmp, head.get((i - count)), tmpLine.get(i), overwrite, psNew, psMod);
                    }
                }
            }
            line = br.readLine();
        }
        if (!instanceError.isEmpty()) {
            throw new InstancesNotFoundException(instanceError);
        }

    }

    /**
     * 
     * @param fstLine The first line of the csvfile to import.
     * @param Set of the relation between existing Property objects and properties from the csv File
     * @return ArrayList<Property>, which contains in order of the first line of the csvFile, the matching of csvFile 
     * columns an Property objects. If a property of the csv file is without a related Propert object, an null entry is added to the list.
     * 
     */
    private static ArrayList<Property> generateCSVImportHead(ArrayList<String> fstLine, Set<Entry<Property, String>> selected) {
        ArrayList<Property> head = new ArrayList<Property>();
        if (fstLine.size() < 2) {
            return null;
        }

        int count = 0;
        if (fstLine.get(0).equals("Name") || fstLine.get(0).equals("name") || fstLine.get(0).equals("NAME")) {
            count++;
        } else {
            return null;
        }

        if (fstLine.get(1).equals("md5") || fstLine.get(1).equals("MD5") || fstLine.get(1).equals("Md5")) {
            count++;
        }

        for (int i = count; i < fstLine.size(); i++) {
            Boolean tmp = false;
            for (Entry ent : selected) {
                if (ent.getValue().equals(fstLine.get(i))) {
                    head.add((Property) ent.getKey());
                    tmp = true;
                    break;
                }

            }
            if (!tmp) {
                head.add(null);
            }

        }
        return head;
    }

    /**
     * 
     * @return All InstanceProperty objects, without the objects whose propertySource is CSVImport.
     * @throws SQLException
     * @throws NoConnectionToDBException
     * @throws PropertyNotInDBException
     * @throws PropertyTypeNotExistException
     * @throws IOException 
     */
    public static Vector<Property> getAllInstancePropertiesWithoutCSVImport() throws SQLException, NoConnectionToDBException, PropertyNotInDBException, PropertyTypeNotExistException, IOException {
        Vector<Property> res = new Vector<Property>();
        PreparedStatement ps = DatabaseConnector.getInstance().getConn().prepareStatement(
                "SELECT idProperty FROM " + table + " WHERE PropertyType=0 AND propertySource !=8;");
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            res.add(PropertyDAO.getById(rs.getInt("idProperty")));
        }
        rs.close();
        ps.close();
        return res;
    }
}
