package keybindbugfixes.mixin.rebind_debug_keys;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import keybindbugfixes.config.Config;
import net.minecraft.client.Keyboard;
import net.minecraft.client.input.KeyInput;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.Window;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(Keyboard.class)
public abstract class KeyboardMixin {
    @WrapOperation(
            method = "onKey",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/util/InputUtil;" +
                            "isKeyPressed(Lnet/minecraft/client/util/Window;I)Z",
                    ordinal = 0
            )
    )
    private boolean fixGlError(Window window, int code, Operation<Boolean> original) {
        return code != GLFW.GLFW_KEY_UNKNOWN && original.call(window, code);
    }

    @ModifyConstant(method = "onKey", constant = @Constant(intValue = InputUtil.GLFW_KEY_F3))
    private int remapF3Key(int value) {
        if (Config.DEBUG_KEY.value.getCategory() == InputUtil.Type.MOUSE) {
            return GLFW.GLFW_KEY_UNKNOWN;
        }

        return Config.DEBUG_KEY.value.getCode();
    }

    @ModifyArg(
            method = "onKey",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/Keyboard;" +
                            "processF3(Lnet/minecraft/client/input/KeyInput;)Z"
            )
    )
    private KeyInput remapF4Key(KeyInput input) {
        if (input.key() == Config.GAME_MODE_CYCLE_KEY.value.getCode()) {
            return new KeyInput(InputUtil.GLFW_KEY_F4, GLFW.glfwGetKeyScancode(GLFW.GLFW_KEY_F4), input.modifiers());
        } else if (input.key() == InputUtil.GLFW_KEY_F4) {
            return new KeyInput(GLFW.GLFW_KEY_UNKNOWN, GLFW.glfwGetKeyScancode(GLFW.GLFW_KEY_UNKNOWN), input.modifiers());
        } else {
            return input;
        }
    }
}