package net.glasslauncher.glassbrigadier.impl.server.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.glasslauncher.glassbrigadier.api.command.CommandProvider;
import net.glasslauncher.glassbrigadier.impl.GlassCommandSource;

import static com.mojang.brigadier.arguments.StringArgumentType.getString;
import static com.mojang.brigadier.arguments.StringArgumentType.greedyString;
import static net.glasslauncher.glassbrigadier.api.predicate.HasPermission.permission;

public class MeCommand implements CommandProvider {

    @Override
    public LiteralArgumentBuilder<GlassCommandSource> get() {
        return LiteralArgumentBuilder.<GlassCommandSource>literal("me")
                .requires(permission("command.me"))
                .then(RequiredArgumentBuilder.<GlassCommandSource, String>argument("message", greedyString())
                                .executes(this::sendMeMessage)
                );
    }

    public int sendMeMessage(CommandContext<GlassCommandSource> context) {
        String message = "* " + context.getSource().getName() + " " + getString(context, "message").trim();
        sendToChatAndLog(context.getSource(), message);
        return 0;
    }
}
