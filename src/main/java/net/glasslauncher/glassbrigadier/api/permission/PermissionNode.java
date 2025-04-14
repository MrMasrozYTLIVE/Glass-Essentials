package net.glasslauncher.glassbrigadier.api.permission;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

@Getter
public class PermissionNode<T> {
    private static final @NotNull Cache<@NotNull String, @NotNull PermissionNode<?>> CACHE = Caffeine.newBuilder().softValues().build();

    public static IsValuePositivePredicate<Boolean> BOOLEAN = thisNode -> thisNode.getValue() != null;

    public static <T> IsValuePositivePredicate<T> notNullPositive() {
        return thisNode -> thisNode.getValue() != null;
    }

    private final String path;
    transient private final IsValuePositivePredicate<T> positivePredicate;

    private PermissionNode(String path, IsValuePositivePredicate<T> positivePredicate) {
        this.path = path;
        this.positivePredicate = positivePredicate;
    }

    /**
     * Get the permission node object relevant to the provided string.
     */
    public static <T> @Nullable PermissionNode<T> ofExisting(String path) {
        //noinspection unchecked Ahahaha fuck type erasure fuck type erasure fuck type erasure fucking why
        return (PermissionNode<T>) CACHE.getIfPresent(path);
    }

    /**
     * Register the permission node for use.
     */
    public static <T> PermissionNode<T> register(String path, IsValuePositivePredicate<T> positivePredicate) {
        //noinspection unchecked Ahahaha fuck type erasure fuck type erasure fuck type erasure fucking why
        return (PermissionNode<T>) CACHE.get(path, path_ -> new PermissionNode<>(path_, positivePredicate));
    }

    @Override
    public String toString() {
        return path;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PermissionNode<?> that = (PermissionNode<?>) o;
        return path.equals(that.path);
    }

    @Override
    public int hashCode() {
        return Objects.hash(path);
    }

    public interface IsValuePositivePredicate<T> {
        boolean isPositive(PermissionNodeInstance<T> thisNode);
    }

}
