package keybindbugfixes.mixin.fix_dismount_sticky_key_reset;

import keybindbugfixes.config.Config;
import keybindbugfixes.mixin.StickyKeyBindingAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.KeyBinding;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayerEntityMixin {
    @Shadow @Final private MinecraftClient client;
    @Shadow protected abstract boolean isSneaking();

    @Unique private boolean keybindbugfixes$overrideSneakingPacket = false;

    @Inject(method = "tick",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/network/ClientPlayerEntity;" +
                            "getRootVehicle()Lnet/minecraft/entity/Entity;"))
    private void untoggleSneakKeyOnDismount(CallbackInfo callbackInfo) {
        if (Config.BugFixes.FIX_DISMOUNT_STICKY_KEY_RESET && this.isSneaking()) {
            KeyBinding sneakKeyBinding = this.client.options.sneakKey;
            StickyKeyBindingAccessor accessor = (StickyKeyBindingAccessor) sneakKeyBinding;

            if (accessor.getToggleGetter().getAsBoolean()) {
                keybindbugfixes$overrideSneakingPacket = true;
                sneakKeyBinding.setPressed(true);
            }
        }
    }

    @Inject(method = "dismountVehicle", at = @At("HEAD"))
    private void dismountVehicle(CallbackInfo callbackInfo) {
        keybindbugfixes$overrideSneakingPacket = false;
    }

    @ModifyArg(method = "tick",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/network/packet/c2s/play/PlayerInputC2SPacket;<init>(FFZZ)V"),
            index = 3)
    private boolean overrideSneakingPacket(boolean original) {
        return keybindbugfixes$overrideSneakingPacket ? true : original;
    }
}