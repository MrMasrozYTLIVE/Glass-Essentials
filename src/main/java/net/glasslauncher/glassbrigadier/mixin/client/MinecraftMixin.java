package net.glasslauncher.glassbrigadier.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.glasslauncher.glassbrigadier.GlassBrigadier;
import net.glasslauncher.glassbrigadier.api.event.CommandRegisterEvent;
import net.minecraft.client.Minecraft;
import net.modificationstation.stationapi.api.StationAPI;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MinecraftMixin {

    @WrapOperation(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;isWorldRemote()Z", ordinal = 0))
    boolean allowChatScreenInSP(Minecraft instance, Operation<Boolean> original) {
        return GlassBrigadier.CONFIG.singlePlayerChat;
    }

    @Inject(method = "init", at = @At("RETURN"))
    void registerCommands(CallbackInfo ci) {
        GlassBrigadier.LOGGER.info("Initializing commands...");
        StationAPI.EVENT_BUS.post(CommandRegisterEvent.builder().commandDispatcher(GlassBrigadier.dispatcher).build());
        GlassBrigadier.LOGGER.info("Registered {} commands.", GlassBrigadier.dispatcher.getRoot().getChildren().size());
    }
}
