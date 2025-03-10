package net.glasslauncher.glassbrigadier.impl.mixinhooks;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.MinecraftServer;

public interface ServerPlayPacketHandlerHooks {
    ServerPlayerEntity getPlayer();
    MinecraftServer getServer();
}
