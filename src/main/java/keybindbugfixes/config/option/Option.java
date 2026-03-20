package keybindbugfixes.config.option;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import keybindbugfixes.KeybindBugFixes;
import keybindbugfixes.MixinPlugin;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

public abstract class Option<T> {
    public final String key;
    public final String mixin;
    public final String labelKey;
    public final String descriptionKey;
    public final String defaultValueString;
    public final boolean isDisabled;

    @Nullable public final String bugId;
    @Nullable public final String link;

    public Text label;
    public Text description;

    public T defaultValue;
    public T value;

    public Option(String key, String mixin, String defaultValue, @Nullable Integer bugNumber) {
        this.key = key;
        this.mixin = mixin;
        this.defaultValueString = defaultValue;
        this.labelKey = KeybindBugFixes.MOD_ID + ".config." + key;
        this.descriptionKey = this.labelKey + ".description";

        this.bugId = bugNumber == null ? null : "MC-" + bugNumber;
        this.link = bugNumber == null ? null : "https://bugs.mojang.com/browse/" + this.bugId;

        this.isDisabled = MixinPlugin.isDisabled(this.mixin);
    }

    public void init() {
        this.label = (Text) (Object) Text.translatable(Option.this.labelKey);
        this.description = (Text) (Object) Text.translatable(Option.this.descriptionKey);

        this.defaultValue = this.parse(this.defaultValueString);
        this.value = this.defaultValue;
    }

    public void reset() {
        this.value = this.defaultValue;
    }

    public boolean isDefault() {
        return this.value.equals(this.defaultValue);
    }

    public abstract T parse(String string);

    public abstract boolean fromJson(JsonElement jsonElement);

    public abstract void toJson(JsonObject json);
}