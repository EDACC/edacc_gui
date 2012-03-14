package edacc.manageDB;

import edacc.model.Verifier;
import edacc.model.VerifierDAO;
import java.sql.SQLException;
import java.util.List;

/**
 *
 * @author simon
 */
public class ManageDBVerifiers {
    private VerifierTableModel model;
    
    public ManageDBVerifiers(VerifierTableModel model) {
        this.model = model;
    }
    
    public void loadVerifiers() throws SQLException {
       model.setVerifiers(VerifierDAO.getAllVerifiers());
    }

    public void addVerifier(Verifier verifier) {
        model.addVerifier(verifier);
    }

    public Verifier getVerifier(int rowIndex) {
        return model.getVerifier(rowIndex);
    }

    public void verifierUpdated(int index) {
        model.fireTableRowsUpdated(index, index);
    }

    public List<Verifier> getVerifiers() {
        return model.getVerifiers();
    }
}
