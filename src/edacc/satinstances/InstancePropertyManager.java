/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edacc.satinstances;

import edacc.model.DatabaseConnector;
import edacc.model.InstanceProperty;
import edacc.model.NoConnectionToDBException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

/**
 *
 * @author dgall
 */
public class InstancePropertyManager {

    private static InstancePropertyManager instance;
    private Hashtable<String, InstanceProperty> instanceProperties;
    private String table = "InstanceProperty";
    private String insertQuery = "INSERT INTO " + table + " (name, valueType, computationClass) VALUES (?, ?, ?);";

    public static InstancePropertyManager getInstance() throws IOException, NoConnectionToDBException, SQLException {
        if (instance == null) {
            instance = new InstancePropertyManager();
        }
        return instance;
    }

    /**
     * Returns the object of the Property with the given name.
     * @param name
     * @return
     */
    public InstanceProperty getByName(String name) {
        return instanceProperties.get(name);
    }

    /**
     * Constructor of the InstancePropertyManager
     *
     * @throws IOException
     */
    private InstancePropertyManager() throws IOException, NoConnectionToDBException, SQLException {
        this.instanceProperties = loadInstanceProperties();
    }

    /**
     * Returns all loaded Property objects.
     *
     * @return
     */
    public Vector<InstanceProperty> getAll() {
        return new Vector<InstanceProperty>(instanceProperties.values());
    }

    /**
     * Loads all InstanceProperty classes from the db and returns a
     * Hashtable with the name as key and an object of the Property as value.
     *
     * @throws IOException
     *             if an error occurs while loading the class files from the db to the filesystem.
     */
    private Hashtable<String, InstanceProperty> loadInstanceProperties() throws IOException, NoConnectionToDBException, SQLException {
        final String query = "SELECT * FROM " + table;
        PreparedStatement ps = DatabaseConnector.getInstance().getConn().prepareStatement(query);
        ResultSet rs = ps.executeQuery();
        LinkedList<File> files = new LinkedList<File>();
        LinkedList<Class<Property>> properties = new LinkedList<Class<Property>>();
        // the tmp directory (base file for all tmp files created in the loading process)
        File base = new File("touch").getAbsoluteFile().getParentFile();
        while (rs.next()) {
            // create a new file in the tmp dir (base) with the right package
            // structure (eg. edacc/satinstances/Foo.class for the class edacc.satinstances.Foo.class)
            File f = new File(rs.getString("computationClassFileName"));
            f.getParentFile().mkdirs();

            FileOutputStream out = new FileOutputStream(f);
            InputStream in = rs.getBinaryStream("computationClass");
            int len = 0;
            byte[] buffer = new byte[256 * 1024];
            while ((len = in.read(buffer)) > -1) {
                out.write(buffer, 0, len);
            }
            out.close();
            in.close();
            files.add(f);
        }

        // Create a new URLClassLoader for loading the Properties with the
        // ClassLoader of the Property.class as parent (which is important because
        // the Classcast won't work with any other ClassLoader)
        ClassLoader cl = new URLClassLoader(new URL[]{base.toURI().toURL()},
                Property.class.getClassLoader());
        List<Class<InstanceProperty>> classes = getClassesFromFiles(files, cl);

        // clean up temporary files
        for (File f : files) {
            cleanupParents(base, f);
        }
        return createPropertyObjects(classes);
    }

    private void cleanupParents(File base, File f) {
        if (!base.getAbsolutePath().equals(f.getAbsolutePath())) {
            f.delete();
            cleanupParents(base.getAbsoluteFile(), f.getAbsoluteFile().getParentFile());
        }
    }

    /**
     * Small utility method to convert a File array to an URL array.
     *
     * @param files
     * @return the new {@link URL} array
     * @throws MalformedURLException
     */
    /*    private URL[] fileListToURLArray(List<File> files)
    throws MalformedURLException {
    URL[] urls = new URL[files.size()];
    int i = 0;
    for (File f : files) {
    System.out.println(f.getParentFile().toURI().toURL());
    urls[i++] = f.getParentFile().toURI().toURL();
    }
    return urls;
    } */
    /**
     * Extracts all pluggable PropertyValueType classes from a list of files.
     *
     * @param files
     * @param cl
     * @return List
     * @throws IOException
     */
    @SuppressWarnings("unchecked")
    private List<Class<InstanceProperty>> getClassesFromFiles(List<File> files, ClassLoader cl)
            throws IOException {
        List<Class<InstanceProperty>> classes = new ArrayList<Class<InstanceProperty>>();
        for (File f : files) {
            if (f.getName().toLowerCase().endsWith(".class")) {
                try {
                    System.out.println("Absolute Path: " + f.getAbsolutePath());
                    Class<?> cls = cl.loadClass("edacc.satinstances." + f.getName().substring(0,
                            f.getName().length() - 6).replace('/', '.'));
                    if (isPluggableClass(cls)) {
                        classes.add((Class<InstanceProperty>) cls);
                    }
                } catch (ClassNotFoundException e) {
                    System.err.println("Can't load Class " + f.getName());
                    e.printStackTrace();
                }
            }
        }
        return classes;
    }

    /**
     * Checks if a given class is pluggable, which means: has the type PropertyValueType.
     *
     * @param cls
     * @return true if class is pluggable
     */
    private boolean isPluggableClass(Class<?> cls) {
        return cls.getSuperclass().equals(PropertyValueType.class);
    }

    /**
     * This method creates the PropertyValueType objects of all found ProeprtyValueTypes and adds them
     * to the list.
     */
    private Hashtable<String, InstanceProperty> createPropertyObjects(
            List<Class<InstanceProperty>> propertyClasses) {
        Hashtable<String, InstanceProperty> properties = new Hashtable<String, InstanceProperty>(
                propertyClasses.size());
        for (Class<InstanceProperty> propertyClass : propertyClasses) {
            try {
                InstanceProperty property = propertyClass.newInstance();
                properties.put(property.getName(), property);
            } catch (InstantiationException e) {
                System.err.println("Can't instantiate Property: "
                        + propertyClass.getName());
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                System.err.println("IllegalAccess for Property: "
                        + propertyClass.getName());
                e.printStackTrace();
            }
        }
        return properties;
    }

    /**
     * Creates a new Property from the given File and adds it to the database
     * @param file
     * @throws IOException
     * @throws NoConnectionToDBException
     * @throws SQLException
     * @author dgall
     */
    public void createNewPropertyValueType(File file) throws IOException, NoConnectionToDBException, SQLException {
        // TODO add
    }

}
