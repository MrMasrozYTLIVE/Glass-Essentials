package net.glasslauncher.glassbrigadier.api.event;

import net.glasslauncher.glassbrigadier.api.storage.player.PlayerStorageFile;
import net.glasslauncher.glassbrigadier.api.storage.server.ServerStorageFile;
import net.mine_diver.unsafeevents.Event;
import org.simpleyaml.configuration.Configuration;

import java.util.Map;

/**
 * This is mostly used internally, though mods are welcome to hook into this system for tying data to players in an easy to access way for admins.
 */
public class GlassBrigadierDefaultsEvent extends Event {

    public void addPlayerDefault(String key, Object entry) {
        PlayerStorageFile.PLAYER_DATA_DEFAULTS.addDefault(key, entry);
    }

    public void addPlayerDefaults(Map<String, Object> map) {
        PlayerStorageFile.PLAYER_DATA_DEFAULTS.addDefaults(map);
    }

    public void addPlayerDefaults(Configuration defaults) {
        PlayerStorageFile.PLAYER_DATA_DEFAULTS.addDefaults(defaults);
    }

    public Configuration getPlayerDefaults() {
        return PlayerStorageFile.PLAYER_DATA_DEFAULTS;
    }

    public void addServerDefault(String key, Object entry) {
        ServerStorageFile.SERVER_DATA_DEFAULTS.addDefault(key, entry);
    }

    public void addServerDefaults(Map<String, Object> map) {
        ServerStorageFile.SERVER_DATA_DEFAULTS.addDefaults(map);
    }

    public void addServerDefaults(Configuration defaults) {
        ServerStorageFile.SERVER_DATA_DEFAULTS.addDefaults(defaults);
    }

    public Configuration getServerDefaults() {
        return ServerStorageFile.SERVER_DATA_DEFAULTS;
    }
    
    
}
