package net.glasslauncher.glassbrigadier.api.storage;

import org.simpleyaml.configuration.file.YamlFile;
import org.simpleyaml.configuration.file.YamlFileWrapper;

import java.util.Objects;

// Also yoinked from GCAPI
// And so the code crimes continue.
public class Storage extends YamlFileWrapper {
    public Storage(YamlFile configuration, String path) {
        super(configuration, path);
    }

    protected Storage(YamlFile configuration, String path, YamlFileWrapper parent) {
        super(configuration, path, parent);
    }

    public <T> Object get(Class<T> type) {
        return configuration.get(path);
    }

    public <T> Object getChild(String key, Class<T> type) {
        return configuration.get(childPath(key));
    }

    @Override
    public Storage path(String path) {
        // Fixes some fuckery with comments.
        return new Storage(this.configuration, path, Objects.equals(this.path, "") ? null : this);
    }
}