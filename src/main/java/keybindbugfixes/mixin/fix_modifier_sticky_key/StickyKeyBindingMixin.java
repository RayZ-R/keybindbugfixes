package keybindbugfixes.mixin.fix_modifier_sticky_key;

import keybindbugfixes.KeybindBugFixes;
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
    private void updateStickyKeyRevertMap(boolean pressed, CallbackInfo callbackInfo) {
        if (Config.BugFixes.FIX_MODIFIER_STICKY_KEY) {
            StickyKeyBinding keyBinding = (StickyKeyBinding) (Object) this;
            StickyKeyBindingAccessor accessor = (StickyKeyBindingAccessor) keyBinding;

            if (accessor.getToggleGetter().getAsBoolean()) {
                if (pressed) {
                    KeybindBugFixes.STICKY_KEY_REVERT_MAP.put(keyBinding, keyBinding.isPressed());
                } else {
                    KeybindBugFixes.STICKY_KEY_REVERT_MAP.remove(keyBinding);
                }
            }
        }
    }
}