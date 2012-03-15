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
    private int idExperiment;
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
        if (parameterInstances == null) {
            this.parameterInstances = new ArrayList<VerifierParameterInstance>();
        } else {
            this.parameterInstances = parameterInstances;
        }
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

    public int getIdExperiment() {
        return idExperiment;
    }

    public void setIdExperiment(int idExperiment) {
        if (isSaved() && this.idExperiment != idExperiment) {
            setModified();
        }
        this.idExperiment = idExperiment;

    }
}
