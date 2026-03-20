package keybindbugfixes.mixin.fix_pick_key_dragging;

import keybindbugfixes.KeybindBugFixes;
import keybindbugfixes.config.Config;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.input.MouseInput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Element.class)
public interface ElementMixin {
    @Inject(method = "mouseMoved", at = @At("HEAD"))
    default void invokePickKeyDraggingLogic(double mouseX, double mouseY, CallbackInfo callbackInfo) {
        if (this instanceof HandledScreen<?> handledScreen
                && Config.FIX_PICK_KEY_DRAGGING.value
                && KeybindBugFixes.draggingPickKey) {
            handledScreen.mouseDragged(new Click(mouseX, mouseY, new MouseInput(-1, 0)), 0, 0);
        }
    }
}