package edacc.experiment;

/**
 * simple interface for a random number generator used in the seed generation
 * @author daniel
 */
public interface RandomNumberGenerator {
    /**
     * Returns the next pseudorandom <code>int</code> value for the random generator in the interval [0..n-1].
     * @param n the maximum value
     * @return next pseudorandom <code>int</code> value
     */
    public int nextInt(int n);
    /**
     * Sets the random seed for the pseudorandom generator
     * @param seed the seed to be used
     */
    public void setSeed(long seed);

}
