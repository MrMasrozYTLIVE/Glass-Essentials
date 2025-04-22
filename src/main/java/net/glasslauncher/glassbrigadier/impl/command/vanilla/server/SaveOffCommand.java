package net.glasslauncher.glassbrigadier.impl.command.vanilla.server;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.loader.api.FabricLoader;
import net.glasslauncher.glassbrigadier.api.command.CommandProvider;
import net.glasslauncher.glassbrigadier.api.command.GlassCommandSource;
import net.glasslauncher.glassbrigadier.impl.argument.GlassCommandBuilder;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.ServerWorld;
import net.modificationstation.stationapi.api.util.Formatting;

import static net.glasslauncher.glassbrigadier.api.predicate.HasPermission.booleanPermission;

public class SaveOffCommand implements CommandProvider {
    @Override
    public LiteralArgumentBuilder<GlassCommandSource> get() {
        return GlassCommandBuilder.literal("save-off", "Turn off server saving until turned back on, or next reboot.")
                .requires(booleanPermission("command.saveoff"))
                .executes(this::saveOff);
    }

    public int saveOff(CommandContext<GlassCommandSource> context) {
        //noinspection deprecation
        MinecraftServer minecraftServer = (MinecraftServer) FabricLoader.getInstance().getGameInstance();
        for (ServerWorld world : minecraftServer.worlds) {
            world.savingDisabled = true;
        }

        sendFeedbackAndLog(context.getSource(), Formatting.YELLOW + "Level saving disabled.");
        return 0;
    }
}
