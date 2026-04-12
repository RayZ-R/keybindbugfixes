package keybindbugfixes.mixin.fix_screen_sticky_key_reset;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.mojang.blaze3d.platform.InputConstants;
import keybindbugfixes.config.Config;
import net.minecraft.client.ToggleKeyMapping;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ToggleKeyMapping.class)
public abstract class ToggleKeyMappingMixin {
    @ModifyExpressionValue(
            method = "shouldRestoreStateOnScreenClosed",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/mojang/blaze3d/platform/InputConstants$Key;" +
                            "getType()Lcom/mojang/blaze3d/platform/InputConstants$Type;"
            )
    )
    private InputConstants.Type skipKeyTypeCondition(InputConstants.Type original) {
        return Config.FIX_SCREEN_STICKY_KEY_RESET.value ? InputConstants.Type.KEYSYM : original;
    }
}