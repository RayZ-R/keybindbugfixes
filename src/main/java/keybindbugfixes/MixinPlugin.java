package keybindbugfixes;

import com.google.common.collect.Sets;
import keybindbugfixes.config.ConfigManager;
import net.fabricmc.loader.api.FabricLoader;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

public class MixinPlugin implements IMixinConfigPlugin {
    public static final Set<String> DISABLED_MIXIN_NAMES = Sets.newHashSet();
    public static final Set<String> DISABLED_OPTION_NAMES = Sets.newHashSet();

    static {
        FabricLoader fabricLoader = FabricLoader.getInstance();
        boolean isRebindAllTheKeysLoaded = fabricLoader.isModLoaded("rebind_all_the_keys");
        boolean isAmecsApiLoaded = fabricLoader.isModLoaded("amecsapi");
        boolean isNmukLoaded = fabricLoader.isModLoaded("nmuk");

        if (isRebindAllTheKeysLoaded || isAmecsApiLoaded || isNmukLoaded) {
            DISABLED_MIXIN_NAMES.add("remove_keybind_conflicts");
            DISABLED_OPTION_NAMES.add("tweak.remove_keybind_conflicts");
        }

        if (isRebindAllTheKeysLoaded) {
            DISABLED_MIXIN_NAMES.add("rebind_debug_keys");
            DISABLED_OPTION_NAMES.add("key.debug");
            DISABLED_OPTION_NAMES.add("key.game_mode_cycle");
        }
    }

    public static boolean shouldAddOption(ConfigManager.Option<?> option) {
        return !DISABLED_OPTION_NAMES.contains(option.name());
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        for (String mixinName : DISABLED_MIXIN_NAMES) {
            if (mixinClassName.startsWith("keybindbugfixes.mixin." + mixinName)) {
                return false;
            }
        }

        return true;
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