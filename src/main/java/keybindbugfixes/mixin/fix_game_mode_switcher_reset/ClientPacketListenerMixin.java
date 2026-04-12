package keybindbugfixes.mixin.fix_game_mode_switcher_reset;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import keybindbugfixes.config.Config;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.world.level.GameType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ClientPacketListener.class)
public abstract class ClientPacketListenerMixin {
    @WrapWithCondition(
            method = "handleRespawn",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/multiplayer/MultiPlayerGameMode;" +
                            "setLocalMode(Lnet/minecraft/world/level/GameType;Lnet/minecraft/world/level/GameType;)V"
            )
    )
    private boolean preventLastGameModeResetOnRespawn(MultiPlayerGameMode gameMode,
                                                      GameType gameType, GameType previousGameType) {
        return !Config.FIX_GAME_MODE_SWITCHER_RESET.value;
    }
}