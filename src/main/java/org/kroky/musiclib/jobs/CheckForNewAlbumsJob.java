/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kroky.musiclib.jobs;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.kroky.musiclib.db.DAO;
import org.kroky.musiclib.db.entities.Album;
import org.kroky.musiclib.db.entities.Band;
import org.kroky.musiclib.gui.MainFrame;
import org.kroky.musiclib.gui.models.AlbumsTableModel;
import org.kroky.musiclib.gui.models.BandsTableModel;
import org.kroky.musiclib.gui.table.BandsTable;
import org.kroky.musiclib.htmlparsers.Parser;
import org.kroky.musiclib.htmlparsers.ParserFactory;

/**
 *
 * @author user
 */
public class CheckForNewAlbumsJob extends AbstractJob {

    private static final Logger LOG = LogManager.getLogger();

    public CheckForNewAlbumsJob(MainFrame mainFrame) {
        super(mainFrame);
    }

    @Override
    public Object run() {
        try {
            // toggle(mainFrame.getBtnBrowse(), mainFrame.getBtnScanDir(), mainFrame.getBtnCheck(),
            // mainFrame.getTableAlbums(), mainFrame.getTableBands(), mainFrame.getTfDirPath());
            disableControls();
            final BandsTable tableBands = mainFrame.getTableBands();
            BandsTableModel model = (BandsTableModel) tableBands.getModel();
            List<Band> bands = new ArrayList<>(model.getBands());
            if (mainFrame.getChkOnlySelected().isSelected()) {
                bands.clear();
                int[] selectedRows = tableBands.getSelectedRows();
                for (int selectedRow : selectedRows) {
                    Band band = tableBands.getBand(selectedRow);
                    bands.add(band);
                }
            }
            initProgressBar("", false, bands.size());

            bands.forEach((band) -> {
                try {
                    processDiscography(band);
                } catch (IOException e) {
                    LOG.warn("Unable to get info for band.", e);
                }
            });
        } finally {
            enableControls();
            resetProgressBar();
        }
        return null;
    }

    private void processDiscography(Band band) throws IOException {
        String url = band.getUrl();
        if (url != null) {
            Document doc = Jsoup.connect(url).get();

            if (!url.equals(doc.location())) {
                band.setUrl(doc.location());
                DAO.saveOrUpdate(band);
                DAO.refresh(band);
                LOG.info(String.format("Updated band's URL from [%s] to [%s]", url, doc.location()));
                final BandsTableModel bandsTableModel = (BandsTableModel) mainFrame.getTableBands().getModel();
                bandsTableModel.fireTableRowsUpdated(0, bandsTableModel.getRowCount() - 1);
            }

            Parser parser = ParserFactory.getInstance().getParser(band.getUrl());
            List<Album> albums = parser.parse(doc);

            albums.forEach(a -> updateBand(band, a.getName(), a.getReleaseYear()));
        } else {
            final String msg = ("No URL defined for " + band.getName()).intern();
            LOG.info(msg);
            setProgressText(msg);
        }
        incProgressBar();
    }

    private void updateBand(Band band, String albumName, int albumYear) {
        Set<Album> albums = band.getAlbums();
        boolean addNew = true;
        for (Album album : albums) {
            if (album.getName().equalsIgnoreCase(albumName) && album.getReleaseYear() == albumYear) {
                addNew = false;
                break;
            }
        }
        if (addNew) {
            Album album = new Album();
            album.setBand(band);
            album.setChecked(0);
            album.setName(albumName);
            album.setReleaseYear(albumYear);
            DAO.saveOrUpdate(album);
            band.getAlbums().add(album);
            DAO.refresh(band);
            LOG.info("Added new album: " + albumYear + " - " + albumName);
            ((AlbumsTableModel) mainFrame.getTableAlbums().getModel()).addAlbum(album);
            final BandsTableModel bandsTableModel = (BandsTableModel) mainFrame.getTableBands().getModel();
            bandsTableModel.fireTableRowsUpdated(0, bandsTableModel.getRowCount() - 1);
        } else {
            LOG.info("Album already exists: " + albumYear + " - " + albumName);
        }
    }
}
