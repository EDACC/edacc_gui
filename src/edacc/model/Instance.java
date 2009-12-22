package edacc.model;

public class Instance extends BaseModel {
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

    private int id;
    private String name;
    private String md5;
    private int numAtoms;
    private int numClauses;
    private int ratio;
    private int maxClauseLength;
}
