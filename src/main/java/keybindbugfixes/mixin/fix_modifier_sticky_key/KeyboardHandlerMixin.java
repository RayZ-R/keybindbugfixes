package keybindbugfixes.mixin.fix_modifier_sticky_key;

import com.llamalad7.mixinextras.sugar.Local;
import keybindbugfixes.ToggleKeyStates;
import keybindbugfixes.config.Config;
import net.minecraft.client.KeyboardHandler;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(KeyboardHandler.class)
public abstract class KeyboardHandlerMixin {
    @Inject(
            method = "keyPress",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/Options;" +
                            "narrator()Lnet/minecraft/client/OptionInstance;",
                    ordinal = 0
            )
    )
    private void revertNarratorModifierKey(CallbackInfo callbackInfo, @Local(name = "screen") Screen screen) {
        if (Config.FIX_MODIFIER_STICKY_KEY.value && screen == null) {
            ToggleKeyStates.revertControl();
        }
    }
}