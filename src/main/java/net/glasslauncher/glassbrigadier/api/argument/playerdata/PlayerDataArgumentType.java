package net.glasslauncher.glassbrigadier.api.argument.playerdata;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.server.MinecraftServer;

import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class PlayerDataArgumentType implements ArgumentType<String> {

    private static final Collection<String> EXAMPLES = List.of("Player");

    public static PlayerDataArgumentType offlinePlayers() {
        return new PlayerDataArgumentType();
    }

    @Override
    public String parse(StringReader reader) {
        return reader.readUnquotedString();
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
            builder.suggest(Minecraft.INSTANCE.player != null ? Minecraft.INSTANCE.player.name : "Player");
            return builder.buildFuture();
        }

        String typedName;

        try {
            typedName = context.getArgument("player", String.class).toLowerCase();
        } catch (IllegalArgumentException ignored) {
            return builder.buildFuture();
        }

        //noinspection deprecation
        MinecraftServer server = (MinecraftServer) FabricLoader.getInstance().getGameInstance();
        File playersFolder = new File(server.properties.getProperty("level-name", "world"), "players");
        if (!playersFolder.exists()) {
            return builder.buildFuture();
        }

        File[] files = playersFolder.listFiles(file -> {
            if (!file.isFile()) {
                return false;
            }
            String[] fileparts = file.getName().split("\\.");
            return fileparts.length == 2 && fileparts[1].equals(".dat");
        });

        if (files == null) {
            return builder.buildFuture();
        }

        for (File file : files) {
            String fileName = file.getName();
            if (!fileName.toLowerCase().startsWith(typedName)) {
                continue;
            }
            builder.suggest(fileName.substring(0, fileName.length() - 4));
        }

        return builder.buildFuture();
    }

    @Override
    public Collection<String> getExamples() {
        return EXAMPLES;
    }

}
