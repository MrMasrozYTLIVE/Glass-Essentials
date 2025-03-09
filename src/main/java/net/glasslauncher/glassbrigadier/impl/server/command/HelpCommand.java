package net.glasslauncher.glassbrigadier.impl.server.command;

import com.google.common.collect.Iterables;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.tree.CommandNode;
import net.glasslauncher.glassbrigadier.api.command.CommandProvider;
import net.glasslauncher.glassbrigadier.impl.GlassBrigadier;
import net.glasslauncher.glassbrigadier.impl.GlassCommandSource;

import java.util.Map;

public class HelpCommand implements CommandProvider {
    private static final SimpleCommandExceptionType FAILED_EXCEPTION = new SimpleCommandExceptionType(() -> "Unknown command or insufficient permissions");

    @Override
    public LiteralArgumentBuilder<GlassCommandSource> get() {
        return LiteralArgumentBuilder.<GlassCommandSource>literal("help").executes((context) -> {
            Map<CommandNode<GlassCommandSource>, String> map = GlassBrigadier.dispatcher.getSmartUsage(GlassBrigadier.dispatcher.getRoot(), context.getSource());

            for (String string : map.values()) {
                context.getSource().sendMessage("/" + string);
            }

            return map.size();
        }).then(RequiredArgumentBuilder.<GlassCommandSource, String>argument("command", StringArgumentType.greedyString()).executes((context) -> {
            ParseResults<GlassCommandSource> parseResults = GlassBrigadier.dispatcher.parse(StringArgumentType.getString(context, "command"), (GlassCommandSource)context.getSource());
            if (parseResults.getContext().getNodes().isEmpty()) {
                throw FAILED_EXCEPTION.create();
            } else {
                Map<CommandNode<GlassCommandSource>, String> map = GlassBrigadier.dispatcher.getSmartUsage(Iterables.getLast(parseResults.getContext().getNodes()).getNode(), context.getSource());

                for (String string : map.values()) {
                    context.getSource().sendMessage("/" + parseResults.getReader().getString() + " " + string);
                }

                return map.size();
            }
        }));
    }
}
