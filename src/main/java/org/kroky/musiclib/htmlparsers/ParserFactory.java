package org.kroky.musiclib.htmlparsers;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

public final class ParserFactory {

    private static final ParserFactory INSTANCE = new ParserFactory();

    private static final Map<String, Parser> PARSER_MAP = new HashMap<>();

    private ParserFactory() {
    }

    public static ParserFactory getInstance() {
        return INSTANCE;
    }

    public Parser getParser(String url) {
        try {
            String domain = new URI(url).getHost();
            if (domain.contains("metal-archives")) {
                return PARSER_MAP.computeIfAbsent(domain, k -> new MetalArchivesParser(domain));
            } else if (url.contains("spirit-of-metal")) {
                return PARSER_MAP.computeIfAbsent(domain, k -> new SpiritOfMetalParser(domain));
            } else {
                throw new RuntimeException(String.format("Parser not implemented for URL: %s", url));
            }
        } catch (URISyntaxException e) {
            throw new RuntimeException(String.format("Cannot get domain from URL: %s", url));
        }

    }
}
