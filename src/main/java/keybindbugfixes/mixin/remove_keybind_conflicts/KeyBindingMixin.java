package keybindbugfixes.mixin.remove_keybind_conflicts;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import keybindbugfixes.config.Config;
import keybindbugfixes.mixin.KeyBindingAccessor;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;
import java.util.Set;

@Mixin(KeyBinding.class)
public abstract class KeyBindingMixin {
    @Unique private static final Map<InputUtil.Key, Set<KeyBinding>> keybindbugfixes$BINDINGS_BY_KEY = Maps.newHashMap();
    @Shadow @Final private static Map<String, KeyBinding> KEYS_BY_ID;
    @Shadow private InputUtil.Key boundKey;

    @Unique
    private static void keybindbugfixes$putKeyBinding(InputUtil.Key key, KeyBinding keyBinding) {
        keybindbugfixes$BINDINGS_BY_KEY.putIfAbsent(key, Sets.newHashSet());
        keybindbugfixes$BINDINGS_BY_KEY.get(key).add(keyBinding);
    }

    @Inject(method = "onKeyPressed", at = @At("HEAD"), cancellable = true)
    private static void onKeyPressed(InputUtil.Key key, CallbackInfo callbackInfo) {
        if (Config.Tweaks.REMOVE_KEYBIND_CONFLICTS) {
            Set<KeyBinding> keyBindings = keybindbugfixes$BINDINGS_BY_KEY.get(key);

            if (keyBindings != null) {
                for (KeyBinding keyBinding : keyBindings) {
                    KeyBindingAccessor accessor = ((KeyBindingAccessor) keyBinding);
                    accessor.setTimesPressed(accessor.getTimesPressed() + 1);
                }
            }

            callbackInfo.cancel();
        }
    }

    @Inject(method = "setKeyPressed", at = @At("HEAD"), cancellable = true)
    private static void setKeyPressed(InputUtil.Key key, boolean pressed, CallbackInfo callbackInfo) {
        if (Config.Tweaks.REMOVE_KEYBIND_CONFLICTS) {
            Set<KeyBinding> keyBindings = keybindbugfixes$BINDINGS_BY_KEY.get(key);

            if (keyBindings != null) {
                for (KeyBinding keyBinding : keyBindings) {
                    keyBinding.setPressed(pressed);
                }
            }

            callbackInfo.cancel();
        }
    }

    @Inject(method = "updateKeysByCode", at = @At("HEAD"))
    private static void updateKeysByCode(CallbackInfo callbackInfo) {
        keybindbugfixes$BINDINGS_BY_KEY.clear();

        for (KeyBinding keyBinding : KEYS_BY_ID.values()) {
            InputUtil.Key boundKey = ((KeyBindingAccessor) keyBinding).getBoundKey();
            keybindbugfixes$putKeyBinding(boundKey, keyBinding);
        }
    }

    @Inject(method = "<init>(Ljava/lang/String;Lnet/minecraft/client/util/InputUtil$Type;ILjava/lang/String;)V",
            at = @At("RETURN"))
    private void addKeyBinding(CallbackInfo callbackInfo) {
        KeyBinding keyBinding = (KeyBinding) (Object) this;
        keybindbugfixes$putKeyBinding(this.boundKey, keyBinding);
    }
}