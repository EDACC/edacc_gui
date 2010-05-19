package edacc.model;

import java.sql.Date;

public class Experiment extends BaseModel implements IntegerPKModel {
    private int id;
    private String description;
    private Date date;
    private int numRuns;
    private int timeOut;
    private int memOut;
    private int maxSeed;
    private boolean autoGeneratedSeeds;
    private boolean linkSeeds;
    private String name;

    @Override
    public int hashCode() {
        return 31 + id;
    }

    @Override
    public boolean equals(Object o) {
        if (o != null && o instanceof Experiment) {
            Experiment e = (Experiment)o;
            return e.id == id && e.description.equals(description) && e.date.equals(date) &&
                    e.numRuns == numRuns && e.timeOut == timeOut && e.name.equals(name);
        }
        return false;
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
    
    
        public boolean isAutoGeneratedSeeds() {
        return autoGeneratedSeeds;
    }

    public void setAutoGeneratedSeeds(boolean autoGeneratedSeeds) {
        this.autoGeneratedSeeds = autoGeneratedSeeds;
        if (this.isSaved()) {
            this.setModified();
        }
    }

    public boolean isLinkSeeds() {
        return linkSeeds;
    }

    public void setLinkSeeds(boolean linkSeeds) {
        this.linkSeeds = linkSeeds;
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

    public int getId() {
        return id;
    }

    protected void setId(int id) {
        this.id = id;
    }

    public int getNumRuns() {
        return numRuns;
    }

    public void setNumRuns(int numRuns) {
        this.numRuns = numRuns;
        if (this.isSaved()) {
            this.setModified();
        }
    }

    public int getTimeOut() {
        return timeOut;
    }

    public void setTimeOut(int timeOut) {
        this.timeOut = timeOut;
        if (this.isSaved()) {
            this.setModified();
        }
    }

    public int getMemOut() {
        return memOut;
    }

    public void setMemOut(int memOut) {
        this.memOut = memOut;
    }

    public int getMaxSeed() {
        return maxSeed;
    }

    public void setMaxSeed(int maxSeed) {
        this.maxSeed = maxSeed;
    }
}
