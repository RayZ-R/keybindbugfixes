package keybindbugfixes.mixin.fix_debug_conflicts.controlling;

import com.blamejared.controlling.client.FreeKeysList;
import com.google.common.collect.Sets;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.platform.InputConstants;
import keybindbugfixes.KeybindBugFixes;
import keybindbugfixes.config.Config;
import net.minecraft.client.KeyMapping;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Set;

@Mixin(FreeKeysList.class)
public abstract class FreeKeysListMixin {
    @WrapOperation(
            method = "lambda$recalculate$2",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/mojang/blaze3d/platform/InputConstants$Key;equals(Ljava/lang/Object;)Z"
            )
    )
    private static boolean skipDebugKeys(InputConstants.Key key, Object object, Operation<Boolean> original,
                                   @Local(argsOnly = true, name = "keyBinding") KeyMapping keyMapping) {
        boolean same = original.call(key, object);

        if (Config.FIX_DEBUG_CONFLICTS.value) {
            Set<KeyMapping> debugKeys = Sets.newHashSet(KeybindBugFixes.minecraft.options.debugKeys);

            if (debugKeys.contains(keyMapping)) {
                return false;
            } else {
                return same;
            }
        } else {
            return same;
        }
    }
}