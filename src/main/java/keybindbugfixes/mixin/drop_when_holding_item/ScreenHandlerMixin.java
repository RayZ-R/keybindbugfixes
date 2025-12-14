package keybindbugfixes.mixin.drop_when_holding_item;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import keybindbugfixes.config.Config;
import net.minecraft.screen.ScreenHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ScreenHandler.class)
public abstract class ScreenHandlerMixin {
    @ModifyExpressionValue(method = "internalOnSlotClick",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;isEmpty()Z", ordinal = 12))
    private boolean dropWhenHoldingItem(boolean original) {
        return Config.Tweaks.DROP_WHEN_HOLDING_ITEM ? true : original;
    }
}