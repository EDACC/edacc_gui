/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edacc.properties;

import edacc.satinstances.PropertyValueType;
import java.util.Vector;
import javax.swing.ComboBoxModel;
import javax.swing.event.ListDataListener;

/**
 *
 * @author rretz
 */
public class PropertyValueComboBoxModel implements ComboBoxModel{
    private Vector<PropertyValueType> list;
    private int selected = -1;

    @Override
    public void setSelectedItem(Object anItem) {
        for(int i = 0; i <= list.size(); i++){
            if(list.get(i).equals(anItem)){
                selected = i;
                break;
            }
        }
    }

    @Override
    public Object getSelectedItem() {
        if(selected != -1)
            return null;
        return list.get(selected);
    }

    @Override
    public int getSize() {
        return list.size();
    }

    @Override
    public Object getElementAt(int index) {
        return list.get(index);
    }

    @Override
    public void addListDataListener(ListDataListener l) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void removeListDataListener(ListDataListener l) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void addPropertyValueTypes(Vector<PropertyValueType> toAdd){
        for(int i = 0; i < toAdd.size(); i++){
            list.add(toAdd.get(i));
        }
    }

    public void removePropertyValueTypes(Vector<PropertyValueType> toRemove){
        for(int i = 0; i < toRemove.size(); i++){
            list.remove(toRemove.get(i));
        }
    }

}
