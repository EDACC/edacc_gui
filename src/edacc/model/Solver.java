package edacc.model;

import edacc.parameterspace.graph.ParameterGraph;
import java.io.File;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.List;
import java.util.Random;
import java.util.Vector;

public class Solver extends BaseModel implements IntegerPKModel, Serializable {

    private static Random rand = new Random();
    
    private int id;
    private String name;
    private String description;
    private transient File[] codeFile;
    private String authors;
    private String version;
    private Vector<SolverBinaries> solverBinaries;
    
    // currently only used for export! (will not be set by DAOs)
    protected List<Parameter> parameters;
    protected ParameterGraph graph;

    public boolean realEquals(Solver other, List<Parameter> params) throws SQLException {
        boolean res = other.name.equals(name) && other.description.equals(description) && other.authors.equals(authors) && other.version.equals(version);
        if (!res)
            return false;
        List<Parameter> ownParams = ParameterDAO.getParameterFromSolverId(id);
        if (ownParams.size() != params.size()) {
            return false;
        }
        for (Parameter ownParam : ownParams) {
            boolean found = false;
            for (Parameter hisParam : params) {
                if (hisParam.realEquals(ownParam)) {
                    found = true;
                    break;
                }
            }
            if (!found)
                return false;
        }
        return true;
    }
    
    public Solver(Solver solver) {
        this(solver.getName(), solver.getDescription(), solver.getAuthors(), solver.getVersion());
    }
    
    public Solver(String name, String description, String authors, String version) {
        this();
        this.name = name;
        this.description = description;
        this.authors = authors;
        this.version = version;
    }
    
    public Solver() {
        this.setNew();
        this.solverBinaries = new Vector<SolverBinaries>();
        // set ID to random number
        this.id = rand.nextInt();
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
