package keybindbugfixes.mixin.fix_modifier_sticky_key;

import com.mojang.blaze3d.platform.InputConstants;
import de.siphalor.amecs.api.KeyBindingUtils;
import de.siphalor.amecs.api.KeyModifiers;
import keybindbugfixes.ToggleKeyStates;
import keybindbugfixes.config.Config;
import net.minecraft.client.KeyMapping;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(KeyMapping.class)
public abstract class KeyMappingMixin {
    @Inject(method = "setDown", at = @At("HEAD"))
    private void revertAmecsModifierKey(CallbackInfo callbackInfo) {
        if (Config.FIX_MODIFIER_STICKY_KEY.value) {
            KeyModifiers modifiers = KeyBindingUtils.getBoundModifiers((KeyMapping) (Object) this);

            if (modifiers.getAlt()) {
                ToggleKeyStates.revert(InputConstants.KEY_LALT);
                ToggleKeyStates.revert(InputConstants.KEY_RALT);
            }

            if (modifiers.getControl()) {
                ToggleKeyStates.revertControl();
            }

            if (modifiers.getShift()) {
                ToggleKeyStates.revert(InputConstants.KEY_LSHIFT);
                ToggleKeyStates.revert(InputConstants.KEY_RSHIFT);
            }
        }
    }
}