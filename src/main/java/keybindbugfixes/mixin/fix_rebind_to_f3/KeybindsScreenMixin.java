package keybindbugfixes.mixin.fix_rebind_to_f3;

import keybindbugfixes.config.Config;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.option.GameOptionsScreen;
import net.minecraft.client.gui.screen.option.KeybindsScreen;
import net.minecraft.client.input.KeyInput;
import net.minecraft.client.option.GameOptions;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(KeybindsScreen.class)
public abstract class KeybindsScreenMixin extends GameOptionsScreen {
    @Unique private boolean keybindbugfixes$skipNextKeyRelease = false;

    public KeybindsScreenMixin(Screen parent, GameOptions gameOptions, Text title) {
        super(parent, gameOptions, title);
    }

    @Inject(
            method = "keyPressed",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/screen/option/ControlsListWidget;update()V",
                    shift = At.Shift.AFTER
            )
    )
    private void skipNextKeyRelease(CallbackInfoReturnable<Boolean> callbackInfo) {
        if (Config.FIX_REBIND_TO_F3.value) {
            this.keybindbugfixes$skipNextKeyRelease = true;
        }
    }

    @Override
    public boolean keyReleased(KeyInput input) {
        if (this.keybindbugfixes$skipNextKeyRelease) {
            this.keybindbugfixes$skipNextKeyRelease = false;
            return true;
        } else {
            return super.keyReleased(input);
        }
    }
}