/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edacc.properties;

import edacc.EDACCManageSolverPropertyDialog;
import javax.swing.JPanel;
import javax.swing.JTable;

/**
 *
 * @author rretz
 */
public class SolverPropertiesController {
    private EDACCManageSolverPropertyDialog main;
    private JPanel panel;
    private JTable tableSolverProperty;

    public SolverPropertiesController(EDACCManageSolverPropertyDialog manage, JPanel panelManageResultProperty, JTable tableResultProperty) {
        this.main = manage;
        this.panel = panelManageResultProperty;
        this.tableSolverProperty = tableResultProperty;
    }


    public void showSolverPropertyEditField(boolean enable) {
        main.enableSolverPropertyEditField(enable);
    }

    public void NewSolverProperty() {
        showSolverPropertyEditField(true);
        clearSolverPropertyEditField();
    }

    private void clearSolverPropertyEditField() {
       main.clearSolverPropertyEditField();
    }

    public void SolverPropertyTypeChanged() {
        main.SolverPropertyTypeChanged();
    }

}
