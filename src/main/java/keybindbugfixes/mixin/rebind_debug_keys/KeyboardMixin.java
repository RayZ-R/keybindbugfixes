package keybindbugfixes.mixin.rebind_debug_keys;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import keybindbugfixes.config.Config;
import net.minecraft.client.Keyboard;
import net.minecraft.client.util.InputUtil;
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
                    target = "Lnet/minecraft/client/util/InputUtil;isKeyPressed(JI)Z",
                    ordinal = 0
            )
    )
    private boolean fixGlError(long handle, int code, Operation<Boolean> original) {
        return code != GLFW.GLFW_KEY_UNKNOWN && original.call(handle, code);
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
                    target = "Lnet/minecraft/client/Keyboard;processF3(I)Z"
            )
    )
    private int remapF4Key(int key) {
        if (key == Config.GAME_MODE_CYCLE_KEY.value.getCode()) {
            return InputUtil.GLFW_KEY_F4;
        } else if (key == InputUtil.GLFW_KEY_F4) {
            return GLFW.GLFW_KEY_UNKNOWN;
        } else {
            return key;
        }
    }
}