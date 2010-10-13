/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edacc.model;

import edacc.properties.SolverPropertyTypeNotExistException;
import edacc.properties.PropertySource;
import edacc.satinstances.PropertyValueType;
import java.io.File;

/**
 *
 * @author rretz
 */
public class Property extends BaseModel implements IntegerPKModel{

   private int id;
   private String name;
   private String description;
   private String RegularExpression;
   private PropertyType type;
   private PropertyValueType valueType;
   private PropertySource source;
   private boolean multiple;
   private ComputationMethod computationMethod;
   private String computationMethodParameters;

    public ComputationMethod getComputationMethod() {
        return computationMethod;
    }

    public PropertyType getType() {
        return type;
    }

    public void setType(PropertyType type) {
        this.type = type;
        if (this.isSaved())
            this.setModified();
    }

    public void setType(int typeId){
        switch(typeId){
            case 0:
                this.type = PropertyType.InstanceProperty;
                break;
            case 1:
                this.type = PropertyType.ResultProperty;
                break;
        }
        if (this.isSaved())
        this.setModified();
    }

    public int getPropertyTypeDBRepresentation() throws PropertyTypeDoesNotExistException{
        switch(this.type){
            case InstanceProperty:
                return 0;
            case ResultProperty:
                return 1;
            default:
                throw new PropertyTypeDoesNotExistException();
        }
    }

    public void setComputationMethod(ComputationMethod computationMethod) {
        this.computationMethod = computationMethod;
        if (this.isSaved())
            this.setModified();
    }

    public String getComputationMethodParameters() {
        return computationMethodParameters;
    }

    public void setComputationMethodParameters(String computationMethodParameters) {
        this.computationMethodParameters = computationMethodParameters;
        if (this.isSaved())
            this.setModified();
    }

   /**
    *
    * @return true if the Property can have more than one sources in the the file to parse (ResultFile or ClientOutput).
    */
    public boolean isMultiple() {
        return multiple;

    }

    public void setMultiple(boolean multiple) {
        this.multiple = multiple;
        if (this.isSaved())
            this.setModified();
    }

    public void setValueType(PropertyValueType valueType) {
        this.valueType = valueType;
        if (this.isSaved())
            this.setModified();
    }


    public void setName(String name){
        this.name = name;
        if (this.isSaved())
            this.setModified();
    }
    
    public String getName() {
        return name;
    }

    public PropertyValueType<?> getPropertyValueType() {
        return this.valueType;
    }

    /**
     *
     * @return the RegularExpression of the Property
     */
    public String getRegularExpression() {
        return RegularExpression;
    }

    public void setRegularExpression(String RegularExpression) {
        this.RegularExpression = RegularExpression;
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

    public void setPropertySource(PropertySource source){
        this.source = source;
    }

    /**
     *
     * @param typeId the DB representation of the PropertySource
     */
    public void setPropertySource(int sourceId) throws SolverPropertyTypeNotExistException{
        switch(sourceId){
            case 0:
                this.source = PropertySource.LauncherOutput;
                break;
            case 1:
                this.source = PropertySource.Parameter;
                break;
            case 2:
                this.source = PropertySource.SolverOutput;
                break;
            case 3:
                this.source = PropertySource.VerifierOutput;
                break;
            case 4:
                this.source = PropertySource.WatcherOutput;
                break;
            default:
                throw new SolverPropertyTypeNotExistException();

        }
    }

    public PropertySource getPropertySource(){
        return this.source;
    }
/**
 *
 * @return the database representation of the PropertySource of the object. The default return is -1.
 */
    public int getPropertySourceDBRepresentation(){
        switch(source){
            case LauncherOutput:
                return 0;
            case Parameter:
                return 1;
            case SolverOutput:
                return 2;
            case VerifierOutput:
                return 3;
            case WatcherOutput:
                return 4;
            default:
                return -1;
        }
    }

    @Override
    public String toString() {
        return name;
    }


}
