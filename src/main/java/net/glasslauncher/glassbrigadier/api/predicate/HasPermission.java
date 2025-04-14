package net.glasslauncher.glassbrigadier.api.predicate;

import net.glasslauncher.glassbrigadier.GlassBrigadier;
import net.glasslauncher.glassbrigadier.api.command.GlassCommandSource;
import net.glasslauncher.glassbrigadier.api.permission.PermissionNode;
import net.glasslauncher.glassbrigadier.api.permission.PermissionNodeInstance;

import java.util.function.Predicate;

public class HasPermission implements Predicate<GlassCommandSource> {

    private final PermissionNode<?> node;

    private HasPermission(PermissionNode<?> node) {
        this.node = node;
    }

    /**
     * Create a predicate that requires the node path given.
     * @param nodePath the node path that must be satisfied by the {@link GlassCommandSource}
     * @param positivePredicate the predicate that decides if this permission should be processed.
     * @return the predicate.
     */
    public static <T> HasPermission permission(String nodePath, PermissionNode.IsValuePositivePredicate<T> positivePredicate) {
        GlassBrigadier.ALL_PERMISSIONS.add(nodePath);
        return new HasPermission(PermissionNode.register(nodePath, positivePredicate));
    }

    /**
     * Create a predicate that requires the node path given.
     * @param nodePath the node path that must be satisfied by the {@link GlassCommandSource}
     * @return the predicate.
     */
    public static HasPermission booleanPermission(String nodePath) {
        GlassBrigadier.ALL_PERMISSIONS.add(nodePath);
        return new HasPermission(PermissionNode.register(nodePath, PermissionNode.BOOLEAN));
    }

    /**
     * Create a predicate that requires the node path given.
     * @param nodePath the node path that must be satisfied by the {@link GlassCommandSource}
     * @return the predicate.
     */
    public static <T> HasPermission simpleNullablePermission(String nodePath) {
        GlassBrigadier.ALL_PERMISSIONS.add(nodePath);
        return new HasPermission(PermissionNode.register(nodePath, PermissionNode.<T>notNullPositive()));
    }

    @Override
    public boolean test(GlassCommandSource commandSource) {
        PermissionNodeInstance<?> instance = PermissionNodeInstance.ofExisting(node, commandSource);
        if (commandSource.satisfiesNode(null)) {
            return true;
        }
        if (instance == null) {
            GlassBrigadier.LOGGER.error("Permission " + node.getPath() + " has no instance!");
            return false;
        }
        return commandSource.satisfiesNode(instance);
    }
}
