package net.glasslauncher.glassbrigadier.impl.permission;

import lombok.SneakyThrows;
import net.glasslauncher.glassbrigadier.GlassBrigadier;
import net.glasslauncher.glassbrigadier.api.permission.PermissionNode;
import net.glasslauncher.glassbrigadier.api.permission.PermissionNodeInstance;
import net.glasslauncher.glassbrigadier.api.storage.world.WorldModStorageFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.simpleyaml.configuration.ConfigurationSection;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class RoleManagerImpl {
    private static final HashMap<String, Role> ROLES = new HashMap<>();
    private static WorldModStorageFile permissionsFile;

    @SneakyThrows // This should never throw a FileNotFound exception.
    public static void setupRoleManager() {
        ROLES.clear();
        permissionsFile = WorldModStorageFile.of(GlassBrigadier.NAMESPACE.id("roles"));
        if (permissionsFile.exists()) {
            loadRoles();
            return;
        }

        try {
            permissionsFile.save();
            GlassBrigadier.LOGGER.info("Created roles file.");
        } catch (IOException ex) {
            GlassBrigadier.LOGGER.error("Couldn't create roles file!", ex);
            throw new RuntimeException(ex);
        }
    }

    public static boolean trySaveRolesFile() {
        try {
            permissionsFile.save();
            return true;
        } catch (IOException e) {
            GlassBrigadier.LOGGER.error("Couldn't save roles file!", e);
            return false;
        }
    }

    private static void loadRoles() {
        ConfigurationSection usersToRoles = (ConfigurationSection) permissionsFile.get("roles");
        if (usersToRoles == null) {
            return;
        }
        Set<String> names = usersToRoles.getKeys(false);
        ROLES.clear();
        for (String name : names) {
            usersToRoles.getList(name).forEach(o -> {
                ConfigurationSection roleObj = (ConfigurationSection) o;
                Role role = new Role(roleObj.getString("suffix"), roleObj.getString("prefix"), roleObj.getInt("power"), roleObj.getString("chain"), roleObj.getString("name"));
                ConfigurationSection permissions = roleObj.getConfigurationSection("permissions");
                Set<PermissionNodeInstance<?>> permissionNodeMap = new HashSet<>();
                permissions.getValues(false).forEach((k, v) -> permissionNodeMap.add(PermissionNodeInstance.of(PermissionNode.ofExisting(k), role, v)));
                role.setPermissions(permissionNodeMap);
                addRole(role);
            });
        }
    }

    public static @Nullable Role get(String name) {
        return ROLES.get(name);
    }

    public static @NotNull Set<Role> getStartingWith(String name) {
        String nameLower = name.toLowerCase();
        Set<Role> roles = new HashSet<>();
        ROLES.values().forEach(role -> {
            if (role.getName().toLowerCase().startsWith(nameLower)) {
                roles.add(role);
            }
        });
        return roles;
    }

    public static boolean addRole(Role role) {
        if (ROLES.containsKey(role.getName())) {
            return false;
        }

        ROLES.put(role.getName(), role);
        return true;
    }

    public static void removeRole(Role role) {
        if (!ROLES.containsKey(role.getName())) {
            return;
        }

        ROLES.remove(role.getName());
    }
}
