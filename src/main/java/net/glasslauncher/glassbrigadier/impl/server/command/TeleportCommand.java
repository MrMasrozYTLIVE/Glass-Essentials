package net.glasslauncher.glassbrigadier.impl.server.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.glasslauncher.glassbrigadier.api.argument.coordinate.Coordinate;
import net.glasslauncher.glassbrigadier.api.argument.playerselector.TargetSelector;
import net.glasslauncher.glassbrigadier.api.command.CommandProvider;
import net.glasslauncher.glassbrigadier.api.utils.EntityUtils;
import net.glasslauncher.glassbrigadier.impl.GlassCommandSource;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.math.Vec3d;

import java.util.List;

import static net.glasslauncher.glassbrigadier.api.argument.coordinate.CoordinateArgumentType.coordinate;
import static net.glasslauncher.glassbrigadier.api.argument.coordinate.CoordinateArgumentType.getCoordinate;
import static net.glasslauncher.glassbrigadier.api.argument.playerselector.TargetSelectorArgumentType.*;
import static net.glasslauncher.glassbrigadier.api.predicate.HasPermission.permission;
import static net.glasslauncher.glassbrigadier.api.predicate.IsWorldly.isWorldly;

public class TeleportCommand implements CommandProvider {
    @Override
    public LiteralArgumentBuilder<GlassCommandSource> get() {
        return LiteralArgumentBuilder.<GlassCommandSource>literal("tp")
                .requires(permission("command.tp"))
                .requires(isWorldly())
                .then(RequiredArgumentBuilder.<GlassCommandSource, Coordinate>argument("pos", coordinate())
                        .executes(this::teleportToPosition)
                )
                .then(RequiredArgumentBuilder.<GlassCommandSource, TargetSelector<?>>argument("target", entity())
                        .executes(this::teleportToEntity)
                )
                .then(RequiredArgumentBuilder.<GlassCommandSource, TargetSelector<?>>argument("teleportees", entities())
                        .then(RequiredArgumentBuilder.<GlassCommandSource, Coordinate>argument("pos", coordinate())
                                .executes(this::teleportOtherToPosition)
                        )
                        .then(RequiredArgumentBuilder.<GlassCommandSource, TargetSelector<?>>argument("target", entity())
                                .executes(this::teleportOtherToEntity)
                        )
                );
    }

    private int teleportToPosition(CommandContext<GlassCommandSource> ctx) {
        GlassCommandSource sender = ctx.getSource();
        Vec3d position = getCoordinate(ctx, "pos").getVec3d(sender);
        this.teleport(sender.getEntity(), position, sender.getEntity().yaw, sender.getEntity().pitch, sender);
        return 1;
    }

    private int teleportToEntity(CommandContext<GlassCommandSource> ctx) {
        GlassCommandSource sender = ctx.getSource();
        Entity target = getEntities(ctx, "target").getEntities(sender).get(0);
        this.teleport(sender.getEntity(), EntityUtils.getPosition(target), target.yaw, target.pitch, sender);
        return 1;
    }

    private int teleportOtherToPosition(CommandContext<GlassCommandSource> ctx) {
        GlassCommandSource sender = ctx.getSource();
        List<Entity> teleportees = getEntities(ctx, "teleportees").getEntities(sender);
        Vec3d position = getCoordinate(ctx, "pos").getVec3d(sender);
        teleportees.forEach(e -> this.teleport(e, position, e.yaw, e.pitch, sender));
        return teleportees.size();
    }

    private int teleportOtherToEntity(CommandContext<GlassCommandSource> ctx) {
        GlassCommandSource sender = ctx.getSource();
        List<Entity> teleportees = getEntities(ctx, "teleportees").getEntities(sender);
        Entity target = getEntities(ctx, "target").getEntities(sender).get(0);
        teleportees.forEach(e -> this.teleport(e, EntityUtils.getPosition(target), e.yaw, e.pitch, sender));
        return teleportees.size();
    }

    private void teleport(Entity entity, Vec3d position, float yaw, float pitch, GlassCommandSource sender) {
        if (entity instanceof ServerPlayerEntity) {
            ((ServerPlayerEntity)entity).networkHandler.teleport(position.x, position.y, position.z, yaw, pitch);
        } else {
            entity.setPositionAndAnglesKeepPrevAngles(position.x, position.y, position.z, yaw, pitch);
        }

        this.sendFeedbackAndLog(sender, String.format("Teleporting %s to %s (%f %f)", EntityUtils.getName(entity), position, yaw, pitch));
    }
}
