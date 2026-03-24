package keybindbugfixes.mixin.rebind_debug_keys;

import keybindbugfixes.config.Config;
import net.minecraft.client.gui.screen.GameModeSwitcherScreen;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(GameModeSwitcherScreen.class)
public abstract class GameModeSwitcherScreenMixin {
    @ModifyConstant(method = "checkForClose", constant = @Constant(intValue = InputUtil.GLFW_KEY_F3))
    private int remapCloseKey(int value) {
        if (Config.DEBUG_KEY.type() == InputUtil.Type.MOUSE) {
            return GLFW.GLFW_KEY_UNKNOWN;
        }

        return Config.DEBUG_KEY.value.getCode();
    }

    @ModifyConstant(method = "keyPressed", constant = @Constant(intValue = InputUtil.GLFW_KEY_F4))
    private int remapCycleKey(int value) {
        if (Config.GAME_MODE_CYCLE_KEY.type() == InputUtil.Type.MOUSE) {
            return GLFW.GLFW_KEY_UNKNOWN;
        }

        return Config.GAME_MODE_CYCLE_KEY.value.getCode();
    }
}