package edacc.model;

import java.io.File;
import java.util.Vector;

public class Solver extends BaseModel implements IntegerPKModel {

    private int id;
    private String name;
    private String description;
    private File[] codeFile;
    private String authors;
    private String version;
    private Vector<SolverBinaries> solverBinaries;

    public Solver() {
        this.setNew();
        this.solverBinaries = new Vector<SolverBinaries>();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
        if (this.isSaved()) {
            this.setModified();
        }
    }

    public String getAuthors() {
        return authors;
    }

    public void setAuthor(String author) {
        this.authors = author;
        if (this.isSaved()) {
            this.setModified();
        }
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
        if (this.isSaved()) {
            this.setModified();
        }
    }

    @Override
    public int getId() {
        return id;
    }

    protected void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    protected File[] getCodeFile() {
        return codeFile;
    }

    public void setCodeFile(File[] codeFile) {
        this.codeFile = codeFile;
        if (this.isSaved()) {
            this.setModified();
        }
    }

    public void addSolverBinary(SolverBinaries b) {
        this.solverBinaries.add(b);
        if (this.isSaved()) {
            this.setModified();
        }
    }

    public void removeSolverBinary(SolverBinaries b) {
        solverBinaries.remove(b);
    }

    public Vector<SolverBinaries> getSolverBinaries() {
        return (Vector<SolverBinaries>) solverBinaries.clone();
    }

    protected void setSolverBinaries(Vector<SolverBinaries> solverBinaries) {
        this.solverBinaries = solverBinaries;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Solver other = (Solver) obj;
        if (this.id != other.id) {
            return false;
        }
        if ((this.name == null) ? (other.name != null) : !this.name.equals(other.name)) {
            return false;
        }
        if ((this.version == null) ? (other.version != null) : !this.version.equals(other.version)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 67 * hash + this.id;
        return hash;
    }

    @Override
    public String toString() {
        String res = name;
        if (version != null && !"".equals(version)) {
            res += " " + version;
        }
        if (authors != null && !"".equals(authors)) {
            res += ", " + authors;
        }
        return res;
    }
}
