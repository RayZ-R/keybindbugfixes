package keybindbugfixes;

import keybindbugfixes.config.ConfigManager;
import net.fabricmc.loader.api.FabricLoader;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

public class MixinPlugin implements IMixinConfigPlugin {
    public static final boolean KEYBINDS_ENABLED = !FabricLoader.getInstance().isModLoaded("rebind_all_the_keys");

    public static boolean shouldAddOption(ConfigManager.Option<?> option) {
        if (!KEYBINDS_ENABLED && (option.name().equals("key.debug")
                || option.name().equals("key.game_mode_cycle")
                || option.name().equals("tweak.remove_keybind_conflicts"))) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        if (!KEYBINDS_ENABLED && (mixinClassName.startsWith("keybindbugfixes.mixin.rebind_debug_keys")
                || mixinClassName.startsWith("keybindbugfixes.mixin.remove_keybind_conflicts"))) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onLoad(String mixinPackage) {}

    @Override
    public String getRefMapperConfig() {
        return "";
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {}

    @Override
    public List<String> getMixins() {
        return List.of();
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {}

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {}
}