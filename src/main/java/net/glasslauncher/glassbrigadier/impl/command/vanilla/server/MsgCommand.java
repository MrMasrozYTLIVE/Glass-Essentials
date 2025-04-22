package net.glasslauncher.glassbrigadier.impl.command.vanilla.server;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.glasslauncher.glassbrigadier.GlassBrigadier;
import net.glasslauncher.glassbrigadier.api.command.CommandProvider;
import net.glasslauncher.glassbrigadier.api.command.GlassCommandSource;
import net.glasslauncher.glassbrigadier.impl.argument.GlassArgumentBuilder;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.play.ChatMessagePacket;
import net.modificationstation.stationapi.api.network.packet.PacketHelper;
import net.modificationstation.stationapi.api.util.Formatting;

import java.util.concurrent.atomic.AtomicBoolean;

import static com.mojang.brigadier.arguments.StringArgumentType.getString;
import static com.mojang.brigadier.arguments.StringArgumentType.greedyString;
import static net.glasslauncher.glassbrigadier.api.argument.playerselector.TargetSelectorArgumentType.entity;
import static net.glasslauncher.glassbrigadier.api.argument.playerselector.TargetSelectorArgumentType.getPlayers;
import static net.glasslauncher.glassbrigadier.api.predicate.HasPermission.booleanPermission;

public class MsgCommand implements CommandProvider {

    @Override
    public LiteralArgumentBuilder<GlassCommandSource> get() {
        return GlassArgumentBuilder.literal("msg")
                .requires(booleanPermission("command.msg"))
                .then(GlassArgumentBuilder.argument("player", entity())
                                .then(GlassArgumentBuilder.<String>argument("message", greedyString())
                                                .executes(this::whisper)
                                )
                );
    }

    public int whisper(CommandContext<GlassCommandSource> context) {
        getPlayers(context, "player").getNames(context.getSource()).forEach(player -> {
            PlayerEntity playerEntity = context.getSource().getPlayerByName(player);

            if (playerEntity == null) {
                context.getSource().sendFeedback(Formatting.RED + "Invalid player name: " + player);
                return;
            }

            String messageArg = getString(context, "message");
            String message = Formatting.GRAY + context.getSource().getSourceName() + " whispers: " + messageArg;
            GlassBrigadier.LOGGER.info(message);
            playerEntity.sendMessage(message);
            context.getSource().sendFeedback("You -> " + playerEntity.name + ": " + messageArg);
        });
        return 0;
    }
}
