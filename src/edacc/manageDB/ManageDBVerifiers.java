package edacc.manageDB;

import edacc.model.Verifier;
import edacc.model.VerifierDAO;
import java.sql.SQLException;
import java.util.LinkedList;
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
       VerifierDAO.clearCache();
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

    public void save() throws SQLException {
        VerifierDAO.saveAllCached();
        List<Verifier> verifiers = new LinkedList<Verifier>();
        for (Verifier v : model.getVerifiers()) {
            if (!v.isSaved()) {
                verifiers.add(v);
            }
        }
        VerifierDAO.saveAll(verifiers);
    }

    public void markAsDeleted(Verifier v) {
        v.setDeleted();
        for (int row = 0; row < model.getRowCount(); row++) {
            if (model.getVerifier(row) == v) {
                model.removeVerifier(row);
                break;
            }
        }
    }
}
