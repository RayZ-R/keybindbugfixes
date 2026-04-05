package keybindbugfixes.config.option;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.client.util.InputUtil;
import org.jetbrains.annotations.Nullable;

public class KeybindOption extends Option<InputUtil.Key> {
    @Nullable public final KeybindOption modifier;

    public KeybindOption(String key, String mixin, String defaultValue,
                         @Nullable Integer bugNumber, @Nullable KeybindOption modifier) {
        super(key, mixin, defaultValue, bugNumber);
        this.modifier = modifier;
    }

    public boolean isUnbound() {
        return this.value.equals(InputUtil.UNKNOWN_KEY);
    }

    @Override
    public InputUtil.Key parse(String string) {
        return InputUtil.fromTranslationKey(string);
    }

    @Override
    public boolean fromJson(JsonElement jsonElement) {
        if (jsonElement.isJsonPrimitive() && jsonElement.getAsJsonPrimitive().isString()) {
            try {
                this.value = InputUtil.fromTranslationKey(jsonElement.getAsString());
                return true;
            } catch (Throwable e) {
                return false;
            }
        } else {
            return false;
        }
    }

    @Override
    public void toJson(JsonObject json) {
        json.addProperty(this.key, this.value.getTranslationKey());
    }
}