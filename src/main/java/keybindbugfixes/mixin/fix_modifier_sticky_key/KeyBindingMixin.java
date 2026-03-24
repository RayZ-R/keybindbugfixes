package keybindbugfixes.mixin.fix_modifier_sticky_key;

import de.siphalor.amecs.api.KeyBindingUtils;
import de.siphalor.amecs.api.KeyModifiers;
import keybindbugfixes.StickyKeyStates;
import keybindbugfixes.config.Config;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(KeyBinding.class)
public abstract class KeyBindingMixin {
    @Inject(method = "setPressed", at = @At("HEAD"))
    private void revertAmecsModifierKey(CallbackInfo callbackInfo) {
        if (Config.FIX_MODIFIER_STICKY_KEY.value) {
            KeyModifiers modifiers = KeyBindingUtils.getBoundModifiers((KeyBinding) (Object) this);

            if (modifiers.getAlt()) {
                StickyKeyStates.revertStickyKeyBinding(InputUtil.GLFW_KEY_LEFT_ALT);
                StickyKeyStates.revertStickyKeyBinding(InputUtil.GLFW_KEY_RIGHT_ALT);
            }

            if (modifiers.getControl()) {
                StickyKeyStates.revertControlModifier();
            }

            if (modifiers.getShift()) {
                StickyKeyStates.revertStickyKeyBinding(InputUtil.GLFW_KEY_LEFT_SHIFT);
                StickyKeyStates.revertStickyKeyBinding(InputUtil.GLFW_KEY_RIGHT_SHIFT);
            }
        }
    }
}