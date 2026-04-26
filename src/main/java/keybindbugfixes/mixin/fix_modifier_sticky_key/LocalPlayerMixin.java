package keybindbugfixes.mixin.fix_modifier_sticky_key;

import keybindbugfixes.ToggleKeyStates;
import keybindbugfixes.config.Config;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LocalPlayer.class)
public abstract class LocalPlayerMixin {
    @Inject(method = "drop", at = @At("HEAD"))
    private void revertDropStackModifierKey(boolean all, CallbackInfoReturnable<Boolean> callbackInfo) {
        if (Config.FIX_MODIFIER_STICKY_KEY.value && all) {
            ToggleKeyStates.revertControl();
        }
    }
}