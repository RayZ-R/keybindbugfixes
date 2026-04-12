package keybindbugfixes.mixin.fix_pressing_f3_twice;

import keybindbugfixes.config.Config;
import net.minecraft.client.KeyboardHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.debug.GameModeSwitcherScreen;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Slice;

@Mixin(KeyboardHandler.class)
public abstract class KeyboardHandlerMixin {
    @Shadow @Final private Minecraft minecraft;

    @ModifyVariable(
            method = "keyPress",
            at = @At("STORE"),
            slice = @Slice(
                    from = @At(
                            value = "INVOKE",
                            target = "Lcom/mojang/blaze3d/platform/InputConstants;" +
                                    "getKey(II)Lcom/mojang/blaze3d/platform/InputConstants$Key;"
                    )
            ),
            ordinal = 2
    )
    private boolean fixF3State(boolean original) {
        boolean isGameModeSwitcherScreen = this.minecraft.screen instanceof GameModeSwitcherScreen;
        return original || (Config.FIX_PRESSING_F3_TWICE.value && isGameModeSwitcherScreen);
    }
}