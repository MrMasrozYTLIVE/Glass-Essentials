package net.glasslauncher.glassbrigadier.impl.command.vanilla;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.glasslauncher.glassbrigadier.GlassBrigadier;
import net.glasslauncher.glassbrigadier.api.command.CommandProvider;
import net.glasslauncher.glassbrigadier.api.command.GlassCommandSource;
import net.glasslauncher.glassbrigadier.impl.argument.GlassCommandBuilder;
import net.modificationstation.stationapi.api.util.Formatting;

import static com.mojang.brigadier.arguments.StringArgumentType.greedyString;
import static net.glasslauncher.glassbrigadier.api.predicate.HasPermission.permission;

public class SayCommand implements CommandProvider {
    @Override
    public LiteralArgumentBuilder<GlassCommandSource> get() {
        return GlassCommandBuilder.literal("say", "Say a message as the server.")
                .requires(permission("command.say"))
                .then(RequiredArgumentBuilder.argument("message", greedyString()))
                .executes(this::say);
    }

    public int say(CommandContext<GlassCommandSource> context) {
        String message = context.getArgument("message", String.class);
        GlassBrigadier.LOGGER.info("[{}] {}", context.getSource().getSourceName(), message);
        context.getSource().getAllPlayers().forEach(player -> player.sendMessage(Formatting.LIGHT_PURPLE + "[Server] " + message));
        return 0;
    }
}
