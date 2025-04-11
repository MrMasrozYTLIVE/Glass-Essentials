package net.glasslauncher.glassbrigadier.mixin.server;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.glasslauncher.glassbrigadier.GlassBrigadier;
import net.glasslauncher.glassbrigadier.api.command.GlassCommandSource;
import net.glasslauncher.glassbrigadier.api.event.CommandRegisterEvent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.Command;
import net.minecraft.server.command.ServerCommandHandler;
import net.modificationstation.stationapi.api.StationAPI;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerCommandHandler.class)
public class ServerCommandHandlerMixin {

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
