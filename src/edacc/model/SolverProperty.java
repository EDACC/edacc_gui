/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edacc.model;

import edacc.properties.SolverPropertyTypeNotExistException;
import edacc.properties.SolverPropertyType;
import edacc.satinstances.PropertyInput;
import edacc.satinstances.PropertyValueType;

/**
 *
 * @author rretz
 */
public class SolverProperty extends BaseModel implements IntegerPKModel{

   private int id;
   private String name;
   private String prefix;
   private String description;
   private PropertyValueType valueType;
   private SolverPropertyType solvPropertyType;

    public void setValueType(PropertyValueType valueType) {
        this.valueType = valueType;
        if (this.isSaved())
            this.setModified();
    }


    public void setName(String name){
        if (this.isSaved())
            this.setModified();
    }
    
    public String getName() {
        return name;
    }

    public PropertyValueType<?> getPropertyValueType() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     *
     * @return the prefix of the SolverProperty
     */
    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
        if (this.isSaved())
            this.setModified();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public void setSolverPropertyType(SolverPropertyType type){
        this.solvPropertyType = type;
    }

    /**
     *
     * @param typeId the DB representation of the SolverPropertyType
     */
    public void setSolverPropertyType(int typeId) throws SolverPropertyTypeNotExistException{
        switch(typeId){
            case 0:
                this.solvPropertyType = SolverPropertyType.ResultFile;
            case 1:
                this.solvPropertyType = SolverPropertyType.ClientOutput;
            case 2:
                this.solvPropertyType = SolverPropertyType.Parameter;
            default:
                throw new SolverPropertyTypeNotExistException();

        }
    }

    public SolverPropertyType getSolverPropertyType(){
        return this.solvPropertyType;
    }
/**
 *
 * @return the database representation of the SolverPropertyType of the object. The default return is -1.
 */
    public int getSolverPropertyTypeDBRepresentation(){
        switch(solvPropertyType){
            case ResultFile:
                return 0;
            case ClientOutput:
                return 1;
            case Parameter:
                return 2;
            default:
                return -1;
        }
    }

}
