package net.glasslauncher.glassbrigadier.api.predicate;

import net.glasslauncher.glassbrigadier.GlassBrigadier;
import net.glasslauncher.glassbrigadier.api.command.GlassCommandSource;
import net.glasslauncher.glassbrigadier.api.permission.PermissionNode;
import net.glasslauncher.glassbrigadier.api.permission.PermissionNodeInstance;
import net.glasslauncher.glassbrigadier.impl.permission.Role;

import java.util.function.Function;
import java.util.function.Predicate;

public class HasPermission implements Predicate<GlassCommandSource> {

    private final PermissionNode<?> node;

    private HasPermission(PermissionNode<?> node) {
        this.node = node;
    }

    /**
     * Create a predicate that requires the node path given.
     * @param nodePath the node path that must be satisfied by the {@link GlassCommandSource}
     * @param positivePredicate the predicate that decides if this permission should be processed and/or considered true.
     * @return the predicate.
     */
    public static <T> HasPermission permission(String nodePath, PermissionNode.IsValuePositivePredicate<T> positivePredicate, Function<Object, T> valueLoadFunction, Function<T, Object> valueSaveFunction, Function<String, T> valueFromArgumentFunction) {
        GlassBrigadier.ALL_PERMISSIONS.add(nodePath);
        return new HasPermission(PermissionNode.register(nodePath, positivePredicate, valueLoadFunction, valueSaveFunction, valueFromArgumentFunction));
    }

    /**
     * Create a predicate that requires the node path given.
     * @param nodePath the node path that must be satisfied by the {@link GlassCommandSource}
     * @return the predicate.
     */
    public static HasPermission booleanPermission(String nodePath) {
        GlassBrigadier.ALL_PERMISSIONS.add(nodePath);
        return new HasPermission(PermissionNode.registerBoolean(nodePath));
    }

    @Override
    public boolean test(GlassCommandSource commandSource) {
        if (commandSource.satisfiesNode(null)) {
            return true;
        }

        //noinspection unchecked REEEEEEEEEEEEEE JAVA TYPE ERASURE
        return commandSource.getPermissions().stream().anyMatch(n -> ((PermissionNodeInstance<Object>) n).getNode().positivePredicate().isPositive((PermissionNodeInstance<Object>) n) && n.getNode().matches(node));
    }
}
