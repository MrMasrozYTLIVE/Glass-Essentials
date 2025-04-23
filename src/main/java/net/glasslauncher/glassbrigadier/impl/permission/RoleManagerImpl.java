package net.glasslauncher.glassbrigadier.impl.permission;

import lombok.SneakyThrows;
import net.glasslauncher.glassbrigadier.GlassBrigadier;
import net.glasslauncher.glassbrigadier.api.permission.PermissionNode;
import net.glasslauncher.glassbrigadier.api.permission.PermissionNodeInstance;
import net.glasslauncher.glassbrigadier.api.storage.world.WorldModStorageFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class RoleManagerImpl {
    private static final HashMap<String, Role> ROLES = new HashMap<>();
    private static WorldModStorageFile rolesFile;

    @SneakyThrows // This should never throw a FileNotFound exception.
    public static void setupRoleManager() {
        ROLES.clear();
        rolesFile = WorldModStorageFile.of(GlassBrigadier.NAMESPACE.id("roles"));
        if (rolesFile.exists()) {
            loadRoles();
            return;
        }

        try {
            rolesFile.save();
            GlassBrigadier.LOGGER.info("Created roles file.");
        } catch (IOException ex) {
            GlassBrigadier.LOGGER.error("Couldn't create roles file!", ex);
            throw new RuntimeException(ex);
        }
    }

    public static boolean updateAndSaveRolesFile() {
        try {
            List<Map<String, Object>> roles = ROLES.values().stream().map(role -> {
                Map<String, Object> map = new HashMap<>();
                map.put("name", role.getName());
                map.put("prefix", role.getPrefix());
                map.put("suffix", role.getSuffix());
                map.put("power", role.getPower());
                if (role.getRoleChain() != null) {
                    map.put("chain", role.getRoleChain().getName());
                }
                //noinspection unchecked Fucking java type erasure bullshit why this is actually fucking braindead, it LITERALLY CAN ALWAYS BE SAFELY CAST TO OBJECT YOU FUCKING IMBECILE
                map.put("permissions", role.getPermissions().stream().map(e -> new BasicEntry<>(e.getNode().path(), ((Function<Object, Object>) e.getNode().valueSaveFunction()).apply(e.getValue()))).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));
                return map;
            }).toList();
            rolesFile.set("roles", roles);
            rolesFile.save();
            return true;
        } catch (IOException e) {
            GlassBrigadier.LOGGER.error("Couldn't save roles file!", e);
            return false;
        }
    }

    private static void loadRoles() {
        //noinspection unchecked
        List<Map<String, Object>> rolesList = (List<Map<String, Object>>) rolesFile.get("roles");
        if (rolesList == null) {
            return;
        }

        rolesList.forEach(roleObj -> {
            Role role = new Role((String) roleObj.get("suffix"), (String) roleObj.get("prefix"), (Integer) roleObj.get("power"), roleObj.get("chain") == null ? null : RoleChain.of((String) roleObj.get("chain")), (String) roleObj.get("name"));
            //noinspection unchecked
            Map<String, Object> permissions = (Map<String, Object>) roleObj.get("permissions");
            Set<PermissionNodeInstance<?>> permissionNodeMap = new HashSet<>();
            permissions.forEach((key, value) -> permissionNodeMap.add(PermissionNodeInstance.ofAndSetValue(PermissionNode.ofExistingOrArbitrary(key), role, value)));
            role.setPermissions(permissionNodeMap);
            addRole(role);
        });
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
        updateAndSaveRolesFile();
        return true;
    }

    public static void removeRole(Role role) {
        if (!ROLES.containsKey(role.getName())) {
            return;
        }

        ROLES.remove(role.getName());
        updateAndSaveRolesFile();
    }
}
