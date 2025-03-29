package keybindbugfixes.config;

import com.google.common.collect.Sets;
import keybindbugfixes.KeybindBugFixes;
import keybindbugfixes.MixinPlugin;
import keybindbugfixes.mixin.KeyBindingAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ElementListWidget;
import net.minecraft.client.gui.widget.TextIconButtonWidget;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.MutableText;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

import java.util.List;
import java.util.Set;

public class BugListWidget extends ElementListWidget<BugListWidget.WidgetEntry> {
    private final Set<KeybindWidgetEntry> keybindWidgets = Sets.newHashSet();
    private final ConfigScreen configScreen;

    private static final int OPTION_WIDTH = 150;
    private static final int BUTTON_WIDTH = 20;
    private static final int PADDING = 7;
    private static final int GAP = 4;

    public BugListWidget(MinecraftClient client, ConfigScreen screen) {
        super(client, screen.width, screen.layout.getContentHeight(), screen.layout.getHeaderHeight(), 25);
        this.centerListVertically = false;
        this.configScreen = screen;
    }

    private void addBugOption(ConfigManager.BugOption option) {
        this.addEntry(new BugWidgetEntry(this.client, this.configScreen, option));
    }

    private void addTweakOption(ConfigManager.TweakOption option) {
        this.addEntry(new TweakWidgetEntry(this.client, this.configScreen, option));
    }

    private void addKeybindOption(ConfigManager.KeybindOption option) {
        KeybindWidgetEntry widget = new KeybindWidgetEntry(this.client, this.configScreen, option);
        this.keybindWidgets.add(widget);
        this.addEntry(widget);
    }

    private void addOption(ConfigManager.Option<?> option) {
        if (option instanceof ConfigManager.BugOption bugOption) {
            addBugOption(bugOption);
        } else if (option instanceof ConfigManager.TweakOption tweakOption) {
            addTweakOption(tweakOption);
        } else if (option instanceof ConfigManager.KeybindOption keybindOption) {
            addKeybindOption(keybindOption);
        }
    }

    private void addTitle(String key) {
        this.addEntry(new TitleWidgetEntry(this.client, this.configScreen, key));
    }

    public void addCategories(List<ConfigManager.Category> categories) {
        for (ConfigManager.Category category : categories) {
            boolean titleAdded = false;

            for (ConfigManager.Option<?> option : category.options()) {
                if (MixinPlugin.shouldAddOption(option)) {
                    if (!titleAdded) {
                        this.addTitle(category.translationKey());
                        titleAdded = true;
                    }

                    this.addOption(option);
                }
            }
        }
    }

    private void updateKeybindWidgets() {
        for (KeybindWidgetEntry widget : keybindWidgets) {
            widget.updateButtonText();
        }
    }

    @Override
    public int getRowWidth() {
        return this.configScreen.width;
    }

    @Override
    protected int getScrollbarX() {
        return this.width - 7;
    }

    private static void drawText(TextRenderer textRenderer, DrawContext context, Text text, int wrapX, int y) {
        int textX = PADDING + GAP;
        int textY = y + 6;

        int textWidth = wrapX - textX - GAP;

        for (OrderedText orderedText : textRenderer.wrapLines(text, textWidth)) {
            context.drawText(textRenderer, orderedText, textX, textY, Colors.WHITE, true);
            textY += 9;
        }
    }

    public static class BugWidgetEntry extends WidgetEntry {
        private final MinecraftClient client;
        private final ConfigScreen configScreen;
        private final ButtonWidget optionButton;
        private final ButtonWidget resetButton;
        private final ButtonWidget linkButton;
        private final ConfigManager.BugOption option;

        private static final int BUG_OPTION_WIDTH = OPTION_WIDTH - BUTTON_WIDTH - GAP;

        public void updateButtonState() {
            this.optionButton.setMessage(this.option.buttonText());
            this.resetButton.active = !this.option.isDefault();
        }

        public BugWidgetEntry(MinecraftClient client, ConfigScreen screen, ConfigManager.BugOption option) {
            this.option = option;
            this.configScreen = screen;
            this.client = client;

            this.linkButton = TextIconButtonWidget.builder(ScreenTexts.EMPTY,
                            button -> this.option.openLink(screen), true)
                    .width(BUTTON_WIDTH)
                    .texture(new Identifier(KeybindBugFixes.MOD_ID +  ":icon/link"), 16, 16)
                    .build();

            this.optionButton = ButtonWidget.builder(this.option.buttonText(), button -> {
                this.option.toggleValue();
                this.updateButtonState();
            })
                    .tooltip(this.option.tooltip())
                    .width(BUG_OPTION_WIDTH)
                    .build();

            this.resetButton = TextIconButtonWidget.builder(ScreenTexts.EMPTY, button -> {
                this.option.resetValue();
                this.updateButtonState();
            }, true)
                    .width(BUTTON_WIDTH)
                    .texture(new Identifier(KeybindBugFixes.MOD_ID + ":icon/reset"), 16, 16)
                    .build();

            this.resetButton.active = !this.option.isDefault();
            this.linkButton.active = this.option.link() != null;
        }

        @Override
        public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight,
                           int mouseX, int mouseY, boolean hovered, float tickDelta) {
            this.linkButton.setPosition(this.configScreen.width - PADDING - GAP - BUTTON_WIDTH, y);
            this.linkButton.render(context, mouseX, mouseY, tickDelta);

            this.optionButton.setPosition(this.linkButton.getX() - GAP - BUG_OPTION_WIDTH, y);
            this.optionButton.render(context, mouseX, mouseY, tickDelta);

            this.resetButton.setPosition(this.optionButton.getX() - GAP - BUTTON_WIDTH, y);
            this.resetButton.render(context, mouseX, mouseY, tickDelta);

            Text text = Text.translatable(this.option.translationKey());
            drawText(this.client.textRenderer, context, text, this.resetButton.getX(), y);
        }

        @Override
        public List<? extends Selectable> selectableChildren() {
            return List.of(this.resetButton, this.optionButton, this.linkButton);
        }

        @Override
        public List<? extends Element> children() {
            return List.of(this.resetButton, this.optionButton, this.linkButton);
        }
    }

    public static class TweakWidgetEntry extends WidgetEntry {
        private final MinecraftClient client;
        private final ConfigScreen configScreen;
        private final ButtonWidget optionButton;
        private final ButtonWidget resetButton;
        private final ConfigManager.TweakOption option;

        public void updateButtonState() {
            this.optionButton.setMessage(this.option.buttonText());
            this.resetButton.active = !this.option.isDefault();
        }

        public TweakWidgetEntry(MinecraftClient client, ConfigScreen screen, ConfigManager.TweakOption option) {
            this.option = option;
            this.configScreen = screen;
            this.client = client;

            this.optionButton = ButtonWidget.builder(this.option.buttonText(), button -> {
                        this.option.toggleValue();
                        this.updateButtonState();
                    })
                    .tooltip(this.option.tooltip())
                    .width(OPTION_WIDTH)
                    .build();

            this.resetButton = TextIconButtonWidget.builder(ScreenTexts.EMPTY, button -> {
                        this.option.resetValue();
                        this.updateButtonState();
                    }, true)
                    .width(BUTTON_WIDTH)
                    .texture(new Identifier(KeybindBugFixes.MOD_ID + ":icon/reset"), 16, 16)
                    .build();

            this.resetButton.active = !this.option.isDefault();
        }

        @Override
        public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight,
                           int mouseX, int mouseY, boolean hovered, float tickDelta) {
            this.optionButton.setPosition(this.configScreen.width - PADDING - GAP - OPTION_WIDTH, y);
            this.optionButton.render(context, mouseX, mouseY, tickDelta);

            this.resetButton.setPosition(this.optionButton.getX() - GAP - BUTTON_WIDTH, y);
            this.resetButton.render(context, mouseX, mouseY, tickDelta);

            Text text = Text.translatable(this.option.translationKey());
            drawText(this.client.textRenderer, context, text, this.resetButton.getX(), y);
        }

        @Override
        public List<? extends Selectable> selectableChildren() {
            return List.of(this.resetButton, this.optionButton);
        }

        @Override
        public List<? extends Element> children() {
            return List.of(this.resetButton, this.optionButton);
        }
    }

    public static class KeybindWidgetEntry extends WidgetEntry {
        private final MinecraftClient client;
        private final ConfigScreen configScreen;
        private final ButtonWidget optionButton;
        private final ButtonWidget resetButton;
        public final ConfigManager.KeybindOption option;
        private MutableText duplicateText;
        private boolean isDuplicate;
        private boolean unknownModifier;
        private boolean sameModifier;

        private void updateWarning() {
            this.duplicateText = Text.empty();
            this.isDuplicate = false;
            this.unknownModifier = false;
            this.sameModifier = false;

            if (!this.option.isUnbound()) {
                ConfigManager.KeybindOption modifier = this.option.modifier();
                InputUtil.Key key = this.option.value();

                if (modifier != null) {
                    if (modifier.value().getCode() == GLFW.GLFW_KEY_UNKNOWN) {
                        this.unknownModifier = true;
                    }

                    if (modifier.value().equals(key)) {
                        this.sameModifier = true;
                    }
                }

                if (modifier == null) {
                    for (KeyBinding keyBinding : this.client.options.allKeys) {
                        if (((KeyBindingAccessor) keyBinding).getBoundKey().equals(key)) {
                            if (this.isDuplicate) {
                                this.duplicateText.append(", ");
                            }

                            this.isDuplicate = true;
                            this.duplicateText.append(Text.translatable(keyBinding.getTranslationKey()));
                        }
                    }
                }

                for (ConfigManager.KeybindOption option : ConfigManager.KEYBIND_OPTIONS) {
                    ConfigManager.KeybindOption optionModifier = option.modifier();
                    boolean sameModifiers;

                    if (modifier != null && optionModifier != null) {
                        sameModifiers = modifier.value() == optionModifier.value();
                    } else {
                        sameModifiers = modifier == null && optionModifier == null;
                    }

                    if (this.option != option && sameModifiers && key.equals(option.value())) {
                        if (this.isDuplicate) {
                            this.duplicateText.append(", ");
                        }

                        this.isDuplicate = true;
                        this.duplicateText.append(Text.translatable(option.translationKey()));
                    }
                }
            }
        }

        private Text getMessage() {
            if (this.isDuplicate || this.unknownModifier || this.sameModifier) {
                return Text.literal("[ ")
                        .append(this.option.value().getLocalizedText().copy()
                                .formatted(Formatting.WHITE))
                        .append(" ]")
                        .formatted(Formatting.RED);
            } else {
                return this.option.value().getLocalizedText();
            }
        }

        private Tooltip getTooltip() {
            ConfigManager.KeybindOption modifier = this.option.modifier();

            if (this.isDuplicate) {
                return Tooltip.of(Text.translatable("controls.keybinds.duplicateKeybinds", duplicateText));
            } else if (this.unknownModifier) {
                return Tooltip.of(Text.translatable("keybindbugfixes.config.keybinds.unknownModifier",
                        Text.translatable(modifier.translationKey())));
            } else if (this.sameModifier) {
                return Tooltip.of(Text.translatable("keybindbugfixes.config.keybinds.sameModifier",
                        Text.translatable(modifier.translationKey())));
            } else {
                return null;
            }
        }

        public void updateButtonText() {
            this.updateWarning();
            this.optionButton.setMessage(this.getMessage());
            this.optionButton.setTooltip(this.getTooltip());
        }

        public void updateButtonState() {
            this.configScreen.list.updateKeybindWidgets();
            this.resetButton.active = !this.option.isDefault();
        }

        private void selectButton() {
            this.configScreen.selectedKeybindWidget = this;
            this.optionButton.setMessage(Text.literal("> ")
                    .append(this.optionButton.getMessage().copy()
                            .formatted(Formatting.WHITE, Formatting.UNDERLINE))
                    .append(" <")
                    .formatted(Formatting.YELLOW));
        }

        public KeybindWidgetEntry(MinecraftClient client, ConfigScreen screen, ConfigManager.KeybindOption option) {
            this.option = option;
            this.configScreen = screen;
            this.client = client;

            this.updateWarning();

            this.optionButton = ButtonWidget.builder(this.getMessage(),
                            button -> this.selectButton())
                    .tooltip(this.getTooltip())
                    .width(150)
                    .build();

            this.resetButton = TextIconButtonWidget.builder(ScreenTexts.EMPTY, button -> {
                this.option.resetValue();
                this.updateButtonState();
            }, true)
                    .texture(new Identifier(KeybindBugFixes.MOD_ID + ":icon/reset"), 16, 16)
                    .width(20)
                    .build();

            this.resetButton.active = !this.option.isDefault();
        }

        @Override
        public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight,
                           int mouseX, int mouseY, boolean hovered, float tickDelta) {
            this.optionButton.setPosition(this.configScreen.width - PADDING - GAP - OPTION_WIDTH, y);
            this.optionButton.render(context, mouseX, mouseY, tickDelta);

            this.resetButton.setPosition(this.optionButton.getX() - GAP - BUTTON_WIDTH, y);
            this.resetButton.render(context, mouseX, mouseY, tickDelta);

            Text text = Text.translatable(this.option.translationKey());
            drawText(this.client.textRenderer, context, text, this.resetButton.getX(), y);
        }

        @Override
        public List<? extends Selectable> selectableChildren() {
            return List.of(this.resetButton, this.optionButton);
        }

        @Override
        public List<? extends Element> children() {
            return List.of(this.resetButton, this.optionButton);
        }
    }

    public static class TitleWidgetEntry extends WidgetEntry {
        private final MinecraftClient client;
        private final ConfigScreen configScreen;
        private final String translationKey;

        public TitleWidgetEntry(MinecraftClient client, ConfigScreen screen, String translationKey) {
            this.client = client;
            this.configScreen = screen;
            this.translationKey = translationKey;
        }

        @Override
        public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight,
                           int mouseX, int mouseY, boolean hovered, float tickDelta) {
            Text text = Text.translatable(this.translationKey);
            int centerX = this.configScreen.width / 2;

            context.drawCenteredTextWithShadow(this.client.textRenderer, text, centerX, y + 5, Colors.WHITE);
        }

        @Override
        public List<? extends Selectable> selectableChildren() {
            return List.of();
        }

        @Override
        public List<? extends Element> children() {
            return List.of();
        }
    }

    public static abstract class WidgetEntry extends Entry<WidgetEntry> {}
}