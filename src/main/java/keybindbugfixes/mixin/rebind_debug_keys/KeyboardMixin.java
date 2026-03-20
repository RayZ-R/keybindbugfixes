package keybindbugfixes.mixin.rebind_debug_keys;

import keybindbugfixes.config.Config;
import net.minecraft.client.Keyboard;
import net.minecraft.client.input.KeyInput;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.Window;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;

@Mixin(Keyboard.class)
public abstract class KeyboardMixin {
    @Redirect(method = "onKey",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/util/InputUtil;isKeyPressed(Lnet/minecraft/client/util/Window;I)Z"))
    private boolean fixUnboundKeyError(Window window, int code) {
        return code != -1 && InputUtil.isKeyPressed(window, code);
    }

    @ModifyConstant(method = "onKey", constant = @Constant(intValue = GLFW.GLFW_KEY_F3))
    private int remapF3KeyBinding(int value) {
        return Config.DEBUG_KEY.value.getCode();
    }

    @ModifyArg(method = "onKey",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/Keyboard;processF3(Lnet/minecraft/client/input/KeyInput;)Z"))
    private KeyInput remapF4KeyBinding(KeyInput input) {
        if (input.key() == Config.GAME_MODE_CYCLE_KEY.value.getCode()) {
            return new KeyInput(GLFW.GLFW_KEY_F4, GLFW.glfwGetKeyScancode(GLFW.GLFW_KEY_F4), input.modifiers());
        } else {
            return input;
        }
    }
}