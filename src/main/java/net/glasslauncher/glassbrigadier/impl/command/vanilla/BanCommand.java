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
import net.minecraft.server.PlayerManager;
import net.modificationstation.stationapi.api.util.Formatting;

import static net.glasslauncher.glassbrigadier.api.argument.playerselector.TargetSelectorArgumentType.getPlayers;
import static net.glasslauncher.glassbrigadier.api.predicate.HasPermission.permission;

public class BanCommand implements CommandProvider {
    @Override
    public LiteralArgumentBuilder<GlassCommandSource> get() {
        return GlassCommandBuilder.create("ban", "Ban a given player.")
                .requires(permission("command.ban"))
                .then(RequiredArgumentBuilder.argument("player", TargetSelectorArgumentType.player()))
                .executes(this::banPlayer);
    }

    public int banPlayer(CommandContext<GlassCommandSource> context) {
        getPlayers(context, "player").getEntities(context.getSource()).forEach(player -> {
            //noinspection deprecation
            PlayerManager playerManager = ((MinecraftServer) FabricLoader.getInstance().getGameInstance()).playerManager;
            if (playerManager.bannedPlayers.contains(player.name.toLowerCase().strip())) {
                context.getSource().sendMessage(Formatting.RED + player.name + " is already banned!");
                return;
            }
            playerManager.banPlayer(player.name);
            sendFeedbackAndLog(context.getSource(), "Banned " + player.name + ".");
        });
        return 0;
    }
}
