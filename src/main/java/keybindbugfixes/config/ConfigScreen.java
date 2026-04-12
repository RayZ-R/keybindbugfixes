package keybindbugfixes.config;

import com.mojang.blaze3d.platform.InputConstants;
import keybindbugfixes.KeybindBugFixes;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

public class ConfigScreen extends Screen {
    private static final Component TITLE = Component.translatable(KeybindBugFixes.MOD_ID + ".config.title");
    private final Screen lastScreen;

    public final HeaderAndFooterLayout layout = new HeaderAndFooterLayout(this);
    private ConfigListWidget configListWidget;

    @Nullable public ConfigListWidget.KeyEntry selectedKeyEntry;

    public ConfigScreen(Screen lastScreen) {
        super(TITLE);
        this.lastScreen = lastScreen;
    }

    @Override
    protected void init() {
        this.addTitle();
        this.addContents();
        this.addFooter();
        this.layout.visitWidgets(this::addRenderableWidget);
        this.repositionElements();
    }

    private void addTitle() {
        this.layout.addTitleHeader(this.title, this.font);
    }

    private void addContents() {
        this.configListWidget = this.layout.addToContents(new ConfigListWidget(this, this.minecraft));
    }

    private void addFooter() {
        Button buttonWidget = Button.builder(CommonComponents.GUI_DONE, button -> this.onClose())
                .width(200)
                .build();

        this.layout.addToFooter(buttonWidget);
    }

    @Override
    protected void repositionElements() {
        this.layout.arrangeElements();
        this.configListWidget.updateSize(this.width, this.layout);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (this.selectedKeyEntry != null) {
            this.selectedKeyEntry.option.value = InputConstants.Type.MOUSE.getOrCreate(button);

            this.selectedKeyEntry = null;
            this.configListWidget.refreshKeyEntries();
            return true;
        } else {
            return super.mouseClicked(mouseX, mouseY, button);
        }
    }

    @Override
    public boolean keyPressed(int keycode, int scancode, int modifiers) {
        if (this.selectedKeyEntry != null) {
            if (keycode == InputConstants.KEY_ESCAPE) {
                this.selectedKeyEntry.option.value = InputConstants.UNKNOWN;
            } else {
                this.selectedKeyEntry.option.value = InputConstants.getKey(keycode, scancode);
            }

            this.selectedKeyEntry = null;
            this.configListWidget.refreshKeyEntries();
            return true;
        } else {
            return super.keyPressed(keycode, scancode, modifiers);
        }
    }

    @Override
    public void removed() {
        Config.saveJson();
    }

    @Override
    public void onClose() {
        this.minecraft.setScreen(this.lastScreen);
    }
}