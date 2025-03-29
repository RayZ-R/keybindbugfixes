package keybindbugfixes.mixin.fix_control_sticky_key_reset;

import keybindbugfixes.KeybindBugFixes;
import keybindbugfixes.config.Config;
import keybindbugfixes.mixin.KeyBindingAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.option.StickyKeyBinding;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin {
    @Inject(method = "handleInputEvents",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/Screen;hasControlDown()Z"))
    private void preventControlStickyKeysResetOnDropStack(CallbackInfo callbackInfo) {
        if (Screen.hasControlDown()) {
            KeybindBugFixes.revertStickyKeyBindings();
        }
    }

    @Inject(method = "doItemPick",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/Screen;hasControlDown()Z"))
    private void preventControlStickyKeysResetOnPickBlockWithNbt(CallbackInfo callbackInfo) {
        if (Screen.hasControlDown()) {
            KeybindBugFixes.revertStickyKeyBindings();
        }
    }
}