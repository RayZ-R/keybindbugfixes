package keybindbugfixes.mixin.add_keybind_duplicates;

import com.llamalad7.mixinextras.sugar.Local;
import keybindbugfixes.config.Config;
import keybindbugfixes.config.option.KeybindOption;
import keybindbugfixes.mixin.KeyBindingAccessor;
import net.minecraft.client.gui.screen.option.ControlsListWidget;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.MutableText;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ControlsListWidget.KeyBindingEntry.class)
public abstract class KeyBindingEntryMixin {
    @Shadow @Final private KeyBinding binding;
    @Shadow private boolean duplicate;

    @Inject(
            method = "update",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/client/gui/screen/option/ControlsListWidget$KeyBindingEntry;duplicate:Z",
                    opcode = Opcodes.GETFIELD,
                    ordinal = 1
            )
    )
    private void handleModdedDuplicates(CallbackInfo callbackInfo, @Local MutableText duplicateText) {
        InputUtil.Key key = ((KeyBindingAccessor) this.binding).getBoundKey();

        if (!this.binding.isUnbound()) {
            for (KeybindOption option : Config.KEYBIND_OPTIONS) {
                if (!option.isDisabled && option.modifier == null && key.equals(option.value)) {
                    if (this.duplicate) {
                        duplicateText.append(", ");
                    }

                    this.duplicate = true;
                    duplicateText.append(option.label);
                }
            }
        }
    }
}