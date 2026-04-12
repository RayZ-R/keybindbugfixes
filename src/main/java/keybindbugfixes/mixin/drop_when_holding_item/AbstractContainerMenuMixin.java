package keybindbugfixes.mixin.drop_when_holding_item;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import keybindbugfixes.config.Config;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Slice;

@Mixin(AbstractContainerMenu.class)
public abstract class AbstractContainerMenuMixin {
    @ModifyExpressionValue(
            method = "doClick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/item/ItemStack;isEmpty()Z",
                    ordinal = 0
            ),
            slice = @Slice(
                    from = @At(
                            value = "FIELD",
                            target = "Lnet/minecraft/world/inventory/ClickType;" +
                                    "THROW:Lnet/minecraft/world/inventory/ClickType;",
                            opcode = Opcodes.GETSTATIC
                    )
            )
    )
    private boolean skipEmptyCarriedCondition(boolean original) {
        return Config.DROP_WHEN_HOLDING_ITEM.value ? true : original;
    }
}