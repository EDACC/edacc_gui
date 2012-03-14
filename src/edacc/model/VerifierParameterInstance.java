package edacc.model;

/**
 *
 * @author simon
 */
public class VerifierParameterInstance extends BaseModel {
    private int parameter_id;
    private Verifier verifier;
    private String value;

    public VerifierParameterInstance() {
        super();
    }
    
    protected VerifierParameterInstance(VerifierParameterInstance pi) {
        this();
        parameter_id = pi.parameter_id;
        verifier = pi.verifier;
        value = pi.value;
    }
    
    public int getParameter_id() {
        return parameter_id;
    }

    public void setParameter_id(int parameter_id) {
        this.parameter_id = parameter_id;
        if (this.isSaved()) {
            this.setModified();
        }
    }

    public Verifier getVerifier() {
        return verifier;
    }

    public void setVerifier(Verifier verifier) {
        this.verifier = verifier;
        if (this.isSaved()) {
            this.setModified();
        }
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
        if (this.isSaved()) {
            this.setModified();
        }
    }
}

