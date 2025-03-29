package keybindbugfixes;

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

import java.util.Set;

public class KeybindBugFixes implements ClientModInitializer {
	public static final String MOD_NAME = "KeybindBugFixes";
	public static final String MOD_ID = "keybindbugfixes";

	public static final Set<StickyKeyBinding> STICKY_KEY_BINDINGS = Sets.newHashSet();
	public static boolean draggingPickKey = false;

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAME);
	public static final Gson GSON;

	static {
		GsonBuilder builder = new GsonBuilder();
		GSON = builder.setPrettyPrinting().create();
	}

	public static void toggleLeftControlStickyKeys() {
		if (Config.BugFixes.FIX_CONTROL_STICKY_KEY_RESET && Screen.hasControlDown()) {
			for (StickyKeyBinding stickyKeyBinding : STICKY_KEY_BINDINGS) {
				if (((KeyBindingAccessor) stickyKeyBinding).getBoundKey().getCode() == GLFW.GLFW_KEY_LEFT_CONTROL) {
					stickyKeyBinding.setPressed(true);
				}
			}
		}
	}

	@Override
	public void onInitializeClient() {
		 ConfigManager.init();
	}
}