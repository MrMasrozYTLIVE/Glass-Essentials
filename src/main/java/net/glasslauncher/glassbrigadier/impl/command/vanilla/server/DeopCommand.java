package net.glasslauncher.glassbrigadier.impl.command.vanilla.server;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.loader.api.FabricLoader;
import net.glasslauncher.glassbrigadier.api.command.CommandProvider;
import net.glasslauncher.glassbrigadier.api.command.GlassCommandSource;
import net.glasslauncher.glassbrigadier.impl.argument.GlassArgumentBuilder;
import net.glasslauncher.glassbrigadier.impl.argument.GlassCommandBuilder;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.modificationstation.stationapi.api.util.Formatting;

import static com.mojang.brigadier.arguments.StringArgumentType.string;
import static net.glasslauncher.glassbrigadier.api.predicate.HasPermission.booleanPermission;

public class DeopCommand implements CommandProvider {
    @Override
    public LiteralArgumentBuilder<GlassCommandSource> get() {
        return GlassCommandBuilder.literal("deop", "Remove operator status from a player.")
                .requires(booleanPermission("command.deop"))
                .then(GlassArgumentBuilder.argument("player", string())
                        .executes(this::deopPlayer)
                );
    }

    public int deopPlayer(CommandContext<GlassCommandSource> context) {
        String player = context.getArgument("player", String.class);
        //noinspection deprecation
        PlayerManager playerManager = ((MinecraftServer) FabricLoader.getInstance().getGameInstance()).playerManager;
        if (!playerManager.isOperator(player)) {
            context.getSource().sendFeedback(Formatting.RED + player + " isn't an op!");
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
