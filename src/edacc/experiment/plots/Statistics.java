package edacc.experiment.plots;

import org.rosuda.JRI.REXP;
import org.rosuda.JRI.RVector;
import org.rosuda.JRI.Rengine;

/**
 *
 * @author simon
 */
public class Statistics {

    /**
     * returns pvalue, cor in a double array.
     * @param re
     * @param xsName
     * @param ysName
     * @return
     */
    public static double[] spearmanCorrelation(Rengine re, String xsName, String ysName) {
        double[] res = null;
        try {
            RVector vec = re.eval("cor.test(" + xsName + ", " + ysName + ",method='spearman')").asVector();
            res = new double[2];
            res[0] = ((REXP) vec.get(2)).asDouble();
            res[1] = ((REXP) vec.get(3)).asDouble();
        } catch (Exception e) {
        }
        return res;
    }

    /**
     * returns pvalue, cor in a double array.
     * @param re
     * @param xsName
     * @param ysName
     * @return
     */
    public static double[] pearsonCorrelation(Rengine re, String xsName, String ysName) {
        double[] res = null;
        try {
            RVector vec = re.eval("cor.test(" + xsName + ", " + ysName + ",method='pearson')").asVector();
            res = new double[2];
            res[0] = ((REXP) vec.get(2)).asDouble();
            res[1] = ((REXP) vec.get(3)).asDouble();
        } catch (Exception e) {
        }
        return res;
    }

    public static double[] kolmogorowSmirnow2sampleTest(Rengine re, String xsName, String ysName) {
        double[] res = null;
        try {
            RVector vec = re.eval("ks.test(" + xsName + ", " + ysName + ",alternative='two.sided')").asVector();
            res = new double[2];
            res[0] = ((REXP) vec.get(0)).asDouble();
            res[1] = ((REXP) vec.get(1)).asDouble();
        } catch (Exception e) {
        }
        return res;
    }

    public static double[] wilcoxTest(Rengine re, String xsName, String ysName) {
        double[] res = null;
        try {
            RVector vec = re.eval("wilcox.test(" + xsName + ", " + ysName + ",alternative='two.sided', paired=0)").asVector();
            res = new double[2];
            res[0] = ((REXP) vec.get(0)).asDouble();
            res[1] = ((REXP) vec.get(2)).asDouble();
        } catch (Exception e) {
        }
        return res;
    }
}
