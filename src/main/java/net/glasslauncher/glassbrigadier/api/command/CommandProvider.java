package net.glasslauncher.glassbrigadier.api.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.glasslauncher.glassbrigadier.GlassBrigadier;

import java.util.function.Supplier;

public interface CommandProvider extends Supplier<LiteralArgumentBuilder<GlassCommandSource>> {

    default void sendFeedbackAndLog(GlassCommandSource source, String message) {
        source.sendFeedback(message);
        GlassBrigadier.LOGGER.info("{}: {}", source.getSourceName(), message);
    }

    default void sendToChatAndLog(GlassCommandSource source, String message) {
        source.getAllPlayers().forEach(player -> player.sendMessage(message));
        GlassBrigadier.LOGGER.info(message);
    }

    default void sendToPlayerAndLog(GlassCommandSource source, String playerName, String message) {
        source.sendMessageToPlayer(playerName, message);
        GlassBrigadier.LOGGER.info(message);
    }

    default void log(String message) {
        GlassBrigadier.LOGGER.info(message);
    }

    default void logError(String message) {
        GlassBrigadier.LOGGER.error(message);
    }

    default void logWarn( String message) {
        GlassBrigadier.LOGGER.warn(message);
    }
}
