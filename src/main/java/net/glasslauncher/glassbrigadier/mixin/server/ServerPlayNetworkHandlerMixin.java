package net.glasslauncher.glassbrigadier.mixin.server;

import com.llamalad7.mixinextras.injector.ModifyReceiver;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.glasslauncher.glassbrigadier.GlassBrigadier;
import net.glasslauncher.glassbrigadier.api.command.GlassCommandSource;
import net.glasslauncher.glassbrigadier.impl.permission.Role;
import net.glasslauncher.glassbrigadier.impl.permission.UserPermissionManagerImpl;
import net.minecraft.network.packet.play.ChatMessagePacket;
import net.minecraft.server.command.Command;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Comparator;
import java.util.Optional;

@Mixin(ServerPlayNetworkHandler.class)
public abstract class ServerPlayNetworkHandlerMixin implements GlassCommandSource {
    @Unique
    private String originalMessage;

    @Shadow public abstract String getName();

    @Inject(method = "handleCommand", at = @At(value = "HEAD"), cancellable = true)
    void hijackCommands(String message, CallbackInfo ci) {
        try {
            GlassBrigadier.dispatcher.execute(message.substring(1), this);
        } catch (CommandSyntaxException e) {
            this.sendMessage(e.getMessage());
        }
        ci.cancel();
    }

    @Inject(method = "onChatMessage", at = @At(value = "HEAD"))
    private void captureMessage(ChatMessagePacket packet, CallbackInfo ci) {
        originalMessage = packet.chatMessage;
    }

    @ModifyVariable(method = "onChatMessage", at = @At(value = "STORE", ordinal = 2), ordinal = 0)
    private String modifyChatSender(String originalOutput) {
        Optional<Role> role = UserPermissionManagerImpl.getRoles(getName()).stream().max(Comparator.comparingInt(Role::getPower));
        return role.map(value -> value.getDisplay(getName()) + " " + originalMessage)
                .orElse(originalOutput);
    }
}
