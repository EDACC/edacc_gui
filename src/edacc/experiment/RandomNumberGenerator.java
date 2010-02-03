package edacc.experiment;

/**
 *
 * @author daniel
 */
public interface RandomNumberGenerator {
    public int nextInt(int n);
    public void setSeed(long seed);

}
