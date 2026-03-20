package keybindbugfixes.mixin.fix_screen_sticky_key_reset;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import keybindbugfixes.config.Config;
import net.minecraft.client.option.StickyKeyBinding;
import net.minecraft.client.util.InputUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(StickyKeyBinding.class)
public abstract class StickyKeyBindingMixin {
    @ModifyExpressionValue(method = "shouldRestoreOnScreenClose",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/util/InputUtil$Key;" +
                            "getCategory()Lnet/minecraft/client/util/InputUtil$Type;"))
    private InputUtil.Type modifyInputType(InputUtil.Type original) {
        return Config.FIX_SCREEN_STICKY_KEY_RESET.value ? InputUtil.Type.KEYSYM : original;
    }
}