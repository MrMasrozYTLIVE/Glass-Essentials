package net.glasslauncher.glassbrigadier.impl.permission;

import lombok.Getter;
import lombok.SneakyThrows;
import net.glasslauncher.glassbrigadier.GlassBrigadier;
import net.glasslauncher.glassbrigadier.api.permission.PermissionNode;
import net.glasslauncher.glassbrigadier.api.storage.world.WorldModStorageFile;
import org.simpleyaml.configuration.ConfigurationSection;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public final class PermissionManagerImpl {
    @Getter
    private static final Map<String, Set<PermissionNode>> permissionsMap = new HashMap<>();
    private static final WorldModStorageFile permissionsFile = WorldModStorageFile.of(GlassBrigadier.NAMESPACE.id("user_permissions"));

    // This should never throw a FileNotFound exception.
    @SneakyThrows
    public static void setupPermissionManager() {
        if (permissionsFile.exists()) {
            loadPermissions();
            return;
        }

        try {
            permissionsFile.save();
            GlassBrigadier.LOGGER.info("Created perms file.");
        } catch (IOException ex) {
            GlassBrigadier.LOGGER.error("Couldn't create perms file!");
            throw new RuntimeException(ex);
        }
    }

    public static boolean trySavePermissionsFile() {
        try {
            permissionsFile.save();
            return true;
        } catch (IOException e) {
            GlassBrigadier.LOGGER.error("Couldn't save perms file!", e);
            return false;
        }
    }

    private static void loadPermissions() {
        ConfigurationSection usersToGroups = (ConfigurationSection) permissionsFile.get("user_groups");
        if (usersToGroups == null) {
            return;
        }
        Set<String> names = usersToGroups.getKeys(false);
        permissionsMap.clear();
        for (String name : names) {
            Set<PermissionNode> permissions = new HashSet<>();
            usersToGroups.getList(name).forEach(permission -> permissions.add(PermissionNode.of((String) permission)));
            permissionsMap.put(name, permissions);
        }
    }
}
