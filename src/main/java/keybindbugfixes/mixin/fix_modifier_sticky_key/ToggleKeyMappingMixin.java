package keybindbugfixes.mixin.fix_modifier_sticky_key;

import keybindbugfixes.KeybindBugFixes;
import keybindbugfixes.ToggleKeyStates;
import keybindbugfixes.config.Config;
import keybindbugfixes.mixin.ToggleKeyMappingAccessor;
import net.minecraft.client.ToggleKeyMapping;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ToggleKeyMapping.class)
public abstract class ToggleKeyMappingMixin {
    @Inject(method = "setDown", at = @At("HEAD"))
    private void updateToggleKeyStates(boolean down, CallbackInfo callbackInfo) {
        if (Config.FIX_MODIFIER_STICKY_KEY.value) {
            ToggleKeyMapping keyMapping = (ToggleKeyMapping) (Object) this;

            if (((ToggleKeyMappingAccessor) keyMapping).getNeedsToggle().getAsBoolean()) {
                if (keyMapping.same(KeybindBugFixes.minecraft.options.keySprint)) {
                    if (down) {
                        ToggleKeyStates.sprintToggleState = keyMapping.isDown();
                        ToggleKeyStates.sprintingState = KeybindBugFixes.minecraft.player.isSprinting();
                    } else {
                        ToggleKeyStates.sprintToggleState = null;
                        ToggleKeyStates.sprintingState = null;
                    }
                } else if (keyMapping.same(KeybindBugFixes.minecraft.options.keyShift)) {
                    if (down) {
                        ToggleKeyStates.shiftToggleState = keyMapping.isDown();
                    } else {
                        ToggleKeyStates.shiftToggleState = null;
                    }
                }
            }
        }
    }
}