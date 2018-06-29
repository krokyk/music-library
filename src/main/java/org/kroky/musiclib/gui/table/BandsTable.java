/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kroky.musiclib.gui.table;

import java.awt.Component;
import java.awt.Font;
import java.util.Set;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SortOrder;
import javax.swing.table.DefaultTableCellRenderer;

import org.kroky.commons.swing.tables.GenericTable;
import org.kroky.musiclib.db.entities.Album;
import org.kroky.musiclib.db.entities.Band;
import org.kroky.musiclib.gui.Colors;
import org.kroky.musiclib.gui.models.BandsTableModel;

/**
 *
 * @author user
 */
public class BandsTable extends GenericTable {

    private final BandsTableModel model;

    // private HighlightPredicate ifRecentlyUpdated = new HighlightPredicate() {
    //
    // @Override
    // public boolean isHighlighted(Component renderer, ComponentAdapter adapter) {
    // int modelIndex = adapter.convertRowIndexToModel(adapter.row);
    // final Band band = model.getBand(modelIndex);
    // return band.isUpdatedRecently();
    // }
    // };
    public BandsTable(BandsTableModel model, int defaultSortColumn, SortOrder defaultSortOrder) {
        super(model, defaultSortColumn, defaultSortOrder);
        this.model = model;

        styleModel.addStyle((c, table, row, column) -> {
            if (table.isRowSelected(row)) {
                c.setForeground(table.getSelectionForeground());
                c.setBackground(table.getSelectionBackground());
                return;
            } else {
                c.setForeground(table.getForeground());
            }
            Band band = model.getBand(convertRowIndexToModel(row));
            Set<Album> albums = band.getAlbums();
            boolean newAlbum = false;
            for (Album album : albums) {
                if (album.getChecked() == 0) {
                    newAlbum = true;
                    break;
                }
            }
            if (newAlbum) {
                c.setBackground(Colors.NEW_ALBUM);
            } else {
                c.setBackground(table.getForeground());
            }
        });
        setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }

    public int getIndexOf(Band band) {
        for (int index = 0; index < getRowCount(); index++) {
            if (getBand(index).equals(band)) {
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
    public Band getBand(int index) {
        return model.getBand(convertRowIndexToModel(index));
    }

    // <editor-fold defaultstate="collapsed" desc="RowRenderer">
    private class RowRenderer extends DefaultTableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
                int row, int column) {
            Component tableCellRendererComponent = super.getTableCellRendererComponent(table, value, isSelected,
                    hasFocus, row, column);
            if (column < 2) {
                Band band = getBand(row);
                Set<Album> albums = band.getAlbums();
                boolean newAlbum = false;
                for (Album album : albums) {
                    if (album.getChecked() == 0) {
                        newAlbum = true;
                        break;
                    }
                }
                if (newAlbum) {
                    tableCellRendererComponent.setBackground(gridColor);
                } else {
                    tableCellRendererComponent.setFont(new Font("Tahoma", Font.BOLD, 11)); // file exists
                }
            }
            return tableCellRendererComponent; // To change body of generated methods, choose Tools | Templates.
        }

    }
    // </editor-fold>
}
