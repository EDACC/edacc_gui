/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edacc.model;

import java.io.File;
import java.io.Serializable;

/**
 *
 * @author dgall
 */
public class SolverBinaries extends BaseModel implements IntegerPKModel, Serializable {

    /**
     * The (db) id of this object.
     */
    private int idSolverBinary;
    /**
     * The id of the associated Solver.
     */
    private int idSolver;
    /**
     * The name of the binary.
     */
    private String binaryName;
    private String rootDir;
    /**
     * The binary files which will be persisted in the db as zip archive.
     * This is always a reference to the real file system. If the object is NEW
     * and binaryFiles is NULL, then no binary has been chosen yet by the user.
     * If the object is SAVED and the binaryFiles is NULL, it hasn't been loaded
     * from the db yet. Use the SolverBinariesDAO to do this.
     * If the object is MODIFIED and binarFiles is NULL, there were no changes
     * concerning the binaryFiles. It is not necessary to save it again. If a value is set,
     * it should be persisted to the db by updating the field.
     * The save method in the SolverBinariesDAO will handle the described procedure.
     */
    private transient File[] binaryFiles;
    /**
     * The md5 sum of the archive.
     * NULL, if no md5 sum has been calculated yet (eg. because of missing binaryArchive).
     */
    private String md5;
    /**
     * A short version string describing the binary.
     */
    private String version;
    /**
     * The command to execute the unzipped binary (eg. "java -jar").
     * NULL, if the file specified in runPath can be executed by ./filename.
     */
    private String runCommand;
    /**
     * The path to the main binary file.
     */
    private String runPath;

    public boolean realEquals(SolverBinaries other) {
        return (other.binaryName == null ? binaryName == null : other.binaryName.equals(binaryName)) && 
                (other.rootDir == null ? rootDir == null : other.rootDir.equals(rootDir)) && 
                (other.md5 == null ? md5 == null : other.md5.equals(md5)) && 
                (other.version == null ? version == null : other.version.equals(version)) && 
                (other.runCommand == null ? runCommand == null : other.runCommand.equals(runCommand)) &&
                (other.runPath == null ? runPath == null : other.runPath.equals(runPath));
    }
    
    public SolverBinaries(Solver s) {
        this(s.getId());
    }

    public SolverBinaries(int idSolver) {
        this.idSolver = idSolver;
        this.setNew();
    }
    
    /**
     * Creates an identical clone of a Solver binary.
     * @param b the solver binary to be cloned.
     */
    public SolverBinaries(SolverBinaries b) {
        this(b.getIdSolver());
        this.setAll(b);
    }
    
    /**
     * Sets all values of this object to the values of the given solverBinary.
     * @param b the solverbinaries with the values to be copied.
     */
    final public void setAll(SolverBinaries b) {
        this.setBinaryArchive(b.getBinaryFiles());
        this.setBinaryName(b.getBinaryName());
        this.setIdSolver(b.getIdSolver());
        this.setMd5(b.getMd5());
        this.setRootDir(b.getRootDir());
        this.setRunCommand(b.getRunCommand());
        this.setRunPath(b.getRunPath());
        this.setVersion(b.getVersion());
    }

    /**
     * @return the idSolverBinary
     */
    public int getIdSolverBinary() {
        return idSolverBinary;
    }

    /**
     * @param idSolverBinary the idSolverBinary to set
     */
    protected void setIdSolverBinary(int idSolverBinary) {
        this.idSolverBinary = idSolverBinary;
    }

    /**
     * @return the idSolver
     */
    public int getIdSolver() {
        return idSolver;
    }

    public void setIdSolver(int idSolver) {
        this.idSolver = idSolver;
    }

    /**
     * @return the binaryName
     */
    public String getBinaryName() {
        return binaryName;
    }

    /**
     * @param binaryName the binaryName to set
     */
    public void setBinaryName(String binaryName) {
        this.binaryName = binaryName;
        if (isSaved()) {
            setModified();
        }
    }

    /**
     * @return the md5
     */
    public String getMd5() {
        return md5;
    }

    /**
     * @param md5 the md5 to set
     */
    public void setMd5(String md5) {
        this.md5 = md5;
        if (isSaved()) {
            setModified();
        }
    }

    /**
     * @return the version
     */
    public String getVersion() {
        return version;
    }

    /**
     * @param version the version to set
     */
    public void setVersion(String version) {
        this.version = version;
        if (isSaved()) {
            setModified();
        }
    }

    /**
     * @return the runCommand
     */
    public String getRunCommand() {
        return runCommand;
    }

    /**
     * @param runCommand the runCommand to set
     */
    public void setRunCommand(String runCommand) {
        this.runCommand = runCommand;
        if (isSaved()) {
            setModified();
        }
    }

    /**
     * @return the runPath
     */
    public String getRunPath() {
        return runPath;
    }

    /**
     * @param runPath the runPath to set
     */
    public void setRunPath(String runPath) {
        this.runPath = runPath;
        if (isSaved()) {
            setModified();
        }
    }

    /**
     * @return the binaryFiles
     */
    public File[] getBinaryFiles() {
        return binaryFiles;
    }

    /**
     * @param binaryFiles the binaryFiles to set
     */
    public void setBinaryArchive(File[] binaryFiles) {
        this.binaryFiles = binaryFiles;
        if (isSaved()) {
            setModified();
        }
    }

    @Override
    public int getId() {
        return getIdSolverBinary();
    }

    public String getRootDir() {
        return rootDir;
    }

    public void setRootDir(String rootDir) {
        this.rootDir = rootDir;
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
