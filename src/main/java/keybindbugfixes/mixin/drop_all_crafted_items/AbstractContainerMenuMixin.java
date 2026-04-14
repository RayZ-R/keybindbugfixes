package keybindbugfixes.mixin.drop_all_crafted_items;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.platform.InputConstants;
import keybindbugfixes.config.Config;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractContainerMenu.class)
public abstract class AbstractContainerMenuMixin {
    @Inject(
            method = "doClick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/player/Player;" +
                            "drop(Lnet/minecraft/world/item/ItemStack;Z)Lnet/minecraft/world/entity/item/ItemEntity;",
                    shift = At.Shift.AFTER
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
    private void dropUntilSlotEmpty(int slotIndex, int buttonNum, ClickType clickType, Player player,
                                    CallbackInfo callbackInfo,
                                    @Local ItemStack itemStack, @Local Slot slot, @Local(ordinal = 2) int amount) {
        if (Config.DROP_ALL_CRAFTED_ITEMS.value && buttonNum == InputConstants.PRESS) {
            while (!itemStack.isEmpty() && ItemStack.isSameItem(slot.getItem(), itemStack)) {
                itemStack = slot.safeTake(amount, Integer.MAX_VALUE, player);
                player.drop(itemStack, true);
            }
        }
    }
}