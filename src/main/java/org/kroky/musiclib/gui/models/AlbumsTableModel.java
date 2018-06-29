/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kroky.musiclib.gui.models;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;
import javax.swing.table.AbstractTableModel;
import org.kroky.musiclib.db.DAO;
import org.kroky.musiclib.db.entities.Album;
import org.kroky.musiclib.db.entities.Band;

/**
 *
 * @author Kroky
 */
public class AlbumsTableModel extends AbstractTableModel {

    private final List<Album> albums;

    private static final String[] COLUMN_NAMES = new String[]{
        "Name", "Year", "Checked"
    };

    private static final Class<?>[] COLUMN_CLASSES = new Class<?>[]{
        Album.class, Integer.class, Boolean.class
    };

    public AlbumsTableModel(Collection<Album> albums) {
        this.albums = new ArrayList<>(albums);
    }

    public AlbumsTableModel() {
        albums = new ArrayList<>();
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return COLUMN_CLASSES[columnIndex];
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return columnIndex == 2;
    }

    @Override
    public String getColumnName(int column) {
        return COLUMN_NAMES[column];
    }

    @Override
    public int getRowCount() {
        return albums.size();
    }

    @Override
    public int getColumnCount() {
        return COLUMN_NAMES.length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Album album = albums.get(rowIndex);
        switch (columnIndex) {
            case 0:
                return album;
            case 1:
                return album.getReleaseYear();
            case 2:
                return album.getChecked() == 1;
            default:
                throw new IndexOutOfBoundsException("Invalid column index: " + String.valueOf(columnIndex));
        }
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        Object oldValue = getValueAt(rowIndex, columnIndex);
        if (oldValue != null && oldValue.equals(aValue)) {
            return;
        }
        Album album = albums.get(rowIndex);
        switch (columnIndex) {
            case 2:
                album.setChecked((boolean) aValue ? 1 : 0);
                break;
            default:
                throw new IllegalArgumentException("Column not editable: " + COLUMN_NAMES[columnIndex]);
        }
        DAO.saveOrUpdate(album);
        fireTableRowsUpdated(rowIndex, rowIndex);
    }

    public Album getAlbum(int index) {
        return albums.get(index);
    }

    public void replaceAll(Collection<Album> albums) {
        this.albums.clear();
        addAll(albums);
    }

    public void addAll(Collection<Album> albums) {
        this.albums.addAll(albums);
        fireTableDataChanged();
    }

    public boolean exists(Album album) {
        return albums.contains(album);
    }

    public Album getAlbum(String name) {
        for (Album album : albums) {
            if (album.getName().equals(name)) {
                return album;
            }
        }
        return null;
    }

    public List<Album> getAlbums() {
        return albums;
    }

    public void addAlbum(Album album) {
        this.albums.add(album);
        fireTableRowsInserted(getRowCount() - 1, getRowCount() - 1);
    }

    public void removeAlbums(int[] indexes) {
        TreeSet<Integer> set = new TreeSet<>();
        for (int index : indexes) {
            set.add(index);
        }
        for (Iterator<Integer> iterator = set.descendingIterator(); iterator.hasNext();) {
            int index = iterator.next();
            final Album album = albums.get(index);
            Band band = album.getBand();
            band.getAlbums().remove(albums.remove(index));
            DAO.saveOrUpdate(band);
        }
        fireTableDataChanged();
    }
}
