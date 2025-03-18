package net.glasslauncher.glassbrigadier.api.storage;

import org.simpleyaml.configuration.comments.format.YamlCommentFormat;
import org.simpleyaml.configuration.file.YamlFile;
import org.simpleyaml.configuration.implementation.api.QuoteStyle;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Yoinked from GCAPI cause this is just so convenient.
public class StorageFile extends YamlFile {

    protected StorageFile(File file) throws IllegalArgumentException {
        super(file);
        options().useComments(true);
        setCommentFormat(YamlCommentFormat.PRETTY);
        options().quoteStyleDefaults().setQuoteStyle(List.class, QuoteStyle.DOUBLE);
        options().quoteStyleDefaults().setQuoteStyle(Map.class, QuoteStyle.DOUBLE);
        options().quoteStyleDefaults().setQuoteStyle(String.class, QuoteStyle.DOUBLE);
        options().quoteStyleDefaults().setQuoteStyle(String[].class, QuoteStyle.DOUBLE);
        options().headerFormatter().commentPrefix("##  ");
    }

    public Float getFloat(String key, Float defaultValue) {
        return (Float) get(key, defaultValue);
    }

    public Float getFloat(String key) {
        return (Float) get(key);
    }

    public <T extends Enum<?>> T getEnum(String key, Class<T> targetEnum, T defaultValue) {
        return targetEnum.getEnumConstants()[getInt(key, defaultValue.ordinal())];
    }

    public <T extends Enum<?>> T getEnum(String key, Class<T> targetEnum) {
        int value = getInt(key, -1);
        if (value < 0) {
            return null;
        }
        return targetEnum.getEnumConstants()[value];
    }

    public <T extends Enum<?>> void setEnum(String key, T value) {
        set(key, value.ordinal());
    }

    // This should be safe enough if the map's already pre-filtered... right?
    // Fuck, this is so hacky.
    public void merge(StorageFile other) {
        merge(map, other.map);
    }

    private void merge(Map<String, Object> self, Map<String, Object> other) {
        other.forEach((key, value) -> {
            if (value.getClass() == HashMap.class && self.get(key) != null) {
                //noinspection unchecked
                merge((HashMap<String, Object>) self.get(key), (HashMap<String, Object>) value);
            }
            else {
                self.put(key, value);
            }
        });
    }

    // I hope you like me fucking with internals
    @Override
    public Storage path(String path) {
        return new Storage(this, path);
    }

    public Storage path() {
        return new Storage(this, "");
    }
}
