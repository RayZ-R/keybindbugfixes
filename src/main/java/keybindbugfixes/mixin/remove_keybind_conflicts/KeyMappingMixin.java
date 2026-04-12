package keybindbugfixes.mixin.remove_keybind_conflicts;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.blaze3d.platform.InputConstants;
import keybindbugfixes.config.Config;
import keybindbugfixes.mixin.KeyMappingAccessor;
import net.minecraft.client.KeyMapping;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;
import java.util.Set;

@Mixin(KeyMapping.class)
public abstract class KeyMappingMixin {
    @Unique private static final Map<InputConstants.Key, Set<KeyMapping>> keybindbugfixes$MAP = Maps.newHashMap();
    @Shadow @Final private static Map<String, KeyMapping> ALL;
    @Shadow private InputConstants.Key key;

    @Unique
    private static void keybindbugfixes$putKeyMapping(InputConstants.Key key, KeyMapping keyMapping) {
        keybindbugfixes$MAP.putIfAbsent(key, Sets.newHashSet());
        keybindbugfixes$MAP.get(key).add(keyMapping);
    }

    @Inject(method = "click", at = @At("HEAD"), cancellable = true)
    private static void click(InputConstants.Key key, CallbackInfo callbackInfo) {
        if (Config.REMOVE_KEYBIND_CONFLICTS.value) {
            Set<KeyMapping> keyMappings = keybindbugfixes$MAP.get(key);

            if (keyMappings != null) {
                for (KeyMapping keyMapping : keyMappings) {
                    KeyMappingAccessor accessor = ((KeyMappingAccessor) keyMapping);
                    accessor.setClickCount(accessor.getClickCount() + 1);
                }
            }

            callbackInfo.cancel();
        }
    }

    @Inject(method = "set", at = @At("HEAD"), cancellable = true)
    private static void set(InputConstants.Key key, boolean state, CallbackInfo callbackInfo) {
        if (Config.REMOVE_KEYBIND_CONFLICTS.value) {
            Set<KeyMapping> keyMappings = keybindbugfixes$MAP.get(key);

            if (keyMappings != null) {
                for (KeyMapping keyMapping : keyMappings) {
                    keyMapping.setDown(state);
                }
            }

            callbackInfo.cancel();
        }
    }

    @Inject(method = "resetMapping", at = @At("HEAD"))
    private static void resetMapping(CallbackInfo callbackInfo) {
        keybindbugfixes$MAP.clear();

        for (KeyMapping keyMapping : ALL.values()) {
            InputConstants.Key key = ((KeyMappingAccessor) keyMapping).getKey();
            keybindbugfixes$putKeyMapping(key, keyMapping);
        }
    }

    @Inject(
            method = "<init>(Ljava/lang/String;Lcom/mojang/blaze3d/platform/InputConstants$Type;ILjava/lang/String;)V",
            at = @At("RETURN")
    )
    private void addKeyMapping(CallbackInfo callbackInfo) {
        KeyMapping keyMapping = (KeyMapping) (Object) this;
        keybindbugfixes$putKeyMapping(this.key, keyMapping);
    }
}