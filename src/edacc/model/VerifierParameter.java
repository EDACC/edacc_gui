package edacc.model;

/**
 *
 * @author simon
 */
public class VerifierParameter extends BaseModel implements IntegerPKModel {

    private int id;
    private int idVerifier;
    private String name;
    private String prefix;
    private String defaultValue;
    private int order;
    private boolean hasValue;
    private boolean mandatory;
    private boolean space;
    private boolean attachToPrevious;

    public VerifierParameter(VerifierParameter p) {
        this();
        id = p.id;
        assign(p);
    }

    public final void assign(VerifierParameter p) {
        setIdVerifier(p.idVerifier);
        setName(p.name);
        setPrefix(p.prefix);
        setDefaultValue(p.defaultValue);
        setOrder(p.order);
        setHasValue(p.hasValue);
        setMandatory(p.mandatory);
        setSpace(p.space);
        setAttachToPrevious(p.attachToPrevious);
    }

    public boolean realEquals(VerifierParameter other) {
        return (name == null ? other.name == null : name.equals(other.name))
                && (prefix == null ? other.prefix == null : prefix.equals(other.prefix))
                && (defaultValue == null ? other.defaultValue == null : defaultValue.equals(other.defaultValue))
                && (order == other.order)
                && (mandatory == other.mandatory)
                && (space == other.space)
                && (attachToPrevious == other.attachToPrevious);
    }

    public VerifierParameter() {
        super();
        name = prefix = defaultValue = "";
        hasValue = true;
        attachToPrevious = false;
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
        if (this.isSaved() && (name == null ? this.name != null : !name.equals(this.name))) {
            this.setModified();
        }
        this.name = name;

    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        if (this.isSaved() && this.order != order) {
            this.setModified();
        }
        this.order = order;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        if (this.isSaved() && this.prefix != prefix) {
            this.setModified();
        }
        this.prefix = prefix;

    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        if (this.isSaved() && (defaultValue == null ? this.defaultValue != null : !defaultValue.equals(this.defaultValue))) {
            this.setModified();
        }
        this.defaultValue = defaultValue;
    }

    public boolean getHasValue() {
        return hasValue;
    }

    public void setHasValue(boolean hasValue) {
        if (this.isSaved() && this.hasValue != hasValue) {
            this.setModified();
        }
        this.hasValue = hasValue;
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
        if (this.isSaved() && this.mandatory != mandatory) {
            this.setModified();
        }
        this.mandatory = mandatory;
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
        if (this.isSaved() && this.space != space) {
            this.setModified();
        }
        this.space = space;
    }

    public int getIdVerifier() {
        return idVerifier;
    }

    public void setIdVerifier(int idVerifier) {
        if (this.isSaved() && this.idVerifier != idVerifier) {
            this.setModified();
        }
        this.idVerifier = idVerifier;
    }

    public boolean isAttachToPrevious() {
        return attachToPrevious;
    }

    public void setAttachToPrevious(boolean attachToPrevious) {
        if (this.isSaved() && this.attachToPrevious != attachToPrevious) {
            this.setModified();
        }
        this.attachToPrevious = attachToPrevious;
    }

    @Override
    public void setSaved() {
        super.setSaved();
    }
}
