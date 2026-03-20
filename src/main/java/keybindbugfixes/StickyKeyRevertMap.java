package keybindbugfixes;

import com.google.common.collect.Maps;
import com.minenash.rebind_all_the_keys.RebindAllTheKeys;
import keybindbugfixes.mixin.KeyBindingAccessor;
import net.minecraft.client.input.SystemKeycodes;
import net.minecraft.client.option.StickyKeyBinding;
import org.lwjgl.glfw.GLFW;

import java.util.Map;

public class StickyKeyRevertMap {
    public static final Map<StickyKeyBinding, Boolean> MAP = Maps.newHashMap();

    public static Map<StickyKeyBinding, Boolean> filterMap(int keycode) {
        Map<StickyKeyBinding, Boolean> filteredMap = Maps.newHashMap();

        for (Map.Entry<StickyKeyBinding, Boolean> entry : MAP.entrySet()) {
            if (((KeyBindingAccessor) entry.getKey()).getBoundKey().getCode() == keycode) {
                filteredMap.put(entry.getKey(), entry.getValue());
            }
        }

        return filteredMap;
    }

    public static void revertStickyKeyBinding(int keycode) {
        Map<StickyKeyBinding, Boolean> filteredMap = filterMap(keycode);

        for (Map.Entry<StickyKeyBinding, Boolean> entry : filteredMap.entrySet()) {
            StickyKeyBinding keyBinding = entry.getKey();
            boolean initialValue = entry.getValue();

            ((KeyBindingAccessor) keyBinding).setPressedState(initialValue);

            if (keyBinding.equals(KeybindBugFixes.client.options.sprintKey) && !initialValue) {
                KeybindBugFixes.client.player.setSprinting(false);
            }

            MAP.remove(keyBinding);
        }
    }

    public static void revertControlModifier() {
        if (SystemKeycodes.IS_MAC_OS) {
            revertStickyKeyBinding(GLFW.GLFW_KEY_LEFT_SUPER);
            revertStickyKeyBinding(GLFW.GLFW_KEY_RIGHT_SUPER);
        } else {
            revertStickyKeyBinding(GLFW.GLFW_KEY_LEFT_CONTROL);
            revertStickyKeyBinding(GLFW.GLFW_KEY_RIGHT_CONTROL);
        }
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

    public static void revertPickBlockModifier() {
        if (KeybindBugFixes.client.isCtrlPressed()) {
            revertControlModifier();
        }
    }

    public static void revertNarratorModifier() {
        revertControlModifier();
    }
}