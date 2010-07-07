package edacc.experiment;

import org.rosuda.JRI.Rengine;

/**
 *
 * @author simon
 */
public class AnalyseController {
    private static Rengine re = null;

    public static Rengine getREngine() throws REngineInitializationException {
        try {
            if (re == null || !re.isAlive()) {
                if (!Rengine.versionCheck()) {
                    throw new REngineInitializationException("** Version mismatch - Java files don't match library version.");
                }
                re = new Rengine(new String[]{}, false, null);
                if (!re.waitForR()) {
                    throw new REngineInitializationException("Cannot load R.");
                }
            }
            if (re.eval("library(JavaGD)") == null) {
                re.end();
                re = null;
                throw new REngineInitializationException("Did not find JavaGD.");
            }
            re.eval("Sys.putenv('JAVAGD_CLASS_NAME'='edacc/model/RPlotDevice')");
            re.eval("JavaGD()");

            return re;
        } catch (Exception ex) {
            throw new REngineInitializationException(ex.getMessage());
        }
    }
}
