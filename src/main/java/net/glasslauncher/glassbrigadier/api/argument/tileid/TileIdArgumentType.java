package net.glasslauncher.glassbrigadier.api.argument.tileid;

import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.glasslauncher.glassbrigadier.impl.utils.StringReaderUtils;
import net.modificationstation.stationapi.api.registry.BlockRegistry;
import net.modificationstation.stationapi.api.util.Identifier;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class TileIdArgumentType implements ArgumentType<TileId> {

    private static final Collection<String> EXAMPLES = Arrays.asList("minecraft:dirt", "2", "32");

    private static final SimpleCommandExceptionType NOT_VALID_ID = new SimpleCommandExceptionType(new LiteralMessage("Invalid Tile ID"));

    private static Set<String> validValues;
    private static Map<Integer, String> intId2Id;

    private static Set<String> getValidValues() {
        if (validValues != null)
            return validValues;

        Set<Identifier> ids = BlockRegistry.INSTANCE.getIds();
        validValues = ids.stream().map(Identifier::toString).collect(Collectors.toSet());

        return validValues;
    }

    private static Map<Integer, String> getIntId2Id() {
        if (intId2Id != null)
            return intId2Id;
        Set<Integer> intIds = BlockRegistry.INSTANCE.getEntrySet().stream().map(entry -> entry.getValue().id).collect(Collectors.toSet());
        intId2Id = intIds.stream().collect(Collectors.toMap(integer -> integer, integer -> BlockRegistry.INSTANCE.getId(integer).map(Identifier::toString).orElse("")));
        return intId2Id;
    }

    public static TileIdArgumentType tileId() {
        return new TileIdArgumentType();
    }

    public static TileId getTileId(final CommandContext<?> context, final String name) {
        return context.getArgument(name, TileId.class);
    }

    @Override
    public TileId parse(StringReader reader) throws CommandSyntaxException {
        int cursor = reader.getCursor();
        String id = StringReaderUtils.readId(reader);
        if (!getValidValues().contains(id)) {
            reader.setCursor(cursor);
            throw NOT_VALID_ID.createWithContext(reader);
        }
        return new TileId(id);
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        for (String validValue : getValidValues()) {
            if (validValue.startsWith(builder.getRemaining()) || validValue.substring(validValue.indexOf(':')+1, validValue.length()-1).startsWith(builder.getRemaining()))
                builder.suggest(validValue);
        }
        getIntId2Id().forEach((integer, id) -> {
            if (integer.toString().startsWith(builder.getRemaining()))
                builder.suggest(id); //Only ever suggest string ids
        });
        return builder.buildFuture();
    }

    @Override
    public Collection<String> getExamples() {
        return EXAMPLES;
    }
}
