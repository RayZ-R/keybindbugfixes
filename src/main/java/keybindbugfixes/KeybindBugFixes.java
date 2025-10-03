package keybindbugfixes;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.minenash.rebind_all_the_keys.RebindAllTheKeys;
import keybindbugfixes.config.ConfigManager;
import keybindbugfixes.mixin.KeyBindingAccessor;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.input.SystemKeycodes;
import net.minecraft.client.option.StickyKeyBinding;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Set;

public class KeybindBugFixes implements ClientModInitializer {
    public static class StickyKeyRevertMap {
        public final Map<StickyKeyBinding, Boolean> map = Maps.newHashMap();

        public void put(StickyKeyBinding keyBinding, Boolean value) {
            this.map.put(keyBinding, value);
        }

        public void remove(StickyKeyBinding keyBinding) {
            this.map.remove(keyBinding);
        }

        public Map<StickyKeyBinding, Boolean> filter(int keycode) {
            Map<StickyKeyBinding, Boolean> result = Maps.newHashMap();

            for (Map.Entry<StickyKeyBinding, Boolean> entry : this.map.entrySet()) {
                if (((KeyBindingAccessor) entry.getKey()).getBoundKey().getCode() == keycode) {
                    result.put(entry.getKey(), entry.getValue());
                }
            }

            return result;
        }
    }

    public static final String MOD_NAME = "KeybindBugFixes";
    public static final String MOD_ID = "keybindbugfixes";

    public static final Set<String> DISABLED_MIXIN_NAMES = Sets.newHashSet();
    public static final Set<String> DISABLED_OPTION_NAMES = Sets.newHashSet();

    private static final boolean IS_REBIND_ALL_THE_KEYS_MOD_LOADED;

    private static boolean loaded = false;
    public static MinecraftClient client;
    public static final FabricLoader FABRIC_LOADER = FabricLoader.getInstance();
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAME);
    public static final Gson GSON;

    public static final StickyKeyRevertMap STICKY_KEY_REVERT_MAP = new StickyKeyRevertMap();
    public static boolean draggingPickKey = false;

    static {
        GsonBuilder builder = new GsonBuilder();
        GSON = builder.serializeNulls().setPrettyPrinting().create();

        IS_REBIND_ALL_THE_KEYS_MOD_LOADED = FABRIC_LOADER.isModLoaded("rebind_all_the_keys");

        preLoad();
    }

    public static void preLoad() {
        if (loaded) {
            return;
        } else {
            loaded = true;
        }

        ConfigManager.preInit();

        boolean isAmecsApiModLoaded = FABRIC_LOADER.isModLoaded("amecsapi");

        if (IS_REBIND_ALL_THE_KEYS_MOD_LOADED) {
            disableMixin("rebind_debug_keys");
        }

        if (!isAmecsApiModLoaded) {
            DISABLED_MIXIN_NAMES.add("fix_modifier_sticky_key.KeyBindingMixin");
        }
    }

    public static void disableMixin(String name) {
        DISABLED_MIXIN_NAMES.add(name);

        for (ConfigManager.Option<?> option : ConfigManager.OPTIONS) {
            if (option.mixinName().equals(name)) {
                DISABLED_OPTION_NAMES.add(option.name());
            }
        }
    }

    public static boolean shouldAddOption(ConfigManager.Option<?> option) {
        return !DISABLED_OPTION_NAMES.contains(option.name());
    }

    public static void revertStickyKeyBinding(int keycode) {
        Map<StickyKeyBinding, Boolean> submap = STICKY_KEY_REVERT_MAP.filter(keycode);

        for (Map.Entry<StickyKeyBinding, Boolean> entry : submap.entrySet()) {
            StickyKeyBinding keyBinding = entry.getKey();
            boolean initialValue = entry.getValue();
            KeyBindingAccessor accessor = (KeyBindingAccessor) keyBinding;

            accessor.setPressedState(initialValue);

            if (keyBinding.equals(client.options.sprintKey) && !initialValue) {
                client.player.setSprinting(false);
            }

            STICKY_KEY_REVERT_MAP.remove(keyBinding);
        }
    }

    public static void revertControlModifier() {
        revertStickyKeyBinding(SystemKeycodes.LEFT_CTRL);
        revertStickyKeyBinding(SystemKeycodes.RIGHT_CTRL);
    }

    public static void revertDropStackModifier() {
        boolean isModifierPressed;

        if (IS_REBIND_ALL_THE_KEYS_MOD_LOADED) {
            isModifierPressed = RebindAllTheKeys.DROP_STACK_MODIFIER.isPressed();
        } else {
            isModifierPressed = client.isCtrlPressed();
        }

        if (isModifierPressed) {
            if (IS_REBIND_ALL_THE_KEYS_MOD_LOADED) {
                KeyBindingAccessor accessor = (KeyBindingAccessor) RebindAllTheKeys.DROP_STACK_MODIFIER;
                revertStickyKeyBinding(accessor.getBoundKey().getCode());
            } else {
                revertControlModifier();
            }
        }
    }

    public static void revertPickBlockModifier() {
        if (client.isCtrlPressed()) {
            revertControlModifier();
        }
    }

    public static void revertNarratorModifier() {
        revertControlModifier();
    }

    @Override
    public void onInitializeClient() {
        client = MinecraftClient.getInstance();
        ConfigManager.init();
    }
}