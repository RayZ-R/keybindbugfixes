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
    private final Screen parent;

    public final ThreePartsLayoutWidget layout = new ThreePartsLayoutWidget(this);
    private ConfigListWidget configListWidget;

    @Nullable public ConfigListWidget.KeybindEntry selectedKeybindEntry;

    public ConfigScreen(Screen parent) {
        super(TITLE_TEXT);
        this.parent = parent;
    }

    @Override
    protected void init() {
        this.configListWidget = this.addDrawableChild(new ConfigListWidget(this, this.client));

        this.initHeader();
        this.initFooter();
        this.layout.forEachChild(this::addDrawableChild);
        this.refreshWidgetPositions();
    }

    protected void initHeader() {
        this.layout.addHeader(this.title, this.textRenderer);
    }

    protected void initFooter() {
        ButtonWidget buttonWidget = ButtonWidget.builder(ScreenTexts.DONE, button -> this.close())
                .width(200)
                .build();

        this.layout.addFooter(buttonWidget);
    }

    @Override
    protected void refreshWidgetPositions() {
        this.layout.refreshPositions();
        this.configListWidget.position(this.width, this.layout);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (this.selectedKeybindEntry != null) {
            this.selectedKeybindEntry.option.value = InputUtil.Type.MOUSE.createFromCode(button);

            this.selectedKeybindEntry = null;
            this.configListWidget.updateKeybindEntries();
            return true;
        } else {
            return super.mouseClicked(mouseX, mouseY, button);
        }
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (this.selectedKeybindEntry != null) {
            if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
                this.selectedKeybindEntry.option.value = InputUtil.UNKNOWN_KEY;
            } else {
                this.selectedKeybindEntry.option.value = InputUtil.fromKeyCode(keyCode, scanCode);
            }

            this.selectedKeybindEntry = null;
            this.configListWidget.updateKeybindEntries();
            return true;
        } else {
            return super.keyPressed(keyCode, scanCode, modifiers);
        }
    }

    @Override
    public void removed() {
        Config.saveJson();
    }

    @Override
    public void close() {
        this.client.setScreen(this.parent);
    }
}