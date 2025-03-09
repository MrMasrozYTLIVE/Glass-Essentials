package net.glasslauncher.glassbrigadier.api.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.CommandNode;
import net.glasslauncher.glassbrigadier.impl.GlassBrigadier;
import net.glasslauncher.glassbrigadier.impl.GlassCommandSource;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

public final class CommandRegistry {

    private static final Map<CommandNode<GlassCommandSource>, String> helpMap = new HashMap<>();

    /**
     * Build and register a command with help text.
     * @param command the {@link LiteralArgumentBuilder} of the command to build and register.
     * @param helpText the text that will be displayed in /help for your command.
     * @return the built {@link CommandNode}.
     */
    public static CommandNode<GlassCommandSource> register(LiteralArgumentBuilder<GlassCommandSource> command, String helpText) {
        CommandNode<GlassCommandSource> result = register(command);
        helpMap.put(result, helpText);
        return result;
    }

    /**
     * Build and register a command without help text.
     * @param command the {@link LiteralArgumentBuilder} of the command to build and register.
     * @return the built {@link CommandNode}.
     */
    public static CommandNode<GlassCommandSource> register(LiteralArgumentBuilder<GlassCommandSource> command) {
        return GlassBrigadier.dispatcher.register(command);
    }

    /**
     * Build and register a supplied command with help text.
     * @param commandSupplier The {@link CommandProvider} to build and register.
     * @param helpText the text that will be displayed in /help for your command.
     * @return the built {@link CommandNode}.
     */
    public static CommandNode<GlassCommandSource> register(CommandProvider commandSupplier,
                                                       String helpText) {
        CommandNode<GlassCommandSource> result = register(commandSupplier);
        helpMap.put(result, helpText);
        return result;
    }

    /**
     * Build and register a supplied command without help text.
     * @param commandSupplier The {@link CommandProvider} to build and register.
     * @return the built {@link CommandNode}.
     */
    public static CommandNode<GlassCommandSource> register(CommandProvider commandSupplier) {
        return GlassBrigadier.dispatcher.register(commandSupplier.get());
    }

    /**
     * Get help text for a given command.
     * @param command the {@link CommandNode} to find help text for.
     * @return the help text, or "No help available" if no help text has been registered.
     */
    @Nonnull
    public static String getHelp(CommandNode<GlassCommandSource> command) {
        if (!helpMap.containsKey(command))
            return "No help available";
        return helpMap.get(command);
    }
}
