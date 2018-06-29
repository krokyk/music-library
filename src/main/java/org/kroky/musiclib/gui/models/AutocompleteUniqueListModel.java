/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kroky.musiclib.gui.models;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import javax.swing.AbstractListModel;
import javax.swing.MutableComboBoxModel;

/**
 *
 * @author Peter Krokavec
 */
public class AutocompleteUniqueListModel<E> extends AbstractListModel<E> implements MutableComboBoxModel<E> {

    private final List<E> data = new ArrayList<>();

    private E selectedItem;

    public AutocompleteUniqueListModel(Collection<E> elements) {
        data.addAll(elements);
    }

    public AutocompleteUniqueListModel(E[] elements) {
        data.addAll(Arrays.asList(elements));
    }

    @Override
    public int getSize() {
        return data.size();
    }

    @Override
    public E getElementAt(int index) {
        int i = 0;
        for (E el : data) {
            if (index == i) {
                return el;
            }
            i++;
        }
        throw new IndexOutOfBoundsException(index + " Size of the data set is " + getSize());
    }

    @Override
    public void setSelectedItem(Object anItem) {
        selectedItem = (E) anItem;
    }

    @Override
    public Object getSelectedItem() {
        return selectedItem;
    }

    @Override
    public void addElement(E item) {
        data.add(item);
    }

    @Override
    public void removeElement(Object obj) {
        data.remove(obj);
    }

    @Override
    public void insertElementAt(E item, int index) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void removeElementAt(int index) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
