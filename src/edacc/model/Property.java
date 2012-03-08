/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edacc.model;

import edacc.properties.PropertyTypeNotExistException;
import edacc.properties.PropertySource;
import edacc.satinstances.PropertyValueType;
import edacc.satinstances.PropertyValueTypeManager;
import java.io.IOException;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author rretz
 */
public class Property extends BaseModel implements IntegerPKModel, Serializable {

    private int id;
    private String name;
    private String description;
    private Vector<String> RegularExpression;
    private PropertyType type;
    private transient PropertyValueType valueType;
    private String propertyValueTypeName;
    private PropertySource source;

    public String getPropertyValueTypeName() {
        return propertyValueTypeName;
    }

    public void setPropertyValueTypeName(String propertyValueTypeName) {
        this.propertyValueTypeName = propertyValueTypeName;
    }

    public PropertySource getSource() {
        return source;
    }

    public void setSource(PropertySource source) {
        this.source = source;
    }
    private boolean multiple;
    private ComputationMethod computationMethod;
    private String computationMethodParameters;
    private Boolean isDefault;

    public ComputationMethod getComputationMethod() {
        return computationMethod;
    }

    public PropertyType getType() {
        return type;
    }

    public void setType(PropertyType type) {
        this.type = type;
        if (this.isSaved()) {
            this.setModified();
        }
    }

    public void setType(int typeId) {
        switch (typeId) {
            case 0:
                this.type = PropertyType.InstanceProperty;
                break;
            case 1:
                this.type = PropertyType.ResultProperty;
                break;
        }
        if (this.isSaved()) {
            this.setModified();
        }
    }

    public int getPropertyTypeDBRepresentation() throws PropertyTypeDoesNotExistException {
        switch (this.type) {
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
        if (this.isSaved()) {
            this.setModified();
        }
    }

    public String getComputationMethodParameters() {
        return computationMethodParameters;
    }

    public void setComputationMethodParameters(String computationMethodParameters) {
        this.computationMethodParameters = computationMethodParameters;
        if (this.isSaved()) {
            this.setModified();
        }
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
        if (this.isSaved()) {
            this.setModified();
        }
    }

    public void setValueType(PropertyValueType valueType) {
        this.valueType = valueType;
        this.propertyValueTypeName = valueType.getName();
        if (this.isSaved()) {
            this.setModified();
        }
    }

    public void setName(String name) {
        this.name = name;
        if (this.isSaved()) {
            this.setModified();
        }
    }

    public String getName() {
        return name;
    }

    public PropertyValueType<?> getPropertyValueType() {
        if (valueType == null) {
            try {
                this.valueType = PropertyValueTypeManager.getInstance().getPropertyValueTypeByName(propertyValueTypeName);
            } catch (IOException ex) {
                Logger.getLogger(Property.class.getName()).log(Level.SEVERE, null, ex);
            } catch (NoConnectionToDBException ex) {
                Logger.getLogger(Property.class.getName()).log(Level.SEVERE, null, ex);
            } catch (SQLException ex) {
                Logger.getLogger(Property.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return this.valueType;
    }

    /**
     *
     * @return the RegularExpression of the Property
     */
    public Vector<String> getRegularExpression() {
        return RegularExpression;
    }

    public void setRegularExpression(Vector<String> RegularExpression) {
        this.RegularExpression = RegularExpression;
        if (this.isSaved()) {
            this.setModified();
        }
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
        if (this.isSaved()) {
            this.setModified();
        }
    }

    @Override
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
        if (this.isSaved()) {
            this.setModified();
        }

    }

    public void setPropertySource(PropertySource source) {
        this.source = source;
    }

    /**
     *
     * @param typeId the DB representation of the PropertySource
     */
    public void setPropertySource(int sourceId) throws PropertyTypeNotExistException {
        switch (sourceId) {
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
            case 5:
                this.source = PropertySource.Instance;
                break; // TODO vernuenftige Absprache
            case 6:
                this.source = PropertySource.InstanceName;
                break;
            case 7:
                this.source = PropertySource.ExperimentResults;
                break;
            case 8:
                this.source = PropertySource.CSVImport;
                break;
            default:
                throw new PropertyTypeNotExistException();

        }
        if (this.isSaved()) {
            this.setModified();
        }
    }

    public PropertySource getPropertySource() {
        return this.source;
    }

    /**
     *
     * @return the database representation of the PropertySource of the object. The default return is -1.
     */
    public int getPropertySourceDBRepresentation() {
        switch (source) {
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
            case Instance:
                return 5;
            case InstanceName:
                return 6;
            case ExperimentResults:
                return 7;
            case CSVImport:
                return 8;
            default:
                return -1;
        }
    }

    @Override
    public String toString() {
        return name;
    }

    public Boolean IsDefault() {
        return isDefault;
    }

    public void setIsDefault(Boolean isDefault) {
        this.isDefault = isDefault;
        if (this.isSaved()) {
            this.setModified();
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Property other = (Property) obj;
        if (this.id != other.id) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 47 * hash + this.id;
        return hash;
    }
}
