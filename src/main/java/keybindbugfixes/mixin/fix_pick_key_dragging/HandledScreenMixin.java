package keybindbugfixes.mixin.fix_pick_key_dragging;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import keybindbugfixes.config.Config;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(HandledScreen.class)
public abstract class HandledScreenMixin {
    @ModifyExpressionValue(
            method = "keyPressed",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/option/KeyBinding;matchesKey(II)Z",
                    ordinal = 1
            )
    )
    private boolean disablePickItemKey(boolean original) {
        return Config.FIX_PICK_KEY_DRAGGING.value ? false : original;
    }

    @ModifyReturnValue(method = "keyPressed", at = @At("TAIL"))
    private boolean defaultToNotProcessed(boolean original) {
        return Config.FIX_PICK_KEY_DRAGGING.value ? false : original;
    }
}