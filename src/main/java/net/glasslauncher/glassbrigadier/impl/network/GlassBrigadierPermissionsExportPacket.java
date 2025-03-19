package net.glasslauncher.glassbrigadier.impl.network;

import lombok.SneakyThrows;
import net.glasslauncher.glassbrigadier.GlassBrigadier;
import net.minecraft.client.Minecraft;
import net.minecraft.network.NetworkHandler;
import net.minecraft.network.packet.Packet;
import net.modificationstation.stationapi.api.network.packet.ManagedPacket;
import net.modificationstation.stationapi.api.network.packet.PacketType;
import net.modificationstation.stationapi.api.util.Formatting;
import org.jetbrains.annotations.NotNull;

import java.io.*;

public class GlassBrigadierPermissionsExportPacket extends Packet implements ManagedPacket<GlassBrigadierPermissionsExportPacket> {
    public static final PacketType<GlassBrigadierPermissionsExportPacket> TYPE = PacketType.builder(true, false, GlassBrigadierPermissionsExportPacket::new).build();

    private String permissions;

    public GlassBrigadierPermissionsExportPacket() {
    }

    public GlassBrigadierPermissionsExportPacket(String permissions) {
        this.permissions = permissions;
    }

    @SneakyThrows
    @Override
    public void read(DataInputStream stream) {
        permissions = stream.readUTF();
    }

    @SneakyThrows
    @Override
    public void write(DataOutputStream stream) {
        stream.writeUTF(permissions);
    }

    @Override
    public void apply(NetworkHandler networkHandler) {
        File file = GlassBrigadier.getConfigFile("local_exported_permissions.txt");

        if (file.exists()) {
            //noinspection ResultOfMethodCallIgnored
            file.delete();
        }

        if (!file.getParentFile().exists()) {
            //noinspection ResultOfMethodCallIgnored
            file.mkdir();
        }

        try (FileWriter fileWriter = new FileWriter(file)) {
            fileWriter.append(permissions);
        } catch (IOException e) {
            Minecraft.INSTANCE.inGameHud.addChatMessage(Formatting.RED + "Failed to write to " + file.getAbsolutePath());
            GlassBrigadier.LOGGER.error("Failed to write to {}", file.getAbsolutePath(), e);
            return;
        }
        Minecraft.INSTANCE.inGameHud.addChatMessage(Formatting.GREEN + "Successfully written received permissions to " + file.getPath());
    }

    @Override
    public int size() {
        return permissions.length() * 8;
    }

    @Override
    public @NotNull PacketType<GlassBrigadierPermissionsExportPacket> getType() {
        return TYPE;
    }
}
