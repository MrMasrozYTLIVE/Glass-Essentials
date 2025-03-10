package net.glasslauncher.glassbrigadier.mixin.server;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.glasslauncher.glassbrigadier.GlassBrigadier;
import net.glasslauncher.glassbrigadier.api.command.GlassCommandSource;
import net.glasslauncher.glassbrigadier.api.event.CommandRegisterEvent;
import net.mine_diver.unsafeevents.EventDispatcher;
import net.mine_diver.unsafeevents.event.EventPhases;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.Command;
import net.minecraft.server.command.ServerCommandHandler;
import net.modificationstation.stationapi.api.StationAPI;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerCommandHandler.class)
public class CommandManagerMixin {

    @Inject(method = "<init>", at = @At("RETURN"))
    void initCommands(MinecraftServer par1, CallbackInfo ci) {
        GlassBrigadier.LOGGER.info("Initializing commands...");
        StationAPI.EVENT_BUS.post(CommandRegisterEvent.builder().commandDispatcher(GlassBrigadier.dispatcher).build());
        GlassBrigadier.LOGGER.info("Registered {} commands.", GlassBrigadier.dispatcher.getRoot().getChildren().size());
    }

    @Inject(method = "executeCommand", at = @At(value = "HEAD"), cancellable = true)
    void hijackCommands(Command command, CallbackInfo ci) {
        try {
            GlassBrigadier.dispatcher.execute(command.commandAndArgs, (GlassCommandSource) command.output);
        } catch (CommandSyntaxException e) {
            command.output.sendMessage(e.getMessage());
        }
        ci.cancel();
    }

}
