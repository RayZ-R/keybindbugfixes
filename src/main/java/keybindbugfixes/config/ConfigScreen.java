package keybindbugfixes.config;

import keybindbugfixes.KeybindBugFixes;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ThreePartsLayoutWidget;
import net.minecraft.client.util.InputUtil;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

public class ConfigScreen extends Screen {
    private static final Text TITLE_TEXT = Text.translatable(KeybindBugFixes.MOD_ID + ".config.title");
    protected final ThreePartsLayoutWidget layout = new ThreePartsLayoutWidget(this);
    protected final Screen parent;
    protected BugListWidget list;

    @Nullable public BugListWidget.KeybindWidgetEntry selectedKeybindWidget;

    protected ConfigScreen(Screen parent) {
        super(TITLE_TEXT);
        this.parent = parent;
    }

    @Override
    protected void init() {
        this.list = this.addDrawableChild(new BugListWidget(this.client, this));
        this.list.addCategories(ConfigManager.CATEGORIES);

        this.initHeader();
        this.initFooter();
        this.layout.forEachChild(this::addDrawableChild);
        this.initTabNavigation();
    }

    protected void initHeader() {
        this.layout.addHeader(this.title, this.textRenderer);
    }

    protected void initFooter() {
        this.layout.addFooter(ButtonWidget.builder(ScreenTexts.DONE, button -> this.close()).width(200).build());
    }

    @Override
    protected void initTabNavigation() {
        this.layout.refreshPositions();
        this.list.position(this.width, this.layout);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (this.selectedKeybindWidget != null) {
            ConfigManager.KeybindOption keybindOption = this.selectedKeybindWidget.option;
            keybindOption.setValue(InputUtil.Type.MOUSE.createFromCode(button));
            this.selectedKeybindWidget.updateButtonState();
            this.selectedKeybindWidget = null;
            return true;
        } else {
            return super.mouseClicked(mouseX, mouseY, button);
        }
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (this.selectedKeybindWidget != null) {
            ConfigManager.KeybindOption keybindOption = this.selectedKeybindWidget.option;

            if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
                keybindOption.setValue(InputUtil.UNKNOWN_KEY);
            } else {
                keybindOption.setValue(InputUtil.fromKeyCode(keyCode, scanCode));
            }

            this.selectedKeybindWidget.updateButtonState();
            this.selectedKeybindWidget = null;
            return true;
        } else {
            return super.keyPressed(keyCode, scanCode, modifiers);
        }
    }

    @Override
    public void removed() {
        ConfigManager.saveOptionValues();
    }

    @Override
    public void close() {
        this.client.setScreen(this.parent);
    }
}