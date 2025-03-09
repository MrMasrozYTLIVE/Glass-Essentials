package net.glasslauncher.glassbrigadier.impl.server.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.glasslauncher.glassbrigadier.api.argument.itemid.ItemId;
import net.glasslauncher.glassbrigadier.api.argument.playerselector.TargetSelector;
import net.glasslauncher.glassbrigadier.api.command.CommandProvider;
import net.glasslauncher.glassbrigadier.impl.GlassCommandSource;
import net.minecraft.entity.player.PlayerEntity;
import net.modificationstation.stationapi.api.util.Formatting;

import java.util.Optional;

import static com.mojang.brigadier.arguments.IntegerArgumentType.getInteger;
import static com.mojang.brigadier.arguments.IntegerArgumentType.integer;
import static net.glasslauncher.glassbrigadier.api.argument.itemid.ItemIdArgumentType.getItemId;
import static net.glasslauncher.glassbrigadier.api.argument.itemid.ItemIdArgumentType.itemId;
import static net.glasslauncher.glassbrigadier.api.argument.playerselector.TargetSelectorArgumentType.entities;
import static net.glasslauncher.glassbrigadier.api.predicate.HasPermission.permission;

public class GiveCommand implements CommandProvider {

    @Override
    public LiteralArgumentBuilder<GlassCommandSource> get() {
        return LiteralArgumentBuilder.<GlassCommandSource>literal("give")
                .requires(permission("command.give"))
                .then(RequiredArgumentBuilder.<GlassCommandSource, TargetSelector<?>>argument("player", entities())
                        .then(RequiredArgumentBuilder.<GlassCommandSource, ItemId>argument("item", itemId())
                                .executes(this::giveItem)
                                .then(RequiredArgumentBuilder.<GlassCommandSource, Integer>argument("count", integer(0, 64))
                                        .executes(this::giveItemWithCount)
                                        .then(RequiredArgumentBuilder.<GlassCommandSource, Integer>argument("meta", integer(0, 15))
                                                .executes(this::giveItemWithCountAndMeta)
                                        )
                                )
                        )
                );
    }

    public int giveItem(CommandContext<GlassCommandSource> context) {
        TargetSelector<PlayerEntity> player1 = context.getArgument("player", null);
        Optional<PlayerEntity> playerEntity = player1.getEntities(context.getSource()).stream().findFirst();

        if (playerEntity.isEmpty()) {
            context.getSource().sendMessage(Formatting.RED + "No player named \"" + context.getInput() + "\" found.");
            return 0;
        }

        int item = getItemId(context, "item").numericId;
        context.getSource().sendMessage("Giving " + playerEntity.get() + " some " + item);
        playerEntity.get().dropItem(item, 1, 0);

        return 0;
    }

    public int giveItemWithCount(CommandContext<GlassCommandSource> context) {
        String playerName = context.getArgument("player", String.class);
        PlayerEntity player = context.getSource().getPlayerByName(playerName);

        if (player == null) {
            context.getSource().sendMessage(Formatting.RED + "No player named \"" + playerName + "\" found.");
            return 0;
        }

        int item = getItemId(context, "item").numericId;
        int count = getInteger(context, "count");
        sendFeedbackAndLog(context.getSource(), "Giving " + player + " " + count + " of " + item);
        player.dropItem(item, count, 0);

        return 0;
    }

    public int giveItemWithCountAndMeta(CommandContext<GlassCommandSource> context) {
        String playerName = context.getArgument("player", String.class);
        PlayerEntity player = context.getSource().getPlayerByName(playerName);

        if (player == null) {
            context.getSource().sendMessage(Formatting.RED + "No player named \"" + playerName + "\" found.");
            return 0;
        }

        int item = getItemId(context, "item").numericId;
        int count = getInteger(context, "count");
        int meta = getInteger(context, "meta");
        sendFeedbackAndLog(context.getSource(), "Giving " + player + " " + count + " of " + item + ":" + meta);
        player.dropItem(item, count, meta);
        return 0;
    }
}
