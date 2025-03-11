package net.glasslauncher.glassbrigadier.impl.command.vanilla;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.loader.api.FabricLoader;
import net.glasslauncher.glassbrigadier.api.argument.playerselector.TargetSelectorArgumentType;
import net.glasslauncher.glassbrigadier.api.command.CommandProvider;
import net.glasslauncher.glassbrigadier.api.command.GlassCommandSource;
import net.glasslauncher.glassbrigadier.impl.argument.GlassCommandBuilder;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.modificationstation.stationapi.api.util.Formatting;
import org.checkerframework.checker.regex.qual.Regex;
import org.intellij.lang.annotations.RegExp;

import javax.annotation.RegEx;

import static com.mojang.brigadier.arguments.StringArgumentType.getString;
import static com.mojang.brigadier.arguments.StringArgumentType.word;
import static net.glasslauncher.glassbrigadier.api.argument.playerselector.TargetSelectorArgumentType.getPlayers;
import static net.glasslauncher.glassbrigadier.api.predicate.HasPermission.permission;

public class BanIpCommand implements CommandProvider {
    @RegExp
    public static final String IP_REGEX = "(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)";
    @Override
    public LiteralArgumentBuilder<GlassCommandSource> get() {
        return GlassCommandBuilder.create("ban-ip", "Ban an IP.")
                .requires(permission("command.banip"))
                .then(RequiredArgumentBuilder.argument("ip", word()))
                .executes(this::banIp);
    }

    public int banIp(CommandContext<GlassCommandSource> context) {
        String ip = getString(context, "ip").toLowerCase().strip();
        if (!ip.matches(IP_REGEX)) {
            context.getSource().sendMessage(Formatting.RED + ip + " isn't a valid IP address!");
            return 0;
        }
        //noinspection deprecation
        PlayerManager playerManager = ((MinecraftServer) FabricLoader.getInstance().getGameInstance()).playerManager;
        if (playerManager.bannedIps.contains(ip)) {
            context.getSource().sendMessage(Formatting.RED + ip + " is already banned!");
            return 0;
        }

        playerManager.banIp(ip);
        sendFeedbackAndLog(context.getSource(), "IP banned " + ip + ".");
        return 0;
    }
}
