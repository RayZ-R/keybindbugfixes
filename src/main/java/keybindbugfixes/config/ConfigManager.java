package keybindbugfixes.config;

import com.google.common.collect.Lists;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;
import java.util.function.Function;

public class ConfigManager {
    public abstract static class Option<T> {
        protected final String translationKey;
        protected final String name;
        protected final T defaultValue;
        protected T value;

        protected final Consumer<T> changeCallback;

        public Option(String name, String translationKey, T defaultValue, Consumer<T> changeCallback) {
            this.defaultValue = defaultValue;
            this.translationKey = translationKey;
            this.value = defaultValue;
            this.name = name;

            this.changeCallback = changeCallback;
        }

        public void setValue(T value) {
            if (this.value != value) {
                this.value = value;
                changeCallback.accept(this.value);
            }
        }

        public void resetValue() {
            setValue(this.defaultValue);
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

        public String name() {
            return this.name;
        }

        public String translationKey() {
            return this.translationKey;
        }

        abstract void writeToJson(JsonObject json);

        abstract boolean loadFromJson(JsonPrimitive jsonPrimitive);
    }

    public static class BugOption extends TweakOption {
        @Nullable private final String link;

        public BugOption(String name,
                         String translationKey,
                         boolean defaultValue,
                         @Nullable String link,
                         Consumer<Boolean> changeCallback) {
            super(name, translationKey, defaultValue, changeCallback);
            this.link = link;
        }

        public void openLink(Screen parent) {
            if (this.link != null) {
                ConfirmLinkScreen.open(parent, this.link);
            }
        }

        public @Nullable String link() {
            return link;
        }
    }

    public static class TweakOption extends Option<Boolean> {
        private final Function<Boolean, Text> buttonTextFactory;
        private final Function<Boolean, Tooltip> tooltipFactory;

        public TweakOption(String name,
                         String translationKey,
                         boolean defaultValue,
                         Consumer<Boolean> changeCallback) {
            super(name, translationKey, defaultValue, changeCallback);

            this.buttonTextFactory = value -> Text.translatable(value ? "gui.yes" : "gui.no")
                    .formatted(value ? Formatting.GREEN : Formatting.RED);

            this.tooltipFactory =value -> Tooltip.of(Text.translatable(translationKey + ".tooltip"));
        }

        @Override
        public void writeToJson(JsonObject json) {
            json.addProperty(this.name, this.value);
        }

        @Override
        public boolean loadFromJson(JsonPrimitive jsonPrimitive) {
            if (jsonPrimitive.isBoolean()) {
                this.setValue(jsonPrimitive.getAsBoolean());
                return true;
            } else {
                return false;
            }
        }

        public void toggleValue() {
            this.setValue(!this.value);
        }

        public Text buttonText() {
            return buttonTextFactory.apply(this.value);
        }

        public Tooltip tooltip() {
            return tooltipFactory.apply(this.value);
        }
    }

    public static class KeybindOption extends Option<InputUtil.Key> {
        @Nullable private KeybindOption modifier;

        public KeybindOption(String name,
                             String translationKey,
                             InputUtil.Key defaultValue,
                             @Nullable String modifierName,
                             Consumer<InputUtil.Key> changeCallback) {
            super(name, translationKey, defaultValue, changeCallback);

            this.modifier = null;
            if (modifierName != null) {
                for (KeybindOption option : KEYBIND_OPTIONS) {
                    if (option.name.equals(modifierName)) {
                        this.modifier = option;
                        break;
                    }
                }
            }
        }

        @Override
        public void writeToJson(JsonObject json) {
            json.addProperty(this.name, this.value.getTranslationKey());
        }

        @Override
        public boolean loadFromJson(JsonPrimitive jsonPrimitive) {
            if (jsonPrimitive.isString()) {
                try {
                    this.setValue(InputUtil.fromTranslationKey(jsonPrimitive.getAsString()));
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

        public Category(String name, String translationKey, Option<?>[] options) {
            this.translationKey = translationKey;
            this.options = Lists.newArrayList(options);
        }

        public List<Option<?>> options() {
            return this.options;
        }

        public String translationKey() {
            return this.translationKey;
        }
    }

    public static class OptionHelper<T> {
        private final Class<?> type;
        private final String name;
        private final String translationKey;
        private final T defaultValue;
        private final Consumer<T> changeCallback;

        public OptionHelper(Class<?> type, Field field, String categoryEntryName) {
            this.type = type;

            this.name = getName(field, categoryEntryName);
            this.translationKey = getTranslationKey(name);
            this.defaultValue = this.getDefaultValue(field);
            this.changeCallback = this.getChangeCallback(field);
        }

        public String name() {
            return this.name;
        }

        public String translationKey() {
            return this.translationKey;
        }

        public T defaultValue() {
            return this.defaultValue;
        }

        public Consumer<T> changeCallback() {
            return this.changeCallback;
        }

        private static String getName(Field field, String categoryEntryName) {
            return categoryEntryName + "." + field.getName().toLowerCase(Locale.ROOT);
        }

        private static String getTranslationKey(String name) {
            return KeybindBugFixes.MOD_ID + ".config." + name;
        }

        @SuppressWarnings("unchecked")
        private T getDefaultValue(Field field) {
            T defaultValue;

            try {
                defaultValue = (T) field.get(type);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }

            return defaultValue;
        }

        private Consumer<T> getChangeCallback(Field field) {
            return value -> {
                try {
                    field.set(null, value);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            };
        }
    }

    public static final List<Option<?>> OPTIONS = Lists.newArrayList();
    public static final List<Category> CATEGORIES = Lists.newArrayList();
    public static final List<KeybindOption> KEYBIND_OPTIONS = Lists.newArrayList();

    private static BugOption newBugOption(Field field, String categoryEntryName) {
        OptionHelper<Boolean> helper = new OptionHelper<>(Boolean.class, field, categoryEntryName);

        String link;

        if (field.isAnnotationPresent(BugInfo.class)) {
            int bugId = field.getAnnotation(BugInfo.class).id();

            if (bugId != -1) {
                link = "https://bugs.mojang.com/browse/MC-" + bugId;
            } else {
                link = null;
            }
        } else {
            link = null;
        }

        BugOption option = new BugOption(
                helper.name(), helper.translationKey(), helper.defaultValue(), link, helper.changeCallback());

        OPTIONS.add(option);
        return option;
    }

    private static TweakOption newTweakOption(Field field, String categoryEntryName) {
        OptionHelper<Boolean> helper = new OptionHelper<>(Boolean.class, field, categoryEntryName);

        TweakOption option = new TweakOption(
                helper.name(), helper.translationKey(), helper.defaultValue(), helper.changeCallback());

        OPTIONS.add(option);
        return option;
    }

    private static KeybindOption newKeybindOption(Field field, String categoryEntryName) {
        OptionHelper<InputUtil.Key> helper = new OptionHelper<>(InputUtil.Key.class, field, categoryEntryName);

        String modifierName;

        if (field.isAnnotationPresent(KeybindInfo.class)) {
            String string = field.getAnnotation(KeybindInfo.class).modifier();

            if (!string.isEmpty()) {
                modifierName = categoryEntryName + "." + string.toLowerCase(Locale.ROOT);
            } else {
                modifierName = null;
            }
        } else {
            modifierName = null;
        }

        KeybindOption option = new KeybindOption(
                helper.name(), helper.translationKey(), helper.defaultValue(), modifierName, helper.changeCallback());

        KEYBIND_OPTIONS.add(option);
        OPTIONS.add(option);
        return option;
    }

    public static void addOptions() {
        Class<?>[] classes = Config.class.getClasses();
        List<Class<?>> classList = Arrays.asList(classes);
        Collections.reverse(classList);

        for (Class<?> categoryClass : classList) {
            if (categoryClass.isAnnotationPresent(CategoryInfo.class)) {
                CategoryInfo categoryInfo = categoryClass.getAnnotation(CategoryInfo.class);
                String name = categoryClass.getSimpleName().toLowerCase(Locale.ROOT);
                String entryName = categoryInfo.entryName();

                List<Option<?>> options = Lists.newArrayList();

                for (Field field : categoryClass.getFields()) {
                    if (field.isAnnotationPresent(BugInfo.class)) {
                        options.add(newBugOption(field, entryName));
                    } else if (field.isAnnotationPresent(TweakInfo.class)) {
                        options.add(newTweakOption(field, entryName));
                    } else if (field.isAnnotationPresent(KeybindInfo.class)) {
                        options.add(newKeybindOption(field, entryName));
                    }
                }

                String translationKey = KeybindBugFixes.MOD_ID + ".config.category." + name;
                CATEGORIES.add(new Category(name, translationKey, options.toArray(Option[]::new)));
            }
        }
    }

    private static Path getConfigPath() {
        return FabricLoader.getInstance().getConfigDir().resolve(KeybindBugFixes.MOD_ID + ".json");
    }

    private static void loadEntryError(String entryName) {
        KeybindBugFixes.LOGGER.error(
                "Couldn't load " + KeybindBugFixes.MOD_NAME + " configuration entry \"{}\", resetting", entryName);
    }

    public static void load() {
        Path configPath = getConfigPath();

        try {
            if (!Files.exists(configPath)) {
                save();
            }

            if (Files.exists(configPath)) {
                BufferedReader reader = Files.newBufferedReader(configPath);
                JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();

                for (Option<?> option : OPTIONS) {
                    JsonPrimitive jsonPrimitive = json.getAsJsonPrimitive(option.name());

                    if (jsonPrimitive != null) {
                        if (!option.loadFromJson(jsonPrimitive)) {
                            loadEntryError(option.name());
                        }
                    } else {
                        loadEntryError(option.name());
                    }
                }
            }
        } catch (Throwable e) {
            KeybindBugFixes.LOGGER.error("Couldn't load " + KeybindBugFixes.MOD_NAME + " configuration file");
        }
    }

    public static void save() {
        Path configPath = getConfigPath();
        JsonObject json = new JsonObject();

        for (Option<?> option : OPTIONS) {
            option.writeToJson(json);
        }

        String jsonString = KeybindBugFixes.GSON.toJson(json);

        try (BufferedWriter writer = Files.newBufferedWriter(configPath)) {
            writer.write(jsonString);
        } catch (IOException e) {
            KeybindBugFixes.LOGGER.error("Couldn't save " + KeybindBugFixes.MOD_NAME + " configuration file");
        }
    }

    public static void init() {
        addOptions();
        load();
        save();
    }
}