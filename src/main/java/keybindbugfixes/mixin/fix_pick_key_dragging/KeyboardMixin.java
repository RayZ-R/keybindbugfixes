package keybindbugfixes.mixin.fix_pick_key_dragging;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import keybindbugfixes.KeybindBugFixes;
import keybindbugfixes.config.Config;
import keybindbugfixes.mixin.HandledScreenAccessor;
import net.minecraft.client.Keyboard;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.input.KeyInput;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Keyboard.class)
public abstract class KeyboardMixin {
    @WrapOperation(
            method = "onKey",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/screen/Screen;" +
                            "keyPressed(Lnet/minecraft/client/input/KeyInput;)Z"
            )
    )
    private static boolean startPickKeyDragging(Screen screen, KeyInput input, Operation<Boolean> original,
                                                @Local(ordinal = 0) int action) {
        boolean processed = original.call(screen, input);

        if (screen instanceof HandledScreen<?> handledScreen
                && Config.FIX_PICK_KEY_DRAGGING.value
                && action == GLFW.GLFW_PRESS
                && !processed) {
            HandledScreenAccessor accessor = (HandledScreenAccessor) handledScreen;
            Slot slot = accessor.getFocusedSlot();
            accessor.setCancelNextRelease(false);

            if (KeybindBugFixes.client.options.pickItemKey.matchesKey(input)
                    && KeybindBugFixes.client.player.isInCreativeMode()
                    && slot != null && slot.id != -1
                    && !accessor.isCursorDragging()) {
                if (handledScreen.getScreenHandler().getCursorStack().isEmpty()) {
                    accessor.invokeOnMouseClick(slot, slot.id, 0, SlotActionType.CLONE);
                    accessor.setCancelNextRelease(true);
                } else {
                    accessor.setCursorDragging(true);
                    accessor.setHeldButtonCode(-1);
                    accessor.getCursorDragSlots().clear();
                    accessor.setHeldButtonType(2);
                    KeybindBugFixes.draggingPickKey = true;
                }
            }
        }

        return true;
    }

    @WrapOperation(
            method = "onKey",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/screen/Screen;" +
                            "keyReleased(Lnet/minecraft/client/input/KeyInput;)Z"
            )
    )
    private static boolean stopPickKeyDragging(Screen screen, KeyInput input, Operation<Boolean> original) {
        boolean processed = original.call(screen, input);

        if (screen instanceof HandledScreen<?> handledScreen
                && Config.FIX_PICK_KEY_DRAGGING.value
                && !processed) {
            HandledScreenAccessor accessor = (HandledScreenAccessor) handledScreen;
            Slot slot = accessor.getFocusedSlot();

            if (accessor.isCursorDragging() && accessor.getHeldButtonCode() != -1) {
                accessor.setCursorDragging(false);
                accessor.getCursorDragSlots().clear();
                accessor.setCancelNextRelease(true);
                return true;
            }

            if (accessor.getCancelNextRelease()) {
                accessor.setCancelNextRelease(false);
                return true;
            }

            if (accessor.isCursorDragging() && !accessor.getCursorDragSlots().isEmpty()) {
                int heldButtonType = accessor.getHeldButtonType();
                accessor.invokeOnMouseClick(null, -999, ScreenHandler.packQuickCraftData(0, heldButtonType), SlotActionType.QUICK_CRAFT);

                for (Slot dragSlot : accessor.getCursorDragSlots()) {
                    accessor.invokeOnMouseClick(dragSlot, dragSlot.id, ScreenHandler.packQuickCraftData(1, heldButtonType), SlotActionType.QUICK_CRAFT);
                }

                accessor.invokeOnMouseClick(null, -999, ScreenHandler.packQuickCraftData(2, heldButtonType), SlotActionType.QUICK_CRAFT);
            } else if (!handledScreen.getScreenHandler().getCursorStack().isEmpty()
                    && KeybindBugFixes.client.options.pickItemKey.matchesKey(input)) {
                accessor.invokeOnMouseClick(slot, slot.id, 0, SlotActionType.CLONE);
            }

            accessor.setCursorDragging(false);
            KeybindBugFixes.draggingPickKey = false;
        }

        return processed;
    }
}