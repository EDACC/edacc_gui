/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edacc.properties;

import edacc.EDACCApp;
import edacc.EDACCImportPropertyCSV;
import edacc.EDACCManagePropertyDialog;
import edacc.model.ComputationMethodDoesNotExistException;
import edacc.model.NoConnectionToDBException;
import edacc.model.Property;
import edacc.model.PropertyDAO;
import edacc.model.PropertyNotInDBException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;

/**
 *
 * @author rretz
 */
public class ImportPropertyCSVController {

    EDACCImportPropertyCSV view;
    File csvFile;
    SystemPropertyTableModel sysPropTableModel;
    CSVPropertyTableModel csvPropTableModel;

    public ImportPropertyCSVController(EDACCImportPropertyCSV view, File csvFile) throws FileNotFoundException, IOException {
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

    private void createCSVPropModel() throws FileNotFoundException, IOException {
        ArrayList<String> csvProps = getCSVProps();
        csvPropTableModel = new CSVPropertyTableModel(csvProps);
    }

    /**
     * Instance identifier columns "name" and "md5" are removed from the possible properties, if they 
     * match with the provided names like "Name", "name", "NAME", "md5", "MD5" or "Md5".
     * @return ArrayList<String> of the possilbe properties identified in the firstLine of the csv file.
     * @throws FileNotFoundException
     * @throws IOException 
     */
    private ArrayList<String> getCSVProps() throws FileNotFoundException, IOException {
        BufferedReader br = new BufferedReader(new FileReader(csvFile));
        String firstLine = br.readLine();
        ArrayList<String> fstLineData = new ArrayList<String>(Arrays.asList(firstLine.split(",")));

        // Remove the provided Instance indentifier names
        fstLineData.remove("Name");
        fstLineData.remove("name");
        fstLineData.remove("NAME");
        fstLineData.remove("md5");
        fstLineData.remove("MD5");
        fstLineData.remove("Md5");

        return fstLineData;
    }

    public String getSelectedCSVProperty() {
        if (view.getSelectedCSVPropertyRow() == -1) {
            return "";
        }
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

    public void ManageProperties() {
        JFrame mainFrame = EDACCApp.getApplication().getMainFrame();
        EDACCManagePropertyDialog manageSolverProperties = new EDACCManagePropertyDialog(mainFrame, true);
        manageSolverProperties.setLocationRelativeTo(mainFrame);
        manageSolverProperties.initialize();
        manageSolverProperties.setVisible(true);
        try {
            sysPropTableModel.updateProperties(new ArrayList<Property>(PropertyDAO.getAllInstanceProperties()));
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

    public void refreshTables() {
        csvPropTableModel.fireTableDataChanged();
        sysPropTableModel.fireTableDataChanged();
    }

    public void refreshSysPropTable() {
        sysPropTableModel.fireTableDataChanged();
    }

    public boolean LinkPropChoosen(int rowOfCSVPropertyTable) {
        return sysPropTableModel.isSelected((String)csvPropTableModel.getValueAt(rowOfCSVPropertyTable, 0));
    }

    public void refreshCSVPropTable() {
       int save  = view.getSelectedCSVPropertyRow();
       csvPropTableModel.fireTableDataChanged();
       view.setSelectedCSVPropertyRow(save);
    }

    public boolean propAlreadyChoosen(int convertRowIndexToModel) {
        return sysPropTableModel.propertyAlreadyChoosen(convertRowIndexToModel);
    }
}
