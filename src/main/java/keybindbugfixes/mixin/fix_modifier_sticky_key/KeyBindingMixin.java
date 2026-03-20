package keybindbugfixes.mixin.fix_modifier_sticky_key;

import de.siphalor.amecs.api.KeyBindingUtils;
import de.siphalor.amecs.api.KeyModifiers;
import keybindbugfixes.StickyKeyRevertMap;
import keybindbugfixes.config.Config;
import net.minecraft.client.option.KeyBinding;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(KeyBinding.class)
public abstract class KeyBindingMixin {
    @Inject(method = "setPressed", at = @At("HEAD"))
    private void revertAmecsModifier(CallbackInfo callbackInfo) {
        if (Config.FIX_MODIFIER_STICKY_KEY.value) {
            KeyModifiers modifiers = KeyBindingUtils.getBoundModifiers((KeyBinding) (Object) this);

            if (modifiers.getAlt()) {
                StickyKeyRevertMap.revertStickyKeyBinding(GLFW.GLFW_KEY_LEFT_ALT);
                StickyKeyRevertMap.revertStickyKeyBinding(GLFW.GLFW_KEY_RIGHT_ALT);
            }

            if (modifiers.getControl()) {
                StickyKeyRevertMap.revertControlModifier();
            }

            if (modifiers.getShift()) {
                StickyKeyRevertMap.revertStickyKeyBinding(GLFW.GLFW_KEY_LEFT_SHIFT);
                StickyKeyRevertMap.revertStickyKeyBinding(GLFW.GLFW_KEY_RIGHT_SHIFT);
            }
        }
    }
}