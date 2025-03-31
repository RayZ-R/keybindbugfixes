package keybindbugfixes;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import keybindbugfixes.config.Config;
import keybindbugfixes.config.ConfigManager;
import keybindbugfixes.mixin.KeyBindingAccessor;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.option.StickyKeyBinding;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class KeybindBugFixes implements ClientModInitializer {
	public static final String MOD_NAME = "KeybindBugFixes";
	public static final String MOD_ID = "keybindbugfixes";

	public static Map<StickyKeyBinding, Boolean> stickyKeyBindingRevertMap = Maps.newHashMap();
	public static boolean draggingPickKey = false;
	public static MinecraftClient client;

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAME);
	public static final Gson GSON;

	static {
		GsonBuilder builder = new GsonBuilder();
		GSON = builder.setPrettyPrinting().create();
	}

	public static void revertStickyKeyBindings() {
		if (Config.BugFixes.FIX_CONTROL_STICKY_KEY_RESET) {
			for (Map.Entry<StickyKeyBinding, Boolean> entry : stickyKeyBindingRevertMap.entrySet()) {
				KeyBinding keyBinding = entry.getKey();
				boolean resetValue = entry.getValue();

				((KeyBindingAccessor) keyBinding).setPressedState(resetValue);

				if (keyBinding.equals(client.options.sprintKey) && !resetValue) {
					client.player.setSprinting(false);
				}
			}
		}
	}

	@Override
	public void onInitializeClient() {
		client = MinecraftClient.getInstance();
		ConfigManager.init();
	}
}