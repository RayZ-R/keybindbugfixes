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
import org.spongepowered.asm.mixin.injection.Slice;

@Mixin(Keyboard.class)
public abstract class KeyboardMixin {
    @Shadow @Final private MinecraftClient client;

    @ModifyVariable(
            method = "onKey",
            at = @At("STORE"),
            slice = @Slice(
                    from = @At(
                            value = "INVOKE",
                            target = "Lnet/minecraft/client/util/InputUtil;" +
                                    "fromKeyCode(II)Lnet/minecraft/client/util/InputUtil$Key;"
                    )
            ),
            ordinal = 2
    )
    private boolean fixF3State(boolean original) {
        boolean isGameModeSelectionScreen = this.client.currentScreen instanceof GameModeSelectionScreen;
        return original || (Config.FIX_PRESSING_F3_TWICE.value && isGameModeSelectionScreen);
    }
}