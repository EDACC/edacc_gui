/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edacc.manageDB;

import edacc.model.Verifier;
import edacc.model.VerifierParameter;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author simon
 */
public class VerifierParameterTableModel extends AbstractTableModel{
    public static final int ORDER = 0;
    public static final int NAME = 1;
    public static final int PREFIX = 2;
    public static final int HASVALUE = 3;
    public static final int MANDATORY = 4;
    public static final int SPACE = 5;
    public static final int ATTACHTOPREVIOUS = 6;
    private Verifier verifier;

    private String[] colums = {"Order", "Name", "Prefix", "Boolean", "Mandatory", "Space", "Attach to previous"};

    private List<VerifierParameter> params;
    /**
     * Sets the verifier.
     * @param solver
     */
    public void setVerifier(Verifier verifier) {
        this.verifier = verifier;
        params = new ArrayList<VerifierParameter>();
        for (VerifierParameter p : verifier.getParameters()) {
            params.add(new VerifierParameter(p));
        }
    }
    
    public void remove(VerifierParameter param){
        int i;
        for (i = 0; i < params.size(); i++) {
            if (params.get(i) == param) {
                params.remove(i);
                
                break;
            }
        }
        fireTableRowsDeleted(i,i);
    }

    @Override
    public int getRowCount() {
        return params == null ? 0 : params.size();
    }

    @Override
    public int getColumnCount() {
        return colums.length;
    }

    @Override
    public String getColumnName(int col){
        return colums[col];
    }

    @Override
    public Object getValueAt(int rowIndex, int columIndex) {
        VerifierParameter p = params.get(rowIndex);
        switch(columIndex){
            case ORDER:
                return p.getOrder();
            case NAME:
                return p.getName();
            case PREFIX:
                return p.getPrefix();
            case HASVALUE:
                return p.getHasValue()?"":"\u2713";//p.getHasValue();
            case MANDATORY:
                return !p.isMandatory()?"":"\u2713";
            case SPACE:
                return !p.getSpace()?"":"\u2713";
            case ATTACHTOPREVIOUS:
                return !p.isAttachToPrevious()?"":"\u2713";
        }
        return null;
    }

    public void addParameter(VerifierParameter param) {
        params.add(param);
        fireTableRowsInserted(params.size()-1, params.size()-1);
    }

    public int getHighestOrder() {
        int max = 0;
        for (VerifierParameter p : params)
            if (p.getOrder() > max)
                max = p.getOrder();
        return max;
    }
    
    public VerifierParameter getParameter(int row) {
        return params.get(row);
    }
    
    public List<VerifierParameter> getParameters() {
        return params;
    }
}
