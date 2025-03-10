package net.glasslauncher.glassbrigadier.mixin.commandsource;

import net.glasslauncher.glassbrigadier.api.permission.PermissionManager;
import net.glasslauncher.glassbrigadier.api.permission.PermissionNode;
import net.glasslauncher.glassbrigadier.api.command.GlassCommandSource;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.lwjgl.util.vector.Vector2f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@SuppressWarnings("AddedMixinMembersNamePattern")
@Mixin(MinecraftServer.class)
public abstract class ExtendedMinecraftServer implements GlassCommandSource {

    @Shadow public PlayerManager playerManager;

    @Override
    public World getWorld() {
        if (getPlayer() != null) {
            return getPlayer().world;
        }
        return null;
    }

    @Override
    public Vec3d getPosition() {
        PlayerEntity player = getPlayer();
        if (player != null) {
            return Vec3d.createCached(player.x, player.y, player.z);
        }

        return Vec3d.createCached(0, 0, 0);
    }

    @Override
    public Vector2f getRotation() {
        PlayerEntity player = getPlayer();
        if (player != null) {
            return new Vector2f(player.pitch, player.yaw);
        }
        return new Vector2f(0f, 0f);
    }

    @Override
    public Set<PermissionNode> getPermissions() {
        return PermissionManager.getNodesForCommandSource((GlassCommandSource) this);
    }

    @Override
    public Set<PermissionNode> getAllPermissions() {
        Set<PermissionNode> set = new HashSet<>();
        set.add(PermissionNode.ROOT);
        return set;
    }

    @Override
    public boolean satisfiesNode(PermissionNode nodeToCheck) {
        return true;
    }

    @Override
    public Entity getEntity() {
        return null;
    }

    @Nullable
    @Override
    public PlayerEntity getPlayer() {
        return null;
    }

    @Override
    public @Nullable PlayerEntity getPlayerByName(String playerName) {
        return playerManager.getPlayer(playerName);
    }

    @Override
    public boolean sendMessageToPlayer(String playerName, String message) {
        PlayerEntity player = getPlayerByName(playerName);
        if (player == null) {
            return false;
        }

        player.sendMessage(message);
        return true;
    }

    @Override
    public boolean sendMessageToPlayer(@Nullable PlayerEntity player, String message) {
        if (player == null) {
            return false;
        }

        player.sendMessage(message);
        return true;

    }

    @Override
    public List<PlayerEntity> getAllPlayers() {
        //noinspection unchecked
        return new ArrayList<PlayerEntity>(playerManager.players);
    }
}
