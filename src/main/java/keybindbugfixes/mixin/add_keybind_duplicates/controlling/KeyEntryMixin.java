package keybindbugfixes.mixin.add_keybind_duplicates.controlling;

import com.blamejared.controlling.client.NewKeyBindsList;
import com.llamalad7.mixinextras.sugar.Local;
import keybindbugfixes.config.Config;
import keybindbugfixes.config.option.KeyOption;
import net.minecraft.client.KeyMapping;
import net.minecraft.network.chat.MutableComponent;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(NewKeyBindsList.KeyEntry.class)
public abstract class KeyEntryMixin {
    @Shadow @Final private KeyMapping key;
    @Shadow private boolean hasCollision;

    @Inject(
            method = "refreshEntry",
            at = @At(
                    value = "FIELD",
                    target = "Lcom/blamejared/controlling/client/NewKeyBindsList$KeyEntry;hasCollision:Z",
                    opcode = Opcodes.GETFIELD,
                    ordinal = 1
            )
    )
    private void handleModdedDuplicates(CallbackInfo callbackInfo,
                                        @Local(name = "duplicates") MutableComponent duplicates) {
        if (this.key.isUnbound()) return;

        for (KeyOption option : Config.KEY_OPTIONS) {
            if (!option.isDisabled && option.modifier == null && this.key.matches(option.value)) {
                if (this.hasCollision) {
                    duplicates.append(", ");
                }

                this.hasCollision = true;
                duplicates.append(option.label);
            }
        }
    }
}