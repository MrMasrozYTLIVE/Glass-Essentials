package net.glasslauncher.glassbrigadier.impl.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.glasslauncher.glassbrigadier.api.command.CommandProvider;
import net.glasslauncher.glassbrigadier.api.command.GlassCommandSource;
import net.glasslauncher.glassbrigadier.api.storage.player.PlayerStorageFile;
import net.glasslauncher.glassbrigadier.impl.argument.GlassArgumentBuilder;
import net.modificationstation.stationapi.api.util.Formatting;
import org.simpleyaml.configuration.ConfigurationSection;

import java.util.ArrayList;

import static com.mojang.brigadier.arguments.IntegerArgumentType.integer;
import static net.glasslauncher.glassbrigadier.GlassBrigadier.*;
import static net.glasslauncher.glassbrigadier.api.predicate.HasPermission.booleanPermission;
import static net.modificationstation.stationapi.api.util.Formatting.GOLD;
import static net.modificationstation.stationapi.api.util.Formatting.RED;

public class HomesCommand implements CommandProvider {
    @Override
    public LiteralArgumentBuilder<GlassCommandSource> get() {
        return GlassArgumentBuilder.literal("homes")
                .requires(booleanPermission("command.homes"))
                .executes(this::listFirstHomes)
                .then(GlassArgumentBuilder.argument("page", integer(0))
                        .executes(this::listHomes)
                );
    }

    public int listFirstHomes(CommandContext<GlassCommandSource> context) {
        return showHomesPage(context, 1);
    }

    public int listHomes(CommandContext<GlassCommandSource> context) {
        int page = context.getArgument("page", Integer.class);
        return showHomesPage(context, page);
    }

    public int showHomesPage(CommandContext<GlassCommandSource> context, int page) {
        PlayerStorageFile playerStorage = PlayerStorageFile.of(context.getSource().getPlayer());
        ConfigurationSection homeStorage = playerStorage.getNotNullSection("homes");

        if (homeStorage.isEmpty()) {
            context.getSource().sendFeedback(Formatting.RED + "You have no homes.");
            return 0;
        }

        int maxPages = (int) Math.ceil(homeStorage.size() / 9d);

        if (page > maxPages) {
            context.getSource().sendFeedback(RED + "There are only " + GOLD + maxPages + RED + " pages.");
            return 0;
        }

        ArrayList<String> keys = new ArrayList<>(homeStorage.getKeys(false));

        context.getSource().sendFeedback(systemMessage("Showing homes page " + RED + page + systemMessageColor() + " of " + RED + maxPages + systemMessageColor() + "."));
        int startIndex = (page - 1) * 9;
        for (int i = startIndex; i < keys.size() && i < startIndex + 9; ++i) {
            context.getSource().sendFeedback(systemBulletPoint(" " + keys.get(i)));
        }

        return homeStorage.size();
    }
}
