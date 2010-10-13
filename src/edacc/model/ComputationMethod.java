/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edacc.model;

import java.io.File;


/**
 *
 * @author rretz
 */
public class ComputationMethod extends BaseModel implements IntegerPKModel {
    private int id;
    private String name;
    private String description;
    private String md5;
    private String binaryName;
    private File binary;

    @Override
    public int getId() {
        return id;
    }
    
    public void setId(int id){
        this.id = id;
        if (this.isSaved())
            this.setModified();
    }

    public File getBinary() {
        return binary;
    }

    public void setBinary(File binary) {
        this.binary = binary;
        if (this.isSaved())
            this.setModified();
    }

    public String getBinaryName() {
        return binaryName;
    }

    public void setBinaryName(String binaryName) {
        this.binaryName = binaryName;
        if (this.isSaved())
            this.setModified();
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
        if (this.isSaved())
            this.setModified();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        if (this.isSaved())
            this.setModified();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
