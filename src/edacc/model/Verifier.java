package edacc.model;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author simon
 */
public class Verifier extends BaseModel implements IntegerPKModel {

    private int id;
    private String name;
    private String description;
    private String md5;
    private String runCommand;
    private String runPath;
    private List<VerifierParameter> parameters;
    private File file;

    public Verifier() {
        id = -1;
        this.parameters = new LinkedList<VerifierParameter>();
        this.setNew();
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

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
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
}
