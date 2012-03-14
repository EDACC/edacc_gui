package edacc.manageDB;

import edacc.JTableTooltipInformation;
import edacc.model.Verifier;
import java.awt.Point;

/**
 *
 * @author simon
 */
public class VerifierTable extends JTableTooltipInformation {

    @Override
    public String getToolTipText() {
        Point mouseLocation = getMousePosition(); 
        
        int row = rowAtPoint(mouseLocation);
        if (row != -1) {
            if (getModel() instanceof VerifierTableModel) {
                VerifierTableModel model = (VerifierTableModel) getModel();
                Verifier v = model.getVerifier(convertRowIndexToModel(row));
                
                super.setToolTipText(v.getDescription().replace("\n", "<br>"));
            } else {
                super.setToolTipText("");
            }
        } else {
            super.setToolTipText("");
        }
        return super.getToolTipText();
    }
    
}
