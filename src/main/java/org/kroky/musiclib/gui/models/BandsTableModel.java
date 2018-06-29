/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kroky.musiclib.gui.models;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import org.kroky.musiclib.db.DAO;
import org.kroky.musiclib.db.entities.Album;
import org.kroky.musiclib.db.entities.Band;

/**
 *
 * @author Kroky
 */
public class BandsTableModel extends AbstractTableModel {

    private final List<Band> bands;

    private static final String[] COLUMN_NAMES = new String[]{
        "Name", "URL", "Missing"
    };

    private static final Class<?>[] COLUMN_CLASSES = new Class<?>[]{
        Band.class, String.class, Integer.class
    };

    public BandsTableModel(Collection<Band> bands) {
        this.bands = new ArrayList<>(bands);
    }

    public BandsTableModel() {
        bands = new ArrayList<>();
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return COLUMN_CLASSES[columnIndex];
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return columnIndex == 1;
    }

    @Override
    public String getColumnName(int column) {
        return COLUMN_NAMES[column];
    }

    @Override
    public int getRowCount() {
        return bands.size();
    }

    @Override
    public int getColumnCount() {
        return COLUMN_NAMES.length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Band band = bands.get(rowIndex);
        switch (columnIndex) {
            case 0:
                return band;
            case 1:
                return band.getUrl();
            case 2:
                int size = band.getAlbums().size();
                int i = 0;
                for (Album album : band.getAlbums()) {
                    if (album.getChecked() == 1) {
                        i++;
                    }
                }
                return size - i;
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
        Band band = bands.get(rowIndex);
        switch (columnIndex) {
            case 1:
                band.setUrl((String) aValue);
                break;
            default:
                throw new IllegalArgumentException("Column not editable: " + COLUMN_NAMES[columnIndex]);
        }
        DAO.saveOrUpdate(band);
        fireTableCellUpdated(rowIndex, columnIndex);
    }

    public Band getBand(int index) {
        return bands.get(index);
    }

    public void replaceAll(Collection<Band> bands) {
        this.bands.clear();
        addAll(bands);
    }

    public void addAll(Collection<Band> bands) {
        this.bands.addAll(bands);
        fireTableDataChanged();
    }

    public boolean exists(Band band) {
        return bands.contains(band);
    }

    public Band getBand(String name) {
        for (Band band : bands) {
            if (band.getName().equals(name)) {
                return band;
            }
        }
        return null;
    }

    public List<Band> getBands() {
        return bands;
    }
}
