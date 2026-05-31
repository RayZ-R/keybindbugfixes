package keybindbugfixes.config;

import com.mojang.blaze3d.platform.InputConstants;
import keybindbugfixes.KeybindBugFixes;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

public class ConfigScreen extends Screen {
    private static final Component TITLE = Component.translatable(KeybindBugFixes.MOD_ID + ".config.title");
    private final Screen lastScreen;

    public final HeaderAndFooterLayout layout = new HeaderAndFooterLayout(this);
    private ConfigListWidget configListWidget;

    @Nullable public ConfigListWidget.KeyEntry selectedKeyEntry;
    private boolean skipNextKeyRelease = false;

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
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        if (this.selectedKeyEntry != null) {
            this.selectedKeyEntry.option.value = InputConstants.Type.MOUSE.getOrCreate(event.button());

            this.selectedKeyEntry = null;
            this.configListWidget.refreshKeyEntries();
            return true;
        } else {
            return super.mouseClicked(event, doubleClick);
        }
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        if (this.selectedKeyEntry != null) {
            if (event.isEscape()) {
                this.selectedKeyEntry.option.value = InputConstants.UNKNOWN;
            } else {
                this.selectedKeyEntry.option.value = InputConstants.getKey(event);
            }

            this.selectedKeyEntry = null;
            this.configListWidget.refreshKeyEntries();
            this.skipNextKeyRelease = true;
            return true;
        } else {
            return super.keyPressed(event);
        }
    }

    @Override
    public boolean keyReleased(KeyEvent event) {
        if (this.skipNextKeyRelease) {
            this.skipNextKeyRelease = false;
            return true;
        } else {
            return super.keyReleased(event);
        }
    }

    @Override
    public void removed() {
        Config.saveJson();
    }

    @Override
    public void onClose() {
        this.minecraft.gui.setScreen(this.lastScreen);
    }
}