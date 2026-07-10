package keybindbugfixes.mixin.add_keybind_duplicates.controlling;

import com.blamejared.controlling.client.FreeKeysList;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.platform.InputConstants;
import keybindbugfixes.config.Config;
import keybindbugfixes.config.option.KeyOption;
import net.minecraft.client.KeyMapping;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.function.Predicate;
import java.util.stream.Stream;

@Mixin(FreeKeysList.class)
public abstract class FreeKeysListMixin {
    @WrapOperation(
            method = "lambda$recalculate$3",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/util/stream/Stream;noneMatch(Ljava/util/function/Predicate;)Z"
            )
    )
    private boolean hideModdedDuplicates(Stream<KeyMapping> stream,
                                        Predicate<KeyMapping> predicate, Operation<Boolean> original,
                                        @Local(argsOnly = true, name = "input") InputConstants.Key key) {
        for (KeyOption option : Config.KEY_OPTIONS) {
            if (!option.isDisabled && option.modifier == null && key.equals(option.value)) {
                return false;
            }
        }

        return original.call(stream, predicate);
    }
}