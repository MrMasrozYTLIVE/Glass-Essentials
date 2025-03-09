package net.glasslauncher.glassbrigadier.impl;

import com.mojang.brigadier.CommandDispatcher;
import net.glasslauncher.glassbrigadier.api.command.CommandRegistry;
import net.glasslauncher.glassbrigadier.impl.client.network.GlassBrigadierAutocompletePacket;
import net.glasslauncher.glassbrigadier.impl.server.command.*;
import net.glasslauncher.mods.gcapi3.api.ConfigRoot;
import net.mine_diver.unsafeevents.listener.EventListener;
import net.modificationstation.stationapi.api.event.mod.InitEvent;
import net.modificationstation.stationapi.api.event.network.packet.PacketRegisterEvent;
import net.modificationstation.stationapi.api.registry.PacketTypeRegistry;
import net.modificationstation.stationapi.api.registry.Registry;
import net.modificationstation.stationapi.api.util.Namespace;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GlassBrigadier {
    @ConfigRoot(value = "config", visibleName = "Config", nameKey = "config.glassbrigadier.config.name")
    public static final GlassBrigadierConfig CONFIG = new GlassBrigadierConfig();

    public static final List<String> previousMessages = new ArrayList<>();
    @SuppressWarnings("UnstableApiUsage")
    public static final Namespace NAMESPACE = Namespace.resolve();
    public static final Logger LOGGER = NAMESPACE.getLogger("Glass Brigadier");

    public static final CommandDispatcher<GlassCommandSource> dispatcher = new CommandDispatcher<>();

    @EventListener(phase = InitEvent.PRE_INIT_PHASE)
    public void init(InitEvent event) {
        CommandRegistry.register(
                new SetTileCommand(), "Set a tile"
        );
        CommandRegistry.register(
                new SummonCommand(), "Spawn an entity"
        );
        CommandRegistry.register(
                new HelpCommand(), "Show help"
        );
        CommandRegistry.register(
                new MeCommand(), "Show a message in chat with the format '* [name] [message]'"
        );
        CommandRegistry.register(
                new MsgCommand(), "Whisper something to a player"
        );
        CommandRegistry.register(
                new GiveCommand(), "Give an item to a player"
        );
        CommandRegistry.register(
                new TimeCommand(), "Set or get the time"
        );
        CommandRegistry.register(
                new PermissionsCommand(), "Query or add permissions to players."
        );
        CommandRegistry.register(
                new TeleportCommand(), "Teleport entities"
        );

        try {
            Field helpMap = CommandRegistry.class.getDeclaredField("helpMap");
            helpMap.setAccessible(true);
            LOGGER.info(((HashMap<?, ?>) helpMap.get(null)).size());
        } catch (IllegalAccessException | NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    @EventListener
    public void onInitialize(PacketRegisterEvent event) {
        Registry.register(PacketTypeRegistry.INSTANCE, GlassBrigadier.NAMESPACE.id("autocomplete"), GlassBrigadierAutocompletePacket.TYPE);
    }
}
