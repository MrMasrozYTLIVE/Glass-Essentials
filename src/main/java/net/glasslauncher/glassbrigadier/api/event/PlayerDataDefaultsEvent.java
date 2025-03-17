package net.glasslauncher.glassbrigadier.api.event;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.experimental.SuperBuilder;
import net.glasslauncher.glassbrigadier.api.permission.PermissionNode;
import net.glasslauncher.glassbrigadier.api.playerstorage.PlayerStorageFile;
import net.glasslauncher.glassbrigadier.api.utils.StorageUtils;
import net.mine_diver.unsafeevents.Event;
import org.jetbrains.annotations.NotNull;
import org.simpleyaml.configuration.Configuration;
import org.simpleyaml.configuration.MemoryConfiguration;

import java.io.File;
import java.util.Map;
import java.util.function.Function;

/**
 * This is mostly used internally, though mods are welcome to hook into this system for tying data to players in an easy to access way for admins.
 */
public class PlayerDataDefaultsEvent extends Event {

    public void addDefault(String key, Object entry) {
        PlayerStorageFile.PLAYER_DATA_DEFAULTS.addDefault(key, entry);
    }

    public void addDefaults(Map<String, Object> map) {
        PlayerStorageFile.PLAYER_DATA_DEFAULTS.addDefaults(map);
    }

    public void addDefaults(Configuration defaults) {
        PlayerStorageFile.PLAYER_DATA_DEFAULTS.addDefaults(defaults);
    }

    public Configuration getDefaults() {
        return PlayerStorageFile.PLAYER_DATA_DEFAULTS;
    }
}
