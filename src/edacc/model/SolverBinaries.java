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
    private int idSolver;

    /**
     * The name of the binary.
     */
    private String binaryName;

    /**
     * The zip archive containing the binaries.
     * This is always a reference to the real file system. If the object is NEW
     * and binaryArchive is NULL, then no binary has been chosen yet by the user.
     * If the object is SAVED and the binaryArchive is NULL, it hasn't been loaded
     * from the db yet. Use the SolverBinariesDAO to do this.
     * If the object is MODIFIED and the binaryArchive is NULL, there were no changes
     * concerning the binaryArchive. It is not necessary to save it again. If a value is set,
     * it should be persisted to the db by updating the field.
     * The save method in the SolverBinariesDAO will handle the described procedure.
     */
    private File binaryArchive;

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
     * @return the binaryArchive
     */
    public File getBinaryArchive() {
        return binaryArchive;
    }

    /**
     * @param binaryArchive the binaryArchive to set
     */
    public void setBinaryArchive(File binaryArchive) {
        this.binaryArchive = binaryArchive;
    }

    @Override
    public int getId() {
        return getIdSolverBinary();
    }
}
