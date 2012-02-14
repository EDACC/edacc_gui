package edacc.model;

import java.io.Serializable;

public class Parameter extends BaseModel implements IntegerPKModel, Serializable {
    private int id;
    private int idSolver;
    private String name;
    private String oldName;
    private String prefix;
    private String defaultValue;
    private int order;
    private boolean hasValue;
    private boolean mandatory;
    private boolean space;
    private boolean attachToPrevious;

    public Parameter(Parameter p) {
        this();
        id = p.id;
        idSolver = p.idSolver;
        name = p.name;
        oldName = p.oldName;
        prefix = p.prefix;
        defaultValue = p.defaultValue;
        order = p.order;
        hasValue = p.hasValue;
        mandatory = p.mandatory;
        space = p.space;
        attachToPrevious = p.attachToPrevious;
    }

    public boolean realEquals(Parameter other) {
        return (name == null ? other.name == null : name.equals(other.name)) &&
                (prefix == null ? other.prefix == null : prefix.equals(other.prefix)) &&
                (defaultValue == null ? other.defaultValue == null : defaultValue.equals(other.defaultValue)) &&
                (order == other.order) &&
                (mandatory == other.mandatory) &&
                (space == other.space) &&
                (attachToPrevious == other.attachToPrevious);
    }
    
    public Parameter() {
        super();
        oldName = null;
        name = prefix = defaultValue = "";
        hasValue=true;
        attachToPrevious=false;
    }

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
        if (oldName == null)
            oldName = this.name;
        this.name = name;
        if (this.isSaved()) {
            this.setModified();
        }
    }
    
    protected String getOldName() {
        return oldName;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
        if (this.isSaved()) {
            this.setModified();
        }
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
        if (this.isSaved()) {
            this.setModified();
        }
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
        if (this.isSaved()) {
            this.setModified();
        }
    }

    public boolean getHasValue() {
        return hasValue;
    }

    public void setHasValue(boolean hasValue) {
        this.hasValue = hasValue;
        if (this.isSaved()) {
            this.setModified();
        }
    }

    /**
     * @return the mandatory
     */
    public boolean isMandatory() {
        return mandatory;
    }

    /**
     * @param mandatory the mandatory to set
     */
    public void setMandatory(boolean mandatory) {
        this.mandatory = mandatory;
        if (this.isSaved()) {
            this.setModified();
        }
    }

    /**
     * @return the space
     */
    public boolean getSpace() {
        return space;
    }

    /**
     * @param space the space to set
     */
    public void setSpace(boolean space) {
        this.space = space;
        if (this.isSaved()) {
            this.setModified();
        }
    }

    public int getIdSolver() {
        return idSolver;
    }

    public void setIdSolver(int idSolver) {
        this.idSolver = idSolver;
    }

    public boolean isAttachToPrevious() {
        return attachToPrevious;
    }

    public void setAttachToPrevious(boolean attachToPrevious) {
        this.attachToPrevious = attachToPrevious;
    }

    @Override
    public void setSaved() {
        super.setSaved();
        oldName = null;
    }
    
    
}
