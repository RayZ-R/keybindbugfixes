package keybindbugfixes;

import com.minenash.rebind_all_the_keys.RebindAllTheKeys;
import com.mojang.blaze3d.platform.InputConstants;
import keybindbugfixes.config.Config;
import keybindbugfixes.mixin.KeyMappingAccessor;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KeybindBugFixes implements ClientModInitializer {
    public static final String MOD_NAME = "KeybindBugFixes";
    public static final String MOD_ID = "keybindbugfixes";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAME);
    public static Minecraft minecraft;

    public static boolean draggingPickKey = false;

    public static final boolean IS_REBIND_ALL_THE_KEYS_LOADED
            = FabricLoader.getInstance().isModLoaded("rebind_all_the_keys");

    public static InputConstants.Key getReloadResourcesKey() {
        if (IS_REBIND_ALL_THE_KEYS_LOADED) {
            return ((KeyMappingAccessor) RebindAllTheKeys.RELOAD_RESOURCES).getKey();
        } else {
            return InputConstants.Type.KEYSYM.getOrCreate(InputConstants.KEY_T);
        }
    }

    @Override
    public void onInitializeClient() {
        minecraft = Minecraft.getInstance();
        Config.init();
        Config.loadJson();
        Config.saveJson();
    }
}