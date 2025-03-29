package keybindbugfixes.mixin.fix_control_sticky_key_reset;

import com.google.common.collect.Lists;
import keybindbugfixes.KeybindBugFixes;
import keybindbugfixes.config.Config;
import keybindbugfixes.mixin.KeyBindingAccessor;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.option.StickyKeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(KeyBinding.class)
public abstract class KeyBindingMixin {
    @Unique private static final List<StickyKeyBinding> keybindbugfixes$STICKY_KEY_BINDINGS = Lists.newArrayList();

    @Inject(method = "<init>(Ljava/lang/String;Lnet/minecraft/client/util/InputUtil$Type;ILjava/lang/String;)V",
            at = @At("RETURN"))
    private void addStickyKeyBinding(CallbackInfo callbackInfo) {
        if (((KeyBinding) (Object) this) instanceof StickyKeyBinding stickyKeyBinding) {
            keybindbugfixes$STICKY_KEY_BINDINGS.add(stickyKeyBinding);
        }
    }

    @Inject(method = "setKeyPressed", at = @At("HEAD"))
    private static void addControlStickyKeyBindingRevertValues(InputUtil.Key key, boolean pressed, CallbackInfo callbackInfo) {
        if (Config.BugFixes.FIX_CONTROL_STICKY_KEY_RESET && key.getCode() == GLFW.GLFW_KEY_LEFT_CONTROL && pressed) {
            KeybindBugFixes.stickyKeyBindingRevertMap.clear();

            for (StickyKeyBinding stickyKeyBinding : keybindbugfixes$STICKY_KEY_BINDINGS) {
                KeyBindingAccessor accessor = (KeyBindingAccessor) stickyKeyBinding;

                if (accessor.getBoundKey().getCode() == GLFW.GLFW_KEY_LEFT_CONTROL) {
                    KeybindBugFixes.stickyKeyBindingRevertMap.put(stickyKeyBinding, stickyKeyBinding.isPressed());
                }
            }
        }
    }
}