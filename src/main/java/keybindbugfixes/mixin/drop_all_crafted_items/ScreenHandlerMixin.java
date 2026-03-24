package keybindbugfixes.mixin.drop_all_crafted_items;

import com.llamalad7.mixinextras.sugar.Local;
import keybindbugfixes.config.Config;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import org.lwjgl.glfw.GLFW;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ScreenHandler.class)
public abstract class ScreenHandlerMixin {
    @Inject(
            method = "internalOnSlotClick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/player/PlayerEntity;" +
                            "dropItem(Lnet/minecraft/item/ItemStack;Z)Lnet/minecraft/entity/ItemEntity;",
                    shift = At.Shift.AFTER
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
    private void dropUntilSlotEmpty(int slotIndex, int button, SlotActionType actionType, PlayerEntity player,
                                    CallbackInfo callbackInfo,
                                    @Local ItemStack itemStack, @Local Slot slot, @Local(ordinal = 2) int amount) {
        if (Config.DROP_ALL_CRAFTED_ITEMS.value && button == GLFW.GLFW_PRESS) {
            while (!itemStack.isEmpty() && ItemStack.areItemsEqual(slot.getStack(), itemStack)) {
                itemStack = slot.takeStackRange(amount, Integer.MAX_VALUE, player);
                player.dropItem(itemStack, true);
            }
        }
    }
}