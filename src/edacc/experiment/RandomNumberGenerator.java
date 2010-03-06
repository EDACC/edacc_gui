package edacc.experiment;

/**
 * simple interface for a random number generator used in the seed generation
 * @author daniel
 */
public interface RandomNumberGenerator {
    public int nextInt(int n);
    public void setSeed(long seed);

}
