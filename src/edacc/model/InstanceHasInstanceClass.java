package edacc.model;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * An object of this class represents relation between instances and instance classes.
 * These instance classes can't be source classes! A source instance class of an instance
 * is defined directly in the fields of an instance!
 * This class only manages user defined instance classes, which have the source flag
 * on <code>false</code>!
 * @author dgall
 */
public class InstanceHasInstanceClass extends BaseModel {

    private Instance instance;
    private InstanceClass instanceClass;

    protected InstanceHasInstanceClass(Instance instance, InstanceClass instanceClass) {
        this.instance = instance;
        this.instanceClass = instanceClass;
    }

    public Instance getInstance() {
        return instance;
    }

    public void setInstance(Instance instance) {
        this.instance = instance;
    }

    public InstanceClass getInstanceClass() {
        return instanceClass;
    }

    /**
     * Sets the instace class of this ibject.
     * @param instanceClass
     * @throws InstanceClassMustNotBeSourceException if the given instance class is a source instance class.
     */
    public void setInstanceClass(InstanceClass instanceClass) throws InstanceClassMustNotBeSourceException {
        if (instanceClass.isSource())
            throw new InstanceClassMustNotBeSourceException();
        this.instanceClass = instanceClass;
    }
}
