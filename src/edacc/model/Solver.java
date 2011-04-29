package edacc.model;

import java.io.File;


public class Solver extends BaseModel implements IntegerPKModel {
    private int id;
    private String name;
    private String description;
    private File[] codeFile;
    private String authors;
    private String version;
    
    public Solver() {
        this.setNew();
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

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Solver other = (Solver) obj;
        return this.getName().equals(other.getName()) && this.getVersion().equals(other.getVersion());
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 17 * hash + (this.name != null ? this.name.hashCode() : 0);
        hash = 17 * hash + (this.version != null ? this.version.hashCode() : 0);
        return hash;
    }

 /*   @Override
    public int hashCode() {
        int hash = 3;
        hash = 29 * hash + (this.md5 != null ? this.md5.hashCode() : 0);
        return hash;
    }*/

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
