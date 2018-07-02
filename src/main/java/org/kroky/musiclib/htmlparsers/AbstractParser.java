package org.kroky.musiclib.htmlparsers;

import java.util.List;

import org.jsoup.nodes.Document;
import org.kroky.musiclib.db.entities.Album;

abstract class AbstractParser implements Parser {

    protected final String domain;

    protected AbstractParser(String url) {
        domain = url;
    }

    @Override
    public List<Album> parse(Document doc) {
        if (!doc.location().contains(domain)) {
            throw new IllegalArgumentException(String.format(
                    "Cannot process '%s'. This parser is to be used on this domain: %s", doc.location(), domain));
        }
        return doParse(doc);
    }

    protected abstract List<Album> doParse(Document doc);

}
