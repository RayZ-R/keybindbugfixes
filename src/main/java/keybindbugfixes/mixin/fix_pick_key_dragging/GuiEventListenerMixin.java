package keybindbugfixes.mixin.fix_pick_key_dragging;

import keybindbugfixes.KeybindBugFixes;
import keybindbugfixes.config.Config;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiEventListener.class)
public interface GuiEventListenerMixin {
    @Inject(method = "mouseMoved", at = @At("HEAD"))
    default void invokePickKeyDraggingLogic(double x, double y, CallbackInfo callbackInfo) {
        if (this instanceof AbstractContainerScreen<?> containerScreen
                && Config.FIX_PICK_KEY_DRAGGING.value
                && KeybindBugFixes.draggingPickKey) {
            containerScreen.mouseDragged(x, y, -1, 0, 0);
        }
    }
}