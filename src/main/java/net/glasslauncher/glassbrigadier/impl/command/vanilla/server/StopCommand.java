package net.glasslauncher.glassbrigadier.impl.command.vanilla.server;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.loader.api.FabricLoader;
import net.glasslauncher.glassbrigadier.api.command.CommandProvider;
import net.glasslauncher.glassbrigadier.api.command.GlassCommandSource;
import net.glasslauncher.glassbrigadier.impl.argument.GlassCommandBuilder;
import net.minecraft.server.MinecraftServer;
import net.modificationstation.stationapi.api.util.Formatting;

import static net.glasslauncher.glassbrigadier.api.predicate.HasPermission.booleanPermission;
import static net.glasslauncher.glassbrigadier.api.predicate.HasPermission.permission;

public class StopCommand implements CommandProvider {
    @Override
    public LiteralArgumentBuilder<GlassCommandSource> get() {
        return GlassCommandBuilder.literal("stop", "Stop the server.")
                .requires(booleanPermission("command.stop"))
                .executes(this::stop);
    }

    public int stop(CommandContext<GlassCommandSource> context) {
        sendToChatAndLog(context.getSource(), Formatting.RED + "Stopping server...");
        //noinspection deprecation
        ((MinecraftServer) FabricLoader.getInstance().getGameInstance()).stop();
        return 0;
    }
}
