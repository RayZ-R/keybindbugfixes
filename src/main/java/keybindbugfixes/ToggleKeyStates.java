package keybindbugfixes;

import com.minenash.rebind_all_the_keys.RebindAllTheKeys;
import keybindbugfixes.mixin.KeyMappingAccessor;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.input.InputQuirks;
import org.lwjgl.glfw.GLFW;

public class ToggleKeyStates {
    public static Boolean sprintToggleState = null;
    public static Boolean sprintingState = null;

    public static Boolean shiftToggleState = null;

    public static void revert(int keycode) {
        KeyMapping keySprint = KeybindBugFixes.minecraft.options.keySprint;
        KeyMapping keyShift = KeybindBugFixes.minecraft.options.keyShift;

        KeyMappingAccessor sprintAccessor = (KeyMappingAccessor) keySprint;
        KeyMappingAccessor shiftAccessor = (KeyMappingAccessor) keyShift;

        if (sprintToggleState != null && sprintAccessor.getKey().getValue() == keycode) {
            sprintAccessor.setDownState(sprintToggleState);
            KeybindBugFixes.minecraft.player.setSprinting(sprintingState);
            sprintToggleState = null;
            sprintingState = null;
        }

        if (shiftToggleState != null && shiftAccessor.getKey().getValue() == keycode) {
            shiftAccessor.setDownState(shiftToggleState);
            shiftToggleState = null;
        }
    }

    public static void revertControl() {
        if (InputQuirks.REPLACE_CTRL_KEY_WITH_CMD_KEY) {
            revert(GLFW.GLFW_KEY_LEFT_SUPER);
            revert(GLFW.GLFW_KEY_RIGHT_SUPER);
        } else {
            revert(GLFW.GLFW_KEY_LEFT_CONTROL);
            revert(GLFW.GLFW_KEY_RIGHT_CONTROL);
        }
    }

    public static void revertDropStackModifier() {
        boolean isModifierPressed;

        if (KeybindBugFixes.IS_REBIND_ALL_THE_KEYS_LOADED) {
            isModifierPressed = RebindAllTheKeys.DROP_STACK_MODIFIER.isDown();
        } else {
            isModifierPressed = KeybindBugFixes.minecraft.hasControlDown();
        }

        if (isModifierPressed) {
            if (KeybindBugFixes.IS_REBIND_ALL_THE_KEYS_LOADED) {
                KeyMappingAccessor accessor = (KeyMappingAccessor) RebindAllTheKeys.DROP_STACK_MODIFIER;
                revert(accessor.getKey().getValue());
            } else {
                revertControl();
            }
        }
    }
}