package keybindbugfixes.mixin.fix_dismount_toggle_sneak;

import keybindbugfixes.KeybindBugFixes;
import keybindbugfixes.config.Config;
import keybindbugfixes.mixin.KeyMappingAccessor;
import keybindbugfixes.mixin.ToggleKeyMappingAccessor;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public abstract class EntityMixin {
    @Inject(method = "stopRiding", at = @At("HEAD"))
    private void resetShiftKey(CallbackInfo callbackInfo) {
        if ((Object) this instanceof LocalPlayer && Config.FIX_DISMOUNT_TOGGLE_SNEAK.value) {
            KeyMapping keyShift = KeybindBugFixes.minecraft.options.keyShift;

            if (((ToggleKeyMappingAccessor) keyShift).getNeedsToggle().getAsBoolean()) {
                ((KeyMappingAccessor) keyShift).setDownState(false);
            }
        }
    }
}