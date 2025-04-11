package net.glasslauncher.glassbrigadier.mixin.commandsource;

import net.glasslauncher.glassbrigadier.api.command.GlassCommandSource;
import net.glasslauncher.glassbrigadier.api.permission.PermissionManager;
import net.glasslauncher.glassbrigadier.api.permission.PermissionNode;
import net.glasslauncher.glassbrigadier.api.permission.PermissionNodeInstance;
import net.glasslauncher.glassbrigadier.api.storage.player.PlayerStorageFile;
import net.glasslauncher.glassbrigadier.impl.permission.UserPermissionManagerImpl;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.ServerWorld;
import net.minecraft.world.World;
import net.modificationstation.stationapi.api.registry.DimensionRegistry;
import net.modificationstation.stationapi.api.util.Identifier;
import net.modificationstation.stationapi.api.util.Namespace;
import org.lwjgl.util.vector.Vector2f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@SuppressWarnings("AddedMixinMembersNamePattern")
@Mixin(MinecraftServer.class)
public abstract class ExtendedMinecraftServer implements GlassCommandSource {

    @Shadow public PlayerManager playerManager;

    @Shadow public abstract ServerWorld getWorld(int dimensionId);

    @Shadow public abstract String getName();

    @Override
    public World getWorld() {
        //noinspection DataFlowIssue
        return getWorld(DimensionRegistry.INSTANCE.get(Identifier.of(Namespace.MINECRAFT, "overworld")).getLegacyID());
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
    public Set<PermissionNodeInstance<?>> getPermissions() {
        return Set.of();
    }

    @Override
    public boolean satisfiesNode(PermissionNodeInstance<?> nodeToCheck) {
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
    public List<PlayerEntity> getAllPlayers() {
        //noinspection unchecked
        return new ArrayList<PlayerEntity>(playerManager.players);
    }

    @Override
    public @Nullable PlayerStorageFile getStorage() {
        return null;
    }
}
