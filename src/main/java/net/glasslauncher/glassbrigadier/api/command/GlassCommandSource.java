package net.glasslauncher.glassbrigadier.api.command;

import net.glasslauncher.glassbrigadier.api.permission.PermissionNode;
import net.glasslauncher.glassbrigadier.api.permission.PermissionNodeInstance;
import net.glasslauncher.glassbrigadier.api.storage.player.PlayerStorageFile;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.lwjgl.util.vector.Vector2f;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Set;

public interface GlassCommandSource {

    // Vanilla method, update with mappings.
    void sendMessage(String message);

    // Vanilla method, update with mappings.
    String getName();

    World getWorld();

    Vec3d getPosition();

    Vector2f getRotation();

    Set<PermissionNodeInstance<?>> getPermissions();

    default boolean satisfiesNode(PermissionNodeInstance<?> nodeToCheck) {
        return nodeToCheck.isSatisfiedBy(getPermissions());
    }

    Entity getEntity();

    @Nullable
    PlayerEntity getPlayer();

    @Nullable
    PlayerEntity getPlayerByName(String playerName);

    default void sendMessageToPlayer(String playerName, String message) {
        sendMessageToPlayer(getPlayerByName(playerName), message);
    }

    default void sendMessageToPlayer(@Nullable PlayerEntity player, String message) {
        if (player == null) {
            return;
        }

        player.sendMessage(message);
    }

    List<PlayerEntity> getAllPlayers();

    @Nullable
    PlayerStorageFile getStorage();
}
