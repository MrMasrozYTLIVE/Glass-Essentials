package net.glasslauncher.glassbrigadier.impl.permission;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.glasslauncher.glassbrigadier.GlassBrigadier;
import net.glasslauncher.glassbrigadier.api.permission.PermissionNodeInstance;
import net.glasslauncher.glassbrigadier.api.storage.world.WorldModStorageFile;
import net.minecraft.entity.player.PlayerEntity;
import org.jetbrains.annotations.NotNull;
import org.simpleyaml.configuration.ConfigurationSection;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public final class UserPermissionManagerImpl {
    private static final Map<String, Set<Role>> usersToRoles = new HashMap<>();
    private static final Map<Role, Set<String>> rolesToUsers = new HashMap<>();
    private static final WorldModStorageFile permissionsFile;

    static {
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
            permissionsFile = null;
        }
        else {
            permissionsFile = WorldModStorageFile.of(GlassBrigadier.NAMESPACE.id("user_roles"));
        }
    }

    @SneakyThrows // This should never throw a FileNotFound exception.
    public static void setupPermissionManager() {
        if (permissionsFile == null) {
            return;
        }
        if (permissionsFile.exists()) {
            loadUserRoles();
            return;
        }

        try {
            permissionsFile.save();
            GlassBrigadier.LOGGER.info("Created perms file.");
        } catch (IOException ex) {
            GlassBrigadier.LOGGER.error("Couldn't create perms file!", ex);
            throw new RuntimeException(ex);
        }
    }

    @AllArgsConstructor
    private static class SerializedUserRoleEntry implements Map.Entry<String, Set<String>> {
        private String key;
        private Set<String> value;

        @Override
        public String getKey() {
            return key;
        }

        @Override
        public Set<String> getValue() {
            return value;
        }

        @Override
        public Set<String> setValue(Set<String> value) {
            return this.value = value;
        }
    }

    public static boolean updateAndSaveRolesFile() {
        try {
            Map<String, Set<String>> data = usersToRoles.entrySet().stream().map(e -> new SerializedUserRoleEntry(e.getKey(), e.getValue().stream().map(Role::getName).collect(Collectors.toSet()))).collect(Collectors.toMap(SerializedUserRoleEntry::getKey, SerializedUserRoleEntry::getValue));
            permissionsFile.set("user_roles", data);
            permissionsFile.save();
            return true;
        } catch (IOException e) {
            GlassBrigadier.LOGGER.error("Couldn't save perms file!", e);
            return false;
        }
    }

    private static void loadUserRoles() {
        ConfigurationSection usersToRolesSection = (ConfigurationSection) permissionsFile.get("user_roles");
        if (usersToRolesSection == null) {
            return;
        }
        Set<String> names = usersToRolesSection.getKeys(false);
        usersToRoles.clear();
        rolesToUsers.clear();
        for (String name : names) {
            Set<Role> roles = new HashSet<>();
            //noinspection unchecked
            ((Set<String>) usersToRolesSection.get(name, new HashSet<>())).forEach(role_ -> {
                Role role = Role.get(role_);
                roles.add(role);
                rolesToUsers.computeIfAbsent(role, e -> new HashSet<>()).add(name);
            });
            UserPermissionManagerImpl.usersToRoles.put(name, roles);
        }
    }

    public static @NotNull Set<Role> getRoles(@NotNull String user) {
        Set<Role> roles = usersToRoles.get(user);
        if (roles == null) {
            return new HashSet<>();
        }
        return new HashSet<>(roles);
    }

    public static @NotNull Set<Role> getRoles(@NotNull PlayerEntity player) {
        return getRoles(player.name);
    }

    public static boolean addUserToRole(@NotNull String user, @NotNull Role role) {
        Set<Role> roles = usersToRoles.computeIfAbsent(user, key -> new HashSet<>());
        Set<String> users = rolesToUsers.computeIfAbsent(role, key -> new HashSet<>());
        if (roles.contains(role)) {
            return false;
        }
        roles.add(role);
        users.add(user);
        updateAndSaveRolesFile();
        return true;
    }

    public static boolean removeUserFromRole(@NotNull String user, @NotNull Role role) {
        Set<Role> roles = usersToRoles.computeIfAbsent(user, key -> new HashSet<>());
        Set<String> users = rolesToUsers.computeIfAbsent(role, key -> new HashSet<>());
        if (!roles.contains(role)) {
            return false;
        }
        roles.remove(role);
        users.remove(user);
        updateAndSaveRolesFile();
        return true;
    }

    public static @NotNull Set<String> getUsers(Role role) {
        Set<String> users = rolesToUsers.get(role);
        if (users == null) {
            return new HashSet<>();
        }
        return users;
    }

    public static @NotNull Set<PermissionNodeInstance<?>> getNodes(String user) {
        return getRoles(user).stream().flatMap(role -> new HashSet<>(role.getPermissions()).stream()).collect(Collectors.toSet());
    }
}
