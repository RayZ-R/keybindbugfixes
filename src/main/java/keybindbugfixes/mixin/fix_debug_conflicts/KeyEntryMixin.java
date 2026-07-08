package keybindbugfixes.mixin.fix_debug_conflicts;

import com.google.common.collect.Sets;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import keybindbugfixes.KeybindBugFixes;
import keybindbugfixes.config.Config;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Options;
import net.minecraft.client.gui.screens.options.controls.KeyBindsList;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Set;

@Mixin(KeyBindsList.KeyEntry.class)
public class KeyEntryMixin {
    @Shadow @Final private KeyMapping key;

    @WrapOperation(
            method = "refreshEntry",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/client/Options;keyMappings:[Lnet/minecraft/client/KeyMapping;",
                    opcode = Opcodes.GETFIELD
            )
    )
    private KeyMapping[] separateDebugKeys(Options options, Operation<KeyMapping[]> original) {
        if (Config.FIX_DEBUG_CONFLICTS.value) {
            Set<KeyMapping> debugKeys = Sets.newHashSet(options.debugKeys);

            if (debugKeys.contains(this.key)) {
                return options.debugKeys;
            } else {
                Set<KeyMapping> allKeys = Sets.newHashSet(options.keyMappings);
                return Sets.difference(allKeys, debugKeys).toArray(new KeyMapping[0]);
            }
        } else {
            return original.call(options);
        }
    }

    @WrapOperation(
            method = "refreshEntry",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/KeyMapping;same(Lnet/minecraft/client/KeyMapping;)Z"
            )
    )
    private boolean preventDebugModifierConflict(KeyMapping key, KeyMapping keyMapping, Operation<Boolean> original) {
        boolean same = original.call(key, keyMapping);

        if (Config.FIX_DEBUG_CONFLICTS.value) {
            KeyMapping keyDebugOverlay = KeybindBugFixes.minecraft.options.keyDebugOverlay;
            KeyMapping keyDebugModifier = KeybindBugFixes.minecraft.options.keyDebugModifier;

            return same && !(keyMapping == keyDebugOverlay || keyMapping == keyDebugModifier);
        } else {
            return same;
        }
    }
}