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
    protected final ThreePartsLayoutWidget layout = new ThreePartsLayoutWidget(this);
    protected OptionListWidget list;
    protected final Screen parent;
    private boolean skipNextKeyRelease = false;

    @Nullable public OptionListWidget.KeybindWidgetEntry selectedKeybindWidget;

    protected ConfigScreen(Screen parent) {
        super(TITLE_TEXT);
        this.parent = parent;
    }

    @Override
    protected void init() {
        this.list = this.addDrawableChild(new OptionListWidget(this.client, this));
        this.list.init(ConfigManager.CATEGORIES);

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
        if (this.list != null) {
            this.list.position(this.width, this.layout);
        }
    }

    @Override
    public boolean mouseClicked(Click click, boolean doubled) {
        if (this.selectedKeybindWidget != null) {
            ConfigManager.KeybindOption keybindOption = this.selectedKeybindWidget.option();
            keybindOption.setValue(InputUtil.Type.MOUSE.createFromCode(click.button()));
            this.selectedKeybindWidget.updateButton();
            this.selectedKeybindWidget = null;
            return true;
        } else {
            return super.mouseClicked(click, doubled);
        }
    }

    @Override
    public boolean keyPressed(KeyInput input) {
        if (this.selectedKeybindWidget != null) {
            ConfigManager.KeybindOption keybindOption = this.selectedKeybindWidget.option();

            if (input.isEscape()) {
                keybindOption.setValue(InputUtil.UNKNOWN_KEY);
            } else {
                keybindOption.setValue(InputUtil.fromKeyCode(input));
            }

            this.selectedKeybindWidget.updateButton();
            this.selectedKeybindWidget = null;
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
        ConfigManager.saveOptionValues();
    }

    @Override
    public void close() {
        this.client.setScreen(this.parent);
    }
}