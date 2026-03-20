package keybindbugfixes;

import com.minenash.rebind_all_the_keys.RebindAllTheKeys;
import keybindbugfixes.config.Config;
import keybindbugfixes.mixin.KeyBindingAccessor;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KeybindBugFixes implements ClientModInitializer {
    public static final String MOD_NAME = "KeybindBugFixes";
    public static final String MOD_ID = "keybindbugfixes";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAME);
    public static MinecraftClient client;

    public static boolean draggingPickKey = false;

    public static final boolean IS_REBIND_ALL_THE_KEYS_LOADED
            = FabricLoader.getInstance().isModLoaded("rebind_all_the_keys");

    public static InputUtil.Key getReloadResourcesKey() {
        if (IS_REBIND_ALL_THE_KEYS_LOADED) {
            return ((KeyBindingAccessor) RebindAllTheKeys.RELOAD_RESOURCES).getBoundKey();
        } else {
            return InputUtil.Type.KEYSYM.createFromCode(GLFW.GLFW_KEY_T);
        }
    }

    @Override
    public void onInitializeClient() {
        client = MinecraftClient.getInstance();
        Config.init();
        Config.loadJson();
        Config.saveJson();
    }
}