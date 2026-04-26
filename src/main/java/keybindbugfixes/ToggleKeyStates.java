package keybindbugfixes;

import com.minenash.rebind_all_the_keys.RebindAllTheKeys;
import keybindbugfixes.mixin.KeyMappingAccessor;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.input.InputQuirks;

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
        revert(InputQuirks.EDIT_SHORTCUT_KEY_LEFT);
        revert(InputQuirks.EDIT_SHORTCUT_KEY_RIGHT);
    }

    public static void revertDropStackModifier() {
        if (KeybindBugFixes.IS_REBIND_ALL_THE_KEYS_LOADED) {
            KeyMappingAccessor accessor = (KeyMappingAccessor) RebindAllTheKeys.DROP_STACK_MODIFIER;
            revert(accessor.getKey().getValue());
        } else {
            revertControl();
        }
    }
}