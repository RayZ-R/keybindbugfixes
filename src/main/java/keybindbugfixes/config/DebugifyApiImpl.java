package keybindbugfixes.config;

import com.google.common.collect.Lists;
import dev.isxander.debugify.api.DebugifyApi;
import keybindbugfixes.KeybindBugFixes;

import java.util.List;

public class DebugifyApiImpl implements DebugifyApi {
    @Override
    public String[] getDisabledFixes() {
        KeybindBugFixes.disableMixins();

        List<String> disabledFixes = Lists.newArrayList();

        for (ConfigManager.OptionInfo optionInfo : ConfigManager.OPTION_INFOS) {
            int bugId = optionInfo.bugId();

            if (bugId != -1) {
                disabledFixes.add("MC-" + bugId);
            }
        }

        return disabledFixes.toArray(String[]::new);
    }
}