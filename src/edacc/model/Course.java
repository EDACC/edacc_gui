package edacc.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author simon
 */
public class Course extends BaseModel implements Serializable {

    private int initialLength;
    private List<InstanceSeed> instanceSeedList;
    private int modifiedIndex;

    public Course() {
        super();
        initialLength = 0;
        modifiedIndex = 0;
        instanceSeedList = new ArrayList<InstanceSeed>();
    }

    protected Course(Course course) {
        this();
        initialLength = course.initialLength;
        modifiedIndex = course.modifiedIndex;
        for (InstanceSeed is : course.instanceSeedList) {
            instanceSeedList.add(new InstanceSeed(is.instance, is.seed));
        }
    }
    
    protected void setInitialLength(int initialLength) {
        this.initialLength = initialLength;
    }

    public int getInitialLength() {
        return initialLength;
    }

    public void add(InstanceSeed elem) {
        instanceSeedList.add(new InstanceSeed(elem.instance, elem.seed));
        if (this.isSaved()) {
            modifiedIndex = instanceSeedList.size()-1;
            this.setModified();
        }
    }

    public InstanceSeed get(int index) {
        return new InstanceSeed(instanceSeedList.get(index).instance, instanceSeedList.get(index).seed);
    }

    protected int getModifiedIndex() {
        return modifiedIndex;
    }
    
    public int getLength() {
        return instanceSeedList.size();
    }

    public void clear() {
        if (getModifiedIndex() != 0) {
            throw new IllegalArgumentException("Cannot change a saved course.");
        }
        instanceSeedList.clear();
    }
    
    public List<InstanceSeed> getInstanceSeedList() {
        return instanceSeedList;
    }
}
