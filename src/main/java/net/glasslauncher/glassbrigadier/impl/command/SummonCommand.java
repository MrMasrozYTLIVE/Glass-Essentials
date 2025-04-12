package net.glasslauncher.glassbrigadier.impl.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.glasslauncher.glassbrigadier.api.argument.coordinate.Coordinate;
import net.glasslauncher.glassbrigadier.api.argument.entityid.EntityType;
import net.glasslauncher.glassbrigadier.api.command.CommandProvider;
import net.glasslauncher.glassbrigadier.api.command.GlassCommandSource;
import net.glasslauncher.glassbrigadier.impl.argument.GlassArgumentBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityRegistry;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import static net.glasslauncher.glassbrigadier.api.argument.coordinate.CoordinateArgumentType.coordinate;
import static net.glasslauncher.glassbrigadier.api.argument.coordinate.CoordinateArgumentType.getCoordinate;
import static net.glasslauncher.glassbrigadier.api.argument.entityid.EntityTypeArgumentType.entityType;
import static net.glasslauncher.glassbrigadier.api.argument.entityid.EntityTypeArgumentType.getEntityType;
import static net.glasslauncher.glassbrigadier.api.predicate.HasPermission.permission;

public class SummonCommand implements CommandProvider {
    @Override
    public LiteralArgumentBuilder<GlassCommandSource> get() {
        return GlassArgumentBuilder.literal("summon")
                .requires(permission("command.summon"))
                .then(GlassArgumentBuilder.argument("id", entityType())
                        .then(GlassArgumentBuilder.argument("pos", coordinate())
                                .executes(this::summonEntity)
                        )
                );
    }

    public int summonEntity(CommandContext<GlassCommandSource> context) {
        Vec3d pos = getCoordinate(context, "pos").getVec3d(context.getSource());
        World world = context.getSource().getWorld();
        EntityType entityType = getEntityType(context, "id");
        Entity entity = EntityRegistry.create(entityType.id(), world);
        entity.setPosition(pos.x, pos.y, pos.z);
        world.spawnEntity(entity);
        sendFeedbackAndLog(context.getSource(), "Summoned " + entityType.id() + " at " + pos.x + " " + pos.y + " " + pos.z);
        return 0;
    }
}
