package keybindbugfixes;

import com.minenash.rebind_all_the_keys.RebindAllTheKeys;
import keybindbugfixes.mixin.KeyBindingAccessor;
import net.minecraft.client.input.SystemKeycodes;
import net.minecraft.client.option.KeyBinding;

public class StickyKeyStates {
    public static Boolean wasSprintToggled = null;
    public static Boolean wasSprinting = null;

    public static Boolean wasSneakToggled = null;

    public static void revertStickyKeyBinding(int keycode) {
        KeyBinding sprintKey = KeybindBugFixes.client.options.sprintKey;
        KeyBinding sneakKey = KeybindBugFixes.client.options.sneakKey;

        KeyBindingAccessor sprintAccessor = (KeyBindingAccessor) sprintKey;
        KeyBindingAccessor sneakAccessor = (KeyBindingAccessor) sneakKey;

        if (wasSprintToggled != null && sprintAccessor.getBoundKey().getCode() == keycode) {
            sprintAccessor.setPressedState(wasSprintToggled);
            KeybindBugFixes.client.player.setSprinting(wasSprinting);
            wasSprintToggled = null;
            wasSprinting = null;
        }

        if (wasSneakToggled != null && sneakAccessor.getBoundKey().getCode() == keycode) {
            sneakAccessor.setPressedState(wasSneakToggled);
            wasSneakToggled = null;
        }
    }

    public static void revertControlModifier() {
        revertStickyKeyBinding(SystemKeycodes.LEFT_CTRL);
        revertStickyKeyBinding(SystemKeycodes.RIGHT_CTRL);
    }

    public static void revertDropStackModifier() {
        boolean isModifierPressed;

        if (KeybindBugFixes.IS_REBIND_ALL_THE_KEYS_LOADED) {
            isModifierPressed = RebindAllTheKeys.DROP_STACK_MODIFIER.isPressed();
        } else {
            isModifierPressed = KeybindBugFixes.client.isCtrlPressed();
        }

        if (isModifierPressed) {
            if (KeybindBugFixes.IS_REBIND_ALL_THE_KEYS_LOADED) {
                KeyBindingAccessor accessor = (KeyBindingAccessor) RebindAllTheKeys.DROP_STACK_MODIFIER;
                revertStickyKeyBinding(accessor.getBoundKey().getCode());
            } else {
                revertControlModifier();
            }
        }
    }
}