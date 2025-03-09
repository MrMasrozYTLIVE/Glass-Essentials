package net.glasslauncher.glassbrigadier.mixin;

import net.glasslauncher.glassbrigadier.api.permission.PermissionManager;
import net.glasslauncher.glassbrigadier.api.permission.PermissionNode;
import net.minecraft.server.PlayerManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Set;

@Mixin(PlayerManager.class)
public class ServerConfigMixin {

    @SuppressWarnings("rawtypes")
    @Redirect(method = "isOperator", at = @At(value = "INVOKE", target = "Ljava/util/Set;contains(Ljava/lang/Object;)Z"))
    boolean isOperator(Set set, Object o) {
        if (o instanceof String) {
            return PermissionNode.OPERATOR.isSatisfiedBy(PermissionManager.getNodesForName((String) o));
        }
        return false;
    }

    @Redirect(method = "isOperator", at = @At(value = "INVOKE", target = "Ljava/lang/String;toLowerCase()Ljava/lang/String;"))
    String dontLowercase(String s) {
        return s;
    }
}
