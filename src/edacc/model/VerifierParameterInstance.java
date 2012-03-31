package edacc.model;

import java.io.Serializable;

/**
 *
 * @author simon
 */
public class VerifierParameterInstance extends BaseModel implements Serializable {

    private int parameter_id;
    private int verifierConfigId;
    private String value;

    public VerifierParameterInstance() {
        super();
    }

    protected VerifierParameterInstance(VerifierParameterInstance pi) {
        this();
        assign(pi);
    }

    public void assign(VerifierParameterInstance other) {
        setParameter_id(other.parameter_id);
        setVerifierConfigId(other.verifierConfigId);
        setValue(other.value);
    }

    public int getParameter_id() {
        return parameter_id;
    }

    public void setParameter_id(int parameter_id) {
        if (this.isSaved() && this.parameter_id != parameter_id) {
            this.setModified();
        }
        this.parameter_id = parameter_id;
    }

    public int getVerifierConfigId() {
        return verifierConfigId;
    }

    public void setVerifierConfigId(int verifierConfigId) {
        if (this.isSaved() && this.verifierConfigId != verifierConfigId) {
            this.setModified();
        }
        this.verifierConfigId = verifierConfigId;

    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        if (this.isSaved() && (this.value == null ? value != null : !this.value.equals(value))) {
            this.setModified();
        }
        this.value = value;
    }
}
