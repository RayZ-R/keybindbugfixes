package keybindbugfixes.mixin.drop_when_holding_item;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import keybindbugfixes.config.Config;
import net.minecraft.screen.ScreenHandler;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Slice;

@Mixin(ScreenHandler.class)
public abstract class ScreenHandlerMixin {
    @ModifyExpressionValue(
            method = "internalOnSlotClick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/item/ItemStack;isEmpty()Z",
                    ordinal = 0
            ),
            slice = @Slice(
                    from = @At(
                            value = "FIELD",
                            target = "Lnet/minecraft/screen/slot/SlotActionType;" +
                                    "THROW:Lnet/minecraft/screen/slot/SlotActionType;",
                            opcode = Opcodes.GETSTATIC
                    )
            )
    )
    private boolean skipEmptyCursorCondition(boolean original) {
        return Config.DROP_WHEN_HOLDING_ITEM.value ? true : original;
    }
}