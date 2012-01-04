package edacc.model;

import java.io.File;
import java.util.HashMap;

public class Instance extends BaseModel implements IntegerPKModel {

    @Override
    public int hashCode() {

        return 31 + id;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj != null && obj instanceof Instance) {
            Instance o = (Instance) obj;
            return (o.name.equals(name) && o.md5.equals(md5) && o.id == id);
        }
        return false;
    }

    public int getId() {
        return id;
    }

    protected void setId(int id) {
        this.id = id;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    protected File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public HashMap<Integer, InstanceHasProperty> getPropertyValues() {
        return propertyValues;
    }

    public void setPropertyValues(HashMap<Integer, InstanceHasProperty> instancePropertyValues) {
        this.propertyValues = instancePropertyValues;
    }

    @Override
    public String toString() {
        return name;
    }
    
    private int id;
    private String name;
    private String md5;
    private File file;
    private HashMap<Integer, InstanceHasProperty> propertyValues;
}
