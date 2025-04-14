package net.glasslauncher.glassbrigadier.impl.command.vanilla.server;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.loader.api.FabricLoader;
import net.glasslauncher.glassbrigadier.api.argument.playerselector.TargetSelectorArgumentType;
import net.glasslauncher.glassbrigadier.api.command.CommandProvider;
import net.glasslauncher.glassbrigadier.api.command.GlassCommandSource;
import net.glasslauncher.glassbrigadier.impl.argument.GlassCommandBuilder;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.ServerWorld;
import net.modificationstation.stationapi.api.util.Formatting;

import static net.glasslauncher.glassbrigadier.api.predicate.HasPermission.booleanPermission;
import static net.glasslauncher.glassbrigadier.api.predicate.HasPermission.permission;

public class SaveOnCommand implements CommandProvider {
    @Override
    public LiteralArgumentBuilder<GlassCommandSource> get() {
        return GlassCommandBuilder.literal("deop", "Remove operator status from a player.")
                .requires(booleanPermission("command.deop"))
                .then(RequiredArgumentBuilder.argument("player", TargetSelectorArgumentType.player()))
                .executes(this::saveOn);
    }

    public int saveOn(CommandContext<GlassCommandSource> context) {
        //noinspection deprecation
        MinecraftServer minecraftServer = (MinecraftServer) FabricLoader.getInstance().getGameInstance();
        for (ServerWorld world : minecraftServer.worlds) {
            world.savingDisabled = false;
        }

        sendFeedbackAndLog(context.getSource(), Formatting.YELLOW + "Level saving enabled.");
        return 0;
    }
}
