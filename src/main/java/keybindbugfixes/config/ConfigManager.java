package keybindbugfixes.config;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import keybindbugfixes.KeybindBugFixes;
import keybindbugfixes.config.annotation.BugInfo;
import keybindbugfixes.config.annotation.CategoryInfo;
import keybindbugfixes.config.annotation.KeybindInfo;
import keybindbugfixes.config.annotation.TweakInfo;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.screen.ConfirmLinkScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

public class ConfigManager {
    public abstract static class Option<T> {
        protected final Field field;
        protected JsonElement jsonElement;

        protected final String name;
        protected final String translationKey;
        protected final String mixinName;
        protected final String entryName;

        protected boolean isDisabled;
        protected Consumer<T> changeCallback;
        protected T defaultValue;
        protected T value;

        public Option(Field field, String entryName, String mixinName) {
            this.field = field;

            String fieldName = field.getName().toLowerCase(Locale.ROOT);
            this.name = entryName + "." + fieldName;
            this.translationKey = KeybindBugFixes.MOD_ID + ".config." + this.name;
            this.mixinName = mixinName.isEmpty() ? fieldName : mixinName;
            this.entryName = entryName;

            OPTION_MIXIN_MAP.put(this.mixinName, this);
            OPTIONS.add(this);
        }

        public String name() {
            return this.name;
        }

        public String translationKey() {
            return this.translationKey;
        }

        public String mixinName() {
            return this.mixinName;
        }

        public boolean isDisabled() {
            return this.isDisabled;
        }


        protected abstract Class<T> valueType();

        public void initValue() {
            this.changeCallback = value -> {
                try {
                    this.field.set(null, value);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            };

            try {
                Class<T> valueType = this.valueType();
                this.defaultValue = valueType.cast(this.field.get(valueType));
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }

        public boolean loadValue() {
            this.value = this.defaultValue;
            
            if (this.isDisabled) {
                return true;
            }

            if (this.jsonElement != null) {
                return this.loadFromJson(this.jsonElement);
            } else {
                return false;
            }
        }

        public T value() {
            return this.value;
        }

        public T defaultValue() {
            return this.defaultValue;
        }

        public boolean isDefault() {
            return this.value.equals(this.defaultValue);
        }

        public void setValue(T value) {
            if (this.value != value) {
                this.value = value;
                this.changeCallback.accept(this.value);
            }
        }

        public void resetValue() {
            this.setValue(this.defaultValue);
        }


        public void write(JsonObject json) {
            if (this.isDisabled) {
                json.add(this.name, null);
            } else {
                this.writeToJson(json);
            }
        }

        public void load(JsonElement jsonElement) {
            this.isDisabled = jsonElement.isJsonNull();
            this.jsonElement = jsonElement;
        }

        protected abstract void writeToJson(JsonObject json);

        protected abstract boolean loadFromJson(JsonElement jsonPrimitive);
    }

    public static class BugOption extends TweakOption {
        @Nullable private final String link;
        private final int id;

        public BugOption(Field field, String entryName, String mixinName, int id) {
            super(field, entryName, mixinName);
            this.id = id;

            if (id != -1) {
                this.link = "https://bugs.mojang.com/browse/MC-" + id;
            } else {
                this.link = null;
            }

            BUG_OPTIONS.add(this);
        }

        public int id() {
            return this.id;
        }

        public @Nullable String link() {
            return this.link;
        }

        public void openLink(Screen parent) {
            if (this.link != null) {
                ConfirmLinkScreen.open(parent, this.link);
            }
        }
    }

    public static class TweakOption extends Option<Boolean> {
        private final Function<Boolean, Text> buttonTextFactory;
        private final Function<Boolean, Tooltip> tooltipFactory;

        public TweakOption(Field field, String entryName, String mixinName) {
            super(field, entryName, mixinName);

            this.buttonTextFactory = value -> Text.translatable(value ? "gui.yes" : "gui.no")
                    .formatted(value ? Formatting.GREEN : Formatting.RED);

            this.tooltipFactory =value -> Tooltip.of(
                    Text.translatable(this.translationKey + ".tooltip"));
        }

        @Override
        protected Class<Boolean> valueType() {
            return Boolean.class;
        }

        @Override
        public void writeToJson(JsonObject json) {
            json.addProperty(this.name, this.value);
        }

        @Override
        public boolean loadFromJson(JsonElement jsonElement) {
            if (jsonElement.isJsonPrimitive() && jsonElement.getAsJsonPrimitive().isBoolean()) {
                this.setValue(jsonElement.getAsBoolean());
                return true;
            } else {
                return false;
            }
        }

        public void toggleValue() {
            this.setValue(!this.value);
        }

        public Text buttonText() {
            return this.buttonTextFactory.apply(this.value);
        }

        public Tooltip tooltip() {
            return this.tooltipFactory.apply(this.value);
        }
    }

    public static class KeybindOption extends Option<InputUtil.Key> {
        @Nullable private KeybindOption modifier;

        public KeybindOption(Field field, String entryName, String mixinName, String modifier) {
            super(field, entryName, mixinName);

            boolean processed = false;

            if (!modifier.isEmpty()) {
                String modifierName = this.entryName + "." + modifier.toLowerCase(Locale.ROOT);

                for (KeybindOption option : KEYBIND_OPTIONS) {
                    if (option.name().equals(modifierName)) {
                        this.modifier = option;
                        processed = true;
                        break;
                    }
                }
            }

            if (!processed) {
                this.modifier = null;
            }

            KEYBIND_OPTIONS.add(this);
        }

        @Override
        protected Class<InputUtil.Key> valueType() {
            return InputUtil.Key.class;
        }

        @Override
        public void writeToJson(JsonObject json) {
            json.addProperty(this.name, this.value.getTranslationKey());
        }

        @Override
        public boolean loadFromJson(JsonElement jsonElement) {
            if (jsonElement.isJsonPrimitive() && jsonElement.getAsJsonPrimitive().isString()) {
                try {
                    this.setValue(InputUtil.fromTranslationKey(jsonElement.getAsString()));
                    return true;
                } catch (Throwable ignored) {
                    return false;
                }
            } else {
                return false;
            }
        }

        public @Nullable KeybindOption modifier() {
            return this.modifier;
        }

        public boolean isUnbound() {
            return this.value.equals(InputUtil.UNKNOWN_KEY);
        }
    }

    public static class Category {
        private final String translationKey;
        private final List<Option<?>> options;

        public Category(String categoryName, Option<?>[] options) {
            this.translationKey = KeybindBugFixes.MOD_ID + ".config.category." + categoryName;
            this.options = Lists.newArrayList(options);
        }

        public String translationKey() {
            return this.translationKey;
        }

        public List<Option<?>> options() {
            return this.options;
        }
    }

    public static class MixinMap {
        public final Map<String, Set<Option<?>>> map = Maps.newHashMap();

        public void put(String key, Option<?> option) {
            this.map.putIfAbsent(key, Sets.newHashSet());
            this.map.get(key).add(option);
        }

        public Set<Map.Entry<String, Set<Option<?>>>> entrySet() {
            return this.map.entrySet();
        }
    }

    public static final List<Category> CATEGORIES = Lists.newArrayList();
    public static final List<Option<?>> OPTIONS = Lists.newArrayList();
    public static final List<BugOption> BUG_OPTIONS = Lists.newArrayList();
    public static final List<KeybindOption> KEYBIND_OPTIONS = Lists.newArrayList();
    public static final MixinMap OPTION_MIXIN_MAP = new MixinMap();

    private static Path getConfigPath() {
        return FabricLoader.getInstance().getConfigDir().resolve(KeybindBugFixes.MOD_ID + ".json");
    }

    public static void initOptions() {
        Class<?>[] classes = Config.class.getClasses();
        List<Class<?>> classList = Arrays.asList(classes);
        Collections.reverse(classList);

        for (Class<?> categoryClass : classList) {
            if (categoryClass.isAnnotationPresent(CategoryInfo.class)) {
                CategoryInfo categoryInfo = categoryClass.getAnnotation(CategoryInfo.class);
                String categoryName = categoryClass.getSimpleName().toLowerCase(Locale.ROOT);
                String entryName = categoryInfo.entryName();

                List<Option<?>> options = Lists.newArrayList();

                for (Field field : categoryClass.getFields()) {
                    if (field.isAnnotationPresent(BugInfo.class)) {
                        BugInfo bugInfo = field.getAnnotation(BugInfo.class);
                        options.add(new BugOption(field, entryName, bugInfo.mixin(), bugInfo.id()));

                    } else if (field.isAnnotationPresent(TweakInfo.class)) {
                        TweakInfo tweakInfo = field.getAnnotation(TweakInfo.class);
                        options.add(new TweakOption(field, entryName, tweakInfo.mixin()));

                    } else if (field.isAnnotationPresent(KeybindInfo.class)) {
                        KeybindInfo keybindInfo = field.getAnnotation(KeybindInfo.class);
                        options.add(new KeybindOption(field, entryName, keybindInfo.mixin(), keybindInfo.modifier()));
                    }
                }

                CATEGORIES.add(new Category(categoryName, options.toArray(Option[]::new)));
            }
        }
    }

    public static void loadOptions() {
        Path configPath = getConfigPath();

        try {
            if (!Files.exists(configPath)) {
                saveOptionValues();
            }

            if (Files.exists(configPath)) {
                BufferedReader reader = Files.newBufferedReader(configPath);
                JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();

                for (Option<?> option : OPTIONS) {
                    JsonElement jsonElement = json.get(option.name());
                    option.load(jsonElement);
                }
            }
        } catch (Throwable e) {
            KeybindBugFixes.LOGGER.error("Couldn't load " + KeybindBugFixes.MOD_NAME + " configuration file");
        }
    }

    public static void initOptionValues() {
        for (Option<?> option : OPTIONS) {
            option.initValue();
        }
    }

    public static void loadOptionValues() {
        for (Option<?> option : OPTIONS) {
            if (!option.loadValue()) {
                KeybindBugFixes.LOGGER.error("Couldn't load "
                        + KeybindBugFixes.MOD_NAME
                        + " configuration entry "
                        + '"' + option.name() + '"'
                        + ", resetting");
            }
        }
    }

    public static void saveOptionValues() {
        Path configPath = getConfigPath();
        JsonObject json = new JsonObject();

        for (Option<?> option : OPTIONS) {
            option.write(json);
        }

        String jsonString = KeybindBugFixes.GSON.toJson(json);

        try (BufferedWriter writer = Files.newBufferedWriter(configPath)) {
            writer.write(jsonString);
        } catch (IOException e) {
            KeybindBugFixes.LOGGER.error("Couldn't save " + KeybindBugFixes.MOD_NAME + " configuration file");
        }
    }

    public static void preInit() {
        initOptions();
        loadOptions();

        for (Map.Entry<String, Set<Option<?>>> entry : OPTION_MIXIN_MAP.entrySet()) {
            String mixinName = entry.getKey();

            boolean allOptionsDisabled = true;
            for (Option<?> option : entry.getValue()) {
                if (option.isDisabled()) {
                    KeybindBugFixes.DISABLED_OPTION_NAMES.add(option.name());
                } else {
                    allOptionsDisabled = false;
                }
            }

            if (allOptionsDisabled) {
                KeybindBugFixes.DISABLED_MIXIN_NAMES.add(mixinName);
            }
        }
    }

    public static void init() {
        initOptionValues();
        loadOptionValues();
        saveOptionValues();
    }
}