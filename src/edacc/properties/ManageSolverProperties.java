/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edacc.properties;

import edacc.EDACCManagePropertyMode;
import javax.swing.JPanel;
import javax.swing.JTable;

/**
 *
 * @author rretz
 */
public class ManageSolverProperties {
    private EDACCManagePropertyMode main;
    private JPanel panel;
    private JTable tableSolverProperty;

    public ManageSolverProperties(EDACCManagePropertyMode manage, JPanel panelManageResultProperty, JTable tableResultProperty) {
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

}
