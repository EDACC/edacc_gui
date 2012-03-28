package edacc.model;

import java.io.File;

/**
 *
 * @author simon
 */
public class CostBinary extends BaseModel implements IntegerPKModel {
    
    private int idCostBinary;
    private int idSolver;
    private String binaryName;
    private String rootDir;
    private transient File[] binaryFiles;
    private String md5;
    private String version;
    private String runCommand;
    private String runPath;
    private String parameters;
    private Cost cost;
    
    public CostBinary(Solver s) {
        this(s.getId());
    }

    public CostBinary(int idSolver) {
        this.idSolver = idSolver;
        this.setNew();
    }
    
    /**
     * Creates an identical clone of a Solver binary.
     * @param b the solver binary to be cloned.
     */
    public CostBinary(CostBinary b) {
        this(b.getIdSolver());
        this.setAll(b);
    }
    
    /**
     * Sets all values of this object to the values of the given costBinary.
     * @param b the CostBinary with the values to be copied.
     */
    final public void setAll(CostBinary b) {
        this.setBinaryArchive(b.getBinaryFiles());
        this.setBinaryName(b.getBinaryName());
        this.setIdSolver(b.getIdSolver());
        this.setMd5(b.getMd5());
        this.setRootDir(b.getRootDir());
        this.setRunCommand(b.getRunCommand());
        this.setRunPath(b.getRunPath());
        this.setVersion(b.getVersion());
        this.setParameters(b.getParameters());
        this.setCost(b.getCost());
    }

    public int getIdCostBinary() {
        return idCostBinary;
    }

    protected void setIdCostBinary(int idCostBinary) {
        this.idCostBinary = idCostBinary;
    }

    public int getIdSolver() {
        return idSolver;
    }

    public void setIdSolver(int idSolver) {
        this.idSolver = idSolver;
    }

    public String getBinaryName() {
        return binaryName;
    }

    public void setBinaryName(String binaryName) {
        this.binaryName = binaryName;
        if (isSaved()) {
            setModified();
        }
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
        if (isSaved()) {
            setModified();
        }
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
        if (isSaved()) {
            setModified();
        }
    }

    public String getRunCommand() {
        return runCommand;
    }

    public void setRunCommand(String runCommand) {
        this.runCommand = runCommand;
        if (isSaved()) {
            setModified();
        }
    }

    public String getRunPath() {
        return runPath;
    }

    public void setRunPath(String runPath) {
        this.runPath = runPath;
        if (isSaved()) {
            setModified();
        }
    }

    public File[] getBinaryFiles() {
        return binaryFiles;
    }

    public void setBinaryArchive(File[] binaryFiles) {
        this.binaryFiles = binaryFiles;
        if (isSaved()) {
            setModified();
        }
    }

    @Override
    public int getId() {
        return getIdCostBinary();
    }

    public String getRootDir() {
        return rootDir;
    }

    public void setRootDir(String rootDir) {
        this.rootDir = rootDir;
    }
    
    public String getParameters() {
        return parameters;
    }
    
    public void setParameters(String parameters) {
        if (isSaved() && (parameters == null ? this.parameters != null : !parameters.equals(this.parameters))) {
            setModified();
        }
        this.parameters = parameters;
    }

    public Cost getCost() {
        return cost;
    }
    
    public void setCost(Cost cost) {
        if (isSaved() && this.cost != cost) {
            setModified();
        }
        this.cost = cost;
    }
    
    @Override
    public String toString() {
        if (version == null || "".equals(version)) {
            return binaryName;
        } else {
            return binaryName + " " + version;
        }

    }
}
