package keybindbugfixes.mixin.fix_pick_key_dragging;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import keybindbugfixes.KeybindBugFixes;
import keybindbugfixes.config.Config;
import keybindbugfixes.mixin.HandledScreenAccessor;
import net.minecraft.client.Keyboard;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
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
                    target = "Lnet/minecraft/client/gui/screen/Screen;keyPressed(III)Z"
            )
    )
    private static boolean startPickKeyDragging(Screen screen, int keycode, int scancode, int modifiers,
                                                Operation<Boolean> original, @Local(ordinal = 2) int action) {
        boolean processed = original.call(screen, keycode, scancode, modifiers);

        if (screen instanceof HandledScreen<?> handledScreen && Config.FIX_PICK_KEY_DRAGGING.value && !processed) {
            MinecraftClient client = MinecraftClient.getInstance();
            boolean inCreative = client.interactionManager.hasCreativeInventory();

            if (client.options.pickItemKey.matchesKey(keycode, scancode) && action == GLFW.GLFW_PRESS && inCreative) {
                HandledScreenAccessor screenAccessor = ((HandledScreenAccessor) handledScreen);

                screenAccessor.setCancelNextRelease(false);

                if (!screenAccessor.isCursorDragging()) {
                    if (handledScreen.getScreenHandler().getCursorStack().isEmpty()) {
                        Slot focusedSlot = screenAccessor.getFocusedSlot();
                        if (focusedSlot != null && focusedSlot.hasStack()) {
                            screenAccessor.invokeOnMouseClick(focusedSlot, focusedSlot.id, 0, SlotActionType.CLONE);
                        }
                    } else {
                        screenAccessor.setCursorDragging(true);
                        screenAccessor.setHeldButtonCode(-1);
                        screenAccessor.getCursorDragSlots().clear();
                        screenAccessor.setHeldButtonType(2);
                        KeybindBugFixes.draggingPickKey = true;
                    }
                }
            }

            return true;
        }

        return processed;
    }

    @WrapOperation(
            method = "onKey",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/screen/Screen;keyReleased(III)Z"
            )
    )
    private static boolean stopPickKeyDragging(Screen screen, int keycode, int scancode, int modifiers,
                                               Operation<Boolean> original) {
        boolean processed = original.call(screen, keycode, scancode, modifiers);

        if (screen instanceof HandledScreen<?> handledScreen && Config.FIX_PICK_KEY_DRAGGING.value) {
            MinecraftClient client = MinecraftClient.getInstance();

            if (client.options.pickItemKey.matchesKey(keycode, scancode)) {
                HandledScreenAccessor screenAccessor = ((HandledScreenAccessor) handledScreen);

                KeybindBugFixes.draggingPickKey = false;

                if (screenAccessor.isCursorDragging() && screenAccessor.getHeldButtonCode() != -1) {
                    screenAccessor.setCursorDragging(false);
                    screenAccessor.getCursorDragSlots().clear();
                    screenAccessor.setCancelNextRelease(true);
                    return true;
                }

                if (screenAccessor.getCancelNextRelease()) {
                    screenAccessor.setCancelNextRelease(false);
                    return true;
                }

                if (screenAccessor.isCursorDragging() && !screenAccessor.getCursorDragSlots().isEmpty()) {
                    int heldButtonType = screenAccessor.getHeldButtonType();
                    screenAccessor.invokeOnMouseClick(null, -999, ScreenHandler.packQuickCraftData(0, heldButtonType), SlotActionType.QUICK_CRAFT);

                    for (Slot slot2x : screenAccessor.getCursorDragSlots()) {
                        screenAccessor.invokeOnMouseClick(slot2x, slot2x.id, ScreenHandler.packQuickCraftData(1, heldButtonType), SlotActionType.QUICK_CRAFT);
                    }

                    screenAccessor.invokeOnMouseClick(null, -999, ScreenHandler.packQuickCraftData(2, heldButtonType), SlotActionType.QUICK_CRAFT);
                }

                screenAccessor.setCursorDragging(false);
                return true;
            }
        }

        return processed;
    }
}