package net.glasslauncher.glassbrigadier.impl.command.vanilla.server;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.loader.api.FabricLoader;
import net.glasslauncher.glassbrigadier.GlassBrigadier;
import net.glasslauncher.glassbrigadier.api.command.CommandProvider;
import net.glasslauncher.glassbrigadier.api.command.GlassCommandSource;
import net.glasslauncher.glassbrigadier.impl.argument.GlassCommandBuilder;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.modificationstation.stationapi.api.util.Formatting;
import org.intellij.lang.annotations.RegExp;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.regex.Pattern;

import static com.mojang.brigadier.arguments.StringArgumentType.getString;
import static com.mojang.brigadier.arguments.StringArgumentType.word;
import static net.glasslauncher.glassbrigadier.api.predicate.HasPermission.permission;

public class BanIpCommand implements CommandProvider {
    public static final Pattern IP_REGEX = Pattern.compile("(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)");

    @Override
    public LiteralArgumentBuilder<GlassCommandSource> get() {
        return GlassCommandBuilder.create("ban-ip", "Ban an IP.")
                .requires(permission("command.banip"))
                .then(RequiredArgumentBuilder.<GlassCommandSource, String>argument("ip", word())
                        .executes(this::banIp)
                );
    }

    public int banIp(CommandContext<GlassCommandSource> context) {
        String ip = getString(context, "ip").toLowerCase().strip();

        if (!IP_REGEX.matcher(ip).matches()) {
            context.getSource().sendMessage(Formatting.RED + ip + " isn't a valid IP address!");
            return 0;
        }
        //noinspection deprecation
        PlayerManager playerManager = ((MinecraftServer) FabricLoader.getInstance().getGameInstance()).playerManager;

        if (playerManager.bannedIps.contains(ip)) {
            context.getSource().sendMessage(Formatting.RED + ip + " is already banned!");
            return 0;
        }

        //noinspection unchecked
        for (ServerPlayerEntity player : new ArrayList<ServerPlayerEntity>(playerManager.players)) {
            if (((InetSocketAddress) player.networkHandler.connection.getAddress()).getHostString().equals(ip)) {
                player.networkHandler.disconnect(Formatting.RED + "Banned by admin.");
            }
        }

        playerManager.banIp(ip);
        sendFeedbackAndLog(context.getSource(), "IP banned " + ip + ".");
        return 0;
    }
}
