    /*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edacc.satinstances;

/**
 *
 * @author rretz
 */
public class FloatPropertyValueType extends PropertyValueType<Float>{

    @Override
    public String getName() {
        return "Float";
    }

    @Override
    public boolean isDefault() {
        return true;
    }

    @Override
    public Float getJavaTypeRepresentation(String p) throws ConvertException {
         try {
            return Float.parseFloat(p);
        } catch (NumberFormatException e) {
            throw new ConvertException(e);
        }
    }

    @Override
    protected String convertToStringRepresentation(Float p) throws ConvertException {
        return Float.toString(p);
    }

    @Override
    public Class<?> getJavaType() {
        return Float.class;
    }

}
