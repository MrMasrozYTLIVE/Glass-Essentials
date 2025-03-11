package net.glasslauncher.glassbrigadier.impl.command.vanilla;

import com.google.common.collect.Iterables;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.ParsedCommandNode;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.tree.CommandNode;
import net.glasslauncher.glassbrigadier.api.command.CommandProvider;
import net.glasslauncher.glassbrigadier.GlassBrigadier;
import net.glasslauncher.glassbrigadier.api.command.GlassCommandSource;
import net.glasslauncher.glassbrigadier.impl.argument.DescriptiveLiteralCommandNode;

import java.util.List;
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
            String command = StringArgumentType.getString(context, "command");
            ParseResults<GlassCommandSource> parseResults = GlassBrigadier.dispatcher.parse(command, context.getSource());
            List<ParsedCommandNode<GlassCommandSource>> commandNodes = parseResults.getContext().getNodes();
            if (!parseResults.getExceptions().isEmpty()) {
                throw parseResults.getExceptions().values().stream().findFirst().get();
            }
            else if (commandNodes.isEmpty()) {
                throw FAILED_EXCEPTION.create();
            }
            else {
                CommandNode<GlassCommandSource> commandNode = Iterables.getLast(parseResults.getContext().getNodes()).getNode();

                Map<CommandNode<GlassCommandSource>, String> map = GlassBrigadier.dispatcher.getSmartUsage(commandNode, context.getSource());

                if (commandNode instanceof DescriptiveLiteralCommandNode<?> literalArgumentBuilder) {
                    context.getSource().sendMessage(literalArgumentBuilder.getDescription());
                }
                for (String string : map.values()) {
                    context.getSource().sendMessage("/" + parseResults.getReader().getString() + " " + string);
                }

                return map.size();
            }
        }));
    }
}
