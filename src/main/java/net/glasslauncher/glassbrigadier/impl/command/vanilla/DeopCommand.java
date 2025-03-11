package net.glasslauncher.glassbrigadier.impl.command.vanilla;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.loader.api.FabricLoader;
import net.glasslauncher.glassbrigadier.api.argument.playerselector.TargetSelectorArgumentType;
import net.glasslauncher.glassbrigadier.api.command.CommandProvider;
import net.glasslauncher.glassbrigadier.api.command.GlassCommandSource;
import net.glasslauncher.glassbrigadier.impl.argument.GlassCommandBuilder;
import net.minecraft.server.MinecraftServer;

import static net.glasslauncher.glassbrigadier.api.argument.playerselector.TargetSelectorArgumentType.getPlayers;
import static net.glasslauncher.glassbrigadier.api.predicate.HasPermission.permission;

public class DeopCommand implements CommandProvider {
    @Override
    public LiteralArgumentBuilder<GlassCommandSource> get() {
        return GlassCommandBuilder.create("deop", "Remove operator status from a player.")
                .requires(permission("command.deop"))
                .then(RequiredArgumentBuilder.argument("player", TargetSelectorArgumentType.player()))
                .executes(this::opPlayer);
    }

    public int opPlayer(CommandContext<GlassCommandSource> context) {
        getPlayers(context, "player").getEntities(context.getSource()).forEach(player -> {
            //noinspection deprecation
            ((MinecraftServer) FabricLoader.getInstance().getGameInstance()).playerManager.removeFromOperators(player.name);
            sendFeedbackAndLog(context.getSource(), "Deopping " + player.name);
        });
        return 0;
    }
}
