package net.glasslauncher.glassbrigadier.impl.argument;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import lombok.Getter;
import net.glasslauncher.glassbrigadier.api.command.GlassCommandSource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
public class GlassCommandBuilder extends LiteralArgumentBuilder<GlassCommandSource> {
    private final List<String> aliases = new ArrayList<>();
    private final String literal;
    private final String description;
    private final String shortDescription;
    private boolean built = false;

    protected GlassCommandBuilder(final String literal, String shortDescription, String description) {
        super(literal);
        this.literal = literal;
        this.shortDescription = shortDescription;
        this.description = description;
    }

    public static GlassCommandBuilder create(final String name, final String shortDescription) {
        return new GlassCommandBuilder(name, shortDescription, shortDescription);
    }

    public static GlassCommandBuilder create(final String name, final String shortDescription, final String description) {
        return new GlassCommandBuilder(name, shortDescription, description);
    }

    @Override
    public LiteralCommandNode<GlassCommandSource> build() {
        String literal;
        if (!built) {
            literal = getLiteral();
        }
        else {
            literal = aliases.remove(0);
        }
        final LiteralCommandNode<GlassCommandSource> result = new DescriptiveLiteralCommandNode<>(literal, getShortDescription(), getDescription(), getCommand(), getRequirement(), getRedirect(), getRedirectModifier(), isFork());

        for (final CommandNode<GlassCommandSource> argument : getArguments()) {
            result.addChild(argument);
        }

        built = true;
        return result;
    }

    public boolean hasAliases() {
        return !aliases.isEmpty();
    }

    public GlassCommandBuilder alias(String... aliases) {
        this.aliases.addAll(Arrays.asList(aliases));
        return this;
    }
}
