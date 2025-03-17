package net.glasslauncher.glassbrigadier.api.event;

import net.glasslauncher.glassbrigadier.api.playerstorage.PlayerStorageFile;
import net.mine_diver.unsafeevents.Event;
import org.simpleyaml.configuration.Configuration;

import java.util.Map;

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
