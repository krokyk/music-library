/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kroky.musiclib.jobs;

import java.io.File;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.NotFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.kroky.musiclib.db.entities.Album;
import org.kroky.musiclib.db.entities.Band;
import org.kroky.musiclib.gui.MainFrame;

/**
 *
 * @author user
 */
public class ScanDirJob extends AbstractJob {

    private static final Pattern PATTERN = Pattern.compile("(.*) - (\\d+) - (.*)");
    private static File lastScannedDir; //so that we are not dependent on mainFrame.getRootDir but rather remember which dir was scanned (now user can change the dir by browse

    public ScanDirJob(MainFrame mainFrame, File dir) {
        super(mainFrame);
        lastScannedDir = dir;
    }

    @Override
    public Object run() {
        try {
            disableControls();
            TreeSet<File> set = new TreeSet<>(FileUtils.listFilesAndDirs(lastScannedDir, new NotFileFilter(TrueFileFilter.INSTANCE), DirectoryFileFilter.DIRECTORY));
            TreeSet<Band> bands = new TreeSet<>();
            String previousBandName = "";
            Band band = new Band(); //can be null, I put it here to avoid the Netbeans' "possible null dereference" warning
            for (File dir : set) {
                Matcher matcher = PATTERN.matcher(dir.getName());
                if (matcher.matches()) {
                    String bandName = matcher.group(1);
                    if (!previousBandName.equals(bandName)) {
                        previousBandName = bandName;
                        band = new Band();
                        band.setAlbums(new TreeSet<>());
                        band.setName(bandName);
                        bands.add(band);
                    }

                    int year = Integer.parseInt(matcher.group(2));
                    String albumName = matcher.group(3);

                    Album album = new Album();
                    album.setBand(band);
                    album.setChecked(1);
                    album.setName(albumName);
                    album.setReleaseYear(year);
                    band.getAlbums().add(album);
                }
            }
            return bands;
        } finally {
            enableControls();
        }
    }

    public static File getLastScannedDir() {
        return lastScannedDir;
    }

}
