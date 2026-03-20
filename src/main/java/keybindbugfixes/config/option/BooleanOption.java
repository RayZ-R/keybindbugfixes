package keybindbugfixes.config.option;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.Nullable;

public class BooleanOption extends Option<Boolean> {
    public BooleanOption(String key, String mixin, boolean defaultValue,
                         @Nullable Integer bugNumber) {
        super(key, mixin, String.valueOf(defaultValue), bugNumber);
    }

    public void toggle() {
        this.value = !this.value;
    }

    @Override
    public Boolean parse(String string) {
        return Boolean.parseBoolean(string);
    }

    @Override
    public boolean fromJson(JsonElement jsonElement) {
        if (jsonElement.isJsonPrimitive() && jsonElement.getAsJsonPrimitive().isBoolean()) {
            this.value = jsonElement.getAsBoolean();
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void toJson(JsonObject json) {
        json.addProperty(this.key, this.value);
    }
}