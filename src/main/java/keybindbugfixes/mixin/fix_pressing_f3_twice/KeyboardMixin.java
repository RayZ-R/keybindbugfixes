package keybindbugfixes.mixin.fix_pressing_f3_twice;

import keybindbugfixes.config.Config;
import net.minecraft.client.Keyboard;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.GameModeSelectionScreen;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(Keyboard.class)
public abstract class KeyboardMixin {
    @Shadow @Final private MinecraftClient client;

    @ModifyVariable(method = "onKey", at = @At("STORE"), ordinal = 2)
    private boolean fixF3State(boolean original) {
        return original || (Config.BugFixes.FIX_PRESSING_F3_TWICE && this.client.currentScreen instanceof GameModeSelectionScreen);
    }
}