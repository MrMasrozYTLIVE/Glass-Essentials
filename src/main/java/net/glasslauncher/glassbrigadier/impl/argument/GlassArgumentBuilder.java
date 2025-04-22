package net.glasslauncher.glassbrigadier.impl.argument;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import net.glasslauncher.glassbrigadier.api.command.GlassCommandSource;

public class GlassArgumentBuilder {

    /**
     * Convenience method
     */
    public static LiteralArgumentBuilder<GlassCommandSource> literal(String name) {
        return LiteralArgumentBuilder.literal(name);
    }

    /**
     * Convenience method
     */
    public static <T> RequiredArgumentBuilder<GlassCommandSource, T> argument(String name, ArgumentType<T> type) {
        return RequiredArgumentBuilder.argument(name, type);
    }
}
