package org.kroky.musiclib.htmlparsers;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.kroky.musiclib.db.entities.Album;

public class SpiritOfMetalParser extends AbstractParser {

    protected SpiritOfMetalParser(String url) {
        super(url);
    }

    @Override
    public List<Album> doParse(Document doc) {
        List<Album> albums = new ArrayList<>();
        Element discography = doc.getElementById("discography");

        discography.select("h4[itemprop=name]").forEach(h4 -> {
            String name = h4.text();
            Album a = new Album();
            a.setName(name);
            for (Element el : h4.siblingElements()) {
                if (el.tagName().equals("div") && "datePublished".equals(el.attr("itemprop"))) {
                    int year = Integer.parseInt(el.text());
                    a.setReleaseYear(year);
                    albums.add(a);
                    break;
                }
            }
        });
        return albums;
    }

}
