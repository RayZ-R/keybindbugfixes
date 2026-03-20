package keybindbugfixes.mixin.fix_dismount_toggle_sneak;

import com.mojang.authlib.GameProfile;
import keybindbugfixes.mixin.StickyKeyBindingAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.PlayerInput;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayerEntityMixin extends AbstractClientPlayerEntity {
    @Shadow @Final private MinecraftClient client;
    @Unique private boolean keybindbugfixes$modifySneakInput = false;

    public ClientPlayerEntityMixin(ClientWorld world, GameProfile profile) {
        super(world, profile);
    }

    @Inject(method = "tick",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/network/ClientPlayNetworkHandler;" +
                            "sendPacket(Lnet/minecraft/network/packet/Packet;)V",
                    shift = At.Shift.AFTER,
                    ordinal = 0))
    private void untoggleSneakKeyOnDismount(CallbackInfo callbackInfo) {
        if (keybindbugfixes.config.Config.FIX_DISMOUNT_TOGGLE_SNEAK.value && this.hasVehicle() && this.isSneaking()) {
            KeyBinding sneakKeyBinding = this.client.options.sneakKey;
            StickyKeyBindingAccessor accessor = (StickyKeyBindingAccessor) sneakKeyBinding;

            if (accessor.getToggleGetter().getAsBoolean()) {
                this.keybindbugfixes$modifySneakInput = true;
                sneakKeyBinding.setPressed(true);
            }
        }
    }

    @Inject(method = "dismountVehicle", at = @At("HEAD"))
    private void dismountVehicle(CallbackInfo callbackInfo) {
        this.keybindbugfixes$modifySneakInput = false;
    }

    @ModifyArg(method = "tick",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/network/packet/c2s/play/PlayerInputC2SPacket;" +
                            "<init>(Lnet/minecraft/util/PlayerInput;)V"))
    private PlayerInput modifyInputPacket(PlayerInput input) {
        return new PlayerInput(
                input.forward(),
                input.backward(),
                input.left(),
                input.right(),
                input.jump(),
                this.keybindbugfixes$modifySneakInput ? true : input.sneak(),
                input.sprint()
        );
    }
}