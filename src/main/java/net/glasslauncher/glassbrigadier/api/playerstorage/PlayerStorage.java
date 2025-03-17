package net.glasslauncher.glassbrigadier.api.playerstorage;

import org.simpleyaml.configuration.file.YamlFile;
import org.simpleyaml.configuration.file.YamlFileWrapper;

import java.util.*;

// Also yoinked from GCAPI
// And so the code crimes continue.
public class PlayerStorage extends YamlFileWrapper {
    public PlayerStorage(YamlFile configuration, String path) {
        super(configuration, path);
    }

    protected PlayerStorage(YamlFile configuration, String path, YamlFileWrapper parent) {
        super(configuration, path, parent);
    }

    public <T> Object get(Class<T> type) {
        return configuration.get(path);
    }

    public <T> Object getChild(String key, Class<T> type) {
        return configuration.get(childPath(key));
    }

    @Override
    public PlayerStorage path(String path) {
        // Fixes some fuckery with comments.
        return new PlayerStorage(this.configuration, path, Objects.equals(this.path, "") ? null : this);
    }
}