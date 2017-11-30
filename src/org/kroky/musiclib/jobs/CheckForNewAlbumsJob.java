/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kroky.musiclib.jobs;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.kroky.common.utils.Utils;
import org.kroky.musiclib.db.DAO;
import org.kroky.musiclib.db.entities.Album;
import org.kroky.musiclib.db.entities.Band;
import org.kroky.musiclib.gui.MainFrame;
import org.kroky.musiclib.gui.models.AlbumsTableModel;
import org.kroky.musiclib.gui.models.BandsTableModel;
import org.kroky.musiclib.gui.table.BandsTable;

/**
 *
 * @author user
 */
public class CheckForNewAlbumsJob extends AbstractJob {

    private static final Logger LOG = LogManager.getLogger();
    private static final Pattern NAME_PATTERN = Pattern.compile("itemprop=\"name\">(.*)</h4>");
    private static final Pattern YEAR_PATTERN = Pattern.compile("itemprop=\"datePublished\">(.*)</div>");

    private static final String SECTION_START = "<section id=\"discography\">";
    private static final String SECTION_END = "Complete discography</a>";

    public CheckForNewAlbumsJob(MainFrame mainFrame) {
        super(mainFrame);
    }

    @Override
    public Object run() {
        try {
            //toggle(mainFrame.getBtnBrowse(), mainFrame.getBtnScanDir(), mainFrame.getBtnCheck(), mainFrame.getTableAlbums(), mainFrame.getTableBands(), mainFrame.getTfDirPath());
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
                String url = band.getUrl();
                if (url != null) {
                    final String msg = ("Checking " + band.getName()).intern();
                    LOG.info(msg);
                    setProgressText(msg);
                    String html = Utils.getHtmlFromUrl(url);

                    html = html.substring(html.indexOf(SECTION_START));
                    html = html.substring(0, html.indexOf(SECTION_END));
                    LOG.trace("HTML Piece:\n" + html);
                    Scanner scanner = new Scanner(html);
                    StringBuilder sb = new StringBuilder();
                    while (scanner.hasNextLine()) {
                        String row = scanner.nextLine();
                        LOG.trace("Processing row:\n" + row);
                        Matcher m = NAME_PATTERN.matcher(row);
                        if (!m.find()) {
                            continue;
                        }
                        String name = m.group(1).trim();
                        LOG.trace("*** FOUND NAME *** " + name);
                        int year = 0;
                        while (scanner.hasNextLine()) {
                            row = scanner.nextLine();
                            m = YEAR_PATTERN.matcher(row);
                            LOG.trace("Processing row:\n" + row);
                            if (!m.find()) {
                                continue;
                            }
                            year = Integer.parseInt(m.group(1).trim());
                            LOG.trace("*** FOUND YEAR *** " + year);
                            break;
                        }
                        Set<Album> albums = band.getAlbums();
                        boolean addNew = true;
                        for (Album album : albums) {
                            if (album.getName().equalsIgnoreCase(name) && album.getReleaseYear() == year) {
                                addNew = false;
                                break;
                            }
                        }
                        if (addNew) {
                            Album album = new Album();
                            album.setBand(band);
                            album.setChecked(0);
                            album.setName(name);
                            album.setReleaseYear(year);
                            DAO.saveOrUpdate(album);
                            band.getAlbums().add(album);
                            DAO.refresh(band);
                            LOG.info("Added new album: " + year + " - " + name);
                            ((AlbumsTableModel) mainFrame.getTableAlbums().getModel()).addAlbum(album);
                            final BandsTableModel bandsTableModel = (BandsTableModel) mainFrame.getTableBands().getModel();
                            bandsTableModel.fireTableRowsUpdated(0, bandsTableModel.getRowCount() - 1);
                        } else {
                            LOG.info("Album already exists: " + year + " - " + name);
                        }
                    }
                } else {
                    final String msg = ("No URL defined for " + band.getName()).intern();
                    LOG.info(msg);
                    setProgressText(msg);
                }
                incProgressBar();
            });
        } finally {
//            toggle(mainFrame.getBtnBrowse(), mainFrame.getBtnScanDir(), mainFrame.getBtnCheck(), mainFrame.getTableAlbums(), mainFrame.getTableBands(), mainFrame.getTfDirPath());
            enableControls();
            resetProgressBar();
        }
        return null;
    }

}
