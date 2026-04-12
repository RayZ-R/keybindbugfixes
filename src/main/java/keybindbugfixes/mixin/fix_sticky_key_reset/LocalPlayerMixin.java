package keybindbugfixes.mixin.fix_sticky_key_reset;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import keybindbugfixes.config.Config;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LocalPlayer.class)
public abstract class LocalPlayerMixin {
    @WrapWithCondition(
            method = "respawn",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/KeyMapping;resetToggleKeys()V"
            )
    )
    private boolean preventToggleKeyResetOnRespawn() {
        return !Config.FIX_STICKY_KEY_RESET.value;
    }
}