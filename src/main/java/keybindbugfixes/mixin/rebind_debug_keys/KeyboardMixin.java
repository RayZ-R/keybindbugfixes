package keybindbugfixes.mixin.rebind_debug_keys;

import keybindbugfixes.config.Config;
import net.minecraft.client.Keyboard;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;

@Mixin(Keyboard.class)
public abstract class KeyboardMixin {
    @Redirect(method = "onKey",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/InputUtil;isKeyPressed(JI)Z"))
    private boolean fixUnboundKeyError(long handle, int code) {
        return code != -1 && InputUtil.isKeyPressed(handle, code);
    }

    @ModifyConstant(method = "onKey", constant = @Constant(intValue = GLFW.GLFW_KEY_F3))
    private int remapF3KeyBinding(int value) {
        return ((InputUtil.Key) Config.Keybinds.DEBUG).getCode();
    }

    @ModifyArg(method = "onKey",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Keyboard;processF3(I)Z"))
    private int remapF4KeyBinding(int key) {
        return key == ((InputUtil.Key) Config.Keybinds.GAME_MODE_CYCLE).getCode() ? GLFW.GLFW_KEY_F4 : key;
    }
}