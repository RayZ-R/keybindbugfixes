package keybindbugfixes.mixin.fix_control_sticky_key_reset;

import keybindbugfixes.KeybindBugFixes;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.option.StickyKeyBinding;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(KeyBinding.class)
public abstract class KeyBindingMixin {
    @Inject(method = "<init>(Ljava/lang/String;Lnet/minecraft/client/util/InputUtil$Type;ILjava/lang/String;)V",
            at = @At("RETURN"))
    private void addStickyKeyBinding(CallbackInfo callbackInfo) {
        if (((KeyBinding) (Object) this) instanceof StickyKeyBinding stickyKeyBinding) {
            KeybindBugFixes.STICKY_KEY_BINDINGS.add(stickyKeyBinding);
        }
    }
}