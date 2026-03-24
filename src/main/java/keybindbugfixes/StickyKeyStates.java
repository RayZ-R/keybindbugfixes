package keybindbugfixes;

import com.minenash.rebind_all_the_keys.RebindAllTheKeys;
import keybindbugfixes.mixin.KeyBindingAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;

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
        if (MinecraftClient.IS_SYSTEM_MAC) {
            revertStickyKeyBinding(InputUtil.GLFW_KEY_LEFT_SUPER);
            revertStickyKeyBinding(InputUtil.GLFW_KEY_RIGHT_SUPER);
        } else {
            revertStickyKeyBinding(InputUtil.GLFW_KEY_LEFT_CONTROL);
            revertStickyKeyBinding(InputUtil.GLFW_KEY_RIGHT_CONTROL);
        }
    }

    public static void revertDropStackModifier() {
        boolean isModifierPressed;

        if (KeybindBugFixes.IS_REBIND_ALL_THE_KEYS_LOADED) {
            isModifierPressed = RebindAllTheKeys.DROP_STACK_MODIFIER.isPressed();
        } else {
            isModifierPressed = Screen.hasControlDown();
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