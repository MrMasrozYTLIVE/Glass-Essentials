package net.glasslauncher.glassbrigadier.impl.server.argument;

import net.glasslauncher.glassbrigadier.api.argument.playerselector.TargetSelector;
import net.glasslauncher.glassbrigadier.impl.GlassCommandSource;
import net.minecraft.entity.Entity;

import java.util.Collections;
import java.util.List;

public class SelfSelector extends TargetSelector<Entity> {
    public SelfSelector() {
        super(Entity.class, null, 1, SortingMethod.RANDOM);
    }

    @Override
    public List<Entity> getEntities(GlassCommandSource sender) {
        return Collections.singletonList(sender.getEntity());
    }
}
