package org.kroky.musiclib.htmlparsers;

import java.util.List;

import org.jsoup.nodes.Document;
import org.kroky.musiclib.db.entities.Album;

public interface Parser {

    public List<Album> parse(Document doc);
}
