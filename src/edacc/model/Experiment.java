package edacc.model;

import java.sql.Date;

public class Experiment extends BaseModel implements IntegerPKModel {

    private int id;
    private String description;
    private Date date;
    private String name;
    private int priority;
    private boolean configurationExp;
    private boolean active;
    private Integer solverOutputPreserveFirst, solverOutputPreserveLast;
    private Integer watcherOutputPreserveFirst, watcherOutputPreserveLast;
    private Integer verifierOutputPreserveFirst, verifierOutputPreserveLast;

    @Override
    public int hashCode() {
        return 31 + id;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Experiment other = (Experiment) obj;
        if (this.id != other.id) {
            return false;
        }
        return true;
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

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
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

    @Override
    public int getId() {
        return id;
    }

    protected void setId(int id) {
        this.id = id;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
        if (this.isSaved()) {
            this.setModified();
        }
    }

    public boolean isConfigurationExp() {
        return configurationExp;
    }

    public void setConfigurationExp(boolean configurationExp) {
        this.configurationExp = configurationExp;
        if (this.isSaved()) {
            this.setModified();
        }
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
        if (this.isSaved()) {
            this.setModified();
        }
    }

    public Integer getSolverOutputPreserveFirst() {
        return solverOutputPreserveFirst;
    }

    public void setSolverOutputPreserveFirst(Integer solverOutputSizePreserveFirst) {
        this.solverOutputPreserveFirst = solverOutputSizePreserveFirst;
        if (this.isSaved()) {
            this.setModified();
        }
    }

    public Integer getSolverOutputPreserveLast() {
        return solverOutputPreserveLast;
    }

    public void setSolverOutputPreserveLast(Integer solverOutputPreserveLast) {
        this.solverOutputPreserveLast = solverOutputPreserveLast;
        if (this.isSaved()) {
            this.setModified();
        }
    }

    public Integer getVerifierOutputPreserveFirst() {
        return verifierOutputPreserveFirst;
    }

    public void setVerifierOutputPreserveFirst(Integer verifierOutputPreserveFirst) {
        this.verifierOutputPreserveFirst = verifierOutputPreserveFirst;
        if (this.isSaved()) {
            this.setModified();
        }
    }

    public Integer getVerifierOutputPreserveLast() {
        return verifierOutputPreserveLast;
    }

    public void setVerifierOutputPreserveLast(Integer verifierOutputPreserveLast) {
        this.verifierOutputPreserveLast = verifierOutputPreserveLast;
        if (this.isSaved()) {
            this.setModified();
        }
    }

    public Integer getWatcherOutputPreserveFirst() {
        return watcherOutputPreserveFirst;
    }

    public void setWatcherOutputPreserveFirst(Integer watcherOutputPreserveFirst) {
        this.watcherOutputPreserveFirst = watcherOutputPreserveFirst;
        if (this.isSaved()) {
            this.setModified();
        }
    }

    public Integer getWatcherOutputPreserveLast() {
        return watcherOutputPreserveLast;
    }

    public void setWatcherOutputPreserveLast(Integer watcherOutputPreserveLast) {
        this.watcherOutputPreserveLast = watcherOutputPreserveLast;
        if (this.isSaved()) {
            this.setModified();
        }
    }

    @Override
    public String toString() {
        return name;
    }
}
