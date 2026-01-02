package keybindbugfixes.config;

import keybindbugfixes.config.annotation.BugInfo;
import keybindbugfixes.config.annotation.CategoryInfo;
import keybindbugfixes.config.annotation.TweakInfo;

public class Config {
    @CategoryInfo(entryName = "bugfix")
    public static class BugFixes {
        @BugInfo(id = 117771)
        public static boolean FIX_PICK_KEY_DRAGGING = true;

        @BugInfo(id = 169163)
        public static boolean FIX_DISMOUNT_TOGGLE_SNEAK = true;

        @BugInfo(id = 300695)
        public static boolean FIX_SCREEN_STICKY_KEY_RESET = true;

        @BugInfo
        public static boolean FIX_MODIFIER_STICKY_KEY = true;

        @BugInfo
        public static boolean FIX_REBIND_TO_F3 = true;
    }

    @CategoryInfo(entryName = "tweak")
    public static class Tweaks {
        @TweakInfo
        public static boolean DROP_WHEN_HOLDING_ITEM = true;
    }

    @CategoryInfo(entryName = "key")
    public static class Keybinds {}
}