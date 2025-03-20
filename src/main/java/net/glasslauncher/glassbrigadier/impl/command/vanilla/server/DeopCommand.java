package net.glasslauncher.glassbrigadier.impl.command.vanilla.server;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.loader.api.FabricLoader;
import net.glasslauncher.glassbrigadier.api.argument.playerselector.TargetSelector;
import net.glasslauncher.glassbrigadier.api.argument.playerselector.TargetSelectorArgumentType;
import net.glasslauncher.glassbrigadier.api.command.CommandProvider;
import net.glasslauncher.glassbrigadier.api.command.GlassCommandSource;
import net.glasslauncher.glassbrigadier.impl.argument.GlassCommandBuilder;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.modificationstation.stationapi.api.util.Formatting;

import static net.glasslauncher.glassbrigadier.api.predicate.HasPermission.permission;

public class DeopCommand implements CommandProvider {
    @Override
    public LiteralArgumentBuilder<GlassCommandSource> get() {
        return GlassCommandBuilder.literal("deop", "Remove operator status from a player.")
                .requires(permission("command.deop"))
                .then(RequiredArgumentBuilder.<GlassCommandSource, TargetSelector<?>>argument("player", TargetSelectorArgumentType.entity())
                        .executes(this::deopPlayer)
                );
    }

    public int deopPlayer(CommandContext<GlassCommandSource> context) {
        String player = context.getArgument("player", String.class);
        //noinspection deprecation
        PlayerManager playerManager = ((MinecraftServer) FabricLoader.getInstance().getGameInstance()).playerManager;
        if (!playerManager.isOperator(player)) {
            context.getSource().sendMessage(Formatting.RED + player + " isn't an op!");
            return 0;
        }

        playerManager.removeFromOperators(player);
        sendFeedbackAndLog(context.getSource(), "Deopping " + player + ".");
        PlayerEntity playerEntity = context.getSource().getPlayerByName(player);
        if (playerEntity != null) {
            playerEntity.sendMessage(Formatting.YELLOW + "You are no longer op.");
        }
        return 0;
    }
}
