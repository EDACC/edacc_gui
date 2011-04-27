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
public class SolverBinary extends BaseModel implements IntegerPKModel {

    private int idSolverBinary;
    private String binaryName;
    private String version;
    private File binaryFilesArchive;
    private String md5;

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
     * @return the binaryFilesArchive
     */
    public File getBinaryFilesArchive() {
        return binaryFilesArchive;
    }

    /**
     * @param binaryFilesArchive the binaryFilesArchive to set
     */
    public void setBinaryFilesArchive(File binaryFilesArchive) {
        this.binaryFilesArchive = binaryFilesArchive;
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

    @Override
    public int getId() {
        return getIdSolverBinary();
    }

}
