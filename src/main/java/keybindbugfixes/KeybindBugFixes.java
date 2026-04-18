package keybindbugfixes;

import keybindbugfixes.config.Config;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.Minecraft;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KeybindBugFixes implements ClientModInitializer {
    public static final String MOD_NAME = "KeybindBugFixes";
    public static final String MOD_ID = "keybindbugfixes";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAME);
    public static Minecraft minecraft;

    public static boolean draggingPickKey = false;

    @Override
    public void onInitializeClient() {
        minecraft = Minecraft.getInstance();
        Config.init();
        Config.loadJson();
        Config.saveJson();
    }
}