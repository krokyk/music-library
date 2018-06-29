/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kroky.musiclib.gui.table;

import java.awt.Component;
import java.awt.Font;
import java.io.File;

import javax.swing.JTable;
import javax.swing.SortOrder;
import javax.swing.table.DefaultTableCellRenderer;

import org.kroky.commons.swing.tables.EditableTable;
import org.kroky.musiclib.db.entities.Album;
import org.kroky.musiclib.gui.models.AlbumsTableModel;
import org.kroky.musiclib.jobs.ScanDirJob;

/**
 *
 * @author user
 */
public class AlbumsTable extends EditableTable {

    private final AlbumsTableModel model;

    // private HighlightPredicate ifRecentlyUpdated = new HighlightPredicate() {
    //
    // @Override
    // public boolean isHighlighted(Component renderer, ComponentAdapter adapter) {
    // int modelIndex = adapter.convertRowIndexToModel(adapter.row);
    // final Album album = model.getAlbum(modelIndex);
    // return album.isUpdatedRecently();
    // }
    // };
    public AlbumsTable(AlbumsTableModel model, int defaultSortColumn, SortOrder defaultSortOrder) {
        super(model, defaultSortColumn, defaultSortOrder);
        this.model = model;
        setDefaultRenderer(Album.class, new RowRenderer());
        setDefaultRenderer(Integer.class, new RowRenderer());
    }

    public int getIndexOf(Album album) {
        for (int index = 0; index < getRowCount(); index++) {
            if (getAlbum(index).equals(album)) {
                return index;
            }
        }
        return -1;
    }

    /**
     * Gets the correct object from the underlying model regardless of sorting
     *
     * @param index
     *            index of the row in the table
     * @return
     */
    public Album getAlbum(int index) {
        return model.getAlbum(convertRowIndexToModel(index));
    }

    public void removeAlbums(int[] indexes) {
        model.removeAlbums(convertRowIndexesToModel(indexes));
    }

    // <editor-fold defaultstate="collapsed" desc="RowRenderer">
    class RowRenderer extends DefaultTableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
                int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            if (column < 2) {
                Album album = getAlbum(row);
                String filename = album.getBand().getName() + " - " + album.getReleaseYear() + " - " + album.getName();
                // filename = filename.contains(": ") ? filename.replaceAll(": ", " - ") : filename;
                if (!new File(ScanDirJob.getLastScannedDir(), filename).exists()) {
                    if (album.getChecked() == 1) {
                        c.setFont(new Font("Tahoma", Font.PLAIN, 11));
                    } else {
                        c.setFont(new Font("Tahoma", Font.ITALIC, 11));
                    }
                } else {
                    c.setFont(new Font("Tahoma", Font.BOLD, 11)); // file exists
                }
            }
            return c; // To change body of generated methods, choose Tools | Templates.
        }

    }
    // </editor-fold>

}
