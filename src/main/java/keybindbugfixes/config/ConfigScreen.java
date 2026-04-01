package keybindbugfixes.config;

import keybindbugfixes.KeybindBugFixes;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ThreePartsLayoutWidget;
import net.minecraft.client.input.KeyInput;
import net.minecraft.client.util.InputUtil;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

public class ConfigScreen extends Screen {
    private static final Text TITLE_TEXT = Text.translatable(KeybindBugFixes.MOD_ID + ".config.title");
    private final Screen parent;

    public final ThreePartsLayoutWidget layout = new ThreePartsLayoutWidget(this);
    private ConfigListWidget configListWidget;

    @Nullable public ConfigListWidget.KeybindEntry selectedKeybindEntry;
    private boolean skipNextKeyRelease = false;

    public ConfigScreen(Screen parent) {
        super(TITLE_TEXT);
        this.parent = parent;
    }

    @Override
    protected void init() {
        this.initHeader();
        this.initBody();
        this.initFooter();
        this.layout.forEachChild(this::addDrawableChild);
        this.refreshWidgetPositions();
    }

    private void initHeader() {
        this.layout.addHeader(this.title, this.textRenderer);
    }

    private void initBody() {
        this.configListWidget = this.layout.addBody(new ConfigListWidget(this, this.client));
    }

    private void initFooter() {
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
    public boolean mouseClicked(Click click, boolean doubled) {
        if (this.selectedKeybindEntry != null) {
            this.selectedKeybindEntry.option.value = InputUtil.Type.MOUSE.createFromCode(click.button());

            this.selectedKeybindEntry = null;
            this.configListWidget.updateKeybindEntries();
            return true;
        } else {
            return super.mouseClicked(click, doubled);
        }
    }

    @Override
    public boolean keyPressed(KeyInput input) {
        if (this.selectedKeybindEntry != null) {
            if (input.isEscape()) {
                this.selectedKeybindEntry.option.value = InputUtil.UNKNOWN_KEY;
            } else {
                this.selectedKeybindEntry.option.value = InputUtil.fromKeyCode(input);
            }

            this.selectedKeybindEntry = null;
            this.configListWidget.updateKeybindEntries();
            this.skipNextKeyRelease = true;
            return true;
        } else {
            return super.keyPressed(input);
        }
    }

    @Override
    public boolean keyReleased(KeyInput input) {
        if (this.skipNextKeyRelease) {
            this.skipNextKeyRelease = false;
            return true;
        } else {
            return super.keyReleased(input);
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