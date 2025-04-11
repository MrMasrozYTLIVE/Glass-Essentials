package net.glasslauncher.glassbrigadier.impl.permission;

import lombok.Getter;
import lombok.Setter;
import net.glasslauncher.glassbrigadier.api.permission.PermissionNode;
import net.glasslauncher.glassbrigadier.api.permission.PermissionNodeInstance;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.simpleyaml.configuration.serialization.ConfigurationSerializable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Getter
public class Role {

    private String prefix;
    private String suffix;
    private int power;
    private String roleChain;
    private String name;

    @Setter
    private Set<PermissionNodeInstance<?>> permissions = new HashSet<>();

    public Role(String prefix, String suffix, int power, String roleChain, String name) {
        this.prefix = prefix;
        this.suffix = suffix;
        this.power = power;
        this.roleChain = roleChain;
        this.name = name;
    }

    public Role(String name) {
        this.power = 0;
        this.name = name;
    }

    public static @Nullable Role get(String name) {
        return RoleManagerImpl.get(name);
    }

    public static @NotNull Set<Role> getStartingWith(String name) {
        return RoleManagerImpl.getStartingWith(name);
    }

    public static boolean addRole(Role role) {
        return RoleManagerImpl.addRole(role);
    }

    public static void removeRole(Role role) {
        RoleManagerImpl.removeRole(role);
    }
}
