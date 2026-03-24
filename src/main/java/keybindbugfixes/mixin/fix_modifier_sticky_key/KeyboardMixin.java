package keybindbugfixes.mixin.fix_modifier_sticky_key;

import com.llamalad7.mixinextras.sugar.Local;
import keybindbugfixes.StickyKeyStates;
import keybindbugfixes.config.Config;
import net.minecraft.client.Keyboard;
import net.minecraft.client.gui.screen.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Keyboard.class)
public abstract class KeyboardMixin {
    @Inject(
            method = "onKey",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/option/GameOptions;" +
                            "getNarrator()Lnet/minecraft/client/option/SimpleOption;",
                    ordinal = 0
            )
    )
    private void revertNarratorModifierKey(CallbackInfo callbackInfo, @Local Screen screen) {
        if (Config.FIX_MODIFIER_STICKY_KEY.value && screen == null) {
            StickyKeyStates.revertControlModifier();
        }
    }
}