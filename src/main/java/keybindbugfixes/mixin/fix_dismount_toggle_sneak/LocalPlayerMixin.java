package keybindbugfixes.mixin.fix_dismount_toggle_sneak;

import keybindbugfixes.config.Config;
import keybindbugfixes.mixin.ToggleKeyMappingAccessor;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LocalPlayer.class)
public abstract class LocalPlayerMixin {
    @Shadow @Final private Minecraft minecraft;
    @Shadow protected abstract boolean isShiftKeyDown();

    @Unique private boolean keybindbugfixes$overrideShiftingPacket = false;

    @Inject(
            method = "tick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/multiplayer/ClientPacketListener;" +
                            "send(Lnet/minecraft/network/protocol/Packet;)V",
                    shift = At.Shift.AFTER,
                    ordinal = 1
            )
    )
    private void untoggleShiftKeyOnDismount(CallbackInfo callbackInfo) {
        if (Config.FIX_DISMOUNT_TOGGLE_SNEAK.value && this.isShiftKeyDown()) {
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

    @ModifyArg(
            method = "tick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/network/protocol/game/ServerboundPlayerInputPacket;<init>(FFZZ)V"
            ),
            index = 3
    )
    private boolean overrideShiftingPacket(boolean original) {
        return this.keybindbugfixes$overrideShiftingPacket ? true : original;
    }
}