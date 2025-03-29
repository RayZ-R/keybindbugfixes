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
        public static boolean FIX_LAST_GAME_MODE_RESET = true;

        @BugInfo(id = 263293)
        public static boolean FIX_STICKY_KEY_RESET = true;

        @BugInfo(id = 117771)
        public static boolean FIX_PICK_BLOCK_DRAGGING = true;

        @BugInfo
        public static boolean FIX_CONTROL_STICKY_KEY_RESET = true;
    }

    @CategoryInfo(entryName = "tweak")
    public static class Tweaks {
        @TweakInfo
        public static boolean REMOVE_KEYBIND_CONFLICTS = true;
    }

    @CategoryInfo(entryName = "key")
    public static class Keybinds {
        @KeybindInfo
        public static InputUtil.Key DEBUG = InputUtil.Type.KEYSYM.createFromCode(GLFW.GLFW_KEY_F3);

        @KeybindInfo(modifier = "DEBUG")
        public static InputUtil.Key GAME_MODE_CYCLE = InputUtil.Type.KEYSYM.createFromCode(GLFW.GLFW_KEY_F4);
    }
}