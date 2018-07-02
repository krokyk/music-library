package org.kroky.musiclib.htmlparsers;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.kroky.musiclib.db.entities.Album;

public class MetalArchivesParser extends AbstractParser {

    protected MetalArchivesParser(String url) {
        super(url);
    }

    @Override
    public List<Album> doParse(Document doc) {
        List<Album> albums = new ArrayList<>();
        Elements trs = doc.getElementsByTag("tr");
        for (Element tr : trs) {
            Elements as = tr.getElementsByTag("a");
            if (!as.isEmpty()) {
                String name = as.get(0).text();
                Elements tds = tr.getElementsByTag("td");
                for (Element td : tds) {
                    try {
                        int year = Integer.parseInt(td.text());
                        Album a = new Album();
                        a.setName(name);
                        a.setReleaseYear(year);
                        albums.add(a);
                        break;
                    } catch (Exception e) {
                        // nothing to do
                    }
                }
            }
        }
        return albums;
    }

}
