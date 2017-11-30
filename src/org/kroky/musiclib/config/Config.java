package org.kroky.musiclib.config;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.kroky.musiclib.exceptions.ConfigurationException;

public final class Config {

    private static final Logger LOG = LogManager.getLogger();
    static final File CONFIG_FILE = new File("./user.properties");

    public void setProperty(String name, String value) {
        put(name, value);
    }

    static final class SingletonHolder {

        private static final Config instance = new Config();
    }

    public static final Pattern SUBST_PATTERN = Pattern.compile("\\$\\{([^\\}]+)\\}");

    private final Properties props = new Properties();

    void putAll(Properties props) {
        this.props.putAll(props);
    }

    void put(Object key, Object value) {
        props.put(key, value);
    }

    private Config() {
    }

    public static Config getInstance() {
        ConfigReader.init();
        return SingletonHolder.instance;
    }

    static Config getNoInitInstance() {
        return SingletonHolder.instance;
    }

    public Integer getInteger(String key, Integer defaultValue) {
        Integer rv = defaultValue;
        try {
            return new Integer((String) getRawProperty(key));
        } catch (NumberFormatException e) {
            LOG.warn("Property " + key + " not found, returning default value " + rv);
        }
        return rv;
    }

    public Integer getInteger(String key) {
        throwIfNull(key);
        try {
            return new Integer((String) getRawProperty(key));
        } catch (NumberFormatException e) {
            throw new ConfigurationException("Cannot get integer from value '" + getRawProperty(key) + "' of property '" + key + "'", e);
        }
    }

    private Object getRawProperty(Object key, Object defaultValue) {
        Object rawProperty = props.get(key);
        if (rawProperty != null) {
            if (rawProperty instanceof String) {
                String prop = rawProperty.toString();
                Matcher m = SUBST_PATTERN.matcher(prop);
                while (m.find()) {
                    String resolvedKey = m.group(1);
                    throwIfNull(resolvedKey);
                    Object value = getRawProperty(resolvedKey, defaultValue);
                    prop = prop.replaceAll("\\$\\{" + resolvedKey + "\\}", value.toString());
                    m = SUBST_PATTERN.matcher(prop);
                }
                return prop;
            }
            return rawProperty;
        } else {
            return defaultValue;
        }
    }

    private Object getRawProperty(Object key) {
        return getRawProperty(key, null);
    }

    public String getString(String key, String defaultValue) {
        return props.getProperty(key, defaultValue);
    }

    public String getString(String key) {
        throwIfNull(key);
        return (String) getRawProperty(key);
    }

    public boolean getBoolean(String key, boolean defaultValue) {
        if (getRawProperty(key) == null) {
            LOG.warn("Property " + key + " not found, returning default value " + defaultValue);
            return defaultValue;
        }
        return Boolean.parseBoolean((String) getRawProperty(key));
    }

    public boolean getBoolean(String key) {
        return Boolean.parseBoolean((String) getRawProperty(key));
    }

    public char[] getChars(String key) {
        throwIfNull(key);
        return ((String) getRawProperty(key)).toCharArray();
    }

    private void throwIfNull(String key) {
        if (getRawProperty(key) == null) {
            throw new ConfigurationException("Property '" + key + "' not found in configuration and no default value was specified.");
        }
    }

    public String[] getStringArray(String key) {
        final String value = (String) getRawProperty(key);
        if (value == null) {
            throw new ConfigurationException("Property '" + key + "' not found in configuration and no default value was specified.");
        } else {
            return getArray(value);
        }
    }

    public String[] getArray(final String value) {
        final String[] arr = value.split(",");
        for (int i = 0; i < arr.length; i++) {
            arr[i] = arr[i].trim();
        }
        return arr;
    }

    public String[] getStringArray(String key, String[] defaultValue) {
        final String value = (String) getRawProperty(key);
        if (value == null) {
            return defaultValue;
        } else {
            return getArray(value);
        }
    }

    public File getFile(String key) {
        String prop = getString(key);
        return new File(prop);
    }

    public Set<Object> getKeySet() {
        return props.keySet();
    }

    public Set<Map.Entry<Object, Object>> entrySet() {
        return props.entrySet();
    }

    public void list(PrintStream out) {
        props.list(out);
    }

    public void save() throws IOException {
        props.store(new FileWriter(CONFIG_FILE), null);
    }
}
