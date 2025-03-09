package net.glasslauncher.glassbrigadier.api.predicate;

import net.glasslauncher.glassbrigadier.impl.GlassCommandSource;

import java.util.function.Predicate;

public class IsWorldly implements Predicate<GlassCommandSource> {

    private IsWorldly() {}

    /**
     * Create a predicate that requires the command source to have a level.
     * @return the predicate.
     */
    public static IsWorldly isWorldly() {
        return new IsWorldly();
    }

    @Override
    public boolean test(GlassCommandSource commandSource) {
        return commandSource.getWorld() != null;
    }
}
