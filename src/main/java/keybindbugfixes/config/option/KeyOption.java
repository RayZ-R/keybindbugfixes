package keybindbugfixes.config.option;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.blaze3d.platform.InputConstants;
import org.jetbrains.annotations.Nullable;

public class KeyOption extends Option<InputConstants.Key> {
    @Nullable public final KeyOption modifier;

    public KeyOption(String key, String mixin, String defaultValue,
                     @Nullable Integer bugNumber, @Nullable KeyOption modifier) {
        super(key, mixin, defaultValue, bugNumber);
        this.modifier = modifier;
    }

    public boolean isUnbound() {
        return this.value.equals(InputConstants.UNKNOWN);
    }

    @Override
    public InputConstants.Key parse(String string) {
        return InputConstants.getKey(string);
    }

    @Override
    public boolean fromJson(JsonElement jsonElement) {
        if (jsonElement.isJsonPrimitive() && jsonElement.getAsJsonPrimitive().isString()) {
            try {
                this.value = InputConstants.getKey(jsonElement.getAsString());
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
        json.addProperty(this.key, this.value.getName());
    }
}