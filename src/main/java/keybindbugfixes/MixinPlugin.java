package keybindbugfixes;

import com.google.common.collect.Sets;
import net.fabricmc.loader.api.FabricLoader;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

public class MixinPlugin implements IMixinConfigPlugin {
    public static final FabricLoader FABRIC_LOADER = FabricLoader.getInstance();
    public static final Set<String> DISABLED_MIXINS = Sets.newHashSet();

    public static boolean isDisabled(String mixin) {
        boolean isDisabled = false;

        for (String disabledMixin : MixinPlugin.DISABLED_MIXINS) {
            if (mixin.equals(disabledMixin)) {
                isDisabled = true;
                break;
            }
        }

        return isDisabled;
    }

    static {
        boolean isRebindAllTheKeysLoaded = FABRIC_LOADER.isModLoaded("rebind_all_the_keys");
        boolean isAmecsApiLoaded = FABRIC_LOADER.isModLoaded("amecsapi");
        boolean isNmukLoaded = FABRIC_LOADER.isModLoaded("nmuk");
        boolean isRrlsLoaded = FABRIC_LOADER.isModLoaded("rrls");

        if (isRebindAllTheKeysLoaded || isAmecsApiLoaded || isNmukLoaded) {
            DISABLED_MIXINS.add("remove_keybind_conflicts");
        }

        if (isRebindAllTheKeysLoaded) {
            DISABLED_MIXINS.add("rebind_debug_keys");
        }

        if (isRrlsLoaded) {
            DISABLED_MIXINS.add("reload_resources_anywhere.MinecraftMixin");
        }

        if (!isAmecsApiLoaded) {
            DISABLED_MIXINS.add("fix_modifier_sticky_key.KeyMappingMixin");
        }
    }

    @Override
    public void onLoad(String mixinPackage) {

    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        for (String mixin : DISABLED_MIXINS) {
            if (mixinClassName.startsWith(KeybindBugFixes.MOD_ID + ".mixin." + mixin)) {
                return false;
            }
        }

        return true;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {

    }

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

    }
}