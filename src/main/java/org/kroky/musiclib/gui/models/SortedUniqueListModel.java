/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kroky.musiclib.gui.models;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.TreeSet;
import javax.swing.AbstractListModel;
import javax.swing.MutableComboBoxModel;

/**
 *
 * @author Peter Krokavec
 */
public class SortedUniqueListModel<E> extends AbstractListModel<E> implements MutableComboBoxModel<E> {

    private final TreeSet<E> data = new TreeSet<>();

    private final int MAX_SIZE;

    private E selectedItem;

    public SortedUniqueListModel(Collection<E> elements) {
        this(elements, -1);
    }

    public SortedUniqueListModel(E[] elements) {
        this(elements, -1);
    }

    public SortedUniqueListModel(Collection<E> elements, int maxSize) {
        MAX_SIZE = maxSize;
        int i = 0;
        for (E el : elements) {
            if (i == MAX_SIZE) {
                break;
            }
            i++;
            data.add(el);
        }
    }

    public SortedUniqueListModel(E[] elements, int maxSize) {
        this(Arrays.asList(elements), maxSize);
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
        for (E el : data) {
            if (el.equals(anItem)) {
                selectedItem = el;
                break;
            }
        }
    }

    @Override
    public E getSelectedItem() {
        return selectedItem;
    }

    @Override
    public void addElement(E item) {
        Iterator<E> it = data.iterator();
        while (it.hasNext()) {
            if (it.next().equals(item)) {
                it.remove();
            }
        }
        //if after removal the size is still at its max (meaning that actually nothing was removed, remove the last item
        if (data.size() == MAX_SIZE) {
            E toRemove = data.last();
            data.remove(toRemove);
        }
        data.add(item);
        fireContentsChanged(this, 0, data.size() - 1);
    }

    @Override
    public void removeElement(Object obj) {
        final E el = (E) obj;
        Iterator<E> it = data.iterator();
        while (it.hasNext()) {
            if (it.next().equals(el)) {
                it.remove();
            }
        }
        fireContentsChanged(this, 0, data.size() - 1);
    }

    @Override
    public void insertElementAt(E item, int index) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void removeElementAt(int index) {
        Iterator<E> it = data.iterator();
        int i = 0;
        while (it.hasNext()) {
            it.next();
            if (index == i) {
                it.remove();
                break;
            }
            i++;
        }
    }

}
