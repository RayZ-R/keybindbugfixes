package keybindbugfixes.mixin.fix_pick_key_dragging;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import keybindbugfixes.config.Config;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(AbstractContainerScreen.class)
public abstract class AbstractContainerScreenMixin {
    @ModifyExpressionValue(
            method = "keyPressed",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/KeyMapping;" +
                            "matches(Lnet/minecraft/client/input/KeyEvent;)Z",
                    ordinal = 1
            )
    )
    private boolean disablePickItemKey(boolean original) {
        return Config.FIX_PICK_KEY_DRAGGING.value ? false : original;
    }
}