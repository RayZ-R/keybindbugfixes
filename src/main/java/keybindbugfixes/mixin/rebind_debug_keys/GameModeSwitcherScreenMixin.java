package keybindbugfixes.mixin.rebind_debug_keys;

import com.mojang.blaze3d.platform.InputConstants;
import keybindbugfixes.config.Config;
import net.minecraft.client.gui.screens.debug.GameModeSwitcherScreen;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(GameModeSwitcherScreen.class)
public abstract class GameModeSwitcherScreenMixin {
    @ModifyConstant(method = "checkToClose", constant = @Constant(intValue = InputConstants.KEY_F3))
    private int remapCloseKey(int value) {
        if (Config.DEBUG_KEY.value.getType() == InputConstants.Type.MOUSE) {
            return GLFW.GLFW_KEY_UNKNOWN;
        }

        return Config.DEBUG_KEY.value.getValue();
    }

    @ModifyConstant(method = "keyPressed", constant = @Constant(intValue = InputConstants.KEY_F4))
    private int remapCycleKey(int value) {
        if (Config.GAME_MODE_CYCLE_KEY.value.getType() == InputConstants.Type.MOUSE) {
            return GLFW.GLFW_KEY_UNKNOWN;
        }

        return Config.GAME_MODE_CYCLE_KEY.value.getValue();
    }
}