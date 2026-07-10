package keybindbugfixes.mixin.add_keybind_duplicates.controlling;

import com.blamejared.controlling.api.DisplayMode;
import com.blamejared.controlling.api.entries.IKeyEntry;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import keybindbugfixes.config.Config;
import keybindbugfixes.config.option.KeyOption;
import net.minecraft.client.KeyMapping;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(DisplayMode.class)
public abstract class DisplayModeMixin {
    @ModifyReturnValue(method = "lambda$static$2", at = @At("TAIL"))
    private static boolean showModdedDuplicates(boolean original,
                                               @Local(argsOnly = true, name = "keyEntry") IKeyEntry keyEntry) {
        KeyMapping keyMapping = keyEntry.getKey();
        if (keyMapping.isUnbound()) return original;

        for (KeyOption option : Config.KEY_OPTIONS) {
            if (!option.isDisabled && option.modifier == null && keyMapping.matches(option.value)) {
                return true;
            }
        }

        return original;
    }
}