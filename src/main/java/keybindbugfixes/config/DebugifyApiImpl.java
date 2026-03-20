package keybindbugfixes.config;

import com.google.common.collect.Lists;
import dev.isxander.debugify.api.DebugifyApi;
import keybindbugfixes.config.option.Option;

import java.util.List;

public class DebugifyApiImpl implements DebugifyApi {
    @Override
    public String[] getDisabledFixes() {
        List<String> disabledFixes = Lists.newArrayList();

        for (Option<?> option : Config.OPTIONS) {
            if (option.bugId == null || option.isDisabled) continue;
            disabledFixes.add(option.bugId);
        }

        return disabledFixes.toArray(String[]::new);
    }
}