package net.glasslauncher.glassbrigadier.mixin.server;

import com.llamalad7.mixinextras.injector.ModifyReceiver;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.glasslauncher.glassbrigadier.GlassBrigadier;
import net.glasslauncher.glassbrigadier.api.command.GlassCommandSource;
import net.minecraft.server.command.Command;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayNetworkHandler.class)
public class ServerPlayNetworkHandlerMixin {

    @Inject(method = "handleCommand", at = @At(value = "HEAD"), cancellable = true)
    void hijackCommands(String message, CallbackInfo ci) {
        try {
            GlassBrigadier.dispatcher.execute(message.substring(1), (GlassCommandSource) this);
        } catch (CommandSyntaxException e) {
            ((GlassCommandSource) this).sendMessage(e.getMessage());
        }
        ci.cancel();
    }
}
