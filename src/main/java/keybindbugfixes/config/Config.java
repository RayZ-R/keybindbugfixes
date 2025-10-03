package keybindbugfixes.config;

import keybindbugfixes.config.annotation.BugInfo;
import keybindbugfixes.config.annotation.CategoryInfo;
import keybindbugfixes.config.annotation.KeybindInfo;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class Config {
    @CategoryInfo(entryName = "bugfix")
    public static class BugFixes {
        @BugInfo(id = 117771)
        public static boolean FIX_PICK_KEY_DRAGGING = true;

        @BugInfo(id = 169163)
        public static boolean FIX_DISMOUNT_TOGGLE_SNEAK = true;

        @BugInfo
        public static boolean FIX_MODIFIER_STICKY_KEY = true;
    }

    @CategoryInfo(entryName = "tweak")
    public static class Tweaks {}

    @CategoryInfo(entryName = "key")
    public static class Keybinds {
        @KeybindInfo(mixin = "rebind_debug_keys")
        public static Object DEBUG = InputUtil.Type.KEYSYM.createFromCode(GLFW.GLFW_KEY_F3);

        @KeybindInfo(modifier = "DEBUG", mixin = "rebind_debug_keys")
        public static Object GAME_MODE_CYCLE = InputUtil.Type.KEYSYM.createFromCode(GLFW.GLFW_KEY_F4);
    }
}