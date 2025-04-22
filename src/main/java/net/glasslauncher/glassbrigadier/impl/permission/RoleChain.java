package net.glasslauncher.glassbrigadier.impl.permission;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.stream.Collectors;

@Getter
public class RoleChain {
    private static final @NotNull Cache<@NotNull String, @NotNull RoleChain> CACHE = Caffeine.newBuilder().softValues().build();

    private String name;

    private Set<Role> roles;

    private RoleChain(String name) {
        this.name = name;
    }

    public static RoleChain of(String name) {
        return CACHE.get(name, RoleChain::new);
    }

    public static boolean exists(String name) {
        return CACHE.getIfPresent(name) != null;
    }

    public static Set<RoleChain> getStartingWith(String name) {
        String finalName = name.toLowerCase();
        return CACHE.asMap().values().stream().filter(roleChain -> roleChain.getName().toLowerCase().startsWith(finalName)).collect(Collectors.toSet());
    }

    public static void invalidateAll() {
        CACHE.invalidateAll();
    }

    public boolean setName(String name) {
        if (exists(name)) {
            return false;
        }

        CACHE.invalidate(this.name);
        this.name = name;
        CACHE.put(name, this);
        return true;
    }
}
