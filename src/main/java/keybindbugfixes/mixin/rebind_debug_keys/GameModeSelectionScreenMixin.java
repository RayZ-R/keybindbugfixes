package keybindbugfixes.mixin.rebind_debug_keys;

import keybindbugfixes.config.Config;
import net.minecraft.client.gui.screen.GameModeSelectionScreen;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(GameModeSelectionScreen.class)
public abstract class GameModeSelectionScreenMixin {
    @ModifyConstant(method = "checkForClose", constant = @Constant(intValue = GLFW.GLFW_KEY_F3))
    private int remapCloseKey(int value) {
        return ((InputUtil.Key) Config.Keybinds.DEBUG).getCode();
    }

    @ModifyConstant(method = "keyPressed", constant = @Constant(intValue = GLFW.GLFW_KEY_F4))
    private int remapCycleKey(int value) {
        return ((InputUtil.Key) Config.Keybinds.GAME_MODE_CYCLE).getCode();
    }
}