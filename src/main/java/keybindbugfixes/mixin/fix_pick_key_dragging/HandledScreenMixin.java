package keybindbugfixes.mixin.fix_pick_key_dragging;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
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
                    target = "Lnet/minecraft/client/option/KeyBinding;" +
                            "matchesKey(Lnet/minecraft/client/input/KeyInput;)Z",
                    ordinal = 1
            )
    )
    private boolean disablePickItemKey(boolean original) {
        return Config.FIX_PICK_KEY_DRAGGING.value ? false : original;
    }
}