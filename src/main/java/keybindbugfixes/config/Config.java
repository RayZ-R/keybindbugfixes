package keybindbugfixes.config;

import com.google.common.collect.Lists;
import com.google.gson.*;
import keybindbugfixes.KeybindBugFixes;
import keybindbugfixes.config.option.BooleanOption;
import keybindbugfixes.config.option.KeybindOption;
import keybindbugfixes.config.option.Option;
import net.fabricmc.loader.api.FabricLoader;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class Config {
    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    public static final List<Option<?>> OPTIONS = Lists.newArrayList();
    public static final List<KeybindOption> KEYBIND_OPTIONS = Lists.newArrayList();

    public static final BooleanOption FIX_PICK_KEY_DRAGGING = new BooleanOption(
            "bugfix.fix_pick_key_dragging",
            "fix_pick_key_dragging",
            true,
            117771
    );

    public static final BooleanOption FIX_DISMOUNT_TOGGLE_SNEAK = new BooleanOption(
            "bugfix.fix_dismount_toggle_sneak",
            "fix_dismount_toggle_sneak",
            true,
            169163
    );

    public static final BooleanOption FIX_SCREEN_STICKY_KEY_RESET = new BooleanOption(
            "bugfix.fix_screen_sticky_key_reset",
            "fix_screen_sticky_key_reset",
            true,
            300695
    );

    public static final BooleanOption FIX_MODIFIER_STICKY_KEY = new BooleanOption(
            "bugfix.fix_modifier_sticky_key",
            "fix_modifier_sticky_key",
            true,
            null
    );

    public static final BooleanOption FIX_REBIND_TO_F3 = new BooleanOption(
            "bugfix.fix_rebind_to_f3",
            "fix_rebind_to_f3",
            true,
            null
    );

    public static final BooleanOption DROP_WHEN_HOLDING_ITEM = new BooleanOption(
            "tweak.drop_when_holding_item",
            "drop_when_holding_item",
            true,
            null
    );

    public static final KeybindOption DEBUG_KEY = new KeybindOption(
            "key.debug",
            "rebind_debug_keys",
            "key.keyboard.f3",
            null,
            null
    );

    public static final KeybindOption GAME_MODE_CYCLE_KEY = new KeybindOption(
            "key.game_mode_cycle",
            "rebind_debug_keys",
            "key.keyboard.f4",
            null,
            DEBUG_KEY
    );

    static {
        for (Field field : Config.class.getFields()) {
            if (!Option.class.isAssignableFrom(field.getType())) continue;

            try {
                Option<?> option = (Option<?>) field.get(null);
                OPTIONS.add(option);

                if (option instanceof KeybindOption keybindOption) {
                    KEYBIND_OPTIONS.add(keybindOption);
                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void init() {
        for (Option<?> option : OPTIONS) {
            option.init();
        }
    }

    private static Path configPath() {
        return FabricLoader.getInstance().getConfigDir().resolve(KeybindBugFixes.MOD_ID + ".json");
    }

    public static void loadJson() {
        Path configPath = configPath();

        if (Files.exists(configPath)) {
            try (BufferedReader reader = Files.newBufferedReader(configPath)) {
                JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();

                for (Option<?> option : OPTIONS) {
                    JsonElement jsonElement = json.get(option.key);

                    if (jsonElement == null || !option.fromJson(jsonElement)) {
                        KeybindBugFixes.LOGGER.error("Couldn't load "
                                + KeybindBugFixes.MOD_NAME
                                + " configuration entry "
                                + '"' + option.key + '"');
                    }
                }
            } catch (Throwable e) {
                KeybindBugFixes.LOGGER.error("Couldn't load " + KeybindBugFixes.MOD_NAME + " configuration file");
            }
        }
    }

    public static void saveJson() {
        Path configPath = configPath();
        JsonObject json = new JsonObject();

        for (Option<?> option : OPTIONS) {
            option.toJson(json);
        }

        String string = GSON.toJson(json);

        try (BufferedWriter writer = Files.newBufferedWriter(configPath)) {
            writer.write(string);
        } catch (Throwable e) {
            KeybindBugFixes.LOGGER.error("Couldn't save " + KeybindBugFixes.MOD_NAME + " configuration file");
        }
    }
}