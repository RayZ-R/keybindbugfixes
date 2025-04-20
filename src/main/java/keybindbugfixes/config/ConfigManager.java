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
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

public class ConfigManager {
    public static class OptionInfo {
        protected final Field field;
        protected final Annotation type;
        protected JsonElement jsonElement;

        protected final String name;
        protected final String translationKey;
        protected final String mixinName;
        protected final String entryName;
        protected final Integer bugId;

        protected boolean isDisabled;

        public OptionInfo(Field field, String entryName, String mixinName) {
            this.field = field;
            this.type = field.getAnnotations()[0];

            String fieldName = field.getName().toLowerCase(Locale.ROOT);
            this.name = entryName + "." + fieldName;
            this.translationKey = KeybindBugFixes.MOD_ID + ".config." + this.name;
            this.mixinName = mixinName.isEmpty() ? fieldName : mixinName;
            this.entryName = entryName;

            OPTION_MIXIN_MAP.put(this.mixinName, this);
            OPTION_INFOS.add(this);

            this.bugId = -1;
        }

        public OptionInfo(Field field, String entryName, String mixinName, int bugId) {
            this.field = field;
            this.type = field.getAnnotations()[0];

            String fieldName = field.getName().toLowerCase(Locale.ROOT);
            this.name = entryName + "." + fieldName;
            this.translationKey = KeybindBugFixes.MOD_ID + ".config." + this.name;
            this.mixinName = mixinName.isEmpty() ? fieldName : mixinName;
            this.entryName = entryName;

            OPTION_MIXIN_MAP.put(this.mixinName, this);
            OPTION_INFOS.add(this);

            this.bugId = bugId;
        }

        public Annotation type() {
            return this.type;
        }

        public String name() {
            return this.name;
        }

        public String mixinName() {
            return this.mixinName;
        }

        public int bugId() {
            return this.bugId;
        }

        public boolean isDisabled() {
            return this.isDisabled;
        }

        protected boolean writeDisabledState(JsonObject json) {
            if (this.isDisabled) {
                json.add(this.name, null);
                return true;
            } else {
                return false;
            }
        }

        protected void loadFromJson(JsonElement jsonElement) {
            if (jsonElement != null) {
                this.isDisabled = jsonElement.isJsonNull();
            }

            this.jsonElement = jsonElement;
        }
    }

    public abstract static class Option<T> {
        protected final OptionInfo optionInfo;

        protected final Consumer<T> changeCallback;
        protected final T defaultValue;
        protected T value;

        private Consumer<T> getChangeCallback(Field field) {
            return value -> {
                try {
                    field.set(null, value);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            };
        }

        private T getDefaultValue(Field field, Class<T> valueType) {
            T defaultValue;

            try {
                defaultValue = valueType.cast(field.get(valueType));
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }

            return defaultValue;
        }

        public Option(OptionInfo optionInfo, Class<T> valueType) {
            this.optionInfo = optionInfo;

            this.changeCallback = getChangeCallback(this.field());
            this.defaultValue = getDefaultValue(this.field(), valueType);
            this.value = this.defaultValue;

            OPTIONS.add(this);
        }

        public Field field() {
            return this.optionInfo.field;
        }

        public JsonElement jsonElement() {
            return this.optionInfo.jsonElement;
        }

        public String name() {
            return this.optionInfo.name;
        }

        public String translationKey() {
            return this.optionInfo.translationKey;
        }

        public String entryName() {
            return this.optionInfo.entryName;
        }

        public boolean isDisabled() {
            return this.optionInfo.isDisabled;
        }

        protected boolean writeDisabledState(JsonObject json) {
            return this.optionInfo.writeDisabledState(json);
        }


        public void setValue(T value) {
            if (this.value != value) {
                this.value = value;
                changeCallback.accept(this.value);
            }
        }

        public void resetValue() {
            this.setValue(this.defaultValue);
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

        public abstract void writeToJson(JsonObject json);

        public abstract boolean loadFromJson(JsonElement jsonPrimitive);
    }

    public static class BugOption extends TweakOption {
        @Nullable private final String link;

        public BugOption(OptionInfo optionInfo) {
            super(optionInfo);

            if (this.field().isAnnotationPresent(BugInfo.class)) {
                int bugId = this.field().getAnnotation(BugInfo.class).id();

                if (bugId != -1) {
                    this.link = "https://bugs.mojang.com/browse/MC-" + bugId;
                } else {
                    this.link = null;
                }
            } else {
                this.link = null;
            }
        }

        public void openLink(Screen parent) {
            if (this.link != null) {
                ConfirmLinkScreen.open(parent, this.link);
            }
        }

        public @Nullable String link() {
            return this.link;
        }
    }

    public static class TweakOption extends Option<Boolean> {
        private final Function<Boolean, Text> buttonTextFactory;
        private final Function<Boolean, Tooltip> tooltipFactory;

        public TweakOption(OptionInfo optionInfo) {
            super(optionInfo, Boolean.class);

            this.buttonTextFactory = value -> Text.translatable(value ? "gui.yes" : "gui.no")
                    .formatted(value ? Formatting.GREEN : Formatting.RED);

            this.tooltipFactory =value -> Tooltip.of(Text.translatable(
                    this.translationKey() + ".tooltip"));
        }

        @Override
        public void writeToJson(JsonObject json) {
            if (!this.writeDisabledState(json)) {
                json.addProperty(this.name(), this.value);
            }
        }

        @Override
        public boolean loadFromJson(JsonElement jsonElement) {
            if (this.isDisabled()) {
                return true;
            } else if (jsonElement.isJsonPrimitive() && jsonElement.getAsJsonPrimitive().isBoolean()) {
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

        public KeybindOption(OptionInfo optionInfo) {
            super(optionInfo, InputUtil.Key.class);

            String modifierString = this.field().getAnnotation(KeybindInfo.class).modifier();
            boolean processed = false;

            if (!modifierString.isEmpty()) {
                String modifierName = this.entryName() + "." + modifierString.toLowerCase(Locale.ROOT);

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
        public void writeToJson(JsonObject json) {
            if (!this.writeDisabledState(json)) {
                json.addProperty(this.name(), this.value.getTranslationKey());
            }
        }

        @Override
        public boolean loadFromJson(JsonElement jsonElement) {
            if (this.isDisabled()) {
                return true;
            } else if (jsonElement.isJsonPrimitive() && jsonElement.getAsJsonPrimitive().isString()) {
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

    public static class InfoCategory {
        private final String translationKey;
        private final List<OptionInfo> options;

        public InfoCategory(String categoryName, OptionInfo[] options) {
            this.translationKey = KeybindBugFixes.MOD_ID + ".config.category." + categoryName;
            this.options = Lists.newArrayList(options);
        }

        public String translationKey() {
            return this.translationKey;
        }

        public List<OptionInfo> options() {
            return this.options;
        }
    }

    public static class Category {
        private final InfoCategory infoCategory;
        private final List<Option<?>> options;

        public Category(InfoCategory infoCategory, Option<?>[] options) {
            this.infoCategory = infoCategory;
            this.options = Lists.newArrayList(options);
        }

        public String translationKey() {
            return this.infoCategory.translationKey();
        }

        public List<Option<?>> options() {
            return this.options;
        }
    }

    public static class MixinMap {
        public final Map<String, Set<OptionInfo>> map = Maps.newHashMap();

        public void put(String key, OptionInfo optionInfo) {
            this.map.putIfAbsent(key, Sets.newHashSet());
            this.map.get(key).add(optionInfo);
        }
    }

    public static final List<InfoCategory> INFO_CATEGORIES = Lists.newArrayList();
    public static final List<Category> CATEGORIES = Lists.newArrayList();

    public static final List<OptionInfo> OPTION_INFOS = Lists.newArrayList();
    public static final List<Option<?>> OPTIONS = Lists.newArrayList();

    public static final List<KeybindOption> KEYBIND_OPTIONS = Lists.newArrayList();
    public static final MixinMap OPTION_MIXIN_MAP = new MixinMap();

    public static void initOptionInfos() {
        Class<?>[] classes = Config.class.getClasses();
        List<Class<?>> classList = Arrays.asList(classes);
        Collections.reverse(classList);

        for (Class<?> categoryClass : classList) {
            if (categoryClass.isAnnotationPresent(CategoryInfo.class)) {
                CategoryInfo categoryInfo = categoryClass.getAnnotation(CategoryInfo.class);
                String categoryName = categoryClass.getSimpleName().toLowerCase(Locale.ROOT);
                String entryName = categoryInfo.entryName();

                List<OptionInfo> options = Lists.newArrayList();

                for (Field field : categoryClass.getFields()) {
                    if (field.isAnnotationPresent(BugInfo.class)) {
                        BugInfo bugInfo = field.getAnnotation(BugInfo.class);
                        options.add(new OptionInfo(field, entryName, bugInfo.mixin(), bugInfo.id()));

                    } else if (field.isAnnotationPresent(TweakInfo.class)) {
                        TweakInfo tweakInfo = field.getAnnotation(TweakInfo.class);
                        options.add(new OptionInfo(field, entryName, tweakInfo.mixin()));

                    } else if (field.isAnnotationPresent(KeybindInfo.class)) {
                        KeybindInfo keybindInfo = field.getAnnotation(KeybindInfo.class);
                        options.add(new OptionInfo(field, entryName, keybindInfo.mixin()));
                    }
                }

                INFO_CATEGORIES.add(new InfoCategory(categoryName, options.toArray(OptionInfo[]::new)));
            }
        }
    }

    public static void initOptions() {
        for (InfoCategory infoCategory : INFO_CATEGORIES) {
            List<Option<?>> options = Lists.newArrayList();

            for (OptionInfo option : infoCategory.options()) {
                if (option.type() instanceof BugInfo) {
                    options.add(new BugOption(option));
                } else if (option.type() instanceof TweakInfo) {
                    options.add(new TweakOption(option));
                } else if (option.type() instanceof KeybindInfo) {
                    options.add(new KeybindOption(option));
                }
            }

            CATEGORIES.add(new Category(infoCategory, options.toArray(Option[]::new)));
        }
    }

    private static Path getConfigPath() {
        return FabricLoader.getInstance().getConfigDir().resolve(KeybindBugFixes.MOD_ID + ".json");
    }

    private static void loadEntryError(String entryName) {
        KeybindBugFixes.LOGGER.error(
                "Couldn't load " + KeybindBugFixes.MOD_NAME + " configuration entry \"{}\", resetting", entryName);
    }

    public static void loadOptionInfos() {
        Path configPath = getConfigPath();

        try {
            if (!Files.exists(configPath)) {
                saveOptions();
            }

            if (Files.exists(configPath)) {
                BufferedReader reader = Files.newBufferedReader(configPath);
                JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();

                for (OptionInfo optionInfo : OPTION_INFOS) {
                    JsonElement jsonElement = json.get(optionInfo.name());
                    optionInfo.loadFromJson(jsonElement);
                }
            }
        } catch (Throwable e) {
            KeybindBugFixes.LOGGER.error("Couldn't load " + KeybindBugFixes.MOD_NAME + " configuration file");
        }
    }

    public static void loadOptions() {
        for (Option<?> option : OPTIONS) {
            JsonElement jsonElement = option.jsonElement();

            if (jsonElement != null) {
                if (!option.loadFromJson(jsonElement)) {
                    loadEntryError(option.name());
                }
            } else {
                loadEntryError(option.name());
            }
        }
    }

    public static void saveOptions() {
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

    public static void preInit() {
        initOptionInfos();
        loadOptionInfos();

        for (Map.Entry<String, Set<ConfigManager.OptionInfo>> entry : OPTION_MIXIN_MAP.map.entrySet()) {
            String mixinName = entry.getKey();

            boolean allOptionsDisabled = true;
            for (ConfigManager.OptionInfo optionInfo : entry.getValue()) {
                if (optionInfo.isDisabled()) {
                    KeybindBugFixes.DISABLED_OPTION_NAMES.add(optionInfo.name());
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
        initOptions();
        loadOptions();
        saveOptions();
    }
}