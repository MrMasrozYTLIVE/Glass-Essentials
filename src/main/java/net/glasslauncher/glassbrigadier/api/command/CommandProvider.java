package net.glasslauncher.glassbrigadier.api.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.glasslauncher.glassbrigadier.GlassBrigadier;

import java.util.function.Supplier;

public interface CommandProvider extends Supplier<LiteralArgumentBuilder<GlassCommandSource>> {

    default void sendFeedbackAndLog(GlassCommandSource source, String message) {
        source.sendMessage(message);
        GlassBrigadier.LOGGER.info("{}: {}", source.getName(), message);
    }

    default void sendToChatAndLog(GlassCommandSource source, String message) {
        source.getAllPlayers().forEach(player -> player.sendMessage(message));
        GlassBrigadier.LOGGER.info(message);
    }

    default void sendToPlayerAndLog(GlassCommandSource source, String playerName, String message) {
        source.sendMessageToPlayer(playerName, message);
        GlassBrigadier.LOGGER.info(message);
    }
}
