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
import net.modificationstation.stationapi.api.util.Formatting;

import static net.glasslauncher.glassbrigadier.api.argument.playerselector.TargetSelectorArgumentType.getPlayers;
import static net.glasslauncher.glassbrigadier.api.predicate.HasPermission.permission;

public class OpCommand implements CommandProvider {
    @Override
    public LiteralArgumentBuilder<GlassCommandSource> get() {
        return GlassCommandBuilder.create("op", "Give the specified player operator status. This is effectively the same as giving them all permissions.")
                .requires(permission("command.op"))
                .then(RequiredArgumentBuilder.argument("player", TargetSelectorArgumentType.player()))
                .executes(this::opPlayer);
    }

    public int opPlayer(CommandContext<GlassCommandSource> context) {
        getPlayers(context, "player").getEntities(context.getSource()).forEach(player -> {
            //noinspection deprecation
            ((MinecraftServer) FabricLoader.getInstance().getGameInstance()).playerManager.addToOperators(player.name);
            sendFeedbackAndLog(context.getSource(), "Opping " + player.name);
            player.sendMessage(Formatting.YELLOW + "You are now op!");
        });
        return 0;
    }
}
