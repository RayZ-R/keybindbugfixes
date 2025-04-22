package keybindbugfixes.mixin.fix_modifier_toggle_control;

import keybindbugfixes.KeybindBugFixes;
import keybindbugfixes.config.Config;
import keybindbugfixes.mixin.KeyBindingAccessor;
import net.minecraft.client.option.StickyKeyBinding;
import org.apache.commons.lang3.tuple.Pair;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(StickyKeyBinding.class)
public abstract class StickyKeyBindingMixin {
    @Inject(method = "setPressed", at = @At("HEAD"))
    private void addControlStickyKeyBindingRevertValues(boolean pressed, CallbackInfo callbackInfo) {
        if (Config.BugFixes.FIX_MODIFIER_TOGGLE_CONTROL && pressed) {
            StickyKeyBinding keyBinding = (StickyKeyBinding) (Object) this;
            KeyBindingAccessor accessor = (KeyBindingAccessor) keyBinding;

            if (accessor.getBoundKey().getCode() == GLFW.GLFW_KEY_LEFT_CONTROL) {
                KeybindBugFixes.stickyKeyRevertState = Pair.of(keyBinding, keyBinding.isPressed());
            }
        }
    }
}