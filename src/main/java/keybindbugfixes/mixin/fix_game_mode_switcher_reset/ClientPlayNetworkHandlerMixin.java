package keybindbugfixes.mixin.fix_game_mode_switcher_reset;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import keybindbugfixes.config.Config;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.world.GameMode;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ClientPlayNetworkHandler.class)
public abstract class ClientPlayNetworkHandlerMixin {
    @WrapWithCondition(method = "onPlayerRespawn",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/network/ClientPlayerInteractionManager;" +
                            "setGameModes(Lnet/minecraft/world/GameMode;Lnet/minecraft/world/GameMode;)V"))
    private boolean preventGameModeSwitcherResetOnDeath(ClientPlayerInteractionManager interactionManager,
                                                        GameMode gameMode, GameMode previousGameMode) {
        return !Config.FIX_GAME_MODE_SWITCHER_RESET.value;
    }
}