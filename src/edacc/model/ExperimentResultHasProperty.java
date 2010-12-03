/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edacc.model;

import edacc.properties.PropertyTypeNotExistException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author rretz
 */
public class ExperimentResultHasProperty extends BaseModel implements IntegerPKModel {

    private int id;
    private ExperimentResult expResult;
    private Property solvProperty;
    private Vector<String> value;
    private int expResId;
    private int propId;

    public int getExpResId() {
        return expResId;
    }

    public void setExpResId(int expResId) {
        this.expResId = expResId;
    }

    public int getPropId() {
        return propId;
    }

    public void setPropId(int propId) {
        this.propId = propId;
    }

    public ExperimentResult getExpResult() {
        if(expResult == null){
            try {
                expResult = ExperimentResultDAO.getById(expResId);
            } catch (NoConnectionToDBException ex) {
                Logger.getLogger(ExperimentResultHasProperty.class.getName()).log(Level.SEVERE, null, ex);
            } catch (SQLException ex) {
                Logger.getLogger(ExperimentResultHasProperty.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ExperimentResultNotInDBException ex) {
                Logger.getLogger(ExperimentResultHasProperty.class.getName()).log(Level.SEVERE, null, ex);
            } catch (PropertyTypeNotExistException ex) {
                Logger.getLogger(ExperimentResultHasProperty.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(ExperimentResultHasProperty.class.getName()).log(Level.SEVERE, null, ex);
            } catch (PropertyNotInDBException ex) {
                Logger.getLogger(ExperimentResultHasProperty.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ComputationMethodDoesNotExistException ex) {
                Logger.getLogger(ExperimentResultHasProperty.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ExpResultHasSolvPropertyNotInDBException ex) {
                Logger.getLogger(ExperimentResultHasProperty.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return expResult;
    }

    public void setExpResult(ExperimentResult expResult) {
        this.expResult = expResult;
        if (this.isSaved())
            this.setModified();
    }

    public Property getProperty() {
        if(solvProperty == null)
            try {
            solvProperty = PropertyDAO.getById(propId);
        } catch (NoConnectionToDBException ex) {
            Logger.getLogger(ExperimentResultHasProperty.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(ExperimentResultHasProperty.class.getName()).log(Level.SEVERE, null, ex);
        } catch (PropertyNotInDBException ex) {
            Logger.getLogger(ExperimentResultHasProperty.class.getName()).log(Level.SEVERE, null, ex);
        } catch (PropertyTypeNotExistException ex) {
            Logger.getLogger(ExperimentResultHasProperty.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ExperimentResultHasProperty.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ComputationMethodDoesNotExistException ex) {
            Logger.getLogger(ExperimentResultHasProperty.class.getName()).log(Level.SEVERE, null, ex);
        }
        return solvProperty;
    }

    public void setSolvProperty(Property solvProperty) {
        this.solvProperty = solvProperty;
        if (this.isSaved())
            this.setModified();
    }

    public Vector<String> getValue() {
        return value;
    }

    public void setValue(Vector<String> value) {
        this.value = value;
                if (this.isSaved())
            this.setModified();
    }

    @Override
    public int getId() {
        return id;
    }
    
    public void setId(int id){
        this.id = id;
        if (this.isSaved())
            this.setModified();
    }


}
