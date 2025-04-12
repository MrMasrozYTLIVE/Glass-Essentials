package net.glasslauncher.glassbrigadier.impl.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.glasslauncher.glassbrigadier.GlassBrigadier;
import net.glasslauncher.glassbrigadier.api.command.CommandProvider;
import net.glasslauncher.glassbrigadier.api.command.GlassCommandSource;
import net.glasslauncher.glassbrigadier.api.storage.world.WorldModStorageFile;
import net.glasslauncher.glassbrigadier.impl.argument.GlassArgumentBuilder;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.modificationstation.stationapi.api.util.Formatting;
import org.simpleyaml.configuration.ConfigurationSection;
import org.simpleyaml.configuration.MemorySection;

import java.util.List;

import static com.mojang.brigadier.arguments.StringArgumentType.word;
import static net.glasslauncher.glassbrigadier.api.predicate.HasPermission.permission;
import static net.glasslauncher.glassbrigadier.api.predicate.IsPlayer.isPlayer;

public class WarpCommand implements CommandProvider {
    @Override
    public LiteralArgumentBuilder<GlassCommandSource> get() {
        return GlassArgumentBuilder.literal("warp")
                .requires(source -> isPlayer().test(source) && permission("command.warp").test(source))
                .then(GlassArgumentBuilder.argument("name", word())
                        .executes(this::warp)
                );
    }

    public int warp(CommandContext<GlassCommandSource> context) {
        String name = context.getArgument("name", String.class);

        WorldModStorageFile serverStorage = WorldModStorageFile.of(GlassBrigadier.NAMESPACE.id("warps"));
        MemorySection warps = (MemorySection) serverStorage.get("warps");
        ConfigurationSection warp = (ConfigurationSection) warps.get(name);

        if (warp == null) {
            context.getSource().sendMessage(Formatting.RED + "No warp named \"" + name + "\".");
            return 0;
        }

        List<Double> warpLoc = warp.getDoubleList("location");

        if (context.getSource().getPlayer() instanceof ServerPlayerEntity serverPlayerEntity) {
            serverPlayerEntity.networkHandler.teleport(warpLoc.get(0), warpLoc.get(1), warpLoc.get(2), serverPlayerEntity.yaw, serverPlayerEntity.pitch);
        }
        else {
            PlayerEntity player = context.getSource().getPlayer();
            player.setPositionAndAnglesKeepPrevAngles(warpLoc.get(0), warpLoc.get(1), warpLoc.get(2), player.yaw, player.pitch);
        }

        context.getSource().sendMessage("Warped to \"" + name + "\".");
        return 0;
    }
}
