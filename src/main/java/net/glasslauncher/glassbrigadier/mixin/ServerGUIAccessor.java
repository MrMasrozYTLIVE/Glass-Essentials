package net.glasslauncher.glassbrigadier.mixin;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.dedicated.gui.DedicatedServerGui;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(DedicatedServerGui.class)
public interface ServerGUIAccessor {
    @Accessor("server")
    MinecraftServer getServer();
}
