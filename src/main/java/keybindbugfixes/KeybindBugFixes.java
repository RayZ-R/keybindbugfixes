package keybindbugfixes;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.minenash.rebind_all_the_keys.RebindAllTheKeys;
import keybindbugfixes.config.Config;
import keybindbugfixes.config.ConfigManager;
import keybindbugfixes.mixin.KeyBindingAccessor;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.option.StickyKeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Set;

public class KeybindBugFixes implements ClientModInitializer {
    public static final String MOD_NAME = "KeybindBugFixes";
    public static final String MOD_ID = "keybindbugfixes";

    public static final Set<String> DISABLED_MIXIN_NAMES = Sets.newHashSet();
    public static final Set<String> DISABLED_OPTION_NAMES = Sets.newHashSet();

    private static final boolean IS_REBIND_ALL_THE_KEYS_MOD_LOADED;
    private static boolean disabledMixins = false;

    public static MinecraftClient client;
    public static final FabricLoader FABRIC_LOADER = FabricLoader.getInstance();
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAME);
    public static final Gson GSON;

    public static final Map<StickyKeyBinding, Boolean> STICKY_KEY_REVERT_MAP = Maps.newHashMap();
    public static boolean draggingPickKey = false;

    static {
        GsonBuilder builder = new GsonBuilder();
        GSON = builder.serializeNulls().setPrettyPrinting().create();

        IS_REBIND_ALL_THE_KEYS_MOD_LOADED = FABRIC_LOADER.isModLoaded("rebind_all_the_keys");
    }

    public static void disableMixins() {
        if (!disabledMixins) {
            disabledMixins = true;

            ConfigManager.preInit();

            boolean isRebindAllTheKeysModLoaded = IS_REBIND_ALL_THE_KEYS_MOD_LOADED;
            boolean isAmecsApiModLoaded = FABRIC_LOADER.isModLoaded("amecsapi");
            boolean isNmukModLoaded = FABRIC_LOADER.isModLoaded("nmuk");
            boolean isRrlsLoaded = FABRIC_LOADER.isModLoaded("rrls");

            if (isRebindAllTheKeysModLoaded || isAmecsApiModLoaded || isNmukModLoaded) {
                disableMixin("remove_keybind_conflicts");
            }

            if (isRebindAllTheKeysModLoaded) {
                disableMixin("rebind_debug_keys");
            }

            if (isRrlsLoaded) {
                DISABLED_MIXIN_NAMES.add("reload_resources_anywhere.MinecraftClientMixin");
            }
        }
    }

    private static void disableMixin(String name) {
        DISABLED_MIXIN_NAMES.add(name);

        for (ConfigManager.OptionInfo optionInfo : ConfigManager.OPTION_INFOS) {
            if (optionInfo.mixinName().equals(name)) {
                DISABLED_OPTION_NAMES.add(optionInfo.name());
            }
        }
    }

    public static InputUtil.Key getReloadResourcesKey() {
        if (IS_REBIND_ALL_THE_KEYS_MOD_LOADED) {
            return ((KeyBindingAccessor) RebindAllTheKeys.RELOAD_RESOURCES).getBoundKey();
        } else {
            return InputUtil.Type.KEYSYM.createFromCode(GLFW.GLFW_KEY_T);
        }
    }

    public static boolean shouldAddOption(ConfigManager.Option<?> option) {
        return !DISABLED_OPTION_NAMES.contains(option.name());
    }

    public static void revertStickyKeyBindings() {
        if (Config.BugFixes.FIX_MODIFIER_TOGGLE_CONTROL) {
            for (Map.Entry<StickyKeyBinding, Boolean> entry : STICKY_KEY_REVERT_MAP.entrySet()) {
                KeyBinding keyBinding = entry.getKey();
                boolean resetValue = entry.getValue();

                ((KeyBindingAccessor) keyBinding).setPressedState(resetValue);

                if (keyBinding.equals(client.options.sprintKey) && !resetValue) {
                    client.player.setSprinting(false);
                }
            }
        }
    }

    @Override
    public void onInitializeClient() {
        client = MinecraftClient.getInstance();
        ConfigManager.init();
    }
}