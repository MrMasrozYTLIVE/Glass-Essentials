package net.glasslauncher.glassbrigadier.impl.command.vanilla.server;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.loader.api.FabricLoader;
import net.glasslauncher.glassbrigadier.api.command.CommandProvider;
import net.glasslauncher.glassbrigadier.api.command.GlassCommandSource;
import net.glasslauncher.glassbrigadier.impl.argument.GlassCommandBuilder;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.modificationstation.stationapi.api.util.Formatting;

import static net.glasslauncher.glassbrigadier.api.predicate.HasPermission.permission;

public class SaveAllCommand implements CommandProvider {
    @Override
    public LiteralArgumentBuilder<GlassCommandSource> get() {
        return GlassCommandBuilder.literal("save-all", "Forces a save of all the loaded levels in the world.")
                .requires(permission("command.saveall"))
                .executes(this::saveAll);
    }

    public int saveAll(CommandContext<GlassCommandSource> context) {
        sendFeedbackAndLog(context.getSource(), Formatting.YELLOW + "Forcing save..");
        //noinspection deprecation
        MinecraftServer minecraftServer = (MinecraftServer) FabricLoader.getInstance().getGameInstance();
        if (minecraftServer.playerManager != null) {
            minecraftServer.playerManager.savePlayers();
        }

        for (World world : minecraftServer.worlds) {
            world.saveWithLoadingDisplay(true, null);
        }

        sendFeedbackAndLog(context.getSource(), Formatting.YELLOW + "Save complete.");
        return 0;
    }
}
