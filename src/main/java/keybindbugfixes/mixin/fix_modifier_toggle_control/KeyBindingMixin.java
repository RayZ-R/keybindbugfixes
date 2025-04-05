package keybindbugfixes.mixin.fix_modifier_toggle_control;

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

    @Inject(method = "setKeyPressed", at = @At("HEAD"), order = 900)
    private static void addControlStickyKeyBindingRevertValues(InputUtil.Key key, boolean pressed, CallbackInfo callbackInfo) {
        if (Config.BugFixes.FIX_MODIFIER_TOGGLE_CONTROL && key.getCode() == GLFW.GLFW_KEY_LEFT_CONTROL && pressed) {
            KeybindBugFixes.STICKY_KEY_REVERT_MAP.clear();

            for (StickyKeyBinding stickyKeyBinding : keybindbugfixes$STICKY_KEY_BINDINGS) {
                KeyBindingAccessor accessor = (KeyBindingAccessor) stickyKeyBinding;

                if (accessor.getBoundKey().getCode() == GLFW.GLFW_KEY_LEFT_CONTROL) {
                    KeybindBugFixes.STICKY_KEY_REVERT_MAP.put(stickyKeyBinding, stickyKeyBinding.isPressed());
                }
            }
        }
    }
}