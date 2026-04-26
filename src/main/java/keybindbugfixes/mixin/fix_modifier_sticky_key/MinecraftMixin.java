package keybindbugfixes.mixin.fix_modifier_sticky_key;

import keybindbugfixes.ToggleKeyStates;
import keybindbugfixes.config.Config;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin {
    @Inject(method = "pickBlock", at = @At(value = "HEAD"))
    private void revertPickBlockWithNbtModifierKey(CallbackInfo callbackInfo) {
        if (Config.FIX_MODIFIER_STICKY_KEY.value && Screen.hasControlDown()) {
            ToggleKeyStates.revertControl();
        }
    }
}