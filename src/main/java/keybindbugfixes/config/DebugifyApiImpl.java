package keybindbugfixes.config;

import com.google.common.collect.Lists;
import dev.isxander.debugify.api.DebugifyApi;

import java.util.List;

public class DebugifyApiImpl implements DebugifyApi {
    @Override
    public String[] getDisabledFixes() {
        List<String> disabledFixes = Lists.newArrayList();

        for (ConfigManager.BugOption option : ConfigManager.BUG_OPTIONS) {
            int id = option.id();

            if (id != -1) {
                disabledFixes.add("MC-" + id);
            }
        }

        return disabledFixes.toArray(String[]::new);
    }
}