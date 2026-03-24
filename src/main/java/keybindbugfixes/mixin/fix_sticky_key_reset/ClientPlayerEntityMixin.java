package keybindbugfixes.mixin.fix_sticky_key_reset;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import keybindbugfixes.config.Config;
import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayerEntityMixin {
    @WrapWithCondition(
            method = "requestRespawn",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/option/KeyBinding;untoggleStickyKeys()V"
            )
    )
    private boolean preventUntoggleStickyKeysOnRespawn() {
        return !Config.FIX_STICKY_KEY_RESET.value;
    }
}