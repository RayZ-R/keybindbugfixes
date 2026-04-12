package keybindbugfixes.mixin.reload_resources_anywhere;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.platform.InputConstants;
import keybindbugfixes.KeybindBugFixes;
import keybindbugfixes.config.Config;
import net.minecraft.client.KeyboardHandler;
import net.minecraft.client.Minecraft;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(KeyboardHandler.class)
public abstract class KeyboardHandlerMixin {
    @Shadow @Final private Minecraft minecraft;
    @Shadow private boolean handledDebugKey;

    @Shadow protected abstract void debugFeedbackTranslated(String key);

    @Inject(
            method = "keyPress",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/client/Minecraft;screen:Lnet/minecraft/client/gui/screens/Screen;",
                    shift = At.Shift.AFTER,
                    ordinal = 0
            ),
            cancellable = true
    )
    private void handleReloadResourcesKey(long window, int keycode, int scancode, int action, int modifiers,
                                          CallbackInfo callbackInfo, @Local boolean f3Pressed) {
        InputConstants.Key key = InputConstants.getKey(keycode, scancode);

        if (Config.RELOAD_RESOURCES_ANYWHERE.value
                && f3Pressed
                && action == GLFW.GLFW_PRESS
                && key.equals(KeybindBugFixes.getReloadResourcesKey())) {
            if (this.minecraft.getOverlay() == null) {
                if (this.minecraft.screen == null) {
                    this.debugFeedbackTranslated("debug.reload_resourcepacks.message");
                }

                this.minecraft.reloadResourcePacks();
            }

            if (this.minecraft.screen == null) {
                this.handledDebugKey = true;
            }

            callbackInfo.cancel();
        }
    }
}