package keybindbugfixes;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import keybindbugfixes.config.Config;
import keybindbugfixes.config.ConfigManager;
import keybindbugfixes.mixin.KeyBindingAccessor;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.option.StickyKeyBinding;
import org.apache.commons.compress.utils.Sets;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Set;

public class KeybindBugFixes implements ClientModInitializer {
	public static final String MOD_NAME = "KeybindBugFixes";
	public static final String MOD_ID = "keybindbugfixes";

	public static Map<StickyKeyBinding, Boolean> stickyKeyBindingRevertMap = Maps.newHashMap();
	public static boolean draggingPickKey = false;

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAME);
	public static final Gson GSON;

	static {
		GsonBuilder builder = new GsonBuilder();
		GSON = builder.setPrettyPrinting().create();
	}

	public static void revertStickyKeyBindings() {
		if (Config.BugFixes.FIX_CONTROL_STICKY_KEY_RESET) {
			for (Map.Entry<StickyKeyBinding, Boolean> entry : stickyKeyBindingRevertMap.entrySet()) {
				((KeyBindingAccessor) entry.getKey()).setPressedState(entry.getValue());
			}
		}
	}

	@Override
	public void onInitializeClient() {
		 ConfigManager.init();
	}
}