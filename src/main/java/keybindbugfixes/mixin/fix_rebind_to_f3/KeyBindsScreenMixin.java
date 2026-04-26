package keybindbugfixes.mixin.fix_rebind_to_f3;

import keybindbugfixes.config.Config;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Options;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.options.OptionsSubScreen;
import net.minecraft.client.gui.screens.options.controls.KeyBindsScreen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(KeyBindsScreen.class)
public abstract class KeyBindsScreenMixin extends OptionsSubScreen {
    @Shadow @Nullable public KeyMapping selectedKey;

    @Unique private boolean keybindbugfixes$skipNextKeyRelease = false;

    public KeyBindsScreenMixin(Screen lastScreen, Options options, Component title) {
        super(lastScreen, options, title);
    }

    @Inject(method = "keyPressed", at = @At("HEAD"))
    private void skipNextKeyRelease(CallbackInfoReturnable<Boolean> callbackInfo) {
        if (Config.FIX_REBIND_TO_F3.value && this.selectedKey != null) {
            this.keybindbugfixes$skipNextKeyRelease = true;
        }
    }

    @Override
    public boolean keyReleased(KeyEvent event) {
        if (this.keybindbugfixes$skipNextKeyRelease) {
            this.keybindbugfixes$skipNextKeyRelease = false;
            return true;
        } else {
            return super.keyReleased(event);
        }
    }
}