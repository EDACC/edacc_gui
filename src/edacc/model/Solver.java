package edacc.model;

import java.io.File;


public class Solver extends BaseModel implements IntegerPKModel {
    private int id;
    private String name;
    private String binaryName;
    private File binaryFile;
    private String description;
    private String md5;
    private File[] codeFile;
    private String authors;
    private String version;
    
    public Solver() {
        this.setNew();
    }

    public String getBinaryName() {
        return binaryName;
    }

    public void setBinaryName(String binaryName) {
        this.binaryName = binaryName;
        if (this.isSaved()) {
            this.setModified();
        }
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

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
        if (this.isSaved()) {
            this.setModified();
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    protected File getBinaryFile() {
        return binaryFile;
    }

    public void setBinaryFile(File binaryFile) {
        this.binaryFile = binaryFile;
        if (this.isSaved()) {
            this.setModified();
        }
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

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Solver other = (Solver) obj;
        if ((this.md5 == null) ? (other.md5 != null) : !this.md5.equals(other.md5)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 29 * hash + (this.md5 != null ? this.md5.hashCode() : 0);
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
