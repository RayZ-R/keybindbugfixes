package keybindbugfixes.mixin.fix_pick_key_dragging;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import keybindbugfixes.config.Config;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(HandledScreen.class)
public abstract class HandledScreenMixin {
    @ModifyExpressionValue(method = "keyPressed",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/option/KeyBinding;" +
                            "matchesKey(Lnet/minecraft/client/input/KeyInput;)Z",
                    ordinal = 1))
    private boolean disablePickItemKeyInInventory(boolean original) {
        return Config.BugFixes.FIX_PICK_KEY_DRAGGING ? false : original;
    }

    @Inject(method = "keyPressed", at = @At("TAIL"), cancellable = true)
    private void disablePickKeyDraggingInSearch(CallbackInfoReturnable<Boolean> callbackInfo) {
        if (Config.BugFixes.FIX_PICK_KEY_DRAGGING) {
            callbackInfo.setReturnValue(false);
        }
    }
}