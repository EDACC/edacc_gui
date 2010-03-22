package edacc.model;

import java.io.File;

public class Instance extends BaseModel {

    @Override
    public int hashCode() {
        return 31 + id;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj != null && obj instanceof Instance) {
            Instance o = (Instance) obj;
            return (o.name.equals(name) && o.maxClauseLength == maxClauseLength
                    && o.md5.equals(md5) && o.numAtoms == numAtoms && o.numClauses == numClauses
                    && o.ratio == ratio && o.id == id);
        }
        return false;
    }

    public int getId() {
        return id;
    }

    protected void setId(int id) {
        this.id = id;
    }

    public int getMaxClauseLength() {
        return maxClauseLength;
    }

    public void setMaxClauseLength(int maxClauseLength) {
        this.maxClauseLength = maxClauseLength;
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

    public int getNumAtoms() {
        return numAtoms;
    }

    public void setNumAtoms(int numAtoms) {
        this.numAtoms = numAtoms;
    }

    public int getNumClauses() {
        return numClauses;
    }

    public void setNumClauses(int numClauses) {
        this.numClauses = numClauses;
    }

    public float getRatio() {
        return ratio;
    }

    public void setRatio(float ratio) {
        this.ratio = ratio;
    }

    protected Instance() {
        id = numAtoms = numClauses = maxClauseLength = 0;
        ratio = 0;
        name = md5 = "";
        file = null;
    }

    protected File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    /**
     *
     * @return the source instance class of an instance.
     * @see InstanceHasInstanceClass for user defined instance classes.
     */
    public InstanceClass getInstanceClass() {
        return instanceClass;
    }

    /**
     * Sets the source instance class of an instance. The instance class set here must be a source instance class!
     * @param instanceClass the source instance class of an instance.
     * @see InstanceHasInstanceClass for user defined instance classes.
     * @throws InstanceClassMustBeSourceException if the given instance class ist not a source instance.
     */
    public void setInstanceClass(InstanceClass instanceClass) throws InstanceClassMustBeSourceException {
        if (!instanceClass.isSource())
            throw new InstanceClassMustBeSourceException();
        this.instanceClass = instanceClass;
    }
    
    private int id;
    private String name;
    private String md5;
    private int numAtoms;
    private int numClauses;
    private float ratio;
    private int maxClauseLength;
    private File file;


    /**
     * The source instance class of the instance.
     * @see InstanceHasInstanceClass for user defined instance classes.
     */
    private InstanceClass instanceClass;
}
