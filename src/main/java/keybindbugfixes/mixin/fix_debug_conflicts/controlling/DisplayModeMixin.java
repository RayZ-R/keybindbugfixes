package keybindbugfixes.mixin.fix_debug_conflicts.controlling;

import com.blamejared.controlling.api.DisplayMode;
import com.blamejared.controlling.api.entries.IKeyEntry;
import com.google.common.collect.Sets;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import keybindbugfixes.KeybindBugFixes;
import keybindbugfixes.config.Config;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Options;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Set;

@Mixin(DisplayMode.class)
public abstract class DisplayModeMixin {
    @WrapOperation(
            method = "lambda$static$2",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/client/Options;keyMappings:[Lnet/minecraft/client/KeyMapping;",
                    opcode = Opcodes.GETFIELD
            )
    )
    private static KeyMapping[] separateDebugKeys(Options options, Operation<KeyMapping[]> original,
                                                  @Local(argsOnly = true, name = "keyEntry") IKeyEntry keyEntry) {
        if (Config.FIX_DEBUG_CONFLICTS.value) {
            Set<KeyMapping> debugKeys = Sets.newHashSet(options.debugKeys);

            if (debugKeys.contains(keyEntry.getKey())) {
                return options.debugKeys;
            } else {
                Set<KeyMapping> allKeys = Sets.newHashSet(options.keyMappings);
                return Sets.difference(allKeys, debugKeys).toArray(KeyMapping[]::new);
            }
        } else {
            return original.call(options);
        }
    }

    @WrapOperation(
            method = "lambda$static$2",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/KeyMapping;isUnbound()Z"
            )
    )
    private static boolean preventDebugModifierConflict(KeyMapping instance, Operation<Boolean> original,
                                                        @Local(argsOnly = true, name = "keyEntry") IKeyEntry keyEntry,
                                                        @Local(name = "key") KeyMapping key) {
        if (Config.FIX_DEBUG_CONFLICTS.value) {
            KeyMapping otherKey = keyEntry.getKey();

            KeyMapping keyDebugOverlay = KeybindBugFixes.minecraft.options.keyDebugOverlay;
            KeyMapping keyDebugModifier = KeybindBugFixes.minecraft.options.keyDebugModifier;

            boolean skip = (key == keyDebugOverlay && otherKey == keyDebugModifier)
                    || (otherKey == keyDebugOverlay && key == keyDebugModifier);

            return original.call(instance) || skip;
        } else {
            return original.call(instance);
        }
    }
}