package keybindbugfixes.mixin.fix_modifier_sticky_key;

import keybindbugfixes.ToggleKeyStates;
import keybindbugfixes.config.Config;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin {
    @Shadow public abstract boolean hasControlDown();

    @Inject(
            method = "handleKeybinds",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/Minecraft;hasControlDown()Z"
            )
    )
    private void revertDropStackModifierKey(CallbackInfo callbackInfo) {
        if (Config.FIX_MODIFIER_STICKY_KEY.value) {
            ToggleKeyStates.revertDropStackModifier();
        }
    }

    @Inject(method = "pickBlock", at = @At(value = "HEAD"))
    private void revertPickBlockWithNbtModifierKey(CallbackInfo callbackInfo) {
        if (Config.FIX_MODIFIER_STICKY_KEY.value && this.hasControlDown()) {
            ToggleKeyStates.revertControl();
        }
    }
}