package keybindbugfixes.mixin.fix_modifier_sticky_key;

import keybindbugfixes.KeybindBugFixes;
import keybindbugfixes.StickyKeyStates;
import keybindbugfixes.config.Config;
import keybindbugfixes.mixin.StickyKeyBindingAccessor;
import net.minecraft.client.option.StickyKeyBinding;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(StickyKeyBinding.class)
public abstract class StickyKeyBindingMixin {
    @Inject(method = "setPressed", at = @At("HEAD"))
    private void updateStickyKeyStates(boolean pressed, CallbackInfo callbackInfo) {
        if (Config.FIX_MODIFIER_STICKY_KEY.value) {
            StickyKeyBinding keyBinding = (StickyKeyBinding) (Object) this;
            StickyKeyBindingAccessor accessor = (StickyKeyBindingAccessor) keyBinding;

            if (accessor.getToggleGetter().getAsBoolean()) {
                if (keyBinding.equals(KeybindBugFixes.client.options.sprintKey)) {
                    if (pressed) {
                        StickyKeyStates.wasSprintToggled = keyBinding.isPressed();
                        StickyKeyStates.wasSprinting = KeybindBugFixes.client.player.isSprinting();
                    } else {
                        StickyKeyStates.wasSprintToggled = null;
                    }
                } else if (keyBinding.equals(KeybindBugFixes.client.options.sneakKey)) {
                    if (pressed) {
                        StickyKeyStates.wasSneakToggled = keyBinding.isPressed();
                    } else {
                        StickyKeyStates.wasSneakToggled = null;
                    }
                }
            }
        }
    }
}