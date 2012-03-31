package edacc.model;

import java.io.File;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author simon
 */
public class Verifier extends BaseModel implements IntegerPKModel, Serializable {

    private int id;
    private String name;
    private String description;
    private String md5;
    private String runCommand;
    private String runPath;
    private List<VerifierParameter> parameters;
    transient private File[] files;
    
    public Verifier() {
        id = -1;
        this.parameters = new LinkedList<VerifierParameter>();
        this.setNew();
    }
    
    public boolean realEquals(Verifier f) {
        if (!name.equals(f.name) || !md5.equals(f.md5) || !runCommand.equals(f.runCommand) || !runPath.equals(f.runPath)) {
            return false;
        }
        if (parameters.size() != f.parameters.size()) {
            return false;
        }
        for (VerifierParameter myparam : parameters) {
            boolean found = false;
            for (VerifierParameter hisparam : f.parameters) {
                if (myparam.realEquals(hisparam)) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                return false;
            }
        }
        return true;
    }
    
    
    protected void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        if (this.isSaved()) {
            this.setModified();
        }
    }

    public List<VerifierParameter> getParameters() {
        return parameters;
    }

    public void setParameters(List<VerifierParameter> parameters) {
        // don't set modified here.
        if (parameters == null) {
            this.parameters = new LinkedList<VerifierParameter>();
        } else {
            this.parameters = parameters;
        }
    }

    public File[] getFiles() {
        return files;
    }

    public void setFiles(File[] files) {
        this.files = files;
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

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
        if (this.isSaved()) {
            this.setModified();
        }
    }

    public String getRunCommand() {
        return runCommand;
    }

    public void setRunCommand(String runCommand) {
        this.runCommand = runCommand;
        if (this.isSaved()) {
            this.setModified();
        }
    }

    public String getRunPath() {
        return runPath;
    }

    public void setRunPath(String runPath) {
        this.runPath = runPath;
        if (this.isSaved()) {
            this.setModified();
        }
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        return name;
    }
    
}
