package keybindbugfixes;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import keybindbugfixes.config.ConfigManager;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.option.StickyKeyBinding;
import org.apache.commons.compress.utils.Sets;
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

	@Override
	public void onInitializeClient() {
		 ConfigManager.init();
	}
}