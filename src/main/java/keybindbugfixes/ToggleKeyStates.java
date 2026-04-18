package keybindbugfixes;

import com.mojang.blaze3d.platform.InputConstants;
import keybindbugfixes.mixin.KeyMappingAccessor;
import net.minecraft.client.KeyMapping;

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
        revert(InputConstants.KEY_LCONTROL);
        revert(InputConstants.KEY_RCONTROL);
    }
}