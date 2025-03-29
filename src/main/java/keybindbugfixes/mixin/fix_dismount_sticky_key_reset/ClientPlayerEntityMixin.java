package keybindbugfixes.mixin.fix_dismount_sticky_key_reset;

import keybindbugfixes.config.Config;
import keybindbugfixes.mixin.StickyKeyBindingAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.KeyBinding;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayerEntityMixin {
    @Shadow @Final private MinecraftClient client;
    @Shadow protected abstract boolean isSneaking();

    @Inject(method = "tick",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/network/ClientPlayerEntity;" +
                            "getRootVehicle()Lnet/minecraft/entity/Entity;"))
    private void untoggleSneakKeyOnDismount(CallbackInfo callbackInfo) {
        if (Config.BugFixes.FIX_DISMOUNT_STICKY_KEY_RESET && this.isSneaking()) {
            KeyBinding sneakKeyBinding = this.client.options.sneakKey;
            StickyKeyBindingAccessor accessor = (StickyKeyBindingAccessor) sneakKeyBinding;

            if (accessor.getToggleGetter().getAsBoolean()) {
                sneakKeyBinding.setPressed(true);
            }
        }
    }
}