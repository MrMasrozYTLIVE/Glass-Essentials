package net.glasslauncher.glassbrigadier.impl.command.vanilla.server;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.loader.api.FabricLoader;
import net.glasslauncher.glassbrigadier.api.argument.playerdata.PlayerDataArgumentType;
import net.glasslauncher.glassbrigadier.api.command.CommandProvider;
import net.glasslauncher.glassbrigadier.api.command.GlassCommandSource;
import net.glasslauncher.glassbrigadier.impl.argument.GlassArgumentBuilder;
import net.glasslauncher.glassbrigadier.impl.argument.GlassCommandBuilder;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.modificationstation.stationapi.api.util.Formatting;

import static net.glasslauncher.glassbrigadier.api.predicate.HasPermission.permission;

public class OpCommand implements CommandProvider {
    @Override
    public LiteralArgumentBuilder<GlassCommandSource> get() {
        return GlassCommandBuilder.literal("op", "Give the specified player operator status.", "Give the specified player operator status. This is effectively the same as giving them all permissions.")
                .requires(permission("command.op"))
                .then(GlassArgumentBuilder.argument("player", PlayerDataArgumentType.offlinePlayers())
                        .executes(this::opPlayer)
                );
    }

    public int opPlayer(CommandContext<GlassCommandSource> context) {
        String player = context.getArgument("player", String.class);
        //noinspection deprecation
        PlayerManager playerManager = ((MinecraftServer) FabricLoader.getInstance().getGameInstance()).playerManager;
        if (playerManager.isOperator(player)) {
            context.getSource().sendFeedback(Formatting.RED + player + " is already an op!");
            return 0;
        }

        playerManager.addToOperators(player);

        sendFeedbackAndLog(context.getSource(), "Opping " + player + ".");
        PlayerEntity playerEntity = context.getSource().getPlayerByName(player);
        if (playerEntity != null) {
            playerEntity.sendMessage(Formatting.YELLOW + "You are now op!");
        }
        return 0;
    }
}
