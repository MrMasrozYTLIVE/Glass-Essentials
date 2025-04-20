package net.glasslauncher.glassbrigadier.api.permission;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.Objects;
import java.util.function.Function;

public record PermissionNode<T>(
        String path,
        IsValuePositivePredicate<T> positivePredicate,
        Function<Object, T> valueLoadFunction, Function<T, Object> valueSaveFunction,
        Function<String, T> valueFromArgumentFunction
) {
    private static final @NotNull Cache<@NotNull String, @NotNull PermissionNode<?>> CACHE = Caffeine.newBuilder().softValues().build();

    public static IsValuePositivePredicate<Boolean> BOOLEAN = notNullPositive();
    public static Function<Object, Boolean> BOOLEAN_LOAD = blindLoad();
    public static Function<Boolean, Object> BOOLEAN_SAVE = blindSave();
    public static Function<String, Boolean> BOOLEAN_PARSE = Boolean::valueOf;

    public static IsValuePositivePredicate<Integer> INTEGER = notNullPositive();
    public static Function<Object, Integer> INTEGER_LOAD = blindLoad();
    public static Function<Integer, Object> INTEGER_SAVE = blindSave();
    public static Function<String, Integer> INTEGER_PARSE = value -> {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException ignored){}
        return null;
    };

    public static <T> Function<Object, T> blindLoad() {
        //noinspection unchecked
        return val -> (T) val;
    }

    public static <T> Function<T, Object> blindSave() {
        return val -> val;
    }

    public static <T> IsValuePositivePredicate<T> notNullPositive() {
        return thisNode -> thisNode.getValue() != null;
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
    public static <T> PermissionNode<T> register(String path, IsValuePositivePredicate<T> positivePredicate, Function<Object, T> valueLoadFunction, Function<T, Object> valueSaveFunction, Function<String, T> valueFromArgumentFunction) {
        //noinspection unchecked Ahahaha fuck type erasure fuck type erasure fuck type erasure fucking why
        return (PermissionNode<T>) CACHE.get(path, path_ -> new PermissionNode<>(path_, positivePredicate, valueLoadFunction, valueSaveFunction, valueFromArgumentFunction));
    }

    /**
     * Register the permission node for use.
     */
    public static <T> PermissionNode<T> registerBoolean(String path) {
        //noinspection unchecked Ahahaha fuck type erasure fuck type erasure fuck type erasure fucking why
        return (PermissionNode<T>) CACHE.get(path, path_ -> new PermissionNode<>(path_, BOOLEAN, BOOLEAN_LOAD, BOOLEAN_SAVE, BOOLEAN_PARSE));
    }

    /**
     * Register the permission node for use.
     */
    public static <T> PermissionNode<T> registerInteger(String path) {
        //noinspection unchecked Ahahaha fuck type erasure fuck type erasure fuck type erasure fucking why
        return (PermissionNode<T>) CACHE.get(path, path_ -> new PermissionNode<>(path_, INTEGER, INTEGER_LOAD, INTEGER_SAVE, INTEGER_PARSE));
    }

    public boolean matches(PermissionNode<?> nodeToCheck) {
        String[] pathElements = path.split("\\.");
        String[] checkPathElements = nodeToCheck.path().split("\\.");
        final int length = Math.max(pathElements.length, checkPathElements.length);

        for (int i = 0; i < length; i++) {
            final String node1 = i < pathElements.length ? pathElements[i] : "*";
            final String node2 = i < checkPathElements.length ? checkPathElements[i] : null;
            if (node2 == null || !nodeSatisfiesNode(node1, node2))
                return false;
        }
        return true;
    }

    public static boolean nodeSatisfiesNode(@Nonnull String node1, String node2) {
        if (node1.equals("*"))
            return true;
        return node1.equals(node2);
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
