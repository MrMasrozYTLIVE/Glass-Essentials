package net.glasslauncher.glassbrigadier.api.argument.permissionnode;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.glasslauncher.glassbrigadier.api.permission.PermissionNode;
import net.glasslauncher.glassbrigadier.impl.utils.StringReaderUtils;
import net.modificationstation.stationapi.api.registry.BlockRegistry;
import net.modificationstation.stationapi.api.registry.Registry;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;

public class PermissionNodeArgumentType implements ArgumentType<PermissionNode<?>> {

    private static final Collection<String> EXAMPLES = Arrays.asList("minecraft.operator", "*", "command.permissions");

    public static PermissionNodeArgumentType permissionNode() {
        return new PermissionNodeArgumentType();
    }

    public static PermissionNode<?> getPermissionNode(final CommandContext<?> context, final String name) {
        return context.getArgument(name, PermissionNode.class);
    }

    @Override
    public PermissionNode<?> parse(StringReader reader) {
        String id = StringReaderUtils.readPermissionNode(reader);
        return PermissionNode.ofExisting(id);
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
