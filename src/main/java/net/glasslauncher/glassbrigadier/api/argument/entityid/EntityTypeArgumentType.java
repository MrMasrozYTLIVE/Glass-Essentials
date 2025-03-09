package net.glasslauncher.glassbrigadier.api.argument.entityid;

import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.glasslauncher.glassbrigadier.impl.server.utils.StringReaderUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityRegistry;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class EntityTypeArgumentType implements ArgumentType<EntityType> {


    private static final Collection<String> EXAMPLES = Arrays.asList("Pig", "Creeper", "Spider");

    private static final SimpleCommandExceptionType NOT_VALID_ID = new SimpleCommandExceptionType(new LiteralMessage("Invalid Entity ID"));

    private static List<String> validValues;

    private static List<String> getValidValues() {
        if (validValues != null)
            return validValues;
        validValues = new ArrayList<>();
        //noinspection unchecked
        validValues.addAll(EntityRegistry.idToClass.keySet());
        return validValues;
    }

    public static EntityTypeArgumentType entityType() {
        return new EntityTypeArgumentType();
    }

    public static EntityType getEntityType(final CommandContext<?> context, final String name) {
        return context.getArgument(name, EntityType.class);
    }

    @Override
    public EntityType parse(StringReader reader) throws CommandSyntaxException {
        int cursor = reader.getCursor();
        String id = StringReaderUtils.readId(reader);
        if (!getValidValues().contains(id)) {
            reader.setCursor(cursor);
            throw NOT_VALID_ID.createWithContext(reader);
        }
        //noinspection unchecked
        return EntityType.of(id, (Class<? extends Entity>) EntityRegistry.idToClass.get(id));
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        for (String validValue : getValidValues()) {
            if (validValue.startsWith(builder.getRemaining()))
                builder.suggest(validValue);
        }
        return builder.buildFuture();
    }

    @Override
    public Collection<String> getExamples() {
        return EXAMPLES;
    }
}
