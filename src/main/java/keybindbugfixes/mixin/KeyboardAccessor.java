package keybindbugfixes.mixin;

import net.minecraft.client.Keyboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Keyboard.class)
public interface KeyboardAccessor {
    @Accessor("switchF3State")
    void setSwitchF3State(boolean switchF3State);
}