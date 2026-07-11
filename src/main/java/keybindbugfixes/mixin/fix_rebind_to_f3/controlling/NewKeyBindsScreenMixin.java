package keybindbugfixes.mixin.fix_rebind_to_f3.controlling;

import com.blamejared.controlling.client.NewKeyBindsScreen;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import keybindbugfixes.config.Config;
import net.minecraft.client.Options;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.options.controls.KeyBindsScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(NewKeyBindsScreen.class)
public abstract class NewKeyBindsScreenMixin extends KeyBindsScreen {
    @Unique private boolean keybindbugfixes$skipNextKeyRelease = false;

    public NewKeyBindsScreenMixin(Screen screen, Options options) {
        super(screen, options);
    }

    @Inject(method = "keyPressed", at = @At("HEAD"))
    private void skipNextKeyRelease(CallbackInfoReturnable<Boolean> callbackInfo) {
        if (Config.FIX_REBIND_TO_F3.value && this.selectedKey != null) {
            this.keybindbugfixes$skipNextKeyRelease = true;
        }
    }

    @ModifyReturnValue(method = "keyReleased", at = @At("TAIL"))
    private boolean skipKeyRelease(boolean original) {
        if (this.keybindbugfixes$skipNextKeyRelease) {
            this.keybindbugfixes$skipNextKeyRelease = false;
            return true;
        } else {
            return original;
        }
    }
}