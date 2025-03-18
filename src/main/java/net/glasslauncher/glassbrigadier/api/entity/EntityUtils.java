package net.glasslauncher.glassbrigadier.api.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public final class EntityUtils {
    public static String getName(final Entity entity) {
        if (entity instanceof PlayerEntity) {
            return ((PlayerEntity) entity).name;
        } else {
            return EntityRegistry.getId(entity);
        }
    }

    public static double distanceBetween(Entity entity, Vec3d pos) {
        double var2 = (entity.x - pos.x);
        double var3 = (entity.y - pos.y);
        double var4 = (entity.z - pos.z);
        return MathHelper.sqrt(var2 * var2 + var3 * var3 + var4 * var4);
    }

    public static Vec3d getPosition(Entity entity) {
        return Vec3d.createCached(entity.x, entity.y, entity.z);
    }
}
