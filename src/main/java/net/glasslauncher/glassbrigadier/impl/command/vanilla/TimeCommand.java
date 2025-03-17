package net.glasslauncher.glassbrigadier.impl.command.vanilla;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.glasslauncher.glassbrigadier.api.command.CommandProvider;
import net.glasslauncher.glassbrigadier.api.command.GlassCommandSource;
import net.minecraft.world.World;

import java.util.function.Function;

import static com.mojang.brigadier.arguments.LongArgumentType.getLong;
import static com.mojang.brigadier.arguments.LongArgumentType.longArg;
import static net.glasslauncher.glassbrigadier.api.predicate.HasPermission.permission;

public class TimeCommand implements CommandProvider {

    @Override
    public LiteralArgumentBuilder<GlassCommandSource> get() {
        return LiteralArgumentBuilder.<GlassCommandSource>literal("time")
                .requires(permission("command.time"))
                .then(
                        LiteralArgumentBuilder.<GlassCommandSource>literal("set")
                                .then(RequiredArgumentBuilder.<GlassCommandSource, Long>argument("time", longArg(0))
                                        .executes(setTime(context -> getLong(context, "time")))
                                ).then(LiteralArgumentBuilder.<GlassCommandSource>literal("day")
                                        .executes(setTime(a -> 0L))
                                ).then(LiteralArgumentBuilder.<GlassCommandSource>literal("noon")
                                        .executes(setTime(a -> 6000L))
                                ).then(LiteralArgumentBuilder.<GlassCommandSource>literal("night")
                                        .executes(setTime(a -> 12000L))
                                ).then(LiteralArgumentBuilder.<GlassCommandSource>literal("midnight")
                                        .executes(setTime(a -> 18000L))
                                )
                )
                .then(
                        LiteralArgumentBuilder.<GlassCommandSource>literal("get")
                                .executes(context -> {
                                    sendFeedbackAndLog(context.getSource(), Long.toString((context.getSource().getWorld().getTime())));
                                    return 0;
                                })
                )
                .then(
                        LiteralArgumentBuilder.<GlassCommandSource>literal("add")
                                .then(RequiredArgumentBuilder.<GlassCommandSource, Long>argument("time", longArg())
                                        .executes(context -> {
                                            World level = (context.getSource()).getWorld();
                                            level.setTime(level.getTime()+getLong(context, "time"));
                                            sendFeedbackAndLog(context.getSource(), "Set time to " + level.getTime());
                                            return 0;
                                        }))
                );
    }

    public Command<GlassCommandSource> setTime(Function<CommandContext<GlassCommandSource>, Long> time) {
        return context -> {
            long timeValue = time.apply(context);
            context.getSource().getWorld().setTime(timeValue);
            sendFeedbackAndLog(context.getSource(), "Set time to " + timeValue);
            return 0;
        };
    }
}
