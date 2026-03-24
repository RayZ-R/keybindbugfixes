package keybindbugfixes.mixin.reload_resources_anywhere;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import keybindbugfixes.config.Config;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Overlay;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin {
    @ModifyExpressionValue(
            method = "startIntegratedServer",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/client/MinecraftClient;" +
                            "overlay:Lnet/minecraft/client/gui/screen/Overlay;"
            )
    )
    public Overlay preventInfiniteLoading(Overlay original) {
        return Config.RELOAD_RESOURCES_ANYWHERE.value ? null : original;
    }
}