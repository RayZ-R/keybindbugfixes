package keybindbugfixes.mixin.reload_resources_anywhere;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import keybindbugfixes.config.Config;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Overlay;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin {
    @ModifyExpressionValue(
            method = "doWorldLoad",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/client/Minecraft;" +
                            "overlay:Lnet/minecraft/client/gui/screens/Overlay;",
                    opcode = Opcodes.GETFIELD
            )
    )
    private Overlay preventInfiniteLoading(Overlay original) {
        return Config.RELOAD_RESOURCES_ANYWHERE.value ? null : original;
    }
}