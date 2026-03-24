package keybindbugfixes.mixin;

import net.minecraft.client.util.InputUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(InputUtil.Key.class)
public interface KeyAccessor {
    @Accessor("type")
    InputUtil.Type getType();
}