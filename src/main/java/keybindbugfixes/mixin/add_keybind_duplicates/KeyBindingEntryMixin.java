package keybindbugfixes.mixin.add_keybind_duplicates;

import com.llamalad7.mixinextras.sugar.Local;
import keybindbugfixes.config.ConfigManager;
import keybindbugfixes.mixin.KeyBindingAccessor;
import net.minecraft.client.gui.screen.option.ControlsListWidget;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ControlsListWidget.KeyBindingEntry.class)
public abstract class KeyBindingEntryMixin {
    @Shadow @Final private KeyBinding binding;
    @Shadow private boolean duplicate;

    @Redirect(method = "update",
            at = @At(value = "FIELD",
                    target = "Lnet/minecraft/client/gui/screen/option/ControlsListWidget$KeyBindingEntry;duplicate:Z",
                    opcode = Opcodes.GETFIELD, ordinal = 1))
    private boolean addModdedDuplicateKeys(ControlsListWidget.KeyBindingEntry keyBindingEntry, @Local MutableText duplicateText) {
        if (!this.binding.isUnbound()) {
            for (ConfigManager.KeybindOption option : ConfigManager.KEYBIND_OPTIONS) {
                if (!option.isDisabled() && option.modifier() == null && option.value().equals(((KeyBindingAccessor) this.binding).getBoundKey())) {
                    if (this.duplicate) {
                        duplicateText.append(", ");
                    }

                    this.duplicate = true;
                    duplicateText.append(Text.translatable(option.translationKey()));
                }
            }
        }

        return this.duplicate;
    }
}