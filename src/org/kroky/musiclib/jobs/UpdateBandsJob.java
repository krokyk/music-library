/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kroky.musiclib.jobs;

import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;
import org.kroky.musiclib.db.DAO;
import org.kroky.musiclib.db.entities.Album;
import org.kroky.musiclib.db.entities.Band;
import org.kroky.musiclib.gui.MainFrame;
import org.kroky.musiclib.gui.models.AlbumsTableModel;
import org.kroky.musiclib.gui.models.BandsTableModel;

/**
 *
 * @author user
 */
public class UpdateBandsJob extends AbstractJob {

    private final Collection<Band> bands;

    public UpdateBandsJob(MainFrame mainFrame, Collection<Band> bands) {
        super(mainFrame);
        this.bands = bands;
    }

    @Override
    public Object run() {
        BandsTableModel model = (BandsTableModel) mainFrame.getTableBands().getModel();
        disableControls();
        initProgressBar(bands.size());
        TreeSet<Band> bandsFromDb = new TreeSet<>();
        try {
            bands.stream().map((band) -> {
                incProgressBar();
                return band;
            }).forEach((band) -> {
                Band bandFromDb = DAO.get(Band.class, band.getName());
                if (bandFromDb != null) {
//                    if (band.getName().equalsIgnoreCase("dark tranquillity")) {
//                        for (Album a : band.getAlbums()) {
//                            if (a.getName().equals("Exposures - In Retrospect and Denial")) {
//                                for (Album dba : bandFromDb.getAlbums()) {
//                                    System.out.println();
//                                }
//                            }
//                        }
//                    }

                    mergeAlbums(bandFromDb.getAlbums(), band.getAlbums());
                    DAO.saveOrUpdate(bandFromDb);
                    bandsFromDb.add(bandFromDb);
                } else {
                    DAO.saveOrUpdate(band);
                    bandsFromDb.add(band);
                }
            });
        } finally {
            enableControls();
            resetProgressBar();
        }
        model.replaceAll(bandsFromDb);
        return null;
    }

    private void mergeAlbums(Set<Album> dbAlbums, Set<Album> hddAlbums) {
        boolean updated = false;
        for (Album hddAlbum : hddAlbums) {
            boolean newAlbum = true;
            for (Album dbAlbum : dbAlbums) {
                if (dbAlbum.equals(hddAlbum)) {
                    dbAlbum.update(hddAlbum);
                    updated = true;
                    newAlbum = false;
                    break;
                }
            }
            if (newAlbum) {
                dbAlbums.add(hddAlbum);
                updated = true;
            }
        }
        if (updated) {
            AlbumsTableModel model = (AlbumsTableModel) mainFrame.getTableAlbums().getModel();
            model.fireTableDataChanged();
        }
    }

}
