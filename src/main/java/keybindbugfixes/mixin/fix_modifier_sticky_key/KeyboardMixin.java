package keybindbugfixes.mixin.fix_modifier_sticky_key;

import com.llamalad7.mixinextras.sugar.Local;
import keybindbugfixes.KeybindBugFixes;
import net.minecraft.client.Keyboard;
import net.minecraft.client.gui.screen.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Keyboard.class)
public abstract class KeyboardMixin {
    @Inject(method = "onKey",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/option/GameOptions;write()V",
                    shift = At.Shift.AFTER))
    private void revertNarratorModifier(CallbackInfo callbackInfo, @Local Screen screen) {
        if (screen == null) {
            KeybindBugFixes.revertNarratorModifier();
        }
    }
}