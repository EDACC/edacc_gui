/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edacc.test;

import edacc.satinstances.ConvertException;
import edacc.test.TestType.MyType;

/**
 *
 * @author dgall
 */
public class TestType extends edacc.satinstances.PropertyValueType<MyType> {

    @Override
    public String getName() {
        return "MyType";
    }

    @Override
    public boolean isDefault() {
        return false;
    }

    @Override
    public MyType getJavaTypeRepresentation(String p) throws ConvertException {
        return new MyType(p.substring(0, p.length()/2), p.substring(p.length()/2, p.length()));
    }

    @Override
    protected String convertToStringRepresentation(MyType p) throws ConvertException {
        return p.s1 + p.s2;
    }

    @Override
    public Class<?> getJavaType() {
        return MyType.class;
    }


    public class MyType {
        
        private String s1;
        private String s2;

        public MyType(String s1, String s2) {
            this.s1 = s1;
            this.s2 = s2;
        }
    }

    public static void main(String args[]) throws ConvertException {
        TestType t = new TestType();
        System.out.println(t.getJavaTypeRepresentation("Hallo").s1);
        System.out.println(t.getJavaTypeRepresentation("Hallo").s2);
        System.out.println(t.getStringRepresentation(t.getJavaTypeRepresentation("Hallo")));
    }


}
