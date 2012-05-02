package edacc.properties;

/**
 *
 * @author simon
 */
public class InstanceComputationMethod {   
    public InstanceComputationMethod(String[] args) {
    }
    
    /**
     * Property calculation method. Should be overwritten.
     * @param instanceId
     * @return
     * @throws Exception 
     */
    public String calculateProperty(int instanceId) throws Exception {
        throw new IllegalArgumentException("TODO: implement");
    }
}
