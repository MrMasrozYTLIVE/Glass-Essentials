package net.glasslauncher.glassbrigadier.impl.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import net.glasslauncher.glassbrigadier.api.argument.playerselector.TargetSelector;
import net.glasslauncher.glassbrigadier.api.command.CommandProvider;
import net.glasslauncher.glassbrigadier.api.permission.PermissionManager;
import net.glasslauncher.glassbrigadier.api.permission.PermissionNode;
import net.glasslauncher.glassbrigadier.api.command.GlassCommandSource;

import java.util.Set;

import static net.glasslauncher.glassbrigadier.api.argument.permissionnode.PermissionNodeArgumentType.getPermissionNode;
import static net.glasslauncher.glassbrigadier.api.argument.permissionnode.PermissionNodeArgumentType.permissionNode;
import static net.glasslauncher.glassbrigadier.api.argument.playerselector.TargetSelectorArgumentType.*;
import static net.glasslauncher.glassbrigadier.api.predicate.HasPermission.permission;

public class PermissionsCommand implements CommandProvider {

    @Override
    public LiteralArgumentBuilder<GlassCommandSource> get() {
        return LiteralArgumentBuilder.<GlassCommandSource>literal("permissions")
                .requires(permission("command.permissions"))
                .then(LiteralArgumentBuilder.<GlassCommandSource>literal("get")
                        .then(RequiredArgumentBuilder.<GlassCommandSource, TargetSelector<?>>argument("player", players())
                                .executes(context -> {
                                    final StringBuilder builder = new StringBuilder();
                                    for (String playerName : getEntities(context, "player").getNames(context.getSource())) {
                                        final Set<PermissionNode> nodes = PermissionManager.getNodesForName(playerName);
                                        builder.append(playerName);
                                        builder.append(" has permissions:");
                                        for (PermissionNode node : nodes) {
                                            builder.append(" ");
                                            builder.append(node.toString());
                                        }
                                        builder.append("\n");
                                    }
                                    builder.deleteCharAt(builder.length()-1); // Remove last newline
                                    context.getSource().sendMessage(builder.toString());
                                    return 0;
                                })
                        )
                )
                .then(LiteralArgumentBuilder.<GlassCommandSource>literal("add")
                        .then(RequiredArgumentBuilder.<GlassCommandSource, TargetSelector<?>>argument("player", players())
                                .then(RequiredArgumentBuilder.<GlassCommandSource, PermissionNode>argument("node", permissionNode())
                                        .executes(context -> {
                                            final StringBuilder builder = new StringBuilder();
                                            final PermissionNode node = getPermissionNode(context, "node");
                                            for (String playerName : getPlayers(context, "player").getNames(context.getSource())) {
                                                final boolean success = PermissionManager.addNodeToName(playerName, node);
                                                builder.append(success ? "Added" : "Failed to add");
                                                builder.append(" node ");
                                                builder.append(node);
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
                .then(LiteralArgumentBuilder.<GlassCommandSource>literal("remove")
                        .then(RequiredArgumentBuilder.<GlassCommandSource, TargetSelector<?>>argument("player", players())
                                .then(RequiredArgumentBuilder.<GlassCommandSource, PermissionNode>argument("node", permissionNode())
                                        .executes(context -> {
                                            final StringBuilder builder = new StringBuilder();
                                            final PermissionNode node = getPermissionNode(context, "node");
                                            for (String playerName : getPlayers(context, "player").getNames(context.getSource())) {
                                                final boolean success = PermissionManager.removeNodeFromName(playerName, node);
                                                builder.append(success ? "Removed" : "Failed to remove");
                                                builder.append(" node ");
                                                builder.append(node);
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
                );
    }
}
