/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edacc.satinstances;

/**
 *
 * @author rretz
 */
public class StringPropertyValueType extends PropertyValueType<String> {

    @Override
    public String getName() {
        return "String";
    }

    @Override
    public boolean isDefault() {
        return true;
    }

    @Override
    public String getJavaTypeRepresentation(String p) throws ConvertException {
            return p;
    }

    @Override
    protected String convertToStringRepresentation(String p) throws ConvertException {
        return p;
    }

    @Override
    public Class<?> getJavaType() {
        return String.class;
    }
}
