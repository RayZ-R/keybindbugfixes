package keybindbugfixes.mixin.fix_modifier_sticky_key;

import keybindbugfixes.StickyKeyStates;
import keybindbugfixes.config.Config;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin {
    @Inject(
            method = "handleInputEvents",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/screen/Screen;hasControlDown()Z"
            )
    )
    private void revertDropStackModifierKey(CallbackInfo callbackInfo) {
        if (Config.FIX_MODIFIER_STICKY_KEY.value) {
            StickyKeyStates.revertDropStackModifier();
        }
    }

    @Inject(method = "doItemPick", at = @At(value = "HEAD"))
    private void revertPickBlockWithNbtModifierKey(CallbackInfo callbackInfo) {
        if (Config.FIX_MODIFIER_STICKY_KEY.value && Screen.hasControlDown()) {
            StickyKeyStates.revertControlModifier();
        }
    }
}