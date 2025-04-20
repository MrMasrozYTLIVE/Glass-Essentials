package net.glasslauncher.glassbrigadier.impl.permission;

import lombok.Getter;
import lombok.Setter;
import net.glasslauncher.glassbrigadier.api.permission.PermissionNode;
import net.glasslauncher.glassbrigadier.api.permission.PermissionNodeInstance;
import net.glasslauncher.glassbrigadier.impl.utils.AMIFormatting;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;

@Getter @Setter
public class Role {

    private String prefix;
    private String suffix;
    private int power;
    private RoleChain roleChain;
    private String name;

    private Set<PermissionNodeInstance<?>> permissions = new HashSet<>();

    public Role(String prefix, String suffix, int power, RoleChain roleChain, String name) {
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

    public boolean setName(String name) {
        if (RoleManagerImpl.get(name) != null) {
            return false;
        }

        RoleManagerImpl.removeRole(this);
        this.name = name;
        RoleManagerImpl.addRole(this);
        return true;
    }

    // TODO: Actually implement, I'm skipping this for 1.0.
    public void setRoleChain(RoleChain roleChain) {
        if (this.roleChain != null) {
//            this.roleChain.removeRole(this);
        }
        this.roleChain = roleChain;
        if (roleChain != null) {
//            this.roleChain.addRole(this);
        }
    }

    public String getDisplay(String user) {
        return AMIFormatting.RESET + (getPrefix() == null ? "" : getPrefix()) + "<" + user + ">" + (getSuffix() == null ? "" : getSuffix()) + AMIFormatting.RESET;
    }

    public boolean setPermission(PermissionNode<?> permission_, @Nullable String value) {
        //noinspection unchecked Java I swear to god
        PermissionNode<Object> permission = (PermissionNode<Object>) permission_;
        if (value == null) {
            permissions.remove(PermissionNodeInstance.ofExisting(permission, this));
        }
        else {
            Object parsed = permission.valueFromArgumentFunction().apply(value);
            if (parsed == null) {
                return false;
            }
            permissions.add(PermissionNodeInstance.ofAndSetValue(permission, this, parsed));
        }
        RoleManagerImpl.updateAndSaveRolesFile();
        return true;
    }
}
