package net.glasslauncher.glassbrigadier.api.event;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.CommandNode;
import lombok.experimental.SuperBuilder;
import net.glasslauncher.glassbrigadier.GlassBrigadier;
import net.glasslauncher.glassbrigadier.api.command.CommandProvider;
import net.glasslauncher.glassbrigadier.api.command.GlassCommandSource;
import net.glasslauncher.glassbrigadier.impl.argument.GlassCommandBuilder;
import net.mine_diver.unsafeevents.Event;
import net.mine_diver.unsafeevents.event.EventPhases;

/**
 * Use this like any other StationAPI event, and use {@link CommandRegisterEvent#register(CommandProvider)} to register commands.
 * <br />
 * Most of the time you don't want to touch the phase of your event listener unless you're doing some cursed internal fuckery.
 */
@SuperBuilder
@EventPhases(value = {
        CommandRegisterEvent.INTERNAL_PHASE,
        CommandRegisterEvent.VANILLA_PHASE,
        EventPhases.DEFAULT_PHASE
})
public class CommandRegisterEvent extends Event {
    /**
     * Used to set up the permission system.
     */
    public static final String INTERNAL_PHASE = "glassbrigadier:internal";
    /**
     * When vanilla commands are re-added by glass brigadier.
     */
    public static final String VANILLA_PHASE = "minecraft";

    /**
     * In most cases you don't want to touch this. See {@link #register(CommandProvider)} instead.
     * <br />
     * The command dispatcher can be used to check what commands exist.
     */
    public final CommandDispatcher<GlassCommandSource> commandDispatcher;

    /**
     * Build and register a supplied command and it's aliases.
     * @param commandSupplier The {@link CommandProvider} to build and register.
     * @return the built {@link CommandNode}.
     */
    public CommandNode<GlassCommandSource> register(CommandProvider commandSupplier) {
        LiteralArgumentBuilder<GlassCommandSource> commandBuilder = commandSupplier.get();
        CommandNode<GlassCommandSource> commandNode = commandDispatcher.register(commandBuilder);
        // This is ultra cursed, but easy aliases are a must.
        if (commandBuilder instanceof GlassCommandBuilder builder) {
            while (builder.hasAliases()) {
                commandDispatcher.register(commandBuilder);
            }
        }
        return commandNode;
    }
}
