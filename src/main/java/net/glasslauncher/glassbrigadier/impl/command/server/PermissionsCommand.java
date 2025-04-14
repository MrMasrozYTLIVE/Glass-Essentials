package net.glasslauncher.glassbrigadier.impl.command.server;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.glasslauncher.glassbrigadier.GlassBrigadier;
import net.glasslauncher.glassbrigadier.api.command.CommandProvider;
import net.glasslauncher.glassbrigadier.api.command.GlassCommandSource;
import net.glasslauncher.glassbrigadier.api.permission.PermissionManager;
import net.glasslauncher.glassbrigadier.impl.argument.GlassArgumentBuilder;
import net.glasslauncher.glassbrigadier.impl.argument.GlassCommandBuilder;
import net.glasslauncher.glassbrigadier.impl.network.GlassBrigadierPermissionsExportPacket;
import net.glasslauncher.glassbrigadier.impl.permission.Role;
import net.glasslauncher.glassbrigadier.impl.permission.UserPermissionManagerImpl;
import net.modificationstation.stationapi.api.network.packet.PacketHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Set;

import static net.glasslauncher.glassbrigadier.GlassBrigadier.*;
import static net.glasslauncher.glassbrigadier.api.argument.role.RoleArgumentType.getRole;
import static net.glasslauncher.glassbrigadier.api.argument.role.RoleArgumentType.role;
import static net.glasslauncher.glassbrigadier.api.argument.playerselector.TargetSelectorArgumentType.*;
import static net.glasslauncher.glassbrigadier.api.predicate.HasPermission.booleanPermission;
import static net.glasslauncher.glassbrigadier.api.predicate.HasPermission.permission;
import static net.glasslauncher.glassbrigadier.api.predicate.IsPlayer.isPlayer;

public class PermissionsCommand implements CommandProvider {

    @Override
    public LiteralArgumentBuilder<GlassCommandSource> get() {
        return GlassCommandBuilder.literal("permissions", "Set permissions on a role.")
                .alias("p")
                .requires(booleanPermission("command.permissions"))
// TODO: Implement once I add Permissible
//                .then(GlassArgumentBuilder.literal("get")
//                        .then(GlassArgumentBuilder.<TargetSelector<?>>argument("player", players())
//                                .executes(context -> {
//                                    final StringBuilder builder = new StringBuilder();
//                                    for (String playerName : getEntities(context, "player").getNames(context.getSource())) {
//                                        final Map<PermissionNode<?>, ?> nodes = UserPermissionManagerImpl.getNodes(playerName);
//                                        builder.append(playerName);
//                                        builder.append(" has permissions:\n");
//                                        for (Map.Entry<PermissionNode<?>, ?> nodeEntry : nodes.entrySet()) {
//                                            builder.append(" ");
//                                            builder.append(nodeEntry.toString());
//                                            builder.append(": ");
//                                            builder.append(nodeEntry.getValue());
//                                            builder.append("\n");
//                                        }
//                                        builder.append("\n");
//                                    }
//                                    builder.deleteCharAt(builder.length() - 1); // Remove last newline
//                                    context.getSource().sendMessage(builder.toString());
//                                    return 0;
//                                })
//                        )
//                )
//                .then(GlassArgumentBuilder.literal("add")
//                        .then(GlassArgumentBuilder.<TargetSelector<?>>argument("player", players())
//                                .then(GlassArgumentBuilder.<PermissionNode<?>>argument("node", permissionNode())
//                                        .executes(context -> {
//                                            final StringBuilder builder = new StringBuilder();
//                                            final PermissionNode<?> node = getPermissionNode(context, "node");
//                                            for (String playerName : getPlayers(context, "player").getNames(context.getSource())) {
//                                                final boolean success = PermissionManager.addRole(playerName, node);
//                                                builder.append(success ? "Added" : "Failed to add");
//                                                builder.append(" node ");
//                                                builder.append(node);
//                                                builder.append(" to ");
//                                                builder.append(playerName);
//                                                builder.append("\n");
//                                            }
//                                            builder.deleteCharAt(builder.length()-1); // Remove last newline
//                                            sendFeedbackAndLog(context.getSource(), builder.toString());
//                                            return 0;
//                                        })
//                                )
//                        )
//                )
//                .then(GlassArgumentBuilder.literal("remove")
//                        .then(GlassArgumentBuilder.<TargetSelector<?>>argument("player", players())
//                                .then(GlassArgumentBuilder.<PermissionNode<?>>argument("node", permissionNode())
//                                        .executes(context -> {
//                                            final StringBuilder builder = new StringBuilder();
//                                            final PermissionNode<?> node = getPermissionNode(context, "node");
//                                            for (String playerName : getPlayers(context, "player").getNames(context.getSource())) {
//                                                final boolean success = PermissionManager.removeNode(playerName, node);
//                                                builder.append(success ? "Removed" : "Failed to remove");
//                                                builder.append(" node ");
//                                                builder.append(node);
//                                                builder.append(" from ");
//                                                builder.append(playerName);
//                                                builder.append("\n");
//                                            }
//                                            builder.deleteCharAt(builder.length()-1); // Remove last newline
//                                            sendFeedbackAndLog(context.getSource(), builder.toString());
//                                            return 0;
//                                        })
//                                )
//                        )
//                )
                .then(GlassArgumentBuilder.literal("getRoles")
                        .then(GlassArgumentBuilder.argument("player", players())
                                .executes(context -> {
                                    final StringBuilder builder = new StringBuilder();
                                    for (String playerName : getEntities(context, "player").getNames(context.getSource())) {
                                        final Set<Role> roles = UserPermissionManagerImpl.getRoles(playerName);
                                        builder.append(systemMessagePrefix());
                                        builder.append(" ");
                                        builder.append(playerName);
                                        builder.append(" has roles:");
                                        for (Role role : roles) {
                                            builder.append("\n");
                                            builder.append(systemBulletPointPrefix());
                                            builder.append(" ");
                                            builder.append(role.getName());
                                        }
                                    }
                                    context.getSource().sendFeedback(builder.toString());
                                    return 0;
                                })
                        )
                )
                .then(GlassArgumentBuilder.literal("addRole")
                        .then(GlassArgumentBuilder.argument("player", players())
                                .then(GlassArgumentBuilder.argument("role", role())
                                        .executes(context -> {
                                            final StringBuilder builder = new StringBuilder();
                                            final Role role = getRole(context, "role");
                                            for (String playerName : getPlayers(context, "player").getNames(context.getSource())) {
                                                final boolean success = PermissionManager.addRole(playerName, role);
                                                builder.append(systemMessagePrefix());
                                                builder.append(" ");
                                                builder.append(success ? "Added" : "Failed to add");
                                                builder.append(" role ");
                                                builder.append(role.getName());
                                                builder.append(" to ");
                                                builder.append(playerName);
                                                builder.append("\n");
                                            }
                                            builder.deleteCharAt(builder.length()-1); // Remove last newline
                                            sendFeedbackAndLog(context.getSource(), builder.toString());
                                            return 0;
                                        })
                                )
                        )
                )
                .then(GlassArgumentBuilder.literal("removeRole")
                        .then(GlassArgumentBuilder.argument("player", players())
                                .then(GlassArgumentBuilder.argument("role", role())
                                        .executes(context -> {
                                            final StringBuilder builder = new StringBuilder();
                                            final Role role = getRole(context, "role");
                                            for (String playerName : getPlayers(context, "player").getNames(context.getSource())) {
                                                final boolean success = PermissionManager.removeRole(playerName, role);
                                                builder.append(systemMessagePrefix());
                                                builder.append(" ");
                                                builder.append(success ? "Removed" : "Failed to remove");
                                                builder.append(" role ");
                                                builder.append(role);
                                                builder.append(" from ");
                                                builder.append(playerName);
                                                builder.append("\n");
                                            }
                                            builder.deleteCharAt(builder.length()-1); // Remove last newline
                                            sendFeedbackAndLog(context.getSource(), builder.toString());
                                            return 0;
                                        })
                                )
                        )
                )
                .then(GlassArgumentBuilder.literal("export")
                        .executes(context -> {
                            sendFeedbackAndLog(context.getSource(), "Printing permissions to file...");
                            File permissionsFile = GlassBrigadier.getConfigFile("permissionsOutput.txt");
                            if (permissionsFile.exists()) {
                                //noinspection ResultOfMethodCallIgnored
                                permissionsFile.delete();
                            }
                            try {
                                //noinspection ResultOfMethodCallIgnored
                                permissionsFile.createNewFile();
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }

                            try (FileOutputStream outputStream = new FileOutputStream(permissionsFile)) {
                                for (String node : GlassBrigadier.ALL_PERMISSIONS) {
                                    outputStream.write((node + "\n").getBytes(StandardCharsets.UTF_8));
                                }
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                            return 0;
                        })
                )
                .then(GlassArgumentBuilder.literal("exportlocal")
                        .requires(isPlayer())
                        .executes(context -> {
                            sendFeedbackAndLog(context.getSource(), "Sending permissions to " + context.getSource().getSourceName() + "...");

                                StringBuilder permissions = new StringBuilder();
                                for (String node : GlassBrigadier.ALL_PERMISSIONS) {
                                    permissions.append(node).append("\n");
                                }
                            PacketHelper.sendTo(context.getSource().getPlayer(), new GlassBrigadierPermissionsExportPacket(permissions.toString()));
                            return 0;
                        })
                );
    }
}
