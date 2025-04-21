package keybindbugfixes.config;

import keybindbugfixes.config.annotation.BugInfo;
import keybindbugfixes.config.annotation.CategoryInfo;
import keybindbugfixes.config.annotation.KeybindInfo;
import keybindbugfixes.config.annotation.TweakInfo;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class Config {
    @CategoryInfo(entryName = "bugfix")
    public static class BugFixes {
        @BugInfo(id = 183776)
        public static boolean FIX_PRESSING_F3_TWICE = true;

        @BugInfo(id = 259571)
        public static boolean FIX_GAME_MODE_SWITCHER_RESET = true;

        @BugInfo(id = 263293)
        public static boolean FIX_STICKY_KEY_RESET = true;

        @BugInfo(id = 117771)
        public static boolean FIX_PICK_KEY_DRAGGING = true;

        @BugInfo(id = 169163)
        public static boolean FIX_DISMOUNT_TOGGLE_SNEAK = true;

        @BugInfo
        public static boolean FIX_MODIFIER_TOGGLE_CONTROL = true;
    }

    @CategoryInfo(entryName = "tweak")
    public static class Tweaks {
        @TweakInfo
        public static boolean REMOVE_KEYBIND_CONFLICTS = true;

        @BugInfo(id = 269020)
        public static boolean RELOAD_RESOURCES_ANYWHERE = true;
    }

    @CategoryInfo(entryName = "key")
    public static class Keybinds {
        @KeybindInfo(mixin = "rebind_debug_keys")
        public static Object DEBUG = InputUtil.Type.KEYSYM.createFromCode(GLFW.GLFW_KEY_F3);

        @KeybindInfo(modifier = "DEBUG", mixin = "rebind_debug_keys")
        public static Object GAME_MODE_CYCLE = InputUtil.Type.KEYSYM.createFromCode(GLFW.GLFW_KEY_F4);
    }
}