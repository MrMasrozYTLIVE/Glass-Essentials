package net.glasslauncher.glassbrigadier.api.playerstorage;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import net.glasslauncher.glassbrigadier.GlassBrigadier;
import net.glasslauncher.glassbrigadier.api.utils.StorageUtils;
import net.minecraft.entity.player.PlayerEntity;
import org.jetbrains.annotations.NotNull;
import org.simpleyaml.configuration.Configuration;
import org.simpleyaml.configuration.MemoryConfiguration;
import org.simpleyaml.configuration.comments.format.YamlCommentFormat;
import org.simpleyaml.configuration.file.YamlFile;
import org.simpleyaml.configuration.implementation.api.QuoteStyle;

import java.io.*;
import java.util.*;
import java.util.function.Function;

// Yoinked from GCAPI cause this is just so convenient.
public class PlayerStorageFile extends YamlFile {
    public static final Configuration PLAYER_DATA_DEFAULTS = new MemoryConfiguration();

    private static final @NotNull Cache<@NotNull String, @NotNull PlayerStorageFile> CACHE = Caffeine.newBuilder().softValues().build();
    private static final @NotNull Function<@NotNull String, @NotNull PlayerStorageFile> PLAYER_STORAGE_FILE_FACTORY = (playerName) -> {
        PlayerStorageFile file = new PlayerStorageFile(StorageUtils.getPlayerStorageFile(playerName));
        try {
            file.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return file;
    };

    public static void invalidateAll() {
        CACHE.invalidateAll();
    }

    public static PlayerStorageFile of(PlayerEntity player) {
        return of(player.name);
    }

    public static PlayerStorageFile of(String playerName) {
        return CACHE.get(playerName, PLAYER_STORAGE_FILE_FACTORY);
    }

    private PlayerStorageFile(File file) throws IllegalArgumentException {
        super(file);
        options().useComments(true);
        setCommentFormat(YamlCommentFormat.PRETTY);
        options().quoteStyleDefaults().setQuoteStyle(List.class, QuoteStyle.DOUBLE);
        options().quoteStyleDefaults().setQuoteStyle(Map.class, QuoteStyle.DOUBLE);
        options().quoteStyleDefaults().setQuoteStyle(String.class, QuoteStyle.DOUBLE);
        options().quoteStyleDefaults().setQuoteStyle(String[].class, QuoteStyle.DOUBLE);
        options().headerFormatter().commentPrefix("##  ");

        //   ###################################################
        setHeader(GlassBrigadier.NAMESPACE.getName().toUpperCase() + """
                 PLAYER DATA FILE
                DO NOT EDIT WITHOUT BACKING UP FIRST
                """);

        setDefaults(PLAYER_DATA_DEFAULTS);
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
    public void merge(PlayerStorageFile other) {
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
    public PlayerStorage path(String path) {
        return new PlayerStorage(this, path);
    }

    public PlayerStorage path() {
        return new PlayerStorage(this, "");
    }
}
