package keybindbugfixes.mixin.fix_pick_key_dragging;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.platform.InputConstants;
import keybindbugfixes.KeybindBugFixes;
import keybindbugfixes.config.Config;
import keybindbugfixes.mixin.AbstractContainerScreenAccessor;
import net.minecraft.client.KeyboardHandler;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(KeyboardHandler.class)
public abstract class KeyboardHandlerMixin {
    @WrapOperation(
            method = "keyPress",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/screens/Screen;" +
                            "keyPressed(Lnet/minecraft/client/input/KeyEvent;)Z"
            )
    )
    private static boolean startPickKeyDragging(Screen screen, KeyEvent event, Operation<Boolean> original,
                                                @Local(ordinal = 0, argsOnly = true) int action) {
        boolean processed = original.call(screen, event);

        if (screen instanceof AbstractContainerScreen<?> containerScreen
                && Config.FIX_PICK_KEY_DRAGGING.value
                && action == InputConstants.PRESS
                && !processed) {
            AbstractContainerScreenAccessor accessor = (AbstractContainerScreenAccessor) containerScreen;
            Slot slot = accessor.getHoveredSlot();
            accessor.setSkipNextRelease(false);

            if (KeybindBugFixes.minecraft.options.keyPickItem.matches(event)
                    && KeybindBugFixes.minecraft.player.hasInfiniteMaterials()
                    && slot != null && slot.index != -1
                    && !accessor.isIsQuickCrafting()) {
                if (containerScreen.getMenu().getCarried().isEmpty()) {
                    accessor.invokeSlotClicked(slot, slot.index, 0, ClickType.CLONE);
                    accessor.setSkipNextRelease(true);
                } else {
                    accessor.setIsQuickCrafting(true);
                    accessor.setQuickCraftingButton(-1);
                    accessor.getQuickCraftSlots().clear();
                    accessor.setQuickCraftingType(2);
                    KeybindBugFixes.draggingPickKey = true;
                }
            }
        }

        return processed;
    }

    @WrapOperation(
            method = "keyPress",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/screens/Screen;" +
                            "keyReleased(Lnet/minecraft/client/input/KeyEvent;)Z"
            )
    )
    private static boolean stopPickKeyDragging(Screen screen, KeyEvent event, Operation<Boolean> original) {
        boolean processed = original.call(screen, event);

        if (screen instanceof AbstractContainerScreen<?> containerScreen
                && Config.FIX_PICK_KEY_DRAGGING.value) {
            AbstractContainerScreenAccessor accessor = (AbstractContainerScreenAccessor) containerScreen;

            if (accessor.isIsQuickCrafting() && accessor.getQuickCraftingButton() != -1) {
                accessor.setIsQuickCrafting(false);
                accessor.getQuickCraftSlots().clear();
                accessor.setSkipNextRelease(true);
                return processed;
            }

            if (accessor.getSkipNextRelease()) {
                accessor.setSkipNextRelease(false);
                return processed;
            }

            if (accessor.isIsQuickCrafting() && !accessor.getQuickCraftSlots().isEmpty()) {
                accessor.invokeSlotClicked(null, -999, AbstractContainerMenu.getQuickcraftMask(0, accessor.getQuickCraftingType()), ClickType.QUICK_CRAFT);

                for (Slot dragSlot : accessor.getQuickCraftSlots()) {
                    accessor.invokeSlotClicked(dragSlot, dragSlot.index, AbstractContainerMenu.getQuickcraftMask(1, accessor.getQuickCraftingType()), ClickType.QUICK_CRAFT);
                }

                accessor.invokeSlotClicked(null, -999, AbstractContainerMenu.getQuickcraftMask(2, accessor.getQuickCraftingType()), ClickType.QUICK_CRAFT);
            }

            accessor.setIsQuickCrafting(false);
            KeybindBugFixes.draggingPickKey = false;
        }

        return processed;
    }
}