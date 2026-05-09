package keybindbugfixes.mixin.add_keybind_duplicates;

import com.llamalad7.mixinextras.sugar.Local;
import keybindbugfixes.config.Config;
import keybindbugfixes.config.option.KeyOption;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.gui.screens.options.controls.KeyBindsList;
import net.minecraft.network.chat.MutableComponent;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(KeyBindsList.KeyEntry.class)
public abstract class KeyEntryMixin {
    @Shadow @Final private KeyMapping key;
    @Shadow private boolean hasCollision;

    @Inject(
            method = "refreshEntry",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/client/gui/screens/options/controls/KeyBindsList$KeyEntry;hasCollision:Z",
                    opcode = Opcodes.GETFIELD,
                    ordinal = 1
            )
    )
    private void handleModdedDuplicates(CallbackInfo callbackInfo,
                                        @Local(name = "tooltip") MutableComponent tooltip) {
        if (this.key.isUnbound()) return;

        for (KeyOption option : Config.KEY_OPTIONS) {
            if (!option.isDisabled && option.modifier == null && this.key.matches(option.value)) {
                if (this.hasCollision) {
                    tooltip.append(", ");
                }

                this.hasCollision = true;
                tooltip.append(option.label);
            }
        }
    }
}