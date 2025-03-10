package net.glasslauncher.glassbrigadier.mixin.brigadierjank;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.CommandNode;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = CommandDispatcher.class, remap = false)
public abstract class FixRecursiveCheck<S> {
    @Shadow protected abstract String getSmartUsage(CommandNode<S> node, S source, boolean optional, boolean deep);

    @Redirect(method = "getSmartUsage(Lcom/mojang/brigadier/tree/CommandNode;Ljava/lang/Object;ZZ)Ljava/lang/String;", at = @At(value = "INVOKE", target = "Lcom/mojang/brigadier/CommandDispatcher;getSmartUsage(Lcom/mojang/brigadier/tree/CommandNode;Ljava/lang/Object;ZZ)Ljava/lang/String;", ordinal = 0))
    String test(CommandDispatcher<S> instance, CommandNode<S> node, S source, boolean child, boolean deep) {
        return getSmartUsage(node, source, child, false);
    }
}
