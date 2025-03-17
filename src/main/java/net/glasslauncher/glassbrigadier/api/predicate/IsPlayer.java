package net.glasslauncher.glassbrigadier.api.predicate;


import net.glasslauncher.glassbrigadier.api.command.GlassCommandSource;

import java.util.function.Predicate;

public class IsPlayer implements Predicate<GlassCommandSource> {

    private IsPlayer() {}

    /**
     * Create a predicate that requires the command source to have a player.
     * @return the predicate.
     */
    public static IsPlayer isPlayer() {
        return new IsPlayer();
    }

    @Override
    public boolean test(GlassCommandSource commandSource) {
        return commandSource.getPlayer() != null;
    }
}
