package keybindbugfixes.mixin.fix_modifier_toggle_control;

import keybindbugfixes.KeybindBugFixes;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin {
    @Inject(method = "handleInputEvents",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/Screen;hasControlDown()Z"))
    private void preventToggleControlOnDropStack(CallbackInfo callbackInfo) {
        if (Screen.hasControlDown()) {
            KeybindBugFixes.revertStickyKeyBindings();
        }
    }

    @Inject(method = "doItemPick",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/Screen;hasControlDown()Z"))
    private void preventToggleControlOnPickBlockWithNbt(CallbackInfo callbackInfo) {
        if (Screen.hasControlDown()) {
            KeybindBugFixes.revertStickyKeyBindings();
        }
    }
}