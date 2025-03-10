package net.glasslauncher.glassbrigadier.api.predicate;

import net.glasslauncher.glassbrigadier.api.permission.PermissionNode;
import net.glasslauncher.glassbrigadier.api.command.GlassCommandSource;

import java.util.function.Predicate;

public class HasPermission implements Predicate<GlassCommandSource> {

    private final PermissionNode node;

    private HasPermission(PermissionNode node) {
        this.node = node;
    }

    /**
     * Create a predicate that requires the node path given.
     * @param nodePath the node path that must be satisfied by the {@link GlassCommandSource}
     * @return the predicate.
     */
    public static HasPermission permission(String nodePath) {
        return new HasPermission(new PermissionNode(nodePath));
    }

    @Override
    public boolean test(GlassCommandSource commandSource) {
        return (commandSource).satisfiesNode(node);
    }
}
