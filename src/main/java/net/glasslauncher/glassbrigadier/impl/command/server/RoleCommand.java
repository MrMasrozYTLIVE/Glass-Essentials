package net.glasslauncher.glassbrigadier.impl.command.server;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.glasslauncher.glassbrigadier.api.command.CommandProvider;
import net.glasslauncher.glassbrigadier.api.command.GlassCommandSource;
import net.glasslauncher.glassbrigadier.impl.argument.GlassArgumentBuilder;
import net.glasslauncher.glassbrigadier.impl.argument.GlassCommandBuilder;
import net.glasslauncher.glassbrigadier.impl.permission.Role;
import net.glasslauncher.glassbrigadier.impl.permission.RoleChain;
import net.glasslauncher.glassbrigadier.impl.permission.RoleManagerImpl;
import net.glasslauncher.glassbrigadier.impl.permission.UserPermissionManagerImpl;

import static com.mojang.brigadier.arguments.StringArgumentType.string;
import static net.glasslauncher.glassbrigadier.GlassBrigadier.systemBulletPointPrefix;
import static net.glasslauncher.glassbrigadier.GlassBrigadier.systemMessagePrefix;
import static net.glasslauncher.glassbrigadier.api.argument.role.RoleArgumentType.role;
import static net.glasslauncher.glassbrigadier.api.predicate.HasPermission.permission;

public class RoleCommand implements CommandProvider {

    @Override
    public LiteralArgumentBuilder<GlassCommandSource> get() {
        return GlassCommandBuilder.literal("roles")
                .alias("r")
                .requires(permission("glassbrigadier.role"))
                .then(GlassArgumentBuilder.literal("list")
                        .executes(this::listRoles)
                )
                .then(GlassArgumentBuilder.literal("create")
                        .then(GlassArgumentBuilder.argument("role", string())
                                .executes(this::createRole)
                        )
                )
                .then(GlassArgumentBuilder.literal("prefix")
                        .then(GlassArgumentBuilder.argument("role", role())
                                .executes(this::setEmptyPrefix)
                                .then(GlassArgumentBuilder.argument("prefix", string())
                                        .executes(this::setPrefix)
                                )
                        )
                )
                .then(GlassArgumentBuilder.literal("suffix")
                        .then(GlassArgumentBuilder.argument("role", role())
                                .executes(this::setEmptySuffix)
                                .then(GlassArgumentBuilder.argument("suffix", string())
                                        .executes(this::setSuffix)
                                )
                        )
                )
                .then(GlassArgumentBuilder.literal("chain")
                        .then(GlassArgumentBuilder.literal("list")
                                .executes(this::listChains)
                        )
                )
                ;
    }

    private int listChains(CommandContext<GlassCommandSource> context) {
        final StringBuilder builder = new StringBuilder();
        builder.append("Available chains:");
        for (RoleChain chain : RoleChain.getStartingWith("")) {
            builder.append("\n ");
            builder.append(chain.getName());
            builder.append(" (");
            builder.append(chain.getRoles().size());
            builder.append(")");
        }
        context.getSource().sendFeedback(builder.toString());
        return 0;
    }

    private int listRoles(CommandContext<GlassCommandSource> context) {
        final StringBuilder builder = new StringBuilder();
        builder.append(systemMessagePrefix());
        builder.append(" ");
        builder.append("Available roles:");
        for (Role role : Role.getStartingWith("")) {
            builder.append("\n");
            builder.append(systemBulletPointPrefix());
            builder.append(" ");
            builder.append(role.getName());
            builder.append(" (");
            int players = UserPermissionManagerImpl.getUsers(role).size();
            builder.append(players);
            builder.append(" ");
            builder.append(players == 1 ? "player" : "players");
            builder.append(")");
        }
        context.getSource().sendFeedback(builder.toString());
        return 0;
    }

    private int setEmptyPrefix(CommandContext<GlassCommandSource> context) {
        return setPrefix(context, null);
    }

    private int setEmptySuffix(CommandContext<GlassCommandSource> context) {
        return setSuffix(context, null);
    }

    private int setSuffix(CommandContext<GlassCommandSource> context) {
        return setSuffix(context, context.getArgument("suffix", String.class));
    }

    private int setPrefix(CommandContext<GlassCommandSource> context) {
        return setPrefix(context, context.getArgument("prefix", String.class));
    }

    private int setSuffix(CommandContext<GlassCommandSource> context, String suffix) {
        Role role = context.getArgument("role", Role.class);
        role.setSuffix(suffix);
        RoleManagerImpl.updateAndSaveRolesFile();
        return 0;
    }

    private int setPrefix(CommandContext<GlassCommandSource> context, String prefix) {
        Role role = context.getArgument("role", Role.class);
        role.setPrefix(prefix);
        RoleManagerImpl.updateAndSaveRolesFile();
        return 0;
    }

    private int createRole(CommandContext<GlassCommandSource> context) {
        String role = context.getArgument("role", String.class);
        RoleManagerImpl.addRole(new Role(role));
        RoleManagerImpl.updateAndSaveRolesFile();
        return 0;
    }
}
