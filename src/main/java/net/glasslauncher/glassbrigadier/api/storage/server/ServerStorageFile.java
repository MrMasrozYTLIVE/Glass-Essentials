package net.glasslauncher.glassbrigadier.api.storage.server;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import net.glasslauncher.glassbrigadier.GlassBrigadier;
import net.glasslauncher.glassbrigadier.api.storage.StorageFile;
import net.glasslauncher.glassbrigadier.api.storage.StorageUtils;
import net.minecraft.entity.player.PlayerEntity;
import org.jetbrains.annotations.NotNull;
import org.simpleyaml.configuration.Configuration;
import org.simpleyaml.configuration.MemoryConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.function.Function;

public class ServerStorageFile extends StorageFile {
    public static final Configuration SERVER_DATA_DEFAULTS = new MemoryConfiguration();

    private static final @NotNull Cache<@NotNull String, @NotNull ServerStorageFile> CACHE = Caffeine.newBuilder().softValues().build();
    private static final @NotNull Function<@NotNull String, @NotNull ServerStorageFile> PLAYER_STORAGE_FILE_FACTORY = (playerName) -> {
        ServerStorageFile file = new ServerStorageFile(StorageUtils.getWorldDir());
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

    public static StorageFile of(PlayerEntity player) {
        return of(player.name);
    }

    public static StorageFile of(String playerName) {
        return CACHE.get(playerName, PLAYER_STORAGE_FILE_FACTORY);
    }

    protected ServerStorageFile(File file) {
        super(file);

        setHeader(GlassBrigadier.NAMESPACE.getName().toUpperCase() + """
                 PLAYER DATA FILE
                DO NOT EDIT WITHOUT BACKING UP FIRST
                """);
    }
}
