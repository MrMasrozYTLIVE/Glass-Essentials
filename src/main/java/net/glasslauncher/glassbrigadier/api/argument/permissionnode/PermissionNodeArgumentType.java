package net.glasslauncher.glassbrigadier.api.argument.permissionnode;

import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandExceptionType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.glasslauncher.glassbrigadier.api.permission.PermissionNode;
import net.glasslauncher.glassbrigadier.impl.utils.StringReaderUtils;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;

public class PermissionNodeArgumentType implements ArgumentType<PermissionNode<?>> {
    public static final CommandExceptionType NODE_EXCEPTION = new SimpleCommandExceptionType(new LiteralMessage("NodeException"));

    private static final Collection<String> EXAMPLES = Arrays.asList("minecraft.operator", "*", "command.permissions");

    public static PermissionNodeArgumentType permissionNode() {
        return new PermissionNodeArgumentType();
    }

    public static PermissionNode<?> getPermissionNode(final CommandContext<?> context, final String name) {
        return context.getArgument(name, PermissionNode.class);
    }

    @Override
    public PermissionNode<?> parse(StringReader reader) throws CommandSyntaxException {
        String id = StringReaderUtils.readPermissionNode(reader);
        PermissionNode<?> node = PermissionNode.ofExisting(id);
        if (node == null) {
            node = PermissionNode.arbitraryBooleanNode(id);
        }
        return node;
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        if (builder.getRemaining().endsWith("."))
            builder.suggest(builder.getRemaining() + "*");
        else
            builder.suggest(builder.getRemaining() + ".");

        return builder.buildFuture();
    }

    @Override
    public Collection<String> getExamples() {
        return EXAMPLES;
    }

}
