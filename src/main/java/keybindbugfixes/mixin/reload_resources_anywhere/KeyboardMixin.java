package keybindbugfixes.mixin.reload_resources_anywhere;

import com.llamalad7.mixinextras.sugar.Local;
import keybindbugfixes.KeybindBugFixes;
import keybindbugfixes.config.Config;
import net.minecraft.client.Keyboard;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Keyboard.class)
public abstract class KeyboardMixin {
    @Shadow @Final private MinecraftClient client;
    @Shadow private boolean switchF3State;

    @Shadow protected abstract void debugLog(String key, Object... args);

    @Inject(method = "onKey",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/util/InputUtil;isKeyPressed(JI)Z",
                    shift = At.Shift.BY,
                    by = 2,
                    ordinal = 0),
            cancellable = true)
    private void handleReloadResourcesKeybind(long window, int keycode, int scancode, int action, int modifiers,
                                              CallbackInfo callbackInfo, @Local boolean f3Pressed) {
        InputUtil.Key key = InputUtil.fromKeyCode(keycode, scancode);

        if (Config.Tweaks.RELOAD_RESOURCES_ANYWHERE
                && f3Pressed
                && action == GLFW.GLFW_PRESS
                && key.equals(KeybindBugFixes.getReloadResourcesKey())) {
            if (this.client.getOverlay() == null) {
                if (this.client.currentScreen == null) {
                    this.debugLog("debug.reload_resourcepacks.message");
                }

                this.client.reloadResources();
            }

            if (this.client.currentScreen == null) {
                this.switchF3State = true;
            }

            callbackInfo.cancel();
        }
    }
}