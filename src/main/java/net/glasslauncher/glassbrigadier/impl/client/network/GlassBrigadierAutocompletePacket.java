package net.glasslauncher.glassbrigadier.impl.client.network;

import com.mojang.brigadier.suggestion.Suggestion;
import lombok.SneakyThrows;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.glasslauncher.glassbrigadier.impl.client.mixinhooks.ChatScreenHooks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.NetworkHandler;
import net.minecraft.network.packet.Packet;
import net.modificationstation.stationapi.api.mod.entrypoint.Entrypoint;
import net.modificationstation.stationapi.api.mod.entrypoint.EventBusPolicy;
import net.modificationstation.stationapi.api.network.packet.ManagedPacket;
import net.modificationstation.stationapi.api.network.packet.PacketHelper;
import net.modificationstation.stationapi.api.network.packet.PacketType;
import org.jetbrains.annotations.NotNull;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Entrypoint(eventBus = @EventBusPolicy(registerInstance = false))
public class GlassBrigadierAutocompletePacket extends Packet implements ManagedPacket<GlassBrigadierAutocompletePacket> {
    public static final PacketType<GlassBrigadierAutocompletePacket> TYPE = PacketType.builder(true, true, GlassBrigadierAutocompletePacket::new).build();

    private String incompleteCommand;

    public GlassBrigadierAutocompletePacket(String incompleteCommand) {
        this.incompleteCommand = incompleteCommand;
    }

    public GlassBrigadierAutocompletePacket() {
    }

    public static void createAndSendToServer(String incompleteCommand) {
        PacketHelper.send(new GlassBrigadierAutocompletePacket(incompleteCommand));
    }

    public static void createAndSendToPlayer(String incompleteCommand, PlayerEntity player) {
        PacketHelper.sendTo(player, new GlassBrigadierAutocompletePacket(incompleteCommand));
    }

    private List<String> bytesToStrings(byte[] bytes) {
        List<String> strings = new ArrayList<>();
        for (int i = 0; i < bytes.length; i++) {
            if (bytes[i] == 0b00000000)
                break;

            final int stringStart = i;
            while (i < bytes.length && bytes[i] != 0b00000000) {
                i++;
            }
            final int stringEnd = i;
            strings.add(new String(Arrays.copyOfRange(bytes, stringStart, stringEnd), StandardCharsets.UTF_8));
        }
        return strings;
    }

    private byte[] stringsToBytes(String[] strings) {
        List<Byte> bytesList = new ArrayList<>();
        for (String string : strings) {
            int length = string.length();
            boolean tooLong = length > 100;
            if (!tooLong) {
                for (byte bt : string.getBytes(StandardCharsets.UTF_8)) {
                    bytesList.add(bt);
                }
                bytesList.add((byte) 0b00000000);
            }
        }
        byte[] bytes = new byte[bytesList.size()];
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = bytesList.get(i);
        }
        return bytes;
    }

    private String[] applySuggestions(String input, List<Suggestion> suggestions) {
        String[] strings = new String[suggestions.size()];
        for (int i = 0; i < suggestions.size(); i++) {
            strings[i] = suggestions.get(i).apply(input);
        }
        return strings;
    }

    @Override
    public @NotNull PacketType<GlassBrigadierAutocompletePacket> getType() {
        return TYPE;
    }

    @Override
    @SneakyThrows
    public void read(DataInputStream stream) {
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
            List<String> completions = bytesToStrings(stream.readUTF().getBytes(StandardCharsets.UTF_8));
            Screen screen = Minecraft.INSTANCE.currentScreen;
            if (screen instanceof ChatScreenHooks chatScreen) {
                if (!completions.isEmpty()) {
                    chatScreen.setMessage("/" + completions.get(0));
                    chatScreen.glass_Essentials$setCompletions(completions);
                }
            }
        }
        else {
            incompleteCommand = stream.readUTF();
        }
    }

    @Override
    @SneakyThrows
    public void write(DataOutputStream stream) {
        stream.writeUTF(incompleteCommand);
    }

    @Override
    public void apply(NetworkHandler networkHandler) {
//        if (networkHandler instanceof ServerPlayNetworkHandler serverPlayNetworkHandler) {
//            ParseResults<GlassCommandSource> parseResults = LegacyBrigadier.dispatcher.parse(incompleteCommand, serverPlayNetworkHandler);
//            Suggestions suggestions;
//            try {
//                suggestions = LegacyBrigadier.dispatcher.getCompletionSuggestions(parseResults).get();
//            } catch (InterruptedException | ExecutionException e) {
//                LegacyBrigadier.LOGGER.error(e);
//                return;
//            }
//            if (!suggestions.getList().isEmpty())
//                PacketHelper.sendTo(((ServerPlayPacketHandlerHooks)networkHandler).getPlayer(), new GlassBrigadierAutocompletePacket(new String(stringsToBytes(applySuggestions(incompleteCommand, suggestions.getList())), StandardCharsets.UTF_8)));
//        }
    }

    @Override
    public int size() {
        return 0;
    }
}
