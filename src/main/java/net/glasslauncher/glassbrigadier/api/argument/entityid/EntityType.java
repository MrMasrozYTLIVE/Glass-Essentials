package net.glasslauncher.glassbrigadier.api.argument.entityid;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import net.minecraft.entity.Entity;

public record EntityType(String id, Class<? extends Entity> entity) {
    private static final Cache<String, EntityType> CACHE = Caffeine.newBuilder().softValues().build();

    public static EntityType of(String id, Class<? extends Entity> entity) {
        return CACHE.get(id, id_ -> new EntityType(id, entity));
    }
}
