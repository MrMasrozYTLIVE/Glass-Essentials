package net.glasslauncher.glassbrigadier.api.permission;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.Getter;
import lombok.Setter;
import net.glasslauncher.glassbrigadier.api.command.GlassCommandSource;
import net.glasslauncher.glassbrigadier.impl.permission.Role;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.Set;

@Getter
public class PermissionNodeInstance<T> {
    private static final @NotNull Cache<@NotNull String, @NotNull PermissionNodeInstance<?>> CACHE = Caffeine.newBuilder().softValues().build();

    private final PermissionNode<T> node;
    private final GlassCommandSource source;
    private final Role role;
    @Setter
    private T value;

    private PermissionNodeInstance(PermissionNode<T> node, GlassCommandSource source) {
        this.node = node;
        this.source = source;
        this.role = null;
    }

    private PermissionNodeInstance(PermissionNode<T> node, Role role) {
        this.node = node;
        this.source = null;
        this.role = role;
    }

    /**
     * Get the permission node object relevant to the provided string.
     */
    public static <T> @Nullable PermissionNodeInstance<T> ofExisting(PermissionNode<T> node, GlassCommandSource source) {
        //noinspection unchecked Ahahaha fuck type erasure fuck type erasure fuck type erasure fucking why
        return (PermissionNodeInstance<T>) CACHE.getIfPresent(getID(node, source));
    }

    /**
     * Get the permission node object relevant to the provided string.
     */
    public static <T> PermissionNodeInstance<T> ofAndSetValue(PermissionNode<T> node, GlassCommandSource source, T value) {
        //noinspection unchecked Ahahaha fuck type erasure fuck type erasure fuck type erasure fucking why
        PermissionNodeInstance<T> instance = (PermissionNodeInstance<T>) CACHE.get(getID(node, source), path_ -> new PermissionNodeInstance<>(node, source));
        instance.setValue(value);
        return instance;
    }

    /**
     * Get the permission node object relevant to the provided string.
     */
    public static <T> @Nullable PermissionNodeInstance<T> ofExisting(PermissionNode<T> node, Role role) {
        //noinspection unchecked Ahahaha fuck type erasure fuck type erasure fuck type erasure fucking why
        return (PermissionNodeInstance<T>) CACHE.getIfPresent(getID(node, role));
    }

    /**
     * Get the permission node object relevant to the provided string.
     */
    public static <T> PermissionNodeInstance<T> ofAndSetValue(PermissionNode<T> node, Role role, T value) {
        //noinspection unchecked Ahahaha fuck type erasure fuck type erasure fuck type erasure fucking why
        PermissionNodeInstance<T> instance = (PermissionNodeInstance<T>) CACHE.get(getID(node, role), path_ -> new PermissionNodeInstance<>(node, role));
        instance.setValue(value);
        return instance;
    }

    private static String getID(PermissionNode<?> path, GlassCommandSource source) {
        return path + ":C:" + source.getSourceName();
    }

    private static String getID(PermissionNode<?> path, Role role) {
        return path + ":R:" + role.getName();
    }

    /**
     * Check if this node satisfies another.
     * @param nodeToCheck the node to check.
     * @return whether this node satisfies the other node.
     */
    public boolean satisfies(@Nonnull PermissionNodeInstance<?> nodeToCheck) {
        if (!node.positivePredicate().isPositive(this)) {
            return false;
        }
        return node.matches(nodeToCheck.node);
    }

    /**
     * Check if this node is satisfied by a {@link Set} of others.
     * @param nodesToCheck the set of nodes to check.
     * @return whether this node is satisfied by any in the set.
     */
    public boolean isSatisfiedBy(Set<PermissionNodeInstance<?>> nodesToCheck) {
        for (PermissionNodeInstance<?> nodeToCheck : nodesToCheck) {
            if (nodeToCheck.satisfies(this))
                return true;
        }
        return false;
    }

    public static void invalidateAll() {
        CACHE.invalidateAll();
    }
}
