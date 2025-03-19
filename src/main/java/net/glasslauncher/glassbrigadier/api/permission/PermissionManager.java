package net.glasslauncher.glassbrigadier.api.permission;

import net.glasslauncher.glassbrigadier.api.command.GlassCommandSource;
import net.glasslauncher.glassbrigadier.impl.permission.PermissionManagerImpl;
import net.minecraft.entity.player.PlayerEntity;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public final class PermissionManager {

    /**
     * Get the permission nodes for a given player.
     * @param player the player to check.
     * @return a list of the player's permission nodes.
     */
    @Nonnull
    public static Set<PermissionNode> getNodes(@Nonnull PlayerEntity player) {
        return getNodes(player.name);
    }

    /**
     * Get the permission nodes for a command source.
     * @param source the source to check.
     * @return a set of the source's permission nodes.
     */
    @Nonnull
    public static Set<PermissionNode> getNodes(@Nonnull GlassCommandSource source) {
        return getNodes(source.getName());
    }

    /**
     * Get the permission nodes for a (player) name.
     * @param name the name to check.
     * @return a set of the permission nodes.
     */
    @Nonnull
    public static Set<PermissionNode> getNodes(@Nonnull String name) {
        final Set<PermissionNode> nodes = PermissionManagerImpl.getPermissionsMap().get(name);
        if (nodes == null)
            return Collections.emptySet();
        return nodes;
    }

    /**
     * Add a node associated with the player given.
     * @param player the player which will be given a new node.
     * @param node the node to add.
     * @return whether the node was successfully added.
     */
    public static boolean addNode(@Nonnull PlayerEntity player, @Nonnull PermissionNode node) {
        return addNode(player.name, node);
    }

    /**
     * Add a node associated with the command source given.
     * @param source the command source which will be given a new node.
     * @param node the node to add.
     * @return whether the node was successfully added.
     */
    public static boolean addNode(@Nonnull GlassCommandSource source, @Nonnull PermissionNode node) {
        return addNode(source.getName(), node);
    }

    /**
     * Add a node associated with the name given.
     * @param name the name which will be given a new node.
     * @param node the node to add.
     * @return whether the node was successfully added.
     */
    public static boolean addNode(@Nonnull String name, @Nonnull PermissionNode node) {
        final Set<PermissionNode> nodes = PermissionManagerImpl.getPermissionsMap().get(name);
        if (nodes == null) {
            final Set<PermissionNode> newNodes = new HashSet<>();
            newNodes.add(node);
            PermissionManagerImpl.getPermissionsMap().put(name, newNodes);
        } else {
            nodes.add(node);
        }
        return PermissionManagerImpl.tryUpdatePermissionsFile();
    }

    /**
     * Remove a node associated with the player given.
     * @param player the player which will lose the node.
     * @param node the node to remove.
     * @return whether the node was successfully removed.
     */
    public static boolean removeNode(@Nonnull PlayerEntity player, @Nonnull PermissionNode node) {
        return removeNode(player.name, node);
    }

    /**
     * Remove a node associated with the command source given.
     * @param source the command source which will lose the node.
     * @param node the node to remove.
     * @return whether the node was successfully removed.
     */
    public static boolean removeNode(@Nonnull GlassCommandSource source, @Nonnull PermissionNode node) {
        return removeNode(source.getName(), node);
    }

    /**
     * Remove a node associated with the name given.
     * @param name the name which will lose the node.
     * @param node the node to remove.
     * @return whether the node was successfully removed.
     */
    public static boolean removeNode(@Nonnull String name, @Nonnull PermissionNode node) {
        final Set<PermissionNode> nodes = PermissionManagerImpl.getPermissionsMap().get(name);
        if (nodes == null) {
            final Set<PermissionNode> newNodes = new HashSet<>();
            PermissionManagerImpl.getPermissionsMap().put(name, newNodes);
        } else {
            nodes.remove(node);
        }
        return PermissionManagerImpl.tryUpdatePermissionsFile();
    }

}
