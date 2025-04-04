package keybindbugfixes.mixin.fix_pressing_f3_twice;

import keybindbugfixes.config.Config;
import keybindbugfixes.mixin.KeyboardAccessor;
import net.minecraft.client.gui.screen.GameModeSelectionScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GameModeSelectionScreen.class)
public abstract class GameModeSelectionScreenMixin extends Screen {
    protected GameModeSelectionScreenMixin(Text title) {
        super(title);
    }

    @Inject(method = "checkForClose",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/MinecraftClient;setScreen(Lnet/minecraft/client/gui/screen/Screen;)V",
                    shift = At.Shift.AFTER))
    private void fixF3State(CallbackInfoReturnable<Boolean> callbackInfo) {
        if (Config.BugFixes.FIX_PRESSING_F3_TWICE) {
            ((KeyboardAccessor) this.client.keyboard).setSwitchF3State(false);
        }
    }
}