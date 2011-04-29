/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edacc.model;

import java.io.File;

/**
 *
 * @author dgall
 */
public class SolverBinaries extends BaseModel implements IntegerPKModel {

    /**
     * The (db) id of this object.
     */
    private int idSolverBinary;

    /**
     * The id of the associated Solver.
     */
    private final int idSolver;

    /**
     * The name of the binary.
     */
    private String binaryName;

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
    private File[] binaryFiles;

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

    public SolverBinaries(Solver s) {
        this.idSolver = s.getId();
        this.setNew();
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
    public void setIdSolverBinary(int idSolverBinary) {
        this.idSolverBinary = idSolverBinary;
    }

    /**
     * @return the idSolver
     */
    public int getIdSolver() {
        return idSolver;
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
    }

    @Override
    public int getId() {
        return getIdSolverBinary();
    }
}
