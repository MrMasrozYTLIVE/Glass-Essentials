package net.glasslauncher.glassbrigadier.api.permission;

import net.glasslauncher.glassbrigadier.api.command.GlassCommandSource;
import net.glasslauncher.glassbrigadier.impl.permission.Role;
import net.glasslauncher.glassbrigadier.impl.permission.UserPermissionManagerImpl;
import net.minecraft.entity.player.PlayerEntity;

import javax.annotation.Nonnull;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public final class PermissionManager {

    /**
     * Get the roles for a given player.
     *
     * @param player the player to check.
     * @return a set of the player's permission nodes.
     */
    @Nonnull
    public static Set<Role> getRoles(@Nonnull PlayerEntity player) {
        return getRoles(player.name);
    }

    /**
     * Get the roles for a command source.
     *
     * @param source the source to check.
     * @return a set of the source's permission nodes.
     */
    @Nonnull
    public static Set<Role> getRoles(@Nonnull GlassCommandSource source) {
        return getRoles(source.getName());
    }

    /**
     * Get the roles for a (player) name.
     * @param name the name to check.
     * @return a set of the permission nodes to their values.
     */
    @Nonnull
    public static Set<Role> getRoles(@Nonnull String name) {
        return UserPermissionManagerImpl.getRoles(name);
    }

    /**
     * Add a node associated with the player given.
     * @param player the player which will be given a new node.
     * @param role the role to add.
     * @return whether the node was successfully added.
     */
    public static boolean addRole(@Nonnull PlayerEntity player, @Nonnull Role role) {
        return addRole(player.name, role);
    }

    /**
     * Add a role to the command source given.
     * @param source the command source which will be given a new role.
     * @param role the role to add.
     * @return whether the node was successfully added.
     */
    public static boolean addRole(@Nonnull GlassCommandSource source, @Nonnull Role role) {
        return addRole(source.getName(), role);
    }

    /**
     * Add a role to the command source given.
     * @param name the name which will be given a new role.
     * @param role the role to add.
     * @return whether the node was successfully added.
     */
    public static boolean addRole(@Nonnull String name, @Nonnull Role role) {
        return UserPermissionManagerImpl.addUserToRole(name, role);
    }

    /**
     * Remove a node associated with the player given.
     * @param player the player which will lose the node.
     * @param role the role to remove.
     * @return whether the node was successfully removed.
     */
    public static boolean removeRole(@Nonnull PlayerEntity player, @Nonnull Role role) {
        return removeRole(player.name, role);
    }

    /**
     * Remove a node associated with the command source given.
     * @param source the command source which will lose the node.
     * @param role the role to remove.
     * @return whether the node was successfully removed.
     */
    public static boolean removeRole(@Nonnull GlassCommandSource source, @Nonnull Role role) {
        return removeRole(source.getName(), role);
    }

    /**
     * Remove a node associated with the name given.
     * @param name the name which will lose the node.
     * @param role the role to remove.
     * @return whether the node was successfully removed.
     */
    public static boolean removeRole(@Nonnull String name, @Nonnull Role role) {
        return UserPermissionManagerImpl.removeUserFromRole(name, role);
    }

}
