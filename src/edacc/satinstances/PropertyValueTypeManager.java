/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edacc.satinstances;

import edacc.EDACCApp;
import edacc.model.DatabaseConnector;
import edacc.model.NoConnectionToDBException;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarInputStream;

/**
 *
 * @author dgall
 */
public class PropertyValueTypeManager {

    private static PropertyValueTypeManager instance;
    private Hashtable<String, PropertyValueType<?>> propertyTypes;
    private String table = "PropertyValueType";
    private String insertQuery = "INSERT INTO " + table + " (name, typeClass, isDefault, typeClassFileName) VALUES (?, ?, ?, ?);";
    private String deleteQuery = "DELETE FROM " + table + " WHERE name=?;";
    public static PropertyValueTypeManager getInstance() throws IOException, NoConnectionToDBException, SQLException {
        if (instance == null) {
            instance = new PropertyValueTypeManager();
        }
        return instance;
    }

    /**
     * Returns the object of the PropertyValueType with the given name.
     * @param name
     * @return
     */
    public PropertyValueType<?> getPropertyValueTypeByName(String name) {
        return propertyTypes.get(name);
    }

    /**
     * Constructor of the PropertyValueTypeManager
     *
     * @throws IOException
     */
    private PropertyValueTypeManager() throws IOException, NoConnectionToDBException, SQLException {
        this.propertyTypes = loadPropertyValueTypes();
    }

    /**
     * Returns all loaded PropertyValueType objects.
     *
     * @return 
     */
    public Vector<PropertyValueType<?>> getAll() {
        return new Vector<PropertyValueType<?>>(propertyTypes.values());
    }

    /**
     * Loads all PropertyValueType classes from the db and returns a
     * Hashtable with the name as key and an object of the PropertyValueType as value.
     *
     * @throws IOException
     *             if an error occurs while loading the class files from the db to the filesystem.
     */
    private Hashtable<String, PropertyValueType<?>> loadPropertyValueTypes() throws IOException, NoConnectionToDBException, SQLException {
        final String query = "SELECT * FROM PropertyValueType";
        PreparedStatement ps = DatabaseConnector.getInstance().getConn().prepareStatement(query);
        ResultSet rs = ps.executeQuery();
        LinkedList<File> files = new LinkedList<File>();
        LinkedList<Class<PropertyValueType<?>>> propertyValueTypes = new LinkedList<Class<PropertyValueType<?>>>();
        // the tmp directory (base file for all tmp files created in the loading process)
        File base = new File("touch").getAbsoluteFile().getParentFile();
        while (rs.next()) {
            // create a new file in the tmp dir (base) with the right package
            // structure (eg. edacc/satinstances/Foo.class for the class edacc.satinstances.Foo.class)
            File f = new File(base.getPath() + "/" + rs.getString("typeClassFileName"));
            f.getParentFile().mkdirs();

            FileOutputStream out = new FileOutputStream(f);
            InputStream in = rs.getBinaryStream("typeClass");
            int len = 0;
            byte[] buffer = new byte[256 * 1024];
            while ((len = in.read(buffer)) > -1) {
                out.write(buffer, 0, len);
            }
            out.close();
            in.close();
            files.add(f);
        }

        // Create a new URLClassLoader for loading the PropertyValueTypes with the
        // ClassLoader of the PropertyValueTypes.class as parent (which is important because
        // the Classcast won't work with any other ClassLoader)
        ClassLoader cl = new URLClassLoader(new URL[]{base.toURI().toURL()},
                PropertyValueType.class.getClassLoader());
        List<Class<PropertyValueType<?>>> classes = getClassesFromFiles(files, cl);

        // clean up temporary files
        for (File f : files) {
            cleanupParents(base, f);
        }
        return createPropertyValueTypeObjects(classes);
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
    private List<Class<PropertyValueType<?>>> getClassesFromFiles(List<File> files, ClassLoader cl)
            throws IOException {
        List<Class<PropertyValueType<?>>> classes = new ArrayList<Class<PropertyValueType<?>>>();
        for (File f : files) {
            if (f.getName().toLowerCase().endsWith(".class")) {
                try {
                    System.out.println("Absolute Path: " + f.getAbsolutePath());
                    Class<?> cls = cl.loadClass("edacc.satinstances." + f.getName().substring(0,
                            f.getName().length() - 6).replace('/', '.'));
                    if (isPluggableClass(cls)) {
                        classes.add((Class<PropertyValueType<?>>) cls);
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
    private Hashtable<String, PropertyValueType<?>> createPropertyValueTypeObjects(
            List<Class<PropertyValueType<?>>> propertyTypeClasses) {
        Hashtable<String, PropertyValueType<?>> propertyTypes = new Hashtable<String, PropertyValueType<?>>(
                propertyTypeClasses.size());
        for (Class<PropertyValueType<?>> propertyType : propertyTypeClasses) {
            try {
                PropertyValueType<?> type = propertyType.newInstance();
                propertyTypes.put(type.getName(), type);
            } catch (InstantiationException e) {
                System.err.println("Can't instantiate PropertyValueType: "
                        + propertyType.getName());
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                System.err.println("IllegalAccess for PropertyValueType: "
                        + propertyType.getName());
                e.printStackTrace();
            }
        }
        return propertyTypes;
    }

   /* public static void main(String[] args) throws IOException, NoConnectionToDBException, SQLException, ClassNotFoundException {
        DatabaseConnector.getInstance().connect("localhost", 3306, "root", "EDACC", "s3cret");
        PropertyValueType t = PropertyValueTypeManager.getInstance().getPropertyValueTypeByName("Test");
        System.out.println(t.getName());
        // Create a File object on the root of the directory containing the class file
       /* File file = new File("/home/dgall/Projekte/EDACC/");
        try {
        // Convert File to a URL
        URL url = file.toURL(); // file:/c:/myclasses/
        URL[] urls = new URL[]{url};
        for (URL u : urls)
        System.out.println(u);
        // Create a new class loader with the directory
        ClassLoader cl = new URLClassLoader(urls);
        // Load in the class; MyClass.class should be located in // the directory file:/c:/myclasses/com/mycompany
        Class cls = cl.loadClass("edacc.satinstances.TestProperty");
        System.out.println(cls.getName());
        } catch (MalformedURLException e) {
        } catch (ClassNotFoundException e) { }*/
   // }

    public Vector<String> readNameFromJarFile(File file) throws IOException{
        Vector<String> names = new Vector<String>();
        JarInputStream jaris = new JarInputStream(new FileInputStream(file));
        JarEntry ent = null;
        ent = jaris.getNextJarEntry();
        while ((ent = jaris.getNextJarEntry()) != null) {
            if (ent.getName().toLowerCase().endsWith(".class")) {
                names.add(ent.getName().substring(0, (ent.getName().length() - 6)));
            }
        }
        return names;

    }

    /**
     * Searchs in the given file (jar) for class files with the names given in the toAdd Vector and adds them to
     * the database and cache.
     * @param toAdd the name of the class files to add
     * @param file the file which contains the class files to add (has to be a jar file)
     * @throws IOException
     * @throws NoConnectionToDBException
     * @throws SQLException
     * @author rretz
     */
    public void addPropertyValueTypes(Vector<String> toAdd, File file) throws IOException, NoConnectionToDBException, SQLException {
        JarFile jf = new JarFile(file);
        LinkedList<File> files = new LinkedList<File>();
        JarInputStream jaris = new JarInputStream(new FileInputStream(file));
        JarEntry ent = null;
        File base = new File("touch").getAbsoluteFile().getParentFile();
        ClassLoader cl = new URLClassLoader(new URL[]{base.toURI().toURL()},
                 PropertyValueType.class.getClassLoader());
        /* Checks every JarEntry if it is one of the searched class files.
         * Load and Add the located classes to the database and cache.
        */
        while ((ent = jaris.getNextJarEntry()) != null) {
            for(int i = 0; i < toAdd.size(); i++){
                if (ent.getName().equals(toAdd.get(i)+ ".class")) {
                    toAdd.remove(i);
                    files.add(getFileOfJarEntry(new JarFile(file), ent, file));
                    List<Class<PropertyValueType<?>>> classes = getClassesFromFiles(files, cl);
                    Enumeration<PropertyValueType<?>> enumerationToAdd = createPropertyValueTypeObjects(classes).elements();

                    if(enumerationToAdd.hasMoreElements()){
                        PropertyValueType<?> tmp = enumerationToAdd.nextElement();
                        PreparedStatement ps = DatabaseConnector.getInstance().getConn().prepareStatement(insertQuery);
                        ps.setString(1, tmp.getName());
                        ps.setBinaryStream(2, new FileInputStream(files.getFirst()));
                        ps.setBoolean(3, tmp.isDefault());
                        ps.setString(4, ent.getName());
                        ps.executeUpdate();
                        propertyTypes.put(tmp.getName(), tmp);
                    }
                    files.clear();
                }
            }
            
        }
    }

    /**
     * Creates the file of the given JarEntry out from the given JarFile.
     * @param jf JarFile which contains the JarEntry
     * @param ent JarEntry from which the file is requested
     * @param root File which is the JarFile
     * @return File of the given JarEntry
     * @throws IOException
     * @author rretz
     */
    private File getFileOfJarEntry(JarFile jf, JarEntry ent, File root) throws IOException{
        File input = new File(ent.getName());
        BufferedInputStream bis = new BufferedInputStream(jf.getInputStream(ent));
        File dir = new File(root.getParent());
        dir.mkdirs();
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(input) );
        for ( int c; ( c = bis.read() ) != -1; )
        bos.write( (byte)c );
        bos.close();
        return input;
    }

    /**
     * Removes the given PropertyValueType from the database and cache.
     * @param toRemove the PropertyValueType object to remove.
     * @throws NoConnectionToDBException
     * @throws SQLException
     * @throws PropertyValueTypeInPropertyException
     */
    public void remove(PropertyValueType<?> toRemove) throws NoConnectionToDBException, SQLException, PropertyValueTypeInPropertyException{
        String query = "SELECT InstanceProperty.valueType, SolverProperty.PropertyValueType_name FROM InstanceProperty, SolverProperty;";
        PreparedStatement ps = DatabaseConnector.getInstance().getConn().prepareStatement(query);
        ResultSet rs = ps.executeQuery();
        if(!rs.next()){
            ps = DatabaseConnector.getInstance().getConn().prepareStatement(deleteQuery);
            ps.setString(1, toRemove.getName());
            ps.executeUpdate();
            propertyTypes.remove(toRemove.getName());
        }
        else
            throw new PropertyValueTypeInPropertyException();
    }

    /**
     * Adds the all PropertyValueType classes from the file "defaultPropertyValueTypes" in the lib directory to the Database. This PropertyValueType classes represent the
     * default types of EDACC.
     * @author rretz
     */
    public void addDefaultToDB() throws NoConnectionToDBException, SQLException, IOException {
        File f = new File(EDACCApp.class.getProtectionDomain().getCodeSource().getLocation().getPath());
        String path;
        if (f.isDirectory()) {
            path = f.getPath() + "/edacc/resources/defaultPropertyValueType.jar";
        } else {
            path = f.getParent() + "/edacc/resources/defaultPropertyValueType.jar";
        }
        
        File defaultTypes = new File(path);
        Vector<String> names = readNameFromJarFile(defaultTypes);
        addPropertyValueTypes(names, defaultTypes);     
    }


}
