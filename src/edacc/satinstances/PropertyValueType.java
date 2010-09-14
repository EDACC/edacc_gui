/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edacc.satinstances;

/**
 *
 * @author dgall
 */
public abstract class PropertyValueType<T> {

    public abstract String getName();
    public abstract boolean isDefault();

    /**
     * Returns the string representation of a PropertyValue p of the given type.
     * @param p
     * @return
     * @throws ConvertException if an error occurs while converting the value to
     * its string representation.
     * @throws IllegalArgumentException if the given property value has the wrong type.
     */
    public String getStringRepresentation(T p) throws ConvertException, IllegalArgumentException {
        if (p.getClass().equals(getJavaType()))
            return convertToStringRepresentation(p);
        throw new ConvertException();
    }

    /**
     * Returns the representation of a string in the given Java type.
     * @param p
     * @return
     * @throws ConvertException if the given string isn't a valid representation of the expected type.
     */
    public abstract T getJavaTypeRepresentation(String p) throws ConvertException;

   /**
    * Returns the string representation of a PropertyValue p of the given type.
    * This method is used for the representation of the value in the database.
    * @param p
    * @return
    * @throws ConvertException if an error occured while converting the value to
    * its string representation.
    */
    protected abstract String convertToStringRepresentation(T p) throws ConvertException;

    /**
     * Returns the Java Type of the Value.
     * @return
     */
    public abstract Class<?> getJavaType();

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof PropertyValueType<?>))
            return false;
        PropertyValueType<?> p = (PropertyValueType<?>) o;
        return p.getJavaType() == this.getJavaType() && p.getName().equals(this.getName());
    }
    
    @Override
    public int hashCode() {
        return this.getJavaType().hashCode() + this.getName().hashCode();
    }
}
