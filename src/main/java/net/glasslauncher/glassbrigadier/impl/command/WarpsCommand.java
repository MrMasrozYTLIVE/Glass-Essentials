package net.glasslauncher.glassbrigadier.impl.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.glasslauncher.glassbrigadier.GlassBrigadier;
import net.glasslauncher.glassbrigadier.api.command.CommandProvider;
import net.glasslauncher.glassbrigadier.api.command.GlassCommandSource;
import net.glasslauncher.glassbrigadier.api.storage.world.WorldModStorageFile;
import net.glasslauncher.glassbrigadier.impl.argument.GlassArgumentBuilder;
import net.modificationstation.stationapi.api.util.Formatting;
import org.simpleyaml.configuration.ConfigurationSection;
import org.simpleyaml.configuration.MemorySection;

import java.util.ArrayList;

import static com.mojang.brigadier.arguments.IntegerArgumentType.integer;
import static net.glasslauncher.glassbrigadier.api.predicate.HasPermission.permission;
import static net.glasslauncher.glassbrigadier.impl.utils.AMIFormatting.BOLD;
import static net.modificationstation.stationapi.api.util.Formatting.*;

public class WarpsCommand implements CommandProvider {
    @Override
    public LiteralArgumentBuilder<GlassCommandSource> get() {
        return GlassArgumentBuilder.literal("warps")
                .requires(permission("command.warp"))
                .executes(this::listFirstWarps)
                .then(GlassArgumentBuilder.argument("page", integer(0))
                        .executes(this::listWarps)
                );
    }

    public int listFirstWarps(CommandContext<GlassCommandSource> context) {
        return showWarpPage(context, 1);
    }

    public int listWarps(CommandContext<GlassCommandSource> context) {
        int page = context.getArgument("page", Integer.class);
        return showWarpPage(context, page);
    }

    public int showWarpPage(CommandContext<GlassCommandSource> context, int page) {
        WorldModStorageFile serverStorage = WorldModStorageFile.of(GlassBrigadier.NAMESPACE.id("warps"));
        MemorySection warpStorage = (MemorySection) serverStorage.get("warps");

        if (warpStorage == null || warpStorage.isEmpty()) {
            context.getSource().sendMessage(Formatting.RED + "This server has no warps.");
            return 0;
        }

        int maxPages = (int) Math.ceil(warpStorage.size() / 9d);

        if (page > maxPages) {
            context.getSource().sendMessage(RED + "There are only " + GOLD + maxPages + RED + " pages.");
            return 0;
        }

        ArrayList<String> keys = new ArrayList<>(warpStorage.getKeys(false));

        context.getSource().sendMessage(AQUA.toString() + BOLD + ">" + GOLD + " Showing warps page " + RED + page + GOLD + " of " + RED + maxPages + GOLD + ".");
        int startIndex = (page - 1) * 9;
        for (int i = startIndex; i < keys.size() && i < startIndex + 9; ++i) {
            ConfigurationSection warp = (ConfigurationSection) warpStorage.get(keys.get(i));

            context.getSource().sendMessage(AQUA.toString() + BOLD + ">" + GOLD + " " + keys.get(i) + GRAY + ": " + warp.get("description", ""));
        }

        return warpStorage.size();
    }
}
