package keybindbugfixes.mixin.fix_dismount_toggle_sneak;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.authlib.GameProfile;
import keybindbugfixes.config.Config;
import keybindbugfixes.mixin.ToggleKeyMappingAccessor;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LocalPlayer.class)
public abstract class LocalPlayerMixin extends AbstractClientPlayer {
    @Shadow @Final private Minecraft minecraft;

    @Unique private boolean keybindbugfixes$overrideShiftingPacket = false;

    public LocalPlayerMixin(ClientLevel level, GameProfile gameProfile) {
        super(level, gameProfile);
    }

    @Inject(
            method = "tick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/multiplayer/ClientPacketListener;" +
                            "send(Lnet/minecraft/network/protocol/Packet;)V",
                    shift = At.Shift.AFTER,
                    ordinal = 0
            )
    )
    private void untoggleShiftKeyOnDismount(CallbackInfo callbackInfo) {
        if (Config.FIX_DISMOUNT_TOGGLE_SNEAK.value && this.isPassenger() && this.isShiftKeyDown()) {
            KeyMapping keyShift = this.minecraft.options.keyShift;
            ToggleKeyMappingAccessor accessor = (ToggleKeyMappingAccessor) keyShift;

            if (accessor.getNeedsToggle().getAsBoolean()) {
                this.keybindbugfixes$overrideShiftingPacket = true;
                keyShift.setDown(true);
            }
        }
    }

    @Inject(method = "removeVehicle", at = @At("HEAD"))
    private void stopOverridingPacket(CallbackInfo callbackInfo) {
        this.keybindbugfixes$overrideShiftingPacket = false;
    }

    @WrapOperation(
            method = "sendShiftKeyState",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/player/LocalPlayer;isShiftKeyDown()Z"
            )
    )
    private boolean overrideShiftingPacket(LocalPlayer player, Operation<Boolean> original) {
        return this.keybindbugfixes$overrideShiftingPacket ? true : original.call(player);
    }
}