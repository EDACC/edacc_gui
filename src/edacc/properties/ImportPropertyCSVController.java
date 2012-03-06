/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edacc.properties;

import edacc.EDACCImportPropertyCSV;
import edacc.model.ComputationMethodDoesNotExistException;
import edacc.model.NoConnectionToDBException;
import edacc.model.PropertyDAO;
import edacc.model.PropertyNotInDBException;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author rretz
 */
public class ImportPropertyCSVController {

    EDACCImportPropertyCSV view;
    File csvFile;
    SystemPropertyTableModel sysPropTableModel;
    CSVPropertyTableModel csvPropTableModel;

    public ImportPropertyCSVController(EDACCImportPropertyCSV view, File csvFile) {
        this.view = view;
        this.csvFile = csvFile;
        createSysPropModel();
        createCSVPropModel();
    }

    public CSVPropertyTableModel getCsvPropTableModel() {
        return csvPropTableModel;
    }

    public SystemPropertyTableModel getSysPropTableModel() {
        return sysPropTableModel;
    }

    private void createSysPropModel() {
        try {
            sysPropTableModel = new SystemPropertyTableModel(PropertyDAO.getAll(), this);
        } catch (NoConnectionToDBException ex) {
            Logger.getLogger(ImportPropertyCSVController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (PropertyNotInDBException ex) {
            Logger.getLogger(ImportPropertyCSVController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (PropertyTypeNotExistException ex) {
            Logger.getLogger(ImportPropertyCSVController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ImportPropertyCSVController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ComputationMethodDoesNotExistException ex) {
            Logger.getLogger(ImportPropertyCSVController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(ImportPropertyCSVController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void createCSVPropModel() {
        ArrayList<String> csvProps = getCSVProps();
        csvPropTableModel = new CSVPropertyTableModel(csvProps);
    }

    private ArrayList<String> getCSVProps() {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public String getSelectedCSVProperty() {
        return (String) csvPropTableModel.getValueAt(view.getSelectedCSVPropertyRow(), 0);
    }

    public void dropCSVProp(int convertRowIndexToModel) {
        String toDrop = (String) csvPropTableModel.getValueAt(view.getSelectedCSVPropertyRow(), 0);
        sysPropTableModel.removeRelated(toDrop);
        csvPropTableModel.removeCSVProperty(convertRowIndexToModel);
        sysPropTableModel.fireTableDataChanged();
        csvPropTableModel.fireTableDataChanged();
    }

    public void importCSVData(Boolean overwrite) {
         PropertyDAO.importCSV(sysPropTableModel.getSelected(), overwrite, csvFile);
    }
}
