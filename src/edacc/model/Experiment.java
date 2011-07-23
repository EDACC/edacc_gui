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
}
