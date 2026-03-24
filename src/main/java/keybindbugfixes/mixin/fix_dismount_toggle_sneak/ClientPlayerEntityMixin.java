package keybindbugfixes.mixin.fix_dismount_toggle_sneak;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.authlib.GameProfile;
import keybindbugfixes.config.Config;
import keybindbugfixes.mixin.StickyKeyBindingAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.world.ClientWorld;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayerEntityMixin extends AbstractClientPlayerEntity {
    @Shadow @Final private MinecraftClient client;

    @Unique private boolean keybindbugfixes$overrideSneakingPacket = false;

    public ClientPlayerEntityMixin(ClientWorld world, GameProfile profile) {
        super(world, profile);
    }

    @Inject(
            method = "tick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/network/ClientPlayNetworkHandler;" +
                            "sendPacket(Lnet/minecraft/network/packet/Packet;)V",
                    shift = At.Shift.AFTER,
                    ordinal = 0
            )
    )
    private void untoggleSneakKeyOnDismount(CallbackInfo callbackInfo) {
        if (Config.FIX_DISMOUNT_TOGGLE_SNEAK.value && this.hasVehicle() && this.isSneaking()) {
            KeyBinding sneakKeyBinding = this.client.options.sneakKey;
            StickyKeyBindingAccessor accessor = (StickyKeyBindingAccessor) sneakKeyBinding;

            if (accessor.getToggleGetter().getAsBoolean()) {
                this.keybindbugfixes$overrideSneakingPacket = true;
                sneakKeyBinding.setPressed(true);
            }
        }
    }

    @Inject(method = "dismountVehicle", at = @At("HEAD"))
    private void stopOverridingPacket(CallbackInfo callbackInfo) {
        this.keybindbugfixes$overrideSneakingPacket = false;
    }

    @WrapOperation(
            method = "sendSneakingPacket",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/network/ClientPlayerEntity;isSneaking()Z"
            )
    )
    private boolean overrideSneakingPacket(ClientPlayerEntity clientPlayerEntity, Operation<Boolean> original) {
        return this.keybindbugfixes$overrideSneakingPacket ? true : original.call(clientPlayerEntity);
    }
}