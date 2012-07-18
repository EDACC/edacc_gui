/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edacc.satinstances;

import edacc.EDACCApp;
import edacc.model.ComputationMethod;
import edacc.model.ComputationMethodAlreadyExistsException;
import edacc.model.ComputationMethodDAO;
import edacc.model.ComputationMethodSameMD5AlreadyExists;
import edacc.model.ComputationMethodSameNameAlreadyExists;
import edacc.model.NoComputationMethodBinarySpecifiedException;
import edacc.model.NoConnectionToDBException;
import edacc.model.Property;
import edacc.model.PropertyDAO;
import edacc.model.PropertyIsUsedException;
import edacc.model.PropertyType;
import edacc.model.PropertyTypeDoesNotExistException;
import edacc.properties.PropertySource;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.SQLException;
import java.util.ArrayList;
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
public class DefaultPropertiesManager {

    private static DefaultPropertiesManager instance;

    public static DefaultPropertiesManager getInstance() {
        if (instance == null) {
            instance = new DefaultPropertiesManager();
        }
        return instance;
    }

    public void addDefaultToDB() throws NoConnectionToDBException, SQLException, IOException, ComputationMethodAlreadyExistsException, NoComputationMethodBinarySpecifiedException, FileNotFoundException, ComputationMethodSameNameAlreadyExists, ComputationMethodSameMD5AlreadyExists, PropertyIsUsedException, PropertyTypeDoesNotExistException {
        addSATPC();
    }

    private void addSATPC() throws FileNotFoundException, IOException, NoConnectionToDBException, SQLException, ComputationMethodAlreadyExistsException, NoComputationMethodBinarySpecifiedException, ComputationMethodSameNameAlreadyExists, ComputationMethodSameMD5AlreadyExists, PropertyIsUsedException, PropertyTypeDoesNotExistException {
        ComputationMethod satpc = new ComputationMethod();
        satpc.setName("satpc");
        satpc.setDescription("A generic program that can calculate a lot of instance properties.");
        satpc.setBinaryName("SATPC.jar");


        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(EDACCApp.class).getContext().getResourceMap();

        File file = new File(edacc.experiment.Util.getPath());
        file = new File(file, "tmp");
        if (file.exists() && !file.isDirectory()) {
            if (!file.delete()) {
                EDACCApp.getLogger().logError("Could not remove file: " + file.getPath());
                return;
            }
        }
        if (!file.exists() && !file.mkdir()) {
            EDACCApp.getLogger().logError("Could not create directory: " + file.getPath());
            return;
        }
        file = new File(file, satpc.getBinaryName());

        byte[] buffer = new byte[2048];
        int read = 0;

        InputStream is = resourceMap.getClassLoader().getResourceAsStream("edacc/resources/SATPC.jar");
        FileOutputStream os = new FileOutputStream(file);
        while ((read = is.read(buffer)) > 0) {
            os.write(buffer, 0, read);
        }
        satpc.setBinary(file);

        ComputationMethodDAO.save(satpc);

        addSATPCProperties(file, satpc);

    }

    private void addSATPCProperties(File f, ComputationMethod satpc) throws IOException, NoConnectionToDBException, SQLException, PropertyIsUsedException, PropertyTypeDoesNotExistException {
        URL url = f.toURI().toURL();
        URL[] urls = new URL[]{url};
        List<Class<SATInstanceProperty>> instancePropertyClasses = extractClassesFromJAR(f, new URLClassLoader(urls, SATInstanceProperty.class.getClassLoader()));
        List<Property> properties = createSATPCPropertyObjects(instancePropertyClasses, satpc);
        System.out.println("Hi!");
        for (Property p : properties) {
            System.out.println("prop: " + p.getName());
            PropertyDAO.save(p);
        }

    }

    /**
     * This method creates the PropertyValueType objects of all found ProeprtyValueTypes and adds them
     * to the list.
     */
    private List<Property> createSATPCPropertyObjects(
            List<Class<SATInstanceProperty>> propertyClasses, ComputationMethod satpc) {
        List<Property> properties = new LinkedList<Property>();
        for (Class<SATInstanceProperty> propertyClass : propertyClasses) {
            try {
                SATInstanceProperty pInst = propertyClass.newInstance();
                Property p = new Property();
                p.setName(propertyClass.getSimpleName());
                p.setDescription(pInst.getName());
                p.setComputationMethod(satpc);
                p.setComputationMethodParameters(propertyClass.getSimpleName());
                p.setPropertyValueTypeName(pInst.getPropertyValueType().getName());
                p.setType(PropertyType.InstanceProperty);
                p.setSource(PropertySource.Instance);
                p.setIsDefault(true);
                properties.add(p);
            } catch (InstantiationException e) {
                /*System.err.println("Can't instantiate Property: "
                        + propertyClass.getName());
                e.printStackTrace();*/
            } catch (IllegalAccessException e) {
                System.err.println("IllegalAccess for PropertyValueType: "
                        + propertyClass.getName());
                e.printStackTrace();
            }
        }
        System.out.println("PROPS: " + properties.size());
        return properties;
    }

    /**
     * Extracts all pluggable PropertyValueType classes from a list of files.
     *
     * @param files
     * @param cl
     * @return List
     * @throws IOException
     */
    /*@SuppressWarnings("unchecked")
    private List<Class<SATInstanceProperty>> getClassesFromFiles(List<File> files, ClassLoader cl)
    throws IOException {
    List<Class<SATInstanceProperty>> classes = new ArrayList<Class<SATInstanceProperty>>();
    for (File f : files) {
    if (f.getName().toLowerCase().endsWith(".class")) {
    try {
    //System.out.println("Absolute Path: " + f.getAbsolutePath());
    Class<?> cls = cl.loadClass("edacc.satinstances." + f.getName().substring(0,
    f.getName().length() - 6).replace('/', '.'));
    if (isPluggableClass(cls)) {
    classes.add((Class<SATInstanceProperty>) cls);
    }
    } catch (ClassNotFoundException e) {
    System.err.println("Can't load Class " + f.getName());
    e.printStackTrace();
    }
    }
    }
    return classes;
    }*/
    /**
     * Checks if a given class is pluggable, which means: has the type PropertyValueType.
     *
     * @param cls
     * @return true if class is pluggable
     */
    private boolean isPluggableClass(Class<?> cls) {
        System.out.println(Modifier.isAbstract(cls.getModifiers()));
        for (Class<?> i : cls.getInterfaces()) {;
            if (i.equals(SATInstanceProperty.class)) {
                return true;
            }
        }
        if (cls.getSuperclass() == null)
            return false;
        return isPluggableClass(cls.getSuperclass()) && !Modifier.isAbstract(cls.getModifiers());
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
    private File getFileOfJarEntry(JarFile jf, JarEntry ent, File root) throws IOException {
        File input = new File(ent.getName());
        BufferedInputStream bis = new BufferedInputStream(jf.getInputStream(ent));
        File dir = new File(root.getParent());
        dir.mkdirs();
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(input));
        for (int c; (c = bis.read()) != -1;) {
            bos.write((byte) c);
        }
        bos.close();
        return input;
    }

    @SuppressWarnings("unchecked")
    private List<Class<SATInstanceProperty>> extractClassesFromJAR(File jar, ClassLoader cl) throws IOException {

        List<Class<SATInstanceProperty>> classes = new ArrayList<Class<SATInstanceProperty>>();
        JarInputStream jaris = new JarInputStream(new FileInputStream(jar));
        JarEntry ent = null;
        while ((ent = jaris.getNextJarEntry()) != null) {
            if (ent.getName().toLowerCase().endsWith(".class")) {
                try {
                    Class<?> cls = cl.loadClass(ent.getName().substring(0, ent.getName().length() - 6).replace('/', '.'));
                    if (isPluggableClass(cls)) {
                        classes.add((Class<SATInstanceProperty>) cls);
                    }
                } catch (ClassNotFoundException e) {
                    System.err.println("Can't load Class " + ent.getName());
                    e.printStackTrace();
                }
            }
        }
        jaris.close();
        System.out.println(classes.size());
        return classes;
    }
}
