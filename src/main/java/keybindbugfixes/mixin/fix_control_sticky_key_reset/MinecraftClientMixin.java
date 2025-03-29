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
    @Unique
    private static void keybindbugfixes$toggleLeftControlStickyKeys() {
        if (Config.BugFixes.FIX_CONTROL_STICKY_KEY_RESET && Screen.hasControlDown()) {
            for (StickyKeyBinding stickyKeyBinding : KeybindBugFixes.STICKY_KEY_BINDINGS) {
                if (((KeyBindingAccessor) stickyKeyBinding).getBoundKey().getCode() == GLFW.GLFW_KEY_LEFT_CONTROL) {
                    stickyKeyBinding.setPressed(true);
                }
            }
        }
    }

    @Inject(method = "handleInputEvents",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/Screen;hasControlDown()Z"))
    private void preventControlStickyKeysResetOnDropStack(CallbackInfo callbackInfo) {
        keybindbugfixes$toggleLeftControlStickyKeys();
    }

    @Inject(method = "doItemPick",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/Screen;hasControlDown()Z"))
    private void preventControlStickyKeysResetOnPickBlockWithNbt(CallbackInfo callbackInfo) {
        keybindbugfixes$toggleLeftControlStickyKeys();
    }
}