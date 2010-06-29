/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edacc.satinstances;

/**
 *
 * @author dgall
 */
public class IntegerPropertyValueType extends PropertyValueType<Integer> {

    @Override
    protected String convertToStringRepresentation(Integer p) throws ConvertException {
        return Integer.toString(p);
    }

    @Override
    public Class<?> getJavaType() {
        return Integer.class;
    }

    @Override
    public Integer getJavaTypeRepresentation(String p) throws ConvertException {
        try {
            return Integer.parseInt(p);
        } catch (NumberFormatException e) {
            throw new ConvertException(e);
        }
    }

    @Override
    public String getName() {
        return "Integer";
    }

}
