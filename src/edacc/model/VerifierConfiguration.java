package edacc.model;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author simon
 */
public class VerifierConfiguration extends BaseModel implements IntegerPKModel {
    private int id;
    private Verifier verifier;
    private List<VerifierParameterInstance> parameterInstances;
    
    public VerifierConfiguration() {
        id = -1;
        verifier = null;
        parameterInstances = new ArrayList<VerifierParameterInstance>();
    }

    @Override
    public int getId() {
        return id;
    }

    protected void setId(int id) {
        this.id = id;
        if (isSaved()) {
            setModified();
        }
    }

    public List<VerifierParameterInstance> getParameterInstances() {
        return parameterInstances;
    }

    public void setParameterInstances(List<VerifierParameterInstance> parameterInstances) {
        this.parameterInstances = parameterInstances;
    }

    public Verifier getVerifier() {
        return verifier;
    }

    public void setVerifier(Verifier verifier) {
        this.verifier = verifier;
        if (isSaved()) {
            setModified();
        }
    }
}
