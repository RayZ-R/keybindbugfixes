package keybindbugfixes.mixin.rebind_debug_keys;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.Window;
import keybindbugfixes.config.Config;
import net.minecraft.client.KeyboardHandler;
import net.minecraft.client.input.KeyEvent;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(KeyboardHandler.class)
public abstract class KeyboardHandlerMixin {
    @WrapOperation(
            method = "keyPress",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/mojang/blaze3d/platform/InputConstants;" +
                            "isKeyDown(Lcom/mojang/blaze3d/platform/Window;I)Z",
                    ordinal = 0
            )
    )
    private boolean fixGlError(Window window, int keycode, Operation<Boolean> original) {
        return keycode != GLFW.GLFW_KEY_UNKNOWN && original.call(window, keycode);
    }

    @ModifyConstant(method = "keyPress", constant = @Constant(intValue = InputConstants.KEY_F3))
    private int remapF3Key(int value) {
        if (Config.DEBUG_KEY.value.getType() == InputConstants.Type.MOUSE) {
            return GLFW.GLFW_KEY_UNKNOWN;
        }

        return Config.DEBUG_KEY.value.getValue();
    }

    @ModifyArg(
            method = "keyPress",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/KeyboardHandler;" +
                            "handleDebugKeys(Lnet/minecraft/client/input/KeyEvent;)Z"
            )
    )
    private KeyEvent remapF4Key(KeyEvent event) {
        if (event.key() == Config.GAME_MODE_CYCLE_KEY.value.getValue()) {
            return new KeyEvent(InputConstants.KEY_F4, GLFW.glfwGetKeyScancode(GLFW.GLFW_KEY_F4), event.modifiers());
        } else if (event.key() == InputConstants.KEY_F4) {
            return new KeyEvent(GLFW.GLFW_KEY_UNKNOWN, GLFW.glfwGetKeyScancode(GLFW.GLFW_KEY_UNKNOWN), event.modifiers());
        } else {
            return event;
        }
    }
}