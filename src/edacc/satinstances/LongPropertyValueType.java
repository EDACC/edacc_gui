/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edacc.satinstances;

/**
 *
 * @author rretz
 */
public class LongPropertyValueType extends PropertyValueType<Long> {

    @Override
    public String getName() {
        return "Long";
    }

    @Override
    public boolean isDefault() {
        return true;
    }

    @Override
    public Long getJavaTypeRepresentation(String p) throws ConvertException {
       try {
            return Long.parseLong(p);
       } catch (NumberFormatException e) {
            throw new ConvertException(e);
       }
    }

    @Override
    protected String convertToStringRepresentation(Long p) throws ConvertException {
        return Long.toString(p);
    }

    @Override
    public Class<?> getJavaType() {
        return long.class;
    }
    
}
