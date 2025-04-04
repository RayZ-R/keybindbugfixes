package keybindbugfixes;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import keybindbugfixes.config.Config;
import keybindbugfixes.config.ConfigManager;
import keybindbugfixes.mixin.KeyBindingAccessor;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.option.StickyKeyBinding;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Set;

public class KeybindBugFixes implements ClientModInitializer {
    public static final String MOD_NAME = "KeybindBugFixes";
    public static final String MOD_ID = "keybindbugfixes";

    public static final Set<String> DISABLED_MIXIN_NAMES = Sets.newHashSet();
    public static final Set<String> DISABLED_OPTION_NAMES = Sets.newHashSet();

    public static MinecraftClient client;
    public static final FabricLoader FABRIC_LOADER = FabricLoader.getInstance();
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAME);
    public static final Gson GSON;

    public static final Map<StickyKeyBinding, Boolean> STICKY_KEY_REVERT_MAP = Maps.newHashMap();
    public static boolean draggingPickKey = false;

    static {
        GsonBuilder builder = new GsonBuilder();
        GSON = builder.setPrettyPrinting().create();

        boolean isRebindAllTheKeysModLoaded = FABRIC_LOADER.isModLoaded("rebind_all_the_keys");
        boolean isAmecsApiModLoaded = FABRIC_LOADER.isModLoaded("amecsapi");
        boolean isNmukModLoaded = FABRIC_LOADER.isModLoaded("nmuk");

        if (isRebindAllTheKeysModLoaded || isAmecsApiModLoaded || isNmukModLoaded) {
            DISABLED_MIXIN_NAMES.add("remove_keybind_conflicts");
            DISABLED_OPTION_NAMES.add("tweak.remove_keybind_conflicts");
        }

        if (isRebindAllTheKeysModLoaded) {
            DISABLED_MIXIN_NAMES.add("rebind_debug_keys");
            DISABLED_OPTION_NAMES.add("key.debug");
            DISABLED_OPTION_NAMES.add("key.game_mode_cycle");
        }
    }

    public static boolean shouldAddOption(ConfigManager.Option<?> option) {
        return !DISABLED_OPTION_NAMES.contains(option.name());
    }

    public static void revertStickyKeyBindings() {
        if (Config.BugFixes.FIX_CONTROL_STICKY_KEY_RESET) {
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