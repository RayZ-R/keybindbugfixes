package keybindbugfixes.config;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import keybindbugfixes.KeybindBugFixes;
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

public class OptionListWidget extends ElementListWidget<OptionListWidget.WidgetEntry> {
    private final Set<KeybindWidgetEntry> keybindWidgets = Sets.newHashSet();
    private final ConfigScreen configScreen;

    private static final int OPTION_WIDTH = 174;
    private static final int BUTTON_WIDTH = 20;
    private static final int PADDING = 7;
    private static final int GAP = 4;

    public OptionListWidget(MinecraftClient client, ConfigScreen screen) {
        super(client, screen.width, screen.layout.getContentHeight(), screen.layout.getHeaderHeight(), 25);
        this.centerListVertically = false;
        this.configScreen = screen;
    }

    private void addTitle(String key) {
        TitleWidgetEntry widget = new TitleWidgetEntry(this.client, this.configScreen, key);
        this.addEntry(widget);
    }

    private void addOption(ConfigManager.Option<?> option) {
        if (option instanceof ConfigManager.BugOption bugOption) {
            BugWidgetEntry widget = new BugWidgetEntry(this.client, this.configScreen, bugOption);
            this.addEntry(widget);

        } else if (option instanceof ConfigManager.TweakOption tweakOption) {
            TweakWidgetEntry widget = new TweakWidgetEntry(this.client, this.configScreen, tweakOption);
            this.addEntry(widget);

        } else if (option instanceof ConfigManager.KeybindOption keybindOption) {
            KeybindWidgetEntry widget = new KeybindWidgetEntry(this.client, this.configScreen, keybindOption);
            this.keybindWidgets.add(widget);
            this.addEntry(widget);
        }
    }

    public void init(List<ConfigManager.Category> categories) {
        for (ConfigManager.Category category : categories) {
            boolean titleAdded = false;

            for (ConfigManager.Option<?> option : category.options()) {
                if (KeybindBugFixes.shouldAddOption(option)) {
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
        for (KeybindWidgetEntry widget : this.keybindWidgets) {
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

    public static class OptionWidget {
        private final MinecraftClient client;
        private final ConfigScreen configScreen;
        private final ConfigManager.Option<?> option;
        private final List<ButtonWidget> widgets;
        private final List<Integer> offsets;

        public OptionWidget(MinecraftClient client,
                            ConfigScreen configScreen,
                            ConfigManager.Option<?> option,
                            List<ButtonWidget> widgets,
                            List<Integer> offsets) {
            this.client = client;
            this.configScreen = configScreen;
            this.option = option;
            this.widgets = widgets;
            this.offsets = offsets;
        }

        private static void drawText(TextRenderer textRenderer, DrawContext context, Text text, int wrapX, int y) {
            int textX = PADDING + GAP;
            int textY = y + 6;

            int textWidth = wrapX - textX;

            for (OrderedText orderedText : textRenderer.wrapLines(text, textWidth)) {
                context.drawText(textRenderer, orderedText, textX, textY, Colors.WHITE, true);
                textY += 9;
            }
        }

        public void render(DrawContext context, int y, int mouseX, int mouseY, float tickDelta) {
            for (int i = 0; i < this.widgets.size(); i++) {
                ButtonWidget widget = this.widgets.get(i);
                int offset = this.offsets.get(i);

                widget.setPosition(this.configScreen.width - offset, y);
                widget.render(context, mouseX, mouseY, tickDelta);
            }

            int wrapX = this.configScreen.width - PADDING - GAP - OPTION_WIDTH - GAP;
            Text text = Text.translatable(this.option.translationKey());
            drawText(this.client.textRenderer, context, text, wrapX, y);
        }

        public List<ButtonWidget> widgets() {
            return this.widgets;
        }
    }

    public static class OptionWidgetBuilder {
        private final MinecraftClient client;
        private final ConfigScreen configScreen;
        private final ConfigManager.Option<?> option;

        private final ButtonWidget centerWidget;
        private final List<ButtonWidget> leftWidgets = Lists.newArrayList();
        private final List<ButtonWidget> rightWidgets = Lists.newArrayList();

        public OptionWidgetBuilder(MinecraftClient client,
                                   ConfigScreen configScreen,
                                   ConfigManager.Option<?> option,
                                   ButtonWidget centerWidget) {
            this.client = client;
            this.configScreen = configScreen;
            this.option = option;

            this.centerWidget = centerWidget;
        }

        public OptionWidgetBuilder addLeft(ButtonWidget widget) {
            this.leftWidgets.addFirst(widget);
            return this;
        }

        public OptionWidgetBuilder addRight(ButtonWidget widget) {
            this.rightWidgets.addLast(widget);
            return this;
        }

        public OptionWidget build() {
            List<ButtonWidget> widgets = Lists.newArrayList();
            List<Integer> offsets = Lists.newArrayList();

            widgets.addAll(this.rightWidgets);
            widgets.add(this.centerWidget);
            widgets.addAll(this.leftWidgets);

            int centerWidgetWidth = OPTION_WIDTH;
            for (ButtonWidget widget : this.leftWidgets) centerWidgetWidth -= widget.getWidth() + GAP;
            for (ButtonWidget widget : this.rightWidgets) centerWidgetWidth -= widget.getWidth() + GAP;
            this.centerWidget.setWidth(centerWidgetWidth);

            int lastOffset = PADDING;

            for (ButtonWidget widget : widgets) {
                lastOffset += widget.getWidth() + GAP;
                offsets.add(lastOffset);
            }

            return new OptionWidget(this.client, this.configScreen, this.option, widgets, offsets);
        }
    }

    public static class BugWidgetEntry extends WidgetEntry {
        private final ConfigManager.BugOption option;
        private final ButtonWidget optionButton;
        private final ButtonWidget resetButton;
        private final ButtonWidget linkButton;
        private final OptionWidget optionWidget;

        public void updateButtonState() {
            this.optionButton.setMessage(this.option.buttonText());
            this.resetButton.active = !this.option.isDefault();
        }

        public BugWidgetEntry(MinecraftClient client, ConfigScreen screen, ConfigManager.BugOption option) {
            this.option = option;

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

            this.optionWidget = new OptionWidgetBuilder(client, screen, option, this.optionButton)
                    .addLeft(this.resetButton)
                    .addRight(this.linkButton)
                    .build();
        }

        @Override
        public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight,
                           int mouseX, int mouseY, boolean hovered, float tickDelta) {
            this.optionWidget.render(context, y, mouseX, mouseY, tickDelta);
        }

        @Override
        public List<? extends Selectable> selectableChildren() {
            return this.optionWidget.widgets();
        }

        @Override
        public List<? extends Element> children() {
            return this.optionWidget.widgets();
        }
    }

    public static class TweakWidgetEntry extends WidgetEntry {
        private final ConfigManager.TweakOption option;
        private final ButtonWidget optionButton;
        private final ButtonWidget resetButton;
        private final OptionWidget optionWidget;

        public void updateButtonState() {
            this.optionButton.setMessage(this.option.buttonText());
            this.resetButton.active = !this.option.isDefault();
        }

        public TweakWidgetEntry(MinecraftClient client, ConfigScreen screen, ConfigManager.TweakOption option) {
            this.option = option;

            this.optionButton = ButtonWidget.builder(this.option.buttonText(), button -> {
                        this.option.toggleValue();
                        this.updateButtonState();
                    })
                    .tooltip(this.option.tooltip())
                    .build();

            this.resetButton = TextIconButtonWidget.builder(ScreenTexts.EMPTY, button -> {
                        this.option.resetValue();
                        this.updateButtonState();
                    }, true)
                    .width(BUTTON_WIDTH)
                    .texture(new Identifier(KeybindBugFixes.MOD_ID + ":icon/reset"), 16, 16)
                    .build();

            this.resetButton.active = !this.option.isDefault();

            this.optionWidget = new OptionWidgetBuilder(client, screen, option, this.optionButton)
                    .addLeft(this.resetButton)
                    .build();
        }

        @Override
        public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight,
                           int mouseX, int mouseY, boolean hovered, float tickDelta) {
            this.optionWidget.render(context, y, mouseX, mouseY, tickDelta);
        }

        @Override
        public List<? extends Selectable> selectableChildren() {
            return this.optionWidget.widgets();
        }

        @Override
        public List<? extends Element> children() {
            return this.optionWidget.widgets();
        }
    }

    public static class KeybindWidgetEntry extends WidgetEntry {
        private final MinecraftClient client;
        private final ConfigScreen configScreen;
        private final ConfigManager.KeybindOption option;
        private final ButtonWidget optionButton;
        private final ButtonWidget resetButton;
        private final OptionWidget optionWidget;

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
                return Tooltip.of(Text.translatable("controls.keybinds.duplicateKeybinds", this.duplicateText));
            } else if (this.unknownModifier) {
                return Tooltip.of(Text.translatable(KeybindBugFixes.MOD_ID + ".config.keybinds.unknownModifier",
                        Text.translatable(modifier.translationKey())));
            } else if (this.sameModifier) {
                return Tooltip.of(Text.translatable(KeybindBugFixes.MOD_ID + ".config.keybinds.sameModifier",
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
            this.client = client;
            this.configScreen = screen;
            this.option = option;

            this.updateWarning();

            this.optionButton = ButtonWidget.builder(this.getMessage(), button -> this.selectButton())
                    .tooltip(this.getTooltip())
                    .build();

            this.resetButton = TextIconButtonWidget.builder(ScreenTexts.EMPTY, button -> {
                        this.option.resetValue();
                        this.updateButtonState();
                    }, true)
                    .width(BUTTON_WIDTH)
                    .texture(new Identifier(KeybindBugFixes.MOD_ID + ":icon/reset"), 16, 16)
                    .build();

            this.resetButton.active = !this.option.isDefault();

            this.optionWidget = new OptionWidgetBuilder(client, screen, option, this.optionButton)
                    .addLeft(this.resetButton)
                    .build();
        }

        public ConfigManager.KeybindOption option() {
            return this.option;
        }

        @Override
        public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight,
                           int mouseX, int mouseY, boolean hovered, float tickDelta) {
            this.optionWidget.render(context, y, mouseX, mouseY, tickDelta);
        }

        @Override
        public List<? extends Selectable> selectableChildren() {
            return this.optionWidget.widgets();
        }

        @Override
        public List<? extends Element> children() {
            return this.optionWidget.widgets();
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