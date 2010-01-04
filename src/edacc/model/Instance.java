package edacc.model;

public class Instance extends BaseModel {

    @Override
    public int hashCode() {
        return 31 + id;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj != null && obj instanceof Instance) {
            Instance o = (Instance)obj;
            return (o.name.equals(name) && o.maxClauseLength == maxClauseLength &&
                    o.md5.equals(md5) && o.numAtoms == numAtoms && o.numClauses == numClauses &&
                    o.ratio == ratio && o.id == id);
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

    public int getRatio() {
        return ratio;
    }

    public void setRatio(int ratio) {
        this.ratio = ratio;
    }

    protected Instance() {
        id = numAtoms = numClauses = ratio = maxClauseLength = 0;
        name = md5 = "";
    }

    public int getFile() {
        return file;
    }

    public void setFile(int file) {
        this.file = file;
    }
    private int id;
    private String name;
    private String md5;
    private int numAtoms;
    private int numClauses;
    private int ratio;
    private int maxClauseLength;
    private int file;


}
