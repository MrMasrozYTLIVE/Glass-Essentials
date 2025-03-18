package net.glasslauncher.glassbrigadier.impl.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.glasslauncher.glassbrigadier.GlassBrigadier;
import net.glasslauncher.glassbrigadier.api.command.CommandProvider;
import net.glasslauncher.glassbrigadier.api.command.GlassCommandSource;
import net.glasslauncher.glassbrigadier.api.storage.world.WorldModStorageFile;
import net.minecraft.util.math.Vec3d;
import org.simpleyaml.configuration.MemorySection;

import java.io.IOException;
import java.util.ArrayList;

import static com.mojang.brigadier.arguments.StringArgumentType.word;
import static net.glasslauncher.glassbrigadier.api.predicate.HasPermission.permission;
import static net.glasslauncher.glassbrigadier.api.predicate.IsPlayer.isPlayer;

public class SetWarpCommand implements CommandProvider {
    @Override
    public LiteralArgumentBuilder<GlassCommandSource> get() {
        return LiteralArgumentBuilder.<GlassCommandSource>literal("setwarp")
                .requires(source -> isPlayer().test(source) && permission("command.setwarp").test(source))
                .then(RequiredArgumentBuilder.<GlassCommandSource, String>argument("name", word())
                        .executes(this::setWarp)
                );
    }

    public int setWarp(CommandContext<GlassCommandSource> context) {
        String name = context.getArgument("name", String.class);
        WorldModStorageFile serverStorage = WorldModStorageFile.of(GlassBrigadier.NAMESPACE.id("warps"));
        MemorySection warps = (MemorySection) serverStorage.get("warps", serverStorage.createSection("warps"));

        Vec3d position = context.getSource().getPosition();
        warps.set(name, new ArrayList<Double>() {{add(position.x); add(position.y); add(position.z);}});
        serverStorage.set("warps", warps);
        try {
            serverStorage.save();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        context.getSource().sendMessage("Set warp \"" + name + "\".");
        return 0;
    }
}
