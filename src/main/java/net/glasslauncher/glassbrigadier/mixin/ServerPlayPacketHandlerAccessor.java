package net.glasslauncher.glassbrigadier.mixin;

import net.glasslauncher.glassbrigadier.impl.server.mixinhooks.ServerPlayPacketHandlerHooks;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ServerPlayNetworkHandler.class)
public interface ServerPlayPacketHandlerAccessor extends ServerPlayPacketHandlerHooks {
    @Override
    @Accessor(value = "player")
    ServerPlayerEntity getPlayer();

    @Override
    @Accessor(value = "server")
    MinecraftServer getServer();
}
