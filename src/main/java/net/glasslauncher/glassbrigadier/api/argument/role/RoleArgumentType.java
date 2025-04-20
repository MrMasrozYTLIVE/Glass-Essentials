package net.glasslauncher.glassbrigadier.api.argument.role;

import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandExceptionType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.glasslauncher.glassbrigadier.impl.permission.Role;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class RoleArgumentType implements ArgumentType<Role> {
    public static final CommandExceptionType ROLE_EXCEPTION = new SimpleCommandExceptionType(new LiteralMessage("RoleException"));

    private static final Collection<String> EXAMPLES = List.of("default");

    public static RoleArgumentType role() {
        return new RoleArgumentType();
    }

    public static Role getRole(final CommandContext<?> context, final String name) {
        return context.getArgument(name, Role.class);
    }

    @Override
    public Role parse(StringReader reader) throws CommandSyntaxException {
        String role_ = reader.readString();
        Role role = Role.get(role_);
        if (role == null) {
            throw new CommandSyntaxException(ROLE_EXCEPTION, new LiteralMessage("Role \"" + role_ + "\" not found!"));
        }
        return role;
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        Set<Role> roles = Role.getStartingWith(builder.getRemaining());
        roles.forEach(role -> builder.suggest(role.getName()));

        return builder.buildFuture();
    }

    @Override
    public Collection<String> getExamples() {
        return EXAMPLES;
    }

}
