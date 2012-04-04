package edacc.model;

import java.io.Serializable;

/**
 *
 * @author simon
 */
public class InstanceSeed implements Serializable {

    public Instance instance;
    public int seed;

    public InstanceSeed(Instance instance, int seed) {
        this.instance = instance;
        this.seed = seed;
    }
}